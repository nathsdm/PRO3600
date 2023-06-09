package com.example.testtest;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class Collec {
    private ArrayList<Carte> collec;
    private double prix; // prix total de la collection
    private int nombre; //nombre de cartes dans la collection

    private String name;

    public Collec(String name) {
        this.collec = new ArrayList<>();
        this.prix = 0;
        this.nombre = 0;
        this.name = name;
    }

    public ArrayList<Carte> getCollection() {

        return collec;
    }

    public ArrayList<String> getCollectionNames() {

        ArrayList<String> collec = new ArrayList<>();
        for (Carte carte : this.getCollection()){
            collec.add(carte.getName());
        }
        return collec;


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
        collec.add(card);


    }

    public ArrayList<String> getCodes() {
        ArrayList<String> codes = new ArrayList<>();
        for (Carte carte : collec) {
            codes.add(carte.getCode());
        }
        return codes;
    }

    public void setCollection(ArrayList<Carte> collection1){
        this.collec = collection1;
    }

    public Carte get_carte(int indice) throws IOException, ExpectedException{
        if ((indice>=0) &&(indice<collec.size())){
            try{
                return collec.get(indice);
            }
            catch (Exception e){
                throw e;
            }
        }
        else {
            throw new ExpectedException("La carte n'existe pas.");
        }
    }

    public void rm_Carte(int indice) throws IOException, ExpectedException{
        if ((indice>=0) &&(indice<collec.size())){
            try{
                collec.remove(indice);
            }
            catch (Exception e){
                throw new ExpectedException("Quelque chose à échoué lors de la supression de la carte.");
            }
        }
        else {
            throw new ExpectedException("La carte n'existe pas : indice trop grand");
        }

    }

    public String as_file() throws IOException,ExpectedException{
        String contenu="nom:"+name+"\n";
        String filename="src/appdata_file/to_drive/"+this.name+".txt";
        for (int i=0; i<collec.size();i++){
            contenu=contenu+collec.get(i).getCode()+"\n";

        }
        contenu=contenu+"EndOf"+name;
        try (PrintWriter out = new PrintWriter(filename)) {
            out.println(contenu);
            return(filename);
        }
        catch (Exception e){
            throw e;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Collec get_collec_from_txt (String filename) throws IOException,ExpectedException{
        Scanner scanner = new Scanner(Paths.get(filename), StandardCharsets.UTF_8.name());
        String file = scanner.useDelimiter("\\A").next();
        scanner.close();
        String[] filesplit = file.split("\n");
        String name=filesplit[0].substring(4);
        Collec collec_in=new Collec(name);
        int i =1;
        while (filesplit[i].indexOf("EndOf")==-1){
            try{
                Carte carte = new Carte();
                carte.init(filesplit[i]);
                collec_in.addCarte(carte);
            }
            catch(Exception e){
                throw e;
            }
            i++;
        }
        System.out.println("returning");
        return collec_in;
    }

    public static void save_collec_drive(Context context, ArrayList<Collec> listecollec)
            throws IOException, GeneralSecurityException, ExpectedException {
        Drive driveService= GoogleAuth.service_Setup(context);
        String to_send;
        String filename;
        for (int i=0; i<listecollec.size();i++){
            to_send=listecollec.get(i).as_file();
            if (i==0){
                filename="CARDS.txt";
            }
            else{
                filename="Collection_"+Integer.toString(i)+".txt";
            }
            GoogleAuth.send_Appdata_files(driveService,filename,to_send);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
