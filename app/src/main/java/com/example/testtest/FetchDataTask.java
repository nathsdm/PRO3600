package com.example.testtest;

import android.os.AsyncTask;

import java.io.IOException;

// Update the FetchDataTask class
public class FetchDataTask extends AsyncTask<String, Void, Collec> {
    private MainActivity mainActivity;

    public FetchDataTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected Collec doInBackground(String... params) {

        try {
            return mainActivity.setup(); // Move your network operation to this method
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void onPostExecute(Collec result) {
        mainActivity.addCards(result);

        }
    }
