package com.example.auth.dto;

import lombok.Data;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class CommentCreateRequest {


    @NotNull(message = "Comment text cannot be null")


    @NotBlank(message = "Comment text cannot be blank")


    @Size(min = 1, max = 500, message = "Comment must be between 1 and 500 characters")
    private String text;
}
