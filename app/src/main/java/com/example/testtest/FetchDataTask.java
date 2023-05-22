package com.example.testtest;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FetchDataTask extends AsyncTask<String, Void, Collec> {  //servait au debug lors du developpement
    private MainActivity mainActivity;

    public FetchDataTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected Collec doInBackground(String... params) {
        try {
            Collec collec = new Collec("collec");

            Carte c1 = new Carte();
            c1.init("GRCR-EN016");

            Carte c2 = new Carte();
            c2.init("LED2-EN039");

            Carte c3 = new Carte();
            c3.init("CT04-EN001");

            Carte c4 = new Carte();
            c4.init("CT06-ENS03");

            Carte c5 = new Carte();
            c5.init("RDS-EN008");

            Carte c6 = new Carte();
            c6.init("DLCS-EN147");

            Carte c7 = new Carte();
            c7.init("DUPO-EN039");

            Carte c8 = new Carte();
            c8.init("JUMP-EN008");

            Carte c9 = new Carte();
            c9.init("SRL-EN000");

            Collec collec1 = new Collec("collec1");
            collec1.addCarte(c1);
            collec1.addCarte(c2);
            collec1.addCarte(c3);
            collec1.addCarte(c4);
            collec1.addCarte(c5);
            collec1.addCarte(c6);
            collec1.addCarte(c7);
            collec1.addCarte(c8);
            collec1.addCarte(c9);



            return collec1;
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
