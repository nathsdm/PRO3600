package com.example.testtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InsertDataTask2 extends AsyncTask<Void, Void, Carte> { //update la database locale à l'ajout d'une carte
    private MainActivity4 mainActivity4;
    private String code;


    public InsertDataTask2(MainActivity4 mainActivity4, String code) {
        this.mainActivity4 = mainActivity4;
        this.code = code;
    }

    @Override
    protected Carte doInBackground(Void... voids) {
        CarteDao carteDao = mainActivity4.getDb().carteDao();
        try {
            Carte carte = new Carte();
            carte.init(code);

            // Check if card already exists in the database
            if (carteDao.findCardByCode(carte.getCode()) != null) {
                // Card already exists, do not insert and return null
                return null;
            }

            // Insert the new card
            carteDao.insert(carte);

            // Return the inserted card
            return carte;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPostExecute(Carte result) {
        if (result != null) {
            Toast.makeText(mainActivity4, "Card inserted: " + result.getCode(), Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(mainActivity4, "Card already exists in the collection", Toast.LENGTH_SHORT).show();
        }
    }
}

