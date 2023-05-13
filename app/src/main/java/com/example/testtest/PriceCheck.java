import com.example.testtest.Collec;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class PriceCheck {



    public static ArrayList<String> priceCheckDown(Collec col) { //return une liste des codes de cartes se trouvant dans la liste dont le prix a baissé significativement dernièrement
        try {                                                   //si il n'y en a pas, renvoie une liste vide
            ArrayList<String> tab = new ArrayList<String>();
            Document document = Jsoup.connect("https://yugiohprices.com/").get();
            Element body = document.body();
            String s = body.text();
            int patternIndex1 = s.indexOf("Falling Cards Card Price Shift");
            int patternIndex2 = s.indexOf("Rising Cards Card Price Shift");
            String s2 = s.substring(patternIndex1, patternIndex2);
            for (String code : col.getCodes()) {
                if (s2.contains(code)) {
                    tab.add(code);
                }

            }
            return (tab);

        } catch (IOException e) {
            e.printStackTrace();
            ArrayList<String> erreur = new ArrayList<String>();
            return (erreur);
        }
    }

    public static ArrayList<String> priceCheckUp(Collec col){ //return une liste des codes de cartes se trouvant dans la liste dont le prix a augmenté significativement dernièrement
        try {                                                   //si il n'y en a pas, renvoie une liste vide
            ArrayList<String> tab = new ArrayList<String>();
            Document document = Jsoup.connect("https://yugiohprices.com/").get();
            Element body = document.body();
            String s = body.text();
            int patternIndex1 = s.indexOf("Rising Cards Card Price Shift");
            int patternIndex2 = s.indexOf("Change Settings Home");
            String s2 = s.substring(patternIndex1 , patternIndex2 );
            for (String code : col.getCodes()) {
                if (s2.contains(code)){
                    tab.add(code);
                }

            }
            return (tab);

        } catch (IOException e){
            e.printStackTrace();
            ArrayList<String> erreur = new ArrayList<String>();
            return(erreur);
        }


    }
    public static double priceDownCode(String code){
        try {                                                   //si il n'y en a pas, renvoie une liste vide
            ArrayList<String> tab = new ArrayList<String>();
            Document document = Jsoup.connect("https://yugiohprices.com/").get();
            Element body = document.body();
            String s = body.text();
            String p1 = "-";
            String p2 = "%";
            int startIndex = s.indexOf(code);
            int Index1 = s.indexOf(p1, startIndex + code.length());
            int Index2 = s.indexOf(p2, startIndex + code.length());
            String down = s.substring(Index1 , Index2 );
            double downDouble = Double.parseDouble(down);
            return(downDouble);



        } catch (IOException e) {
            e.printStackTrace();
            return (0);
        }
    }

    public static double priceUpCode(String code){
        try {                                                   //si il n'y en a pas, renvoie une liste vide
            ArrayList<String> tab = new ArrayList<String>();
            Document document = Jsoup.connect("https://yugiohprices.com/").get();
            Element body = document.body();
            String s = body.text();
            String p1 = "+";
            String p2 = "%";
            int startIndex = s.indexOf(code);
            int Index1 = s.indexOf(p1, startIndex + code.length());
            int Index2 = s.indexOf(p2, startIndex + code.length());
            String down = s.substring(Index1 , Index2 );
            double downDouble = Double.parseDouble(down);
            return(downDouble);



        } catch (IOException e) {
            e.printStackTrace();
            return (0);
        }
    }

}