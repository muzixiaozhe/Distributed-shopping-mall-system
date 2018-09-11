package com.pyg.shop.service;


import com.pyg.pojo.TbSeller;
import com.pyg.sellergoods.service.SellerService;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import java.util.ArrayList;
import java.util.List;


public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        TbSeller one = sellerService.findOne(s);
        List<GrantedAuthority> grantedAuths=new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_SHOP"));
        if (one==null||!"1".equals(one.getStatus())){
            return null;
        }
        return new User(s,one.getPassword(),grantedAuths);
    }
}
