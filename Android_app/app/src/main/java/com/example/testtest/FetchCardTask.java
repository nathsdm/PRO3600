package com.example.testtest;

import android.os.AsyncTask;

import java.io.IOException;

public class FetchCardTask extends AsyncTask<String, Void, Carte> {

    @Override
    protected Carte doInBackground(String... strings) {
        String cardCode = strings[0];
        Carte card = new Carte();
        try {
            card.init(cardCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return card;
    }
}

