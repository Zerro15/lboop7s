package com.example.lab5.framework.dto;

import lombok.Data;

@Data
public class CompositeRenameRequest {
    private String oldName;
    private String newName;
}
