package com.example.testapp2.Data.models;

public class SearchHistoryItem {
    private String phoneNumber;
    private long timestamp;
    private String searchResult; // новое поле

    public SearchHistoryItem() {
        // Пустой конструктор для Firestore
    }

    public SearchHistoryItem(String phoneNumber, long timestamp, String searchResult) {
        this.phoneNumber = phoneNumber;
        this.timestamp = timestamp;
        this.searchResult = searchResult;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(String searchResult) {
        this.searchResult = searchResult;
    }
}
