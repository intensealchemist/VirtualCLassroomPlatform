package com.virtualclassroom.dto;

/**
 * DTO for course enrollment requests
 */
public class EnrollmentRequest {

    private String enrollmentKey;

    public EnrollmentRequest() {
    }

    public EnrollmentRequest(String enrollmentKey) {
        this.enrollmentKey = enrollmentKey;
    }

    public String getEnrollmentKey() {
        return enrollmentKey;
    }

    public void setEnrollmentKey(String enrollmentKey) {
        this.enrollmentKey = enrollmentKey;
    }
}