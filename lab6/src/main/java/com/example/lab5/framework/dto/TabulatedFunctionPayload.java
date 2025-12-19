package com.example.lab5.framework.dto;

public class TabulatedFunctionPayload {
    private String name;
    private double[] xValues;
    private double[] yValues;
    private String factoryType;
    private boolean insertable;
    private boolean removable;


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double[] getXValues() { return xValues; }
    public void setXValues(double[] xValues) { this.xValues = xValues; }

    public double[] getYValues() { return yValues; }
    public void setYValues(double[] yValues) { this.yValues = yValues; }

    public String getFactoryType() { return factoryType; }
    public void setFactoryType(String factoryType) { this.factoryType = factoryType; }

    public boolean isInsertable() { return insertable; }
    public void setInsertable(boolean insertable) { this.insertable = insertable; }

    public boolean isRemovable() { return removable; }
    public void setRemovable(boolean removable) { this.removable = removable; }
}
