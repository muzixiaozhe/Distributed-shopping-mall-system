package com.pyg.seckill.service;



import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;


public class UserDetailServiceImpl implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        List<GrantedAuthority> grantedAuths=new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_SHOP"));
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new User(s,"",grantedAuths);
    }
}
