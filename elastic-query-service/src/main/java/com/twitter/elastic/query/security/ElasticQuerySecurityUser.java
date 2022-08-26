package com.twitter.elastic.query.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class ElasticQuerySecurityUser implements UserDetails {

    private final String userName;


    private Collection<? extends GrantedAuthority> authorities;

    public ElasticQuerySecurityUser(String userName, Collection<? extends GrantedAuthority> authorities) {
        this.userName = userName;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "N/A";
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
    public static class Builder {

        private Builder() {
        }

        private static final Builder self = new Builder();
        private String userName;
        private Collection<? extends GrantedAuthority> authorities;

        public static Builder withUserName(String userName) {
            self.userName = userName;
            return self;
        }

        public static Builder withAuthorities(Collection<? extends GrantedAuthority> authorities) {
            self.authorities = authorities;
            return self;
        }

        public static ElasticQuerySecurityUser build() {
            return new ElasticQuerySecurityUser(Builder.self.userName, Builder.self.authorities);
        }
    }

    public static class AltBuilder {
        private AltBuilder() {
        }

        private static AltBuilder self;
        private String userName;
        private Collection<? extends GrantedAuthority> authorities;

        public static AltBuilder builder() {
            var altBuilder = new AltBuilder();
            return self = altBuilder;
        }

        public AltBuilder withUserName(String userName) {
            self.userName = userName;
            return self;
        }

        public AltBuilder withAuthorities(Collection<? extends GrantedAuthority> authorities) {
            self.authorities = authorities;
            return self;
        }

        public ElasticQuerySecurityUser build() {
            return new ElasticQuerySecurityUser(self.userName, self.authorities);
        }

    }
}
