package com.example.testtest;

import java.util.ArrayList;
import java.util.Collections;


public class Collec {
    private ArrayList<Carte> collection;
    private double prix; // prix total de la collection
    private int nombre; //nombre de cartes dans la collection

    public Collec() {
        this.collection = new ArrayList<>();
        this.prix = 0;
        this.nombre = 0;
    }

    public ArrayList<Carte> getCollection() {

        return collection;
    }

    public double getPrix() {
        return prix;
    }

    public int getNombre() {
        return nombre;
    }

    public void addCarte(Carte card) {
        prix = prix + card.getPrix();
        nombre = nombre + 1;
        collection.add(card);


    }

    public ArrayList<String> getCodes() {
        ArrayList<String> codes = new ArrayList<>();
        for (Carte carte : collection) {
            codes.add(carte.getCode());
        }
        return codes;
    }

    public void setCollection(ArrayList<Carte> collection1){
        this.collection = collection1;
    }

}
