package com.firomsa.ecommerce.v1.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewRequestDTO {
    @NotNull(message = "rating is required")
    @Min(0)
    @Max(5)
    private int rating;
    @NotBlank(message = "comment is required")
    private String comment;
}
