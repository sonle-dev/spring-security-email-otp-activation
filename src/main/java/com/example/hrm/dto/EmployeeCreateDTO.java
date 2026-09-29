package com.example.hrm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreateDTO {
    private String fullName;
    private String email;
    private String department;
    private MultipartFile avatarFile;
}