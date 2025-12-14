package com.example.lab5.ui.dto;

public class MathFunctionOption implements Comparable<MathFunctionOption> {
    private final String localizedName;
    private final String key;

    public MathFunctionOption(String localizedName, String key) {
        this.localizedName = localizedName;
        this.key = key;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int compareTo(MathFunctionOption other) {
        return this.localizedName.compareToIgnoreCase(other.localizedName);
    }
}
