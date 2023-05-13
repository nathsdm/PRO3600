package com.example.testtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

public class SecondActivity extends AppCompatActivity {

    ConstraintLayout mainView;
    int screenWidth;

    boolean isExpanded = false;


    private ConstraintLayout.LayoutParams originalLayoutParams;










    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        int tailleCarte = screenWidth/2;

        mainView = findViewById(R.id.mainView);

        String cardName1 = getIntent().getStringExtra("cardName1");
        String cardName2 = getIntent().getStringExtra("cardName2");
        String edition = getIntent().getStringExtra("edition");
        String price = getIntent().getStringExtra("price");
        String rarete = getIntent().getStringExtra("rarete");
        String desc = getIntent().getStringExtra("desc");





        // Find the ImageButton by its id
        ImageButton imageButton = findViewById(R.id.SecondAvtivityButton);

        ViewGroup.LayoutParams params = imageButton.getLayoutParams();
        params.width = tailleCarte;
        params.height = (int) (tailleCarte*1.45762);
        imageButton.setLayoutParams(params);

        // Fetch and set the image of the ImageButton
        YugiohCardImageFetcher.fetchCardImage(this, cardName1, imageButton);

        TextView textView1 = findViewById(R.id.textView);
        textView1.setText("Nom : " + cardName2);

        TextView textView2 = findViewById(R.id.textView2);
        textView2.setText("Edition : " + edition);

        TextView textView3 = findViewById(R.id.textView3);
        textView3.setText("Prix : " + price + " €");

        TextView textView4 = findViewById(R.id.textView4);
        textView4.setText("Rarete : " + rarete);

        TextView textView5 = findViewById(R.id.textView5);
        textView5.setText(desc);




        originalLayoutParams = (ConstraintLayout.LayoutParams) imageButton.getLayoutParams();


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                intent.putExtra("cardName1", cardName1);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }});}}