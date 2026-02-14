package com.hotel.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成admin123的BCrypt密码
        String adminPassword = encoder.encode("admin123");
        System.out.println("admin123: " + adminPassword);
        
        // 生成user123的BCrypt密码
        String userPassword = encoder.encode("user123");
        System.out.println("user123: " + userPassword);
    }
}
