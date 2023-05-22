package com.example.testtest;

import androidx.room.*;

@Database(entities = {Carte.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CarteDao carteDao();
}
