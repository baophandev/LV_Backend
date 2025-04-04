package com.example.PhoneShop.controller;

import com.example.PhoneShop.configuration.VNPayConfig;
import com.example.PhoneShop.utils.VNPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/vnpay")
public class VNPayController {
    @GetMapping("/create-payment")
    public Map<String, Object> createPayment(
            @RequestParam("amount") int amount,
            @RequestParam("orderInfo") String orderInfo,
            @RequestParam(value = "orderType", required = false, defaultValue = "other") String orderType,
            @RequestParam(value = "language", required = false, defaultValue = "vn") String locale,
            @RequestParam(value = "bankCode", required = false) String bankCode) throws UnsupportedEncodingException {

        String vnp_TxnRef = String.format("%08d", new Random().nextInt(100000000));
        long vnp_Amount = amount* 100L; // amount là số tiền VND

        String vnp_IpAddr = VNPayConfig.getIpAddress();
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String vnp_CreateDate = formatter.format(cld.getTime());  //

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());


        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.VNP_VERSION);
        vnp_Params.put("vnp_Command", VNPayConfig.VNP_COMMAND);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.VNP_TMNCODE);
        vnp_Params.put("vnp_Amount", String.valueOf(vnp_Amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", locale);
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.VNP_RETURNURL);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder queryString = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                // Xây dựng hashData
                hashData.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()))
                        .append('&');

                // Xây dựng queryString (THÊM DẤU '&' SAU MỖI CẶP KEY-VALUE)
                queryString.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()))
                        .append('&'); // ⬅️ Thêm dấu '&' ở đây
            }
        }

        // Xóa dấu '&' cuối cùng
        if (hashData.length() > 0) {
            hashData.setLength(hashData.length() - 1);
        }
        if (queryString.length() > 0) {
            queryString.setLength(queryString.length() - 1);
        }

        String vnp_SecureHash = VNPayUtils.hmacSHA512(VNPayConfig.VNP_HASHSECRET, hashData.toString());
        queryString.append("&vnp_SecureHash=").append(vnp_SecureHash);

        String paymentUrl = VNPayConfig.VNP_PAYURL + "?" + queryString.toString();
        Map<String, Object> response = new HashMap<>();
        response.put("code", "00");
        response.put("message", "success");
        response.put("paymentUrl", paymentUrl);


        return response;
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<Void> handleVNPayReturn(HttpServletRequest request) {
        Map<String, String> params = getRequestParams(request);
        String redirectUrl;

        if (!isValidSignature(params)) {
            redirectUrl = "http://localhost:3000/payment-result?status=error&message=Chữ%20ký%20không%20hợp%20lệ";
        } else {
            String responseCode = params.get("vnp_ResponseCode");
            String txnRef = params.get("vnp_TxnRef");

            if ("00".equals(responseCode)) {
                updateOrderStatus(txnRef, "PAID");
                redirectUrl = "http://localhost:5173/vnpay/success";
            } else {
                String errorMsg = getErrorMessage(responseCode);
                redirectUrl = "http://localhost:5173/vnpay/fail" + URLEncoder.encode(errorMsg, StandardCharsets.UTF_8);
            }
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }


    // Lấy các tham số từ request
    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            params.put(paramName, request.getParameter(paramName));
        }
        return params;
    }

    // Kiểm tra chữ ký
    private boolean isValidSignature(Map<String, String> params) {
        try {
            // Tạo lại chuỗi hashData
            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            for (String fieldName : fieldNames) {
                if (!"vnp_SecureHash".equals(fieldName)) { // Bỏ qua SecureHash
                    String fieldValue = params.get(fieldName);
                    hashData.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                            .append("=")
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()))
                            .append("&");
                }
            }
            if (hashData.length() > 0) {
                hashData.setLength(hashData.length() - 1); // Xóa dấu '&' cuối
            }

            // Tạo chữ ký
            String calculatedSignature = VNPayUtils.hmacSHA512(VNPayConfig.VNP_HASHSECRET, hashData.toString());
            String receivedSignature = params.get("vnp_SecureHash");

            return calculatedSignature.equalsIgnoreCase(receivedSignature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xử lý thông báo lỗi
    private String getErrorMessage(String responseCode) {
        switch (responseCode) {
            case "00": return "Giao dịch thành công";
            case "07": return "Giao dịch nghi ngờ gian lận";
            case "09": return "Giao dịch thất bại";
            default: return "Lỗi không xác định";
        }
    }

    // Hàm cập nhật trạng thái đơn hàng (ví dụ)
    private void updateOrderStatus(String txnRef, String status) {
        // Triển khai logic cập nhật CSDL tại đây
        System.out.println("Cập nhật đơn hàng " + txnRef + " sang trạng thái: " + status);
    }
}