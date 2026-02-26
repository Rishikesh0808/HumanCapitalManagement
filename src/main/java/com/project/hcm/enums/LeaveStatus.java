package com.project.hcm.enums;

public enum LeaveStatus {

    PENDING("P", "Pending"),
    APPROVED("A", "Approved"),
    REJECTED("R", "Rejected"),
    CANCELLED("C", "Cancelled");

    private final String code;
    private final String description;

    LeaveStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDbValue() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static LeaveStatus fromCode(String code) {
        if (code == null) return null;

        for (LeaveStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown leave status code: " + code);
    }
}
