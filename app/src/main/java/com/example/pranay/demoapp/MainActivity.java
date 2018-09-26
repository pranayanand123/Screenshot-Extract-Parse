package com.example.pranay.demoapp;

import android.app.ProgressDialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    DatabaseReference mDatabase;


    ImageView imgView;
    FirebaseVisionImage img;
    TextView vehicleNo;
    ListView listViewReviews;
    Bitmap bitmanp_image;
    TextView mainMessage;
    List<Review> reviewList;
    Pattern pattern = Pattern.compile("[A-Z]{2}[0-9]{1,2}[A-Z]{1,2}[0-9]{4}");
    //Context context = getApplicationContext();
    String lump;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //imgView = (ImageView) findViewById(R.id.imageView);
        mainMessage = (TextView) findViewById(R.id.mainMessage);
        vehicleNo = (TextView) findViewById(R.id.vehicleNo);
        listViewReviews = (ListView) findViewById(R.id.listViewReviews);
        reviewList = new ArrayList<>();
        progressDialog = new ProgressDialog(MainActivity.this);







        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    void handleSendImage(Intent intent) {
        progressDialog.setMessage("Fetching details...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            //imgView.setImageURI(imageUri);
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
                                vehicleNo.setText(matcher.group());
                                mDatabase = FirebaseDatabase.getInstance().getReference().child(matcher.group());
                                mDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        reviewList.clear();
                                        for(DataSnapshot reviewSnapshot:dataSnapshot.getChildren()){
                                            String name = reviewSnapshot.getKey();
                                            String review = reviewSnapshot.getValue(String.class);
                                            Review review_item = new Review(name+":", review);
                                            Log.d("firebase_return", name);
                                            Log.d("firebase_return", review);
                                            reviewList.add(review_item);



                                        }
                                        ReviewAdapter adapter = new ReviewAdapter(MainActivity.this, reviewList);
                                        listViewReviews.setAdapter(adapter);
                                        progressDialog.dismiss();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        progressDialog.dismiss();
                                        mainMessage.clearComposingText();
                                        mainMessage.setText("Sorry couldn't find any details.");

                                    }
                                });

                            }else {
                                Log.d("extract", "ERROR");
                                mainMessage.clearComposingText();
                                mainMessage.setText("Please enter a valid image.");
                                progressDialog.dismiss();

                            }

                            // ...
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    progressDialog.dismiss();
                                    mainMessage.clearComposingText();
                                    mainMessage.setText("Please enter a valid image.");
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                                    // ...
                                }
                            });
        }
    }
}
