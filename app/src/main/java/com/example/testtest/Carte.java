package com.example.testtest;


import androidx.annotation.NonNull;
import androidx.room.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

@Entity
public class Carte {
    // PARTIE 1 : IMAGE DE LA CARTE
    @Ignore
    private File image; // photo de la carte

    // PARTIE 2 : INFORMATION / TEXTE ECRIT SUR LA CARTE
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "code")
    private String code; // code de la carte X

    @ColumnInfo(name = "name")
    private String name; // nom inscrit sur la carte X

    @Ignore
    private String name_en; // nom de la carte en anglais X

    @Ignore
    private String categorie; // monstre, magie, piège

    @ColumnInfo(name = "rarete")
    private String rarete; // common, rare, super rare, ..., rareté de la carte, se voit avec les éléments brillants de la carte.

    @Ignore
    private String effet;

    @Ignore
    private boolean first_ed; // si c'est une première édition ou pas. CHOIX

    @ColumnInfo(name = "frameType")
    private String frameType;

    @ColumnInfo(name = "description")
    private String description;

    // PARTIE 3 : LIENS, INFORMATIONS SUPLéMENTAIRES
    @ColumnInfo(name = "set_long")
    private String set_long; // nom complet du set; X

    @Ignore
    private String lien_cm; // lien CardMarket X

    @Ignore
    private String lien_ygopro_name; // lien vers la page YGOPRO dont on pull le prix et les informations. X

    @ColumnInfo(name = "prix")
    private float prix; // le prix de la carte

    @Ignore
    private String lang_id; // les 2 lettres qui indiquent la langue X

    @Ignore
    private String etat; // état de la carte, arbitraire, uniquement pour le lien CardMarket. CHOIX

    @Ignore
    private String marche; // possédée (de base), wanted (non-possédée, influe sur la photo), vitrine(automatiquement dans une liste spéciale). CHOIX
//c'est pas super pratique tout ça, mais j'ai du mal à voir un découpage des méthodes qui colle bien et qui soit moins compliqué
    //découper en monstre / magie / piege ?

   // private int quantite = 1; //nombre de copies de la carte



   /* public Carte(String code, String nom)throws IOException { //builder1

        this.code = code;
        this.name = nom;
        this.lang_id = code.substring(code.indexOf('-')+1);
        this.lang_id = this.lang_id.substring(0,2);
        this.lang_id = this.lang_id.replaceAll("\\d","");
        String code_norm; //code anglais pour YGO_prices et YGOpro avec le code.
        switch (this.lang_id.length()){
            case 0:
                code_norm = code;
                break;
            case 1:
                code_norm = code.substring(0,code.indexOf('-')+1)+"E"+code.substring(code.indexOf('-')+2);
                break;
            default:
                code_norm = code.substring(0,code.indexOf('-')+1)+"EN"+code.substring(code.indexOf('-')+3);
                break;
        }

        this.description = nomToDescription(name);

        this.lien_ygopro_name = makeYGOPRO_link(this.name,this.code);
        String out_ygopro = new Scanner(new URL(lien_ygopro_name).openStream(), "UTF-8").useDelimiter("\\A").next();
        if ((lang_id.equals("EN"))||(lang_id.equals("E"))||(lang_id.equals(""))){
            this.name_en = this.name;
        }
        else{
            this.name_en = getInfoFromYGO_String(out_ygopro, "name_en");
        }
        String card_sets = out_ygopro.substring(out_ygopro.indexOf("\"card_sets\":"));
        card_sets=card_sets.substring(card_sets.indexOf("["),card_sets.indexOf("]"));
        String set_info="";
        while (set_info.indexOf(code_norm)==-1){
            set_info=card_sets.substring(card_sets.indexOf("{"),card_sets.indexOf("}")+1);
            if (set_info.indexOf(code_norm)==-1){
                card_sets=card_sets.substring(card_sets.indexOf("}")+1);
            }
        }
        this.set_long = getInfoFromYGO_String(set_info,"set_name");
        this.rarete= getInfoFromYGO_String(set_info,"set_rarity");
        this.effet=getInfoFromYGO_String(out_ygopro,"desc");
        this.prix=getPriceFromYGO_int(set_info);
        this.lien_cm=get_CMlink();
    }
*/

    public Carte(){ //le type de database utilisé pour enregister les collections localement necessite un constructeur vide.

    }


    public void init(String code_in)throws IOException{ //correspond au constructeur de la carte
        this.code = code_in;
        this.lang_id = code.substring(code.indexOf('-')+1);
        this.lang_id = this.lang_id.substring(0,2);
        this.lang_id = this.lang_id.replaceAll("\\d","");

        String code_norm; //code anglais pour YGO_prices et YGOpro avec le code.
        if ((lang_id.equals("EN"))||(lang_id.equals("E"))||(lang_id.equals(""))){
            code_norm=code;
        }
        else{
            switch (this.lang_id.length()){
                case 0:
                    code_norm = code_in;
                    break;
                case 1:
                    code_norm = code_in.substring(0,code.indexOf('-')+1)+"E"+code.substring(code.indexOf('-')+2);
                    break;
                default:
                    code_norm = code_in.substring(0,code.indexOf('-')+1)+"EN"+code.substring(code.indexOf('-')+3);
                    break;
            }
        }

        String code_page = "https://db.ygoprodeck.com/api/v7/cardsetsinfo.php?setcode="+code_norm;
        String out_code_page = new Scanner(new URL(code_page).openStream(), "UTF-8").useDelimiter("\\A").next();
        this.name = getInfoFromYGO_String(out_code_page,"name");
        this.frameType = nomToFrameType(name);
        this.name_en = this.name;
        this.lien_ygopro_name = makeYGOPRO_link_no_language(this.name);
        this.description = nomToDescription(name);
        String out_ygopro = new Scanner(new URL(lien_ygopro_name).openStream(), "UTF-8").useDelimiter("\\A").next();

        String card_sets = "";
        if (out_ygopro.contains("\"card_sets\":")) {
            card_sets = out_ygopro.substring(out_ygopro.indexOf("\"card_sets\":"));
            if (card_sets.contains("[") && card_sets.contains("]")) {
                card_sets = card_sets.substring(card_sets.indexOf("["), card_sets.indexOf("]"));
            }
        }

        String set_info = "";
        while (!card_sets.isEmpty() && card_sets.contains("{") && card_sets.contains("}") && set_info.indexOf(code_norm) == -1) {
            set_info = card_sets.substring(card_sets.indexOf("{"), card_sets.indexOf("}") + 1);
            if (set_info.indexOf(code_norm) == -1 && card_sets.contains("}")) {
                card_sets = card_sets.substring(card_sets.indexOf("}") + 1);
            }
        }

        this.set_long = getInfoFromYGO_String(set_info,"set_name");
        this.rarete= getInfoFromYGO_String(set_info,"set_rarity");
        this.effet=getInfoFromYGO_String(out_ygopro,"desc");
        this.prix=getPriceFromYGO_int(set_info);
        this.lien_cm=get_CMlink();
    }


    @Override
    public String toString() {
        return this.getName();  // nécessaire pour les menus déroulants.
    }

   /* public static Carte attempt_card(String name_in, String code_in)throws IOException, ExpectedException{
        String lang_id = code_in.substring(code_in.indexOf('-')+1);
        lang_id = lang_id.replaceAll("\\d","");
        if (lang_id.length()>2){
            lang_id = lang_id.substring(0,2);
        }
        code_in = code_in.substring(0,code_in.indexOf("-")+1)+lang_id;
        //on s'assure que le code est incomplet.

        try{
            String res = Carte.isItReal(code_in,name_in);
            String code_full = code_in+res.substring((res.length()-3), res.length());
            String type_got = Carte.whatType(name_in,code_full);
            Carte card1;
            switch (type_got){
                case "Monster":
                    card1 = new Carte_monster(code_full,name_in);
                    break;
                case "Spell":
                    card1 = new Carte_spell(code_full,name_in);
                    break;
                case "Trap":
                    card1 = new Carte_trap(code_full,name_in);
                    break;
                default:
                    card1 = new Carte(code_full,name_in);
            }
            return(card1);
        }
        catch (Exception e){
            throw new ExpectedException("Il y a eu un problème : vérifiez votre connection internet, ou les informations rentrées.");
        }
    }*/

   /* public static Carte attempt_card(String code_in)throws IOException, ExpectedException{
        String lang = code_in.substring(code_in.indexOf("-")+1,code_in.indexOf("-")+3).replaceAll("\\d","");

        String code_norm; //code anglais pour YGO_prices et YGOpro avec le code.
        if ((lang.equals("EN"))||(lang.equals("E"))||(lang.equals(""))){
            code_norm=code_in;
        }
        else{
            switch (lang.length()){
                case 0:
                    code_norm = code_in;
                    break;
                case 1:
                    code_norm = code_in.substring(0,code_in.indexOf('-')+1)+"E"+code_in.substring(code_in.indexOf('-')+2);
                    break;
                default:
                    code_norm = code_in.substring(0,code_in.indexOf('-')+1)+"EN"+code_in.substring(code_in.indexOf('-')+3);
                    break;
            }
        }
        try{
            String name_in = Carte.isItReal(code_norm);
            String type_got;
            try{
                type_got = Carte.whatType(name_in,code_norm);
            }
            catch(Exception e){
                throw e;
            }
            Carte card;

            switch (type_got){
                case "Monster":
                    card = new Carte_monster(code_in);
                    break;
                case "Spell":
                    card = new Carte_spell(code_in);
                    break;
                case "Trap":
                    card = new Carte_trap(code_in);
                    break;
                default:
                    throw new ExpectedException("La categorie de cette carte n'est pas reconnue.");
            }


            return(card);
        }
        catch(Exception e){
            throw new ExpectedException("Il y a eu un problème : vérifiez votre connection internet, ou les informations rentrées.");
        }
    }*/
    public void Afficher(){
        System.out.println("Code sur la carte : "+this.code);
        System.out.println("Nom de la carte : "+this.name);
        System.out.println("Nom anglais : "+this.name_en);
        System.out.println("Catégorie : "+this.categorie);
        System.out.println("Rareté : "+this.rarete);
        System.out.println("Set : "+this.set_long);
        System.out.println(this.lien_ygopro_name);
        System.out.println("Lien vers la page CardMarket : "+this.lien_cm);
        System.out.println("Langue : "+this.lang_id);
        System.out.println("Description : "+this.effet);
        System.out.println("Prix : "+this.prix);
    }
    public String getLang_cm (String lang_str){
        //renvoie le morceau de lien pour avoir la carte dans une certaine langue pour CardMarket
        //entrée : un string, les 2 lettres qui désignent la langue de la carte.
        //sortie : le morceau de lien qui correspond.
        String lang_cm;
        switch (lang_str){
            case "FR":
                lang_cm="language=2";
                break;
            case "EN":
                lang_cm="language=1";
                break;
            case "DE":
                lang_cm="language=3";
                break;
            case "IT":
                lang_cm="language=5";
                break;
            case "SP":
                lang_cm="language=4";
                break;
            case "PT":
                lang_cm="language=8";
                break;
            default:
                lang_cm="";
                break;
        }
        return (lang_cm);
    }
    public String get_CMlink()throws IOException {
        String lien= "https://www.cardmarket.com/fr/YuGiOh/Products/Singles/"+(this.set_long.replaceAll("([^a-zA-ZÀ-ÖÙ-öù-ÿĀ-žḀ-ỿ0-9 ])", "")).replace(" ","-")+"/"+(this.name_en.replaceAll("([^a-zA-ZÀ-ÖÙ-öù-ÿĀ-žḀ-ỿ0-9 ])", "")).replace(" ","-")+"?"+getLang_cm(lang_id)+"&"+etat;
        return(lien);
    }

 /*   public static String isItReal(String code_inc, String name)throws IOException,ExpectedException{
        String ret = "";
        String box = code_inc.substring(0,code_inc.indexOf("-"));
        String link_name = makeYGOPRO_link(name,code_inc);
        try {
            String out_name = new Scanner(new URL(link_name).openStream(), "UTF-8").useDelimiter("\\A").next();
            if (out_name.indexOf(box)!=-1){
                ret = out_name.substring(out_name.indexOf(box));
                ret = ret.substring(0,ret.indexOf("\""));

            }
            else {
                throw new ExpectedException("Le code en entrée ne correspond pas.");
            }

        }
        catch (Exception e){
            throw new ExpectedException("Le nom de la carte ne correspond pas.");
        }
        return(ret);
    }

    public static String isItReal(String codefull_norm)throws IOException, ExpectedException {
        String ret = "";

        String link_name = makeYGOPRO_setlink(codefull_norm);
        try {
            String out_name = new Scanner(new URL(link_name).openStream(), "UTF-8").useDelimiter("\\A").next();
            ret = getInfoFromYGO_String(out_name, "name");

        } catch (Exception e) {
            throw new ExpectedException("Le code n'a pas été reconnu.");
        }

        return (ret);
    }*/

    public static boolean goodCodeOrBadCode(String code)throws IOException{ //return true si un code est valide, false sinon
        boolean ret = true;

        String url = "https://db.ygoprodeck.com/api/v7/cardsetsinfo.php?setcode=" + code;
        Document doc = Jsoup.connect(url).ignoreContentType(true).get();
        String text = doc.body().text();
        String firstSevenChars = text.substring(0, 7);
        if (firstSevenChars == "{\"error"){
            ret = false;
        }
        return (ret);

    }


    public static String whatType (String name_in,String code_in)throws IOException, ExpectedException{
        String link = makeYGOPRO_link(name_in,code_in);
        try{
            String out_prices = new Scanner(new URL(link).openStream(), "UTF-8").useDelimiter("\\A").next();
            String card_type= getInfoFromYGO_String(out_prices,"type");
            if (card_type.indexOf("Monster")!=-1){
                return("Monster");
            }
            if (card_type.indexOf("Spell")!=-1) {
                return ("Spell");
            }
            if(card_type.indexOf("Trap")!=-1) {
                return("Trap");
            }
            throw new ExpectedException("Erreur, la catégorie de la carte n'a pas été reconnue.");

        }
        catch (Exception e){
            throw new ExpectedException("Erreur, vérifiez votre connection internet ou les informations entrées.");
        }
    }

    public static String getInfoFromYGO_String(String page, String info){
        String info_norm = "\""+info+"\":\"";
        String fin = "\",";
        String hold;
        if (page.indexOf(info_norm)!=-1){
            hold = page.substring(page.indexOf(info_norm)+info_norm.length());
            hold=hold.substring(0,hold.indexOf(fin));
        }
        else {
            hold="Erreur : information introuvable";
        }
        return(hold);
    }


    public static int getInfoFromYGO_int(String page, String info){
        String info_norm = "\""+info+"\":";
        String fin = ",";
        String hold;
        if (page.indexOf(info_norm)!=-1){
            hold = page.substring(page.indexOf(info_norm)+info_norm.length());
            hold=hold.substring(0,hold.indexOf(fin));
        }
        else {
            hold="-1";
        }
        return(Integer.parseInt(hold));
    }

    public static float getPriceFromYGO_int(String page){
        String info_norm = "\"set_price\":\"";
        String fin = "\"}";
        String hold;
        if (page.indexOf(info_norm)!=-1){
            hold = page.substring(page.indexOf(info_norm)+info_norm.length());
            hold=hold.substring(0,hold.indexOf(fin));
        }
        else {
            hold="-1";
        }
        return(Float.parseFloat(hold));
    }

    public static String makeYGOPRO_link(String name, String code){
        String link_name = "https://db.ygoprodeck.com/api/v7/cardinfo.php?name="+name.replace(" ","%20");
        String lang = code.substring(code.indexOf('-')+1);
        lang = lang.replaceAll("\\d","");
        lang=lang.toLowerCase();
        if ((!lang.equals("en"))&&(!lang.equals("e"))&&(!lang.equals(""))){
            link_name=link_name+"&language="+lang;
        }
        return(link_name);
    }

    public static String makeYGOPRO_link_no_language(String name){
        String link_name = "https://db.ygoprodeck.com/api/v7/cardinfo.php?name="+name.replace(" ","%20");
        return(link_name);
    }

    public static String makeYGOPRO_setlink(String code_en){
        String link_name = "https://db.ygoprodeck.com/api/v7/cardsetsinfo.php?setcode="+code_en;
        return(link_name);
    }




    //AddToCollection
    //RemoveFromCollection
    //Remove
    //Getters


    public static String nomToRarete(String nom){try{
        String cardset = "https://db.ygoprodeck.com/api/v7/cardinfo.php?name="+nom;
        String out = new Scanner(new URL(cardset).openStream(), "UTF-8").useDelimiter("\\A").next();
        int p1 = out.indexOf("\"set_rarity\":\"") + "\"set_rarity\":\"".length();
        int p2 = out.indexOf("\",\"set_rarity_code");
        return out.substring(p1, p2);
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }}


    public String nomToDescription(String nom) {
        try {
            String cardset = "https://db.ygoprodeck.com/api/v7/cardinfo.php?name=" + nom;
            String out = new Scanner(new URL(cardset).openStream(), "UTF-8").useDelimiter("\\A").next();
            int p1 = out.indexOf("\"desc\":") + "\"desc\":".length();
            int p2 = out.indexOf(",\"race");
            return out.substring(p1, p2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String nomToFrameType(String nom) {
        try {
            String cardset = "https://db.ygoprodeck.com/api/v7/cardinfo.php?name=" + nom;
            String out = new Scanner(new URL(cardset).openStream(), "UTF-8").useDelimiter("\\A").next();
            int p1 = out.indexOf("\"frameType\":\"") + "\"frameType\":\"".length();
            int p2 = out.indexOf("\",\"desc\"");
            return out.substring(p1, p2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String nomToFrameCategorie(String nom) {
        try {
            String cardset = "https://db.ygoprodeck.com/api/v7/cardinfo.php?name=" + nom;
            String out = new Scanner(new URL(cardset).openStream(), "UTF-8").useDelimiter("\\A").next();
            int p1 = out.indexOf("\"frameType\":\"") + "\"frameType\":\"".length();
            int p2 = out.indexOf("\",\"desc\"");
            return out.substring(p1, p2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




    public File getImage() {
        return image;
    }

    public String getDescription(){
        return description;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getName_en() {
        return name_en;
    }

    public String getCategorie() {
        return categorie;
    }



    public String getRarete() {
        return rarete;
    }

    public boolean isFirst_ed() {
        return first_ed;
    }

    public String getSet_long() {
        return set_long;
    }

    public String getLien_cm() {
        return lien_cm;
    }

    public String getLien_ygopro_name() {
        return lien_ygopro_name;
    }

    public float getPrix() {
        return prix;
    }

    public String getLang_id() {
        return lang_id;
    }

    public String getEtat() {
        return etat;
    }

    public String getMarche() {
        return marche;
    }

    public String getEffet() {
        return effet;
    }

    public String getFrameType(){return frameType;}

    //Setters

    public void setImage(File image) {
        this.image = image;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public void setRarete(String rarete) {
        this.rarete = rarete;
    }

    public void setFirst_ed(boolean first_ed) {
        this.first_ed = first_ed;
    }

    public void setSet_long(String set_long) {
        this.set_long = set_long;
    }

    public void setLien_cm(String lien_cm) {
        this.lien_cm = lien_cm;
    }

    public void setLien_ygopro_name(String lien_ygopro_name) {
        this.lien_ygopro_name = lien_ygopro_name;
    }
    public void setFrameType(String frameType){this.frameType = frameType;}

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public void setLang_id(String lang_id) {
        this.lang_id = lang_id;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public void setMarche(String marche) {
        this.marche = marche;
    }

    public void setEffet(String effet) {
        this.effet = effet;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
