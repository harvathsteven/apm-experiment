package com.example.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
} 