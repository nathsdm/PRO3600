package com.example.testtest;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.util.ArrayList;

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
        ArrayList<Carte> cards = result.getCollection();
        CarteArrayAdapter adapter = new CarteArrayAdapter(mainActivity, android.R.layout.simple_dropdown_item_1line, cards);
        mainActivity.searchBar.setAdapter(adapter);
    }
}
