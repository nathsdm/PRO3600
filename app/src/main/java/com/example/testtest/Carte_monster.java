package com.example.testtest;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Carte_monster extends Carte{
    public Carte_monster(){

    }
  /*  private String attributs; //pour les monstres uniquement (lumière, ténèbres, vent, feu, eau, terre, divin). Pour les magies ou pièges: null
    private String[] type; //type, supertype, effet, ...
    //pour les monstres : guerrier, magicien, ... / fusion, xyz, toon, ... / effet ou normal (à noter comme SANS EFFET)
    private int atk;
    private int def;
    private int niveau;
    private int linkval;
    private int scale;
    public Carte_monster(String code, String nom) throws IOException {
        super(code, nom);
        this.setCategorie("monster");

        String out_ygopro = new Scanner(new URL(this.getLien_ygopro_name()).openStream(), "UTF-8").useDelimiter("\\A").next();
        //statistiques de la carte.
        this.attributs=getInfoFromYGO_String(out_ygopro,"attribute");
        String hold = getInfoFromYGO_String(out_ygopro,"type");
        hold=hold.substring(0,hold.indexOf("Monster")-1);
        hold=hold.replace(" ","/");
        hold=getInfoFromYGO_String(out_ygopro,"race")+"/"+hold;
        this.type=hold.split("/");
        this.atk = getInfoFromYGO_int(out_ygopro,"atk");
        this.def = getInfoFromYGO_int(out_ygopro,"def");
        this.scale=getInfoFromYGO_int(out_ygopro,"scale");
        this.niveau=getInfoFromYGO_int(out_ygopro,"level");
        this.linkval=getInfoFromYGO_int(out_ygopro,"linkval");
    }

    public Carte_monster(String code_en) throws IOException {
        super(code_en);
        this.setCategorie("monster");

        String out_ygopro = new Scanner(new URL(this.getLien_ygopro_name()).openStream(), "UTF-8").useDelimiter("\\A").next();
        //statistiques de la carte.
        this.attributs=getInfoFromYGO_String(out_ygopro,"attribute");
        String hold =getInfoFromYGO_String(out_ygopro,"type");
        hold=hold.substring(0,hold.indexOf("Monster")-1);
        hold=hold.replace(" ","/");
        hold=getInfoFromYGO_String(out_ygopro,"race")+"/"+hold;
        this.type=hold.split("/");
        this.atk = getInfoFromYGO_int(out_ygopro,"atk");
        this.def = getInfoFromYGO_int(out_ygopro,"def");
        this.scale=getInfoFromYGO_int(out_ygopro,"scale");
        this.niveau=getInfoFromYGO_int(out_ygopro,"level");
        this.linkval=getInfoFromYGO_int(out_ygopro,"linkval");
    }

    public void Afficher(){
        super.Afficher();
        System.out.print("Type : ");
        for (int i =0; i<this.type.length;i++){
            System.out.print(this.type[i]);
            if(i!=this.type.length-1){
                System.out.print("/");
            }
            else {
                System.out.print("\n");
            }
        }
        System.out.println("ATK : "+atk);
        if (linkval==-1) {
            System.out.println("DEF : "+def);
            System.out.println("Niveau : "+niveau);
            if(scale!=-1){
                System.out.println("Echelle Pendule : "+scale);
            }
        }
        else{
            System.out.println("Valeur Lien : "+linkval);
        }
    }*/
}

