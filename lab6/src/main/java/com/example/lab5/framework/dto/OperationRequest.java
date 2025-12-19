package com.example.lab5.framework.dto;

import java.util.List;

public class OperationRequest {
    private String operation; // add, sub, mul, div
    private List<PointDTO> leftPoints;
    private List<PointDTO> rightPoints;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<PointDTO> getLeftPoints() {
        return leftPoints;
    }

    public void setLeftPoints(List<PointDTO> leftPoints) {
        this.leftPoints = leftPoints;
    }

    public List<PointDTO> getRightPoints() {
        return rightPoints;
    }

    public void setRightPoints(List<PointDTO> rightPoints) {
        this.rightPoints = rightPoints;
    }
}
