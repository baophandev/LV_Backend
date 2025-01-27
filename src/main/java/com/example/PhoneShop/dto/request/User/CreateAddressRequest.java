package com.example.PhoneShop.dto.request.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAddressRequest {
    @NotBlank(message = "User ID cannot be blank")
    String userId;

    @NotBlank(message = "Province cannot be blank")
    String province;

    @NotBlank(message = "District cannot be blank")
    String district;

    @NotBlank(message = "Ward cannot be blank")
    String ward;

    @NotBlank(message = "Detail cannot be blank")
    String detail;

    @NotBlank(message = "Receiver name cannot be blank")
    String receiverName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Receiver phone must be a valid 10-digit number")
    String receiverPhone;
}
