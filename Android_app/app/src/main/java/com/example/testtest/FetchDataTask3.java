package com.example.testtest;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageButton;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

public class FetchDataTask3 extends AsyncTask<Void, Void, List<Carte>> {  //rajoute une carte a l'interface d'une collection secondaire (pas la principale)
    private MainActivity mainActivity;
    private Collec collec;

    public FetchDataTask3(MainActivity mainActivity, Collec collec) {
        this.mainActivity = mainActivity;
        this.collec = collec;
    }

    @Override
    protected List<Carte> doInBackground(Void... voids) {
        CarteDao carteDao = mainActivity.getDb().carteDao();
        return carteDao.getAll();  // Assuming getAll() is the method to retrieve all cards
    }

    @Override
    protected void onPostExecute(List<Carte> cartes) {
        ConstraintLayout layout = (ConstraintLayout) mainActivity.findViewById(R.id.image_buttons_container);

        // Use an iterator to safely remove ImageButtons
        List<View> viewsToRemove = new ArrayList<>();
        for(int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if(child instanceof ImageButton) {
                viewsToRemove.add(child);
            }
        }

        // Remove the views
        for (View view : viewsToRemove) {
            layout.removeView(view);
        }

        mainActivity.addCards2(collec);
    }

}