package com.example.testapp2.Data.models;

public class SearchHistoryItem {
    private String phoneNumber;
    private long timestamp;

    public SearchHistoryItem() {
        // Пустой конструктор для Firebase
    }

    public SearchHistoryItem(String phoneNumber, long timestamp) {
        this.phoneNumber = phoneNumber;
        this.timestamp = timestamp;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}