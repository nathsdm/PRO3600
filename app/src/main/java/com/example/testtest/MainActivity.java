package com.example.testtest;

import com.example.testtest.MainActivity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {


    AutoCompleteTextView searchBar;

    Collec collec = new Collec();
    int bellowCard1 = 0;
    int bellowCard2 = 0;

    String sortingType = ""; //Price ou Name ou rien(rien quand l'appli est lancée)

    String buttonOrCard = "card";  //card ou button








    public static Collec setup() throws IOException {

        Collec collec1 = new Collec();

        Carte c1 = new Carte("GRCR-EN016");
        Carte c2 = new Carte("LED2-EN039");
        Carte c3 = new Carte("CT04-EN001");
        Carte c4 = new Carte("CT06-ENS03");
        Carte c5 = new Carte("RDS-EN008");
        Carte c6 = new Carte("DLCS-EN147");
        Carte c7 = new Carte("DUPO-EN039 ");
        Carte c8 = new Carte("JUMP-EN008");
        Carte c9 = new Carte("SRL-EN000");


        collec1.addCarte(c1);
        collec1.addCarte(c2);
        collec1.addCarte(c3);
        collec1.addCarte(c4);
        collec1.addCarte(c5);
        collec1.addCarte(c6);
        collec1.addCarte(c7);
        collec1.addCarte(c8);
        collec1.addCarte(c9);



        return collec1;
    }


    public Collec sortCollectionByName(Collec col) {
        ArrayList<Carte> sortedCollection = new ArrayList<>(col.getCollection());

        Collections.sort(sortedCollection, new Comparator<Carte>() {
            @Override
            public int compare(Carte c1, Carte c2) {
                return c1.getName().compareTo(c2.getName());
            }
        });

        Collec colRetur = col;
        colRetur.setCollection(sortedCollection);
        return colRetur;

    }

    public Collec sortCollectionByPrice(Collec col) {
        ArrayList<Carte> sortedCollection = new ArrayList<>(col.getCollection());

        Collections.sort(sortedCollection, new Comparator<Carte>() {
            @Override
            public int compare(Carte c1, Carte c2) {
                return -Double.compare(c1.getPrix(), c2.getPrix());
            }
        });

        Collec colReturn = col;
        colReturn.setCollection(sortedCollection);
        return colReturn;
    }




    public void sortNameCard() {
        if (sortingType != "Name"){
            ConstraintLayout imageButtonsContainer = findViewById(R.id.image_buttons_container);
            imageButtonsContainer.removeAllViews();
            Collec col1 = sortCollectionByName(collec);

            ArrayList<Carte> tempCollection = new ArrayList<>(col1.getCollection());

            collec.getCollection().clear();

            for (Carte carte : tempCollection) {
                int i = collec.getNombre();
                collec.addCarte(carte);
                if (i == 1) {
                    ImageButton newImageButton = createNewImageButtonStart(0.116, carte);
                    bellowCard1 = newImageButton.getId();
                    imageButtonsContainer.addView(newImageButton);
                    YugiohCardImageFetcher.fetchCardImage(this, carte.getName().replace(" ", "_").replace("-", "_"), newImageButton);
                } else if (i == 2) {
                    ImageButton newImageButton = createNewImageButtonStart(0.93, carte);
                    bellowCard2 = newImageButton.getId();
                    imageButtonsContainer.addView(newImageButton);
                    YugiohCardImageFetcher.fetchCardImage(this, carte.getName().replace(" ", "_").replace("-", "_"), newImageButton);
                } else if (i % 2 == 1) {
                    ImageButton newImageButton = createNewImageButton(0.116, bellowCard1, carte);
                    bellowCard1 = newImageButton.getId();
                    imageButtonsContainer.addView(newImageButton);
                    YugiohCardImageFetcher.fetchCardImage(this, carte.getName().replace(" ", "_").replace("-", "_"), newImageButton);
                } else if (i % 2 == 0) {
                    ImageButton newImageButton = createNewImageButton(0.93, bellowCard2, carte);
                    bellowCard2 = newImageButton.getId();
                    imageButtonsContainer.addView(newImageButton);
                    YugiohCardImageFetcher.fetchCardImage(this, carte.getName().replace(" ", "_").replace("-", "_"), newImageButton);
                }
            }

            collec.setCollection(tempCollection);
            sortingType = "Name";}

    }

    public void sortNameButton(){

        ConstraintLayout imageButtonsContainer = findViewById(R.id.image_buttons_container);
        imageButtonsContainer.removeAllViews();
        Collec col1 = sortCollectionByName(collec);
        Collec tempCollec = new Collec();
        tempCollec.setCollection(col1.getCollection());
        try {
            switchToButtons(tempCollec);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        collec.setCollection(tempCollec.getCollection());

        sortingType = "Name";



    }





    public void sortPriceCard() {
        if (sortingType != "Price"){
            ConstraintLayout imageButtonsContainer = findViewById(R.id.image_buttons_container);
            imageButtonsContainer.removeAllViews();
            Collec col1 = sortCollectionByPrice(collec);

            ArrayList<Carte> tempCollection = new ArrayList<>(col1.getCollection());

            collec.getCollection().clear();

            for (Carte carte : tempCollection) {
                int i = collec.getNombre();
                collec.addCarte(carte);
                if (i == 1) {
                    ImageButton newImageButton = createNewImageButtonStart(0.116, carte);
                    bellowCard1 = newImageButton.getId();
                    imageButtonsContainer.addView(newImageButton);
                    YugiohCardImageFetcher.fetchCardImage(this, carte.getName().replace(" ", "_").replace("-", "_"), newImageButton);
                } else if (i == 2) {
                    ImageButton newImageButton = createNewImageButtonStart(0.93, carte);
                    bellowCard2 = newImageButton.getId();
                    imageButtonsContainer.addView(newImageButton);
                    YugiohCardImageFetcher.fetchCardImage(this, carte.getName().replace(" ", "_").replace("-", "_"), newImageButton);
                } else if (i % 2 == 1) {
                    ImageButton newImageButton = createNewImageButton(0.116, bellowCard1, carte);
                    bellowCard1 = newImageButton.getId();
                    imageButtonsContainer.addView(newImageButton);
                    YugiohCardImageFetcher.fetchCardImage(this, carte.getName().replace(" ", "_").replace("-", "_"), newImageButton);
                } else if (i % 2 == 0) {
                    ImageButton newImageButton = createNewImageButton(0.93, bellowCard2, carte);
                    bellowCard2 = newImageButton.getId();
                    imageButtonsContainer.addView(newImageButton);
                    YugiohCardImageFetcher.fetchCardImage(this, carte.getName().replace(" ", "_").replace("-", "_"), newImageButton);
                }
            }

            collec.setCollection(tempCollection);
            sortingType = "Price";}

    }

    public void sortPriceButton(){

            ConstraintLayout imageButtonsContainer = findViewById(R.id.image_buttons_container);
            imageButtonsContainer.removeAllViews();
            Collec col1 = sortCollectionByPrice(collec);
            Collec tempCollec = new Collec();
            tempCollec.setCollection(col1.getCollection());
            try {
                switchToButtons(tempCollec);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            collec.setCollection(tempCollec.getCollection());

            sortingType = "Price";



        }







    public void addCards(Collec col){
        ConstraintLayout imageButtonsContainer = findViewById(R.id.image_buttons_container);
        for(Carte carte : col.getCollection()){
            collec.addCarte(carte);
            int i = collec.getNombre();
            if (i==1){
                ImageButton newImageButton = createNewImageButtonStart(0.116, carte);
                bellowCard1 = newImageButton.getId();
                imageButtonsContainer.addView(newImageButton);
                YugiohCardImageFetcher.fetchCardImage(this, carte.getName().replace(" ","_").replace("-","_"), newImageButton );
            }
            else if (i==2){
                ImageButton newImageButton = createNewImageButtonStart(0.93, carte);
                bellowCard2 = newImageButton.getId();
                imageButtonsContainer.addView(newImageButton);
                YugiohCardImageFetcher.fetchCardImage(this,carte.getName().replace(" ","_").replace("-","_"), newImageButton );
            }
            else if (i%2 == 1){
                ImageButton newImageButton = createNewImageButton(0.116, bellowCard1, carte);
                bellowCard1 = newImageButton.getId();
                imageButtonsContainer.addView(newImageButton);
                YugiohCardImageFetcher.fetchCardImage(this, carte.getName().replace(" ","_").replace("-","_"), newImageButton );

            }
            else if (i%2 == 0){
                ImageButton newImageButton = createNewImageButton(0.93, bellowCard2, carte);
                bellowCard2 = newImageButton.getId();
                imageButtonsContainer.addView(newImageButton);
                YugiohCardImageFetcher.fetchCardImage(this, carte.getName().replace(" ","_").replace("-","_"), newImageButton );

            }
        }

    }

    public void switchToButtons(Collec collec) throws IOException {
        ConstraintLayout layout = findViewById(R.id.image_buttons_container);
        int previousViewId = View.NO_ID;


        layout.removeAllViews();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        int buttonHeight = (int) (height * 0.12); // 12% of screen's height

        for (Carte carte : collec.getCollection()) {
            Button button = new Button(this);
            button.setId(View.generateViewId());
            button.setText(carte.getName() + " - $" + carte.getPrix()); // concatenate the name and price
            button.setGravity(Gravity.START | Gravity.CENTER_VERTICAL); // set the text to the left
            button.setPadding(10, 0, 0, 0); // add some padding to the left
            int color = ContextCompat.getColor(this, getResources().getIdentifier(chooseColor(carte), "color", getPackageName()));
            button.setBackgroundTintList(ColorStateList.valueOf(color));

            ConstraintLayout.LayoutParams buttonParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    buttonHeight);
            if (previousViewId != View.NO_ID) {
                buttonParams.topToBottom = previousViewId;
            } else {
                buttonParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            }
            button.setLayoutParams(buttonParams);

            button.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("cardName1", carte.getName().replace(" ","_").replace("-","_"));
                intent.putExtra("cardName2", carte.getName());
                intent.putExtra("edition", carte.getSet_long());
                intent.putExtra("price", String.valueOf(carte.getPrix()));
                intent.putExtra("rarete", carte.getRarete());
                intent.putExtra("desc", carte.getDescription());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });

            layout.addView(button);
            previousViewId = button.getId();
        }
        buttonOrCard = "button";
    }

    public void switchToCard(Collec col){
        ConstraintLayout layout = findViewById(R.id.image_buttons_container);
        layout.removeAllViews();
        ConstraintLayout imageButtonsContainer = findViewById(R.id.image_buttons_container);
        int i = 0;
        for(Carte carte : col.getCollection()){
            i = i + 1;
            if (i==1){
                ImageButton newImageButton = createNewImageButtonStart(0.116, carte);
                bellowCard1 = newImageButton.getId();
                imageButtonsContainer.addView(newImageButton);
                YugiohCardImageFetcher.fetchCardImage(this, carte.getName().replace(" ","_").replace("-","_"), newImageButton );
            }
            else if (i==2){
                ImageButton newImageButton = createNewImageButtonStart(0.93, carte);
                bellowCard2 = newImageButton.getId();
                imageButtonsContainer.addView(newImageButton);
                YugiohCardImageFetcher.fetchCardImage(this,carte.getName().replace(" ","_").replace("-","_"), newImageButton );
            }
            else if (i%2 == 1){
                ImageButton newImageButton = createNewImageButton(0.116, bellowCard1, carte);
                bellowCard1 = newImageButton.getId();
                imageButtonsContainer.addView(newImageButton);
                YugiohCardImageFetcher.fetchCardImage(this, carte.getName().replace(" ","_").replace("-","_"), newImageButton );

            }
            else if (i%2 == 0){
                ImageButton newImageButton = createNewImageButton(0.93, bellowCard2, carte);
                bellowCard2 = newImageButton.getId();
                imageButtonsContainer.addView(newImageButton);
                YugiohCardImageFetcher.fetchCardImage(this, carte.getName().replace(" ","_").replace("-","_"), newImageButton );

            }
        }
        buttonOrCard = "card";
    }




    public String chooseColor(Carte carte){
        if (carte.getFrameType().equals("spell")){
            return "green";
        }
        else if (carte.getFrameType().equals("trap")){
            return "purple";
        }
           else if (carte.getFrameType().equals("ritual")){
                return "lBlue";
            }
            else if (carte.getFrameType().equals("effect")){
                return "orange";
            }
            else if (carte.getFrameType().equals("normal")){
                return "yellow";
            }
            else if (carte.getFrameType().equals("pendulum")){
                return "green";
            }
            else if (carte.getFrameType().equals("link")){
                return "blue";
            }
            else if (carte.getFrameType().equals("fusion")){
                return "violet";
            }
            else if (carte.getFrameType().equals("synchro")){
                return "white1";
            }
            else if (carte.getFrameType().equals("xyz")){
                return "black";
            }

            return "gray";


        }













    private ImageButton createNewImageButton(double horizontalBias, int topToBottomOf, Carte card) {
        ImageButton newImageButton = new ImageButton(this);
        newImageButton.setId(View.generateViewId());




        // Set layout width and height
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics())
        );

        // Set other properties of the ImageButton
        newImageButton.setLayoutParams(layoutParams);
        newImageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        newImageButton.setImageResource(R.drawable.yugioh);
        newImageButton.setBackgroundColor(Color.TRANSPARENT);

        // Set constraint properties
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topToBottom = topToBottomOf;
        layoutParams.horizontalBias = (float) horizontalBias;
        newImageButton.setScaleType(ImageView.ScaleType.CENTER);

        newImageButton.setTag(card);


        newImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("cardName1", card.getName().replace(" ","_").replace("-","_"));
                intent.putExtra("cardName2", card.getName());
                intent.putExtra("edition", card.getSet_long());
                intent.putExtra("price", String.valueOf(card.getPrix()));
                intent.putExtra("rarete", card.getRarete());
                intent.putExtra("desc", card.getDescription());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        return newImageButton;
    }




    private ImageButton createNewImageButtonStart(double horizontalBias, Carte card) {
        ImageButton newImageButton = new ImageButton(this);
        newImageButton.setId(View.generateViewId());

        // Set layout width and height
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics())
        );

        // Set other properties of the ImageButton
        newImageButton.setLayoutParams(layoutParams);
        newImageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        newImageButton.setImageResource(R.drawable.yugioh);
        newImageButton.setBackgroundColor(Color.TRANSPARENT);
        newImageButton.setScaleType(ImageView.ScaleType.CENTER);

        // Set constraint properties
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.horizontalBias = (float) horizontalBias;

        newImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("cardName1", card.getName().replace(" ","_").replace("-","_"));
                intent.putExtra("cardName2", card.getName());
                intent.putExtra("edition", card.getSet_long());
                intent.putExtra("price", String.valueOf(card.getPrix()));
                intent.putExtra("rarete", card.getRarete());
                intent.putExtra("desc", card.getDescription());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        return newImageButton;
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.main_menu:
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    return true;
                case R.id.camera_menu:
                    startActivity(new Intent(MainActivity.this, MainActivity4.class));
                    return true;
            }
            return false;
        }
    };









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        Button buttonCamera = findViewById(R.id.button_camera);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.example.testtest.MainActivity4.class);
                startActivity(intent);
            }
        });



        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        View actionBarView = getSupportActionBar().getCustomView();

        searchBar = findViewById(R.id.search_bar);

        ImageButton switchButton = findViewById(R.id.switch_button);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonOrCard.equals("card")){
                    try {
                        switchToButtons(collec);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else{
                    switchToCard(collec);
                }

            }
        });


        ImageButton dropdownMenuButton = getSupportActionBar().getCustomView().findViewById(R.id.dropdown_menu_button);
        dropdownMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show menu
                PopupMenu popup = new PopupMenu(MainActivity.this, dropdownMenuButton);
                popup.getMenuInflater().inflate(R.menu.dropdown_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item1:
                                if (buttonOrCard == "card"){
                                    sortNameCard();
                                }
                                else {
                                    sortNameButton();
                                }
                                return true;
                            case R.id.item2:
                                if (buttonOrCard == "card"){
                                    sortPriceCard();
                                }
                                else{
                                    sortPriceButton();
                                }
                                return true;
                            case R.id.item3:
                                // Handle item 3 click
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();
            }
        });




        FetchDataTask fetchDataTask = new FetchDataTask(this);
        fetchDataTask.execute();


        ArrayList<Carte> cards = collec.getCollection();

        ArrayAdapter<Carte> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line, cards);

        searchBar.setAdapter(adapter);


        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Carte selectedCard = (Carte) parent.getItemAtPosition(position);
                searchBar.setText("");

                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("cardName1", selectedCard.getName().replace(" ","_").replace("-","_"));
                intent.putExtra("cardName2", selectedCard.getName());
                intent.putExtra("edition", selectedCard.getSet_long());
                intent.putExtra("price", String.valueOf(selectedCard.getPrix()));
                intent.putExtra("rarete", selectedCard.getRarete());
                intent.putExtra("desc", selectedCard.getDescription());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });




    }}
