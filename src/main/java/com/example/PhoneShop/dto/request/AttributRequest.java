package com.example.PhoneShop.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttributRequest {
    @NotBlank(message = "Operation system can not be blank")
    String os;

    @NotBlank(message = "Cpu name can not be blank")
    String cpu;

    @NotBlank(message = "Ram can not be blank")
    String ram;

    @NotBlank(message = "Rom can not be blank")
    String rom;

    @NotBlank(message = "Camera can not be blank")
    String camera;

    @NotBlank(message = "Pin volume can not be blank")
    String pin;

    @NotBlank(message = "Sim can not be blank")
    String sim;

    String others;
}
