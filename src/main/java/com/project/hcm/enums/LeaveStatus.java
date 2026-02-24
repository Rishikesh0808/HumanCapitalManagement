package com.project.hcm.enums;

public enum LeaveStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String dbValue;

    LeaveStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static LeaveStatus mapEnumsFromDbValue(String value) {
        if (value == null) {
            return null;
        }
        for (LeaveStatus status : values()) {
            if (status.dbValue.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown leave status: " + value);
    }
}
