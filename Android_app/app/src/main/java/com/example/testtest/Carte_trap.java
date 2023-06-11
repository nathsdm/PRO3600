package com.example.testtest;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Carte_trap extends Carte {

    public Carte_trap(){}
    /*private String carac;
    public Carte_trap(String code, String nom) throws IOException {
        super(code, nom);
        this.setCategorie("trap");
        String out_ygoprices = new Scanner(new URL(this.getLien_ygopro_name()).openStream(), "UTF-8").useDelimiter("\\A").next();
        this.carac=getInfoFromYGO_String(out_ygoprices,"race");
    }

    public Carte_trap(String code) throws IOException {
        super(code);
        this.setCategorie("trap");
        String out_ygoprices = new Scanner(new URL(this.getLien_ygopro_name()).openStream(), "UTF-8").useDelimiter("\\A").next();
        this.carac=getInfoFromYGO_String(out_ygoprices,"race");
    }

    public void Afficher(){
        super.Afficher();
        System.out.println(carac);
    }*/
}
