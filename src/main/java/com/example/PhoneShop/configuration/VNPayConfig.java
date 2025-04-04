package com.example.PhoneShop.configuration;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;


public class VNPayConfig {
    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND = "pay";
    public static final String VNP_TMNCODE = "TYIS368V"; // Thay bằng mã thực tế
    public static final String VNP_RETURNURL = "http://localhost:8080/phone/api/vnpay/vnpay-return";
    public static final String VNP_PAYURL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String VNP_HASHSECRET = "URC7H4B4EH89E3AX9750MM0VYJ4NQ0GE"; // Thay bằng key thực tế

    // Lấy IP người dùng
    public static String getIpAddress() {
        return "127.0.0.1"; // Thay bằng logic lấy IP thực tế nếu cần
    }
}