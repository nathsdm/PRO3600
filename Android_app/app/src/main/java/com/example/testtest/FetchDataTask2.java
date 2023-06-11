package com.example.testtest;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;

public class FetchDataTask2 extends AsyncTask<String, Void, Collec> {
    private MainActivity mainActivity;
    private String code;

    public FetchDataTask2(MainActivity mainActivity, String code) { //rajoute une carte à la collection principale. Elle doit contenir toutes
        this.mainActivity = mainActivity;                        //les cartes de toutes les autres collections
        this.code = code;
    }

    @Override
    protected Collec doInBackground(String... params) {
        try {
            Collec collec = new Collec("collec");

            Carte c1 = new Carte();
            c1.init(code);




            collec.addCarte(c1);




            return collec;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPostExecute(Collec result) {
        mainActivity.addCards(result);
        ArrayList<Carte> cards = result.getCollection();   //update la barre de recherche
        CarteArrayAdapter adapter = new CarteArrayAdapter(mainActivity, android.R.layout.simple_dropdown_item_1line, cards);
        mainActivity.searchBar.setAdapter(adapter);
    }
}
