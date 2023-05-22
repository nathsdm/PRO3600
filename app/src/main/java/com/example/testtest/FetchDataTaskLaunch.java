package com.example.testtest;

import android.os.AsyncTask;

import java.util.List;

public class FetchDataTaskLaunch extends AsyncTask<Void, Void, List<Carte>> { //affiche la collection principale au lancement de l'appli
    private MainActivity mainActivity;

    public FetchDataTaskLaunch(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected List<Carte> doInBackground(Void... voids) {
        CarteDao carteDao = mainActivity.getDb().carteDao();
        return carteDao.getAll();  // Assuming getAll() is the method to retrieve all cards
    }

    @Override
    protected void onPostExecute(List<Carte> cartes) {
        Collec collec = new Collec("collec");
        for (Carte c : cartes) {
            collec.addCarte(c);
        }

        mainActivity.addCards(collec);
    }
}
