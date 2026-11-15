package com.example.demo;

import jakarta.validation.constraints.NotBlank;

public record Task(
        long id,
        @NotBlank(message = "Title cannot be blank")
        String title,
        boolean done
) {}