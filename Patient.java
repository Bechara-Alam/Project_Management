package com.example.tatwa10.ModelClass;

import com.google.gson.annotations.SerializedName;

public class Patient {

    @SerializedName("patientId")
    private int patientId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("bloodType")
    private String bloodType;

    @SerializedName("address")
    private String address;

    // =========================
    // EXTRA FIELDS (FIXED)
    // =========================

    @SerializedName("dateOfBirth")
    private String dateOfBirth;

    @SerializedName("nationalId")
    private String nationalId;

    @SerializedName("allergies")
    private String allergies;

    @SerializedName("diseases")
    private String diseases;

    @SerializedName("medications")
    private String medications;

    @SerializedName("country")
    private String country;

    @SerializedName("city")
    private String city;

    // =========================
    // EMPTY CONSTRUCTOR
    // =========================
    public Patient() {}

    // =========================
    // GETTERS
    // =========================
    public int getPatientId() { return patientId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getBloodType() { return bloodType; }
    public String getAddress() { return address; }

    public String getDateOfBirth() { return dateOfBirth; }
    public String getNationalId() { return nationalId; }
    public String getAllergies() { return allergies; }
    public String getDiseases() { return diseases; }
    public String getMedications() { return medications; }
    public String getCountry() { return country; }
    public String getCity() { return city; }

    // =========================
    // SETTERS
    // =========================
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public void setAddress(String address) { this.address = address; }

    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public void setDiseases(String diseases) { this.diseases = diseases; }
    public void setMedications(String medications) { this.medications = medications; }
    public void setCountry(String country) { this.country = country; }
    public void setCity(String city) { this.city = city; }
}