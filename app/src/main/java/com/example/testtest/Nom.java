package com.example.testtest;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Nom {

        public static int levenshtein(String a, String b) {
            int[][] dp = new int[a.length() + 1][b.length() + 1];

            for (int i = 0; i <= a.length(); i++) {
                dp[i][0] = i;
            }

            for (int j = 0; j <= b.length(); j++) {
                dp[0][j] = j;
            }

            for (int i = 1; i <= a.length(); i++) {
                for (int j = 1; j <= b.length(); j++) {
                    int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(
                            dp[i - 1][j - 1] + cost,
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1)
                    );
                }
            }

            return dp[a.length()][b.length()];
        }

        public static String findClosestName(String inputName, List<String> cardNames) {
            int minDistance = Integer.MAX_VALUE;
            String closestName = "";

            for (String cardName : cardNames) {
                int distance = levenshtein(inputName.toLowerCase(), cardName.toLowerCase());

                if (distance < minDistance) {
                    minDistance = distance;
                    closestName = cardName;
                }
            }

            return closestName;
        }
    public static List<String> getCardNames(Context context) {
        List<String> cardNames = new ArrayList<>();

        try {


            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("cards.txt"))
            );



            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Read line: " + line); // Debug message

                if (line.chars().anyMatch(Character::isLowerCase)) {
                    cardNames.add(line);
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cardNames;
    }

    public static List<String> findCodesForName(String name, Context context) {
        List<String> codes = new ArrayList<>();

        try {
            InputStream inputStream = context.getAssets().open("cards.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            boolean isNameFound = false;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                if (!isNameFound) {
                    if (line.trim().equalsIgnoreCase(name)) {
                        isNameFound = true;
                    }
                } else {
                    if (!line.matches("^[A-Za-z]+$")) {
                        codes.add(line);
                    } else {
                        // We have reached the next name, so stop reading
                        break;
                    }
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return codes;
    }

}



