package com.example.testtest;


import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


public class OCR {

    public interface TextRecognitionCallback {
        void onSuccess(String text);
        void onFailure(Exception e);
    }

    public static void recognizeText(Bitmap bitmap, final TextRecognitionCallback callback) {

        // Préparer l'image pour ML Kit
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        // Obtenir une instance de TextRecognizer
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        // Lancer la détection de texte
        Task<Text> result = recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
                        // Construire une chaîne avec le texte reconnu
                        StringBuilder recognizedText = new StringBuilder();
                        for (Text.TextBlock block : visionText.getTextBlocks()) {
                            for (Text.Line line : block.getLines()) {
                                for (Text.Element element : line.getElements()) {
                                    recognizedText.append(element.getText()).append(" ");
                                }
                            }
                        }

                        // Appeler la méthode de réussite du callback avec le texte reconnu
                        callback.onSuccess(recognizedText.toString());
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Appeler la méthode d'échec du callback avec l'exception
                                callback.onFailure(e);
                            }
                        });
    }

}
