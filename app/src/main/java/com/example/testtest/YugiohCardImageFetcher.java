package com.example.testtest;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class YugiohCardImageFetcher {



    public static void fetchCardImage(Context context, String cardName, ImageView imageView) {
        String baseUrl = "https://static-7.studiobebop.net/ygo_data/card_images/";
        String imageUrl = baseUrl + cardName + ".jpg";

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        ImageRequest imageRequest = new ImageRequest(imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("YugiohCardImageFetcher", "Error fetching image: " + error.getMessage());
                    }
                });

        requestQueue.add(imageRequest);
    }


    }


