package com.cag.rna_analyzer.service;

import com.cag.rna_analyzer.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDAO userDao;

    public void getUser() {
        userDao.readUserfromDB();
    }
}
