package com.firomsa.ecommerce.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChapaResponse {
    private String event;
    private String first_name;
    private String last_name;
    private String email;
    private String mobile;
    private String currency;
    private String amount;
    private String charge;
    private String status;
    private String failure_reason;
    private String mode;
    private String reference;
    private String created_at;
    private String updated_at;
    private String type;
    private String tx_ref;
    private String payment_method;
    private Customization customization;
    private String meta;
}

@Data
class Customization {
    private String title;
    private String description;
    private String logo;
}
