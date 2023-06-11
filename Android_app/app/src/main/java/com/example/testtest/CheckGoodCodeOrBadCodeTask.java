package com.example.testtest;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;

public class CheckGoodCodeOrBadCodeTask extends AsyncTask<String, Void, Boolean> {

    private MainActivity4 activity;
    private String code;

    public CheckGoodCodeOrBadCodeTask(MainActivity4 activity) {
        this.activity = activity;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        this.code = strings[0];
        try {
            return Carte.goodCodeOrBadCode(code);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            activity.addCarteToMain(code);
        } else {
            Toast.makeText(activity, "Ce code est invalide", Toast.LENGTH_LONG).show();
        }
    }
}

