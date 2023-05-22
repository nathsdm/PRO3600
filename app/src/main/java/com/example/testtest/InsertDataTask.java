package com.example.testtest;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsertDataTask extends AsyncTask<Void, Void, List<String>> { //servait au debug
    private MainActivity mainActivity;
    private ArrayList<Carte> cards;

    public InsertDataTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.cards = cards;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        CarteDao carteDao = mainActivity.getDb().carteDao();
        List<String> existingCards = new ArrayList<>();

        try {

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

            List<Carte> cards = Arrays.asList(c1, c2, c3, c4, c5, c6, c7, c8, c9);


            for (Carte card : cards) {
                // Check if the card already exists in the database
                Carte existingCard = carteDao.findCardByCode(card.getCode());
                if (existingCard == null) {
                    // If the card is not found in the database, insert it
                    carteDao.insert(card);
                } else {
                    // If the card is found in the database, print a message (you will need to modify this)
                    existingCards.add(card.getCode());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        return existingCards;
    }
    @Override
    protected void onPostExecute(List<String> existingCards) {
        // Loop through each existing card and show a toast message
        for (String cardCode : existingCards) {
            Toast.makeText(mainActivity, "You've already added this card to this collection: " + cardCode, Toast.LENGTH_SHORT).show();
        }
    }
}
