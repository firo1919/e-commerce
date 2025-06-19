package com.firomsa.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewRequestDTO {
    @NotBlank(message = "rating is required")
    @Min(0)
    private int rating;
    @NotBlank(message = "comment is required")
    private String comment;
}
