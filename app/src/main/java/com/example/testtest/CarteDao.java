package com.example.testtest;

import androidx.room.*;

import java.util.List;

@Dao
public interface CarteDao {
    @Query("SELECT * FROM carte")
    List<Carte> getAll();

    @Insert
    void insert(Carte carte);

    @Delete
    void delete(Carte carte);

    @Query("SELECT * FROM Carte WHERE code = :code LIMIT 1")
    Carte findCardByCode(String code);
}


