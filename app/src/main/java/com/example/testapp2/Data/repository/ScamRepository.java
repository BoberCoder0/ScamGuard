package com.example.testapp2.Data.repository;

import android.content.Context;

import com.example.testapp2.Data.database.DatabaseHelper;
import com.example.testapp2.Data.models.ScamInfo;
import com.example.testapp2.utils.PhoneUtils;

public class ScamRepository {

    private final DatabaseHelper databaseHelper;

    public ScamRepository(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public ScamInfo searchInDatabase(String phoneNumber) {
        return databaseHelper.getScamInfo(phoneNumber);
    }

    public String normalizePhoneNumber(String phoneNumber) {
        return PhoneUtils.normalizePhoneNumber(phoneNumber);
    }
}