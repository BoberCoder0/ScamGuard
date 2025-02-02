package com.example.testapp2;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.testapp2.Data.models.ScamInfo;
import com.example.testapp2.Data.repository.ScamRepository;

public class SearchViewModel extends ViewModel {

    private final ScamRepository scamRepository;
    private final MutableLiveData<ScamInfo> searchResult = new MutableLiveData<>();

    public SearchViewModel(Context context) {
        scamRepository = new ScamRepository(context);
    }

    public LiveData<ScamInfo> getSearchResult() {
        return searchResult;
    }

    public void searchPhoneNumber(String phoneNumber) {
        String normalizedNumber = scamRepository.normalizePhoneNumber(phoneNumber);
        if (normalizedNumber == null) {
            searchResult.postValue(null);
        } else {
            searchResult.postValue(scamRepository.searchInDatabase(normalizedNumber));
        }
    }
}
