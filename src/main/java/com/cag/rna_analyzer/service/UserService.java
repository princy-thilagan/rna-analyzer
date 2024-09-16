package com.cag.rna_analyzer.service;

import com.cag.rna_analyzer.dao.UserDao;
import com.cag.rna_analyzer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserDao userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user != null) {
            var appUser = org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).build();
            return appUser;
        }
        return null;
    }

    public void getUser() {
    }

}