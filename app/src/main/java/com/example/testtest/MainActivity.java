package com.example.testtest;

import com.example.testtest.MainActivity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.room.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    AutoCompleteTextView searchBar;

    private AppDatabase db;

    public AppDatabase getDb() {
        return db;
    }

    Collec collec = new Collec("collec");
    int bellowCard1 = 0;
    int bellowCard2 = 0;

    String sortingType = ""; //Price ou Name ou rien(rien quand l'appli est lancée)

    String buttonOrCard = "card";  //card ou button, quel type de vue est a l'écran ? (vue par cartes au lancement, peut être changé par le bouton en haut a gauche

    Collec currentCollec = collec;

    ArrayList<Collec> collecList = new ArrayList<>();













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
        Collec tempCollec = new Collec("tempCollec");
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
            Collec tempCollec = new Collec("tempCollec");
            tempCollec.setCollection(col1.getCollection());
            try {
                switchToButtons(tempCollec);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            collec.setCollection(tempCollec.getCollection());

            sortingType = "Price";



        }


    public void showNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a Name");

        // Set up the input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                createCollec(name);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void showErrorName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("This name is already used");

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }








    public void addCards(Collec col){
        ConstraintLayout imageButtonsContainer = findViewById(R.id.image_buttons_container);

        List<Carte> cartes = new ArrayList<>(col.getCollection());

        for(Carte carte : cartes){
            currentCollec.addCarte(carte);
            int i = currentCollec.getNombre();
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

    public void addCards2(Collec col){
        ConstraintLayout imageButtonsContainer = findViewById(R.id.image_buttons_container);

        List<Carte> cartes = new ArrayList<>(col.getCollection());
        Collec collecBis = new Collec("bis");

        for(Carte carte : cartes){
            collecBis.addCarte(carte);
            int i = collecBis.getNombre();
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

    public void createCollec(String nom){
    ArrayList<String> noms = new ArrayList<>();
    for (Collec colle : collecList){
        noms.add(colle.getName());
    }
    if (!noms.contains(nom)){
        Collec a = new Collec(nom);
        collecList.add(a);
    }
    else {
        showErrorName();
    }
    }







    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.mainMenu:
                    // Do nothing if the current activity is MainActivity
                    return true;
                case R.id.cameraMenu:
                    startActivity(new Intent(MainActivity.this, MainActivity4.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String cardCode = sharedPref.getString("card_code", null);
        if (cardCode != null) {
            FetchCardTask fetchCardTask = new FetchCardTask(){
                @Override
                protected void onPostExecute(Carte card) {
                    if (currentCollec != collec){
                        collec.addCarte(card);
                    }
                    FetchDataTask2 fetchDataTask2 = new FetchDataTask2(MainActivity.this, cardCode);
                    fetchDataTask2.execute();
                }
            };
            fetchCardTask.execute(cardCode);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();
        }
    }











    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        collecList.add(collec);









        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "carte-database").build();


       /* InsertDataTask insertDataTask = new InsertDataTask(this);
        insertDataTask.execute();


        FetchDataTask fetchDataTask = new FetchDataTask(this);
        fetchDataTask.execute();*/


        FetchDataTaskLaunch fetchDataTaskLaunch = new FetchDataTaskLaunch(this);
        fetchDataTaskLaunch.execute();



        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        View actionBarView = getSupportActionBar().getCustomView();

        searchBar = findViewById(R.id.search_bar);

        ImageButton switchButton = getSupportActionBar().getCustomView().findViewById(R.id.switch_button);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show menu
                final PopupMenu popupRight = new PopupMenu(MainActivity.this, switchButton);
                popupRight.getMenuInflater().inflate(R.menu.dropdown_menu_right, popupRight.getMenu());

                popupRight.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item1:
                                showNameDialog();
                                return true;
                            case R.id.item2:
                                // Create a new popup for switching collections
                                PopupMenu switchPopup = new PopupMenu(MainActivity.this, switchButton);
                                for (int i = 0; i < collecList.size(); i++) {
                                    switchPopup.getMenu().add(0, i, 0, collecList.get(i).getName());
                                }
                                // Handle click on each collection
                                switchPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    public boolean onMenuItemClick(MenuItem item) {
                                        // Here you can handle the click on each collection
                                        // The item id will correspond to the collection index in the list
                                        Collec clickedCollec = collecList.get(item.getItemId());
                                        currentCollec = clickedCollec;
                                        FetchDataTask3 fetchDataTask3 = new FetchDataTask3(MainActivity.this, clickedCollec);
                                        fetchDataTask3.execute();



                                        return true;
                                    }
                                });
                                switchPopup.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupRight.show();
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
                                if (buttonOrCard == "card") {
                                    sortNameCard();
                                } else {
                                    sortNameButton();
                                }
                                return true;
                            case R.id.item2:
                                if (buttonOrCard == "card") {
                                    sortPriceCard();
                                } else {
                                    sortPriceButton();
                                }
                                return true;
                            case R.id.item3:
                                if (buttonOrCard == "card") {
                                    try {
                                        switchToButtons(collec);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    switchToCard(collec);
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();
            }
        });


       // FetchDataTask fetchDataTask = new FetchDataTask(this);
       // fetchDataTask.execute();


        ArrayList<Carte> cards = collec.getCollection();


                ArrayAdapter<Carte> adapter = new ArrayAdapter<Carte>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, cards);


                searchBar.setAdapter(adapter);


        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Carte selectedCard = (Carte) parent.getItemAtPosition(position);
                searchBar.setText("");

                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("cardName1", selectedCard.getName().replace(" ", "_").replace("-", "_"));
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
