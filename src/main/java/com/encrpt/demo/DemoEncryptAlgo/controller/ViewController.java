package com.encrpt.demo.DemoEncryptAlgo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Dùng @Controller thường để trả về trang giao diện
public class ViewController {

    @GetMapping("/")
    public String homePage() {
        // Trả về file index.html nằm trong thư mục templates
        return "index"; 
    }
}