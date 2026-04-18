package com.example.tatwa10.ModelClass;

public class Notification {

    private int id;
    private int patientId;
    private String message;
    private String createdAt;

    public int getId() {
        return id;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}