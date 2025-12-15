package com.example.lab5.framework.dto;

public class MathFunctionDTO {
    private String key;
    private String label;
    private String description;
    private String example;
    private String category;
    private String functionType;
    private boolean custom;
    private String formula;

    // Геттеры и сеттеры
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getFunctionType() { return functionType; }
    public void setFunctionType(String functionType) { this.functionType = functionType; }

    public boolean isCustom() { return custom; }
    public void setCustom(boolean custom) { this.custom = custom; }

    public String getFormula() { return formula; }
    public void setFormula(String formula) { this.formula = formula; }
}