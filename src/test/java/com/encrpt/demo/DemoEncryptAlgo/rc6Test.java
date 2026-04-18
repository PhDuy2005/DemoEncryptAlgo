package com.encrpt.demo.DemoEncryptAlgo;
import java.util.HexFormat; // Java 17+

import com.encrpt.demo.DemoEncryptAlgo.algo.rc6.Rc6Algorithm;


public class rc6Test {
    public static void main(String[] args) {
        // Khởi tạo 16 byte giá trị 0 (Không phải ký tự '0')
        byte[] key = new byte[16]; 
        byte[] plaintext = new byte[16]; 

        Rc6Algorithm algo = new Rc6Algorithm();
        
        // Chạy mã hóa
        byte[] ciphertext = algo.encrypt(plaintext, key);

        // Chuyển kết quả sang Hex để so sánh
        // Lưu ý: Kết quả có thể dài hơn 16 byte do hàm pad() của bạn
        String hexResult = bytesToHex(ciphertext);
        
        System.out.println("Kết quả mã hóa (Hex): " + hexResult);
        System.out.println("Chuẩn RSA (Hex)    : 3704703e01e695a8504064509b7a35f0...");
        
        if (hexResult.startsWith("3704703e01e695a8504064509b7a35f0")) {
            System.out.println("=> THUẬT TOÁN ĐÚNG CHUẨN!");
        } else {
            System.out.println("=> KẾT QUẢ VẪN SAI LỆCH.");
        }
    }

    // Hàm phụ trợ nếu bạn dùng Java cũ hơn 17
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}