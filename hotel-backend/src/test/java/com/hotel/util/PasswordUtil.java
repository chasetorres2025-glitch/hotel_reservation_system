package com.hotel.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 测试密码匹配
        String rawPassword = "admin123";
        String dbPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH";
        
        boolean matches = encoder.matches(rawPassword, dbPassword);
        System.out.println("Password matches: " + matches);
        
        // 生成新的密码
        String newPassword = encoder.encode("admin123");
        System.out.println("New encoded password for admin123: " + newPassword);
        
        String userPassword = encoder.encode("user123");
        System.out.println("New encoded password for user123: " + userPassword);
    }
}
