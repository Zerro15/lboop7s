package com.example.lab5.framework.dto;

import java.util.List;

public class FactoryStateResponse {
    private String activeKey;
    private List<TabulatedFactoryDTO> factories;

    public String getActiveKey() {
        return activeKey;
    }

    public void setActiveKey(String activeKey) {
        this.activeKey = activeKey;
    }

    public List<TabulatedFactoryDTO> getFactories() {
        return factories;
    }

    public void setFactories(List<TabulatedFactoryDTO> factories) {
        this.factories = factories;
    }
}
