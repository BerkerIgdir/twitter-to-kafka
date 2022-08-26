package com.twitter.elastic.query.security.service;

import com.twitter.elastic.query.security.ElasticQuerySecurityUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ElasticQuerySecurityService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
         return ElasticQuerySecurityUser.AltBuilder.builder().withUserName(username).build();
    }
}
