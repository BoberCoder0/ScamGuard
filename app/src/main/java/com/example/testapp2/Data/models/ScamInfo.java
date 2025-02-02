package com.example.testapp2.Data.models;

public class ScamInfo {
    private String category;
    private String complaints;
    private String comment;
    private String result;

    public ScamInfo(String category, String complaints, String comment,String result) {
        this.category = category;
        this.complaints = complaints;
        this.comment = comment;
        this.result = result;
    }

    public String getCategory() {
        return category;
    }

    public String getComplaints() {
        return complaints;
    }

    public String getComment() {
        return comment;
    }

    public String getResult() {
        return result;
    }
}
