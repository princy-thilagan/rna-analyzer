package com.cag.rna_analyzer.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class UserDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public void readUserfromDB() {
    }
}
