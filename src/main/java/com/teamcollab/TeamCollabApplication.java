package com.teamcollab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TeamCollabApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamCollabApplication.class, args);
        System.out.println("========================================");
        System.out.println(" TeamCollab 后端已启动: http://localhost:8080");
        System.out.println(" 登录接口: POST /api/auth/login");
        System.out.println("========================================");
    }
}
