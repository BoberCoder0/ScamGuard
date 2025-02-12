package com.example.testapp2.Data.models;

import java.util.List;

public class User {
    private String nikName;
    private String email;
    private List<String> historyCall;
    //private ? photo;

    public User() {}
    public User(String NikName, String email, List<String> historyCall) {
        this.nikName = NikName;
        this.email = email;
        this.historyCall = historyCall;
    }

    public String getNikName () {
        return nikName;
    }
    public void setNikName (String NewNikName) {
        this.nikName =NewNikName;
    }
    public String getEmail () {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getHistoryCall() {
        return historyCall;
    }

    public void setHistoryCall(List<String> historyCall) {
        this.historyCall = historyCall;
    }
}
