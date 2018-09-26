package com.example.pranay.demoapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView imgView;
    FirebaseVisionImage img;
    Bitmap bitmanp_image;
    Pattern pattern = Pattern.compile("[A-Z]{2}[A-Z0-9]{1,2}[A-Z]{1,3}[A-Z0-9]{4}");
    //Context context = getApplicationContext();
    String lump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = (ImageView) findViewById(R.id.imageView);




        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        }

    }
    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            imgView.setImageURI(imageUri);
            try {
                bitmanp_image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            img = FirebaseVisionImage.fromBitmap(bitmanp_image);
            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();
            textRecognizer.processImage(img)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText result) {
                            // Task completed successfully
                            Toast.makeText(MainActivity.this,"Completed", Toast.LENGTH_SHORT).show();
                            lump = result.getText();
                            Log.d("text",lump);
                            Matcher matcher = pattern.matcher(lump);
                            if (matcher.find()) {
                                Log.d("extract", String.valueOf(matcher.start()));
                                Log.d("extract", String.valueOf(matcher.end()));
                                Log.d("extract", String.valueOf(matcher.group()));
                            }else {
                                Log.d("extract", "ERROR");

                            }

                            // ...
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                                    // ...
                                }
                            });
        }
    }
}
