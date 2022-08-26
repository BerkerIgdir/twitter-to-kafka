package com.twitter.elastic.query.security.converter;

import com.twitter.elastic.query.security.ElasticQuerySecurityUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtToValidTokenConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String REALM_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String SCOPE_PREFIX = "SCOPE_";
    private static final String SCOPE_CLAIM = "scope";
    private static final String SCOPE_CLAIM_SEPARATOR = " ";
    private static final String USER_NAME_CLAIM = "preferred_username";


    private final UserDetailsService elasticQueryUserDetailService;

    public JwtToValidTokenConverter(UserDetailsService elasticQueryUserDetailService) {
        this.elasticQueryUserDetailService = elasticQueryUserDetailService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        var userName = (String) source.getClaims().get(USER_NAME_CLAIM);
        var userDetail =(ElasticQuerySecurityUser) elasticQueryUserDetailService.loadUserByUsername(userName);
        userDetail.setAuthorities(getCombinedAuth(source));
        return new UsernamePasswordAuthenticationToken(userDetail.getUsername(),userDetail.getPassword(),userDetail.getAuthorities());
    }

    private List<SimpleGrantedAuthority> getCombinedAuth(Jwt source) {
        var roles = getRolesOutOfJWT(source);
        roles.addAll(getScopesOutOfJWT(source));
       return roles.stream()
               .map(SimpleGrantedAuthority::new)
               .toList();
    }

    @SuppressWarnings("unchecked")
    private List<String> getRolesOutOfJWT(Jwt source) {
        Map<String, List<String>> realm = (Map<String, List<String>>) source.getClaims().get(REALM_CLAIM);
        List<String> roles = realm.get(ROLES_CLAIM);
        return roles.stream()
                .map(String::toUpperCase)
                .map(ROLE_PREFIX::concat)
                .collect(Collectors.toList());
    }

    private List<String> getScopesOutOfJWT(Jwt source) {
        String scope = (String) source.getClaims().get(SCOPE_CLAIM);
        return Arrays.stream(scope.split(SCOPE_CLAIM_SEPARATOR))
                .map(s -> SCOPE_PREFIX.concat(s.toUpperCase()))
                .toList();
    }

}
