package com.onlie.voting.onlinevotingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.onlie.voting.onlinevotingsystem.Helper.GraphicOverlay;
import com.onlie.voting.onlinevotingsystem.Helper.RectOverlay;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

public class CameraActivity extends AppCompatActivity {

    private Button faceDetectionButton,InstantVote;
    private GraphicOverlay graphicOverlay;
    private CameraView cameraView;
    private DatabaseReference mref;
    AlertDialog alertDialog;
    String check,Phone;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        LoadingBar=new ProgressDialog(this);
        mref= FirebaseDatabase.getInstance().getReference();

        faceDetectionButton = findViewById(R.id.detect_face_btn);
        graphicOverlay=findViewById(R.id.graphic_overlay);
        cameraView=findViewById(R.id.camera_view);
        InstantVote=findViewById(R.id.instantvote);

        Intent i=getIntent();
        Phone=i.getStringExtra("phone");

        InstantVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoadingBar.setTitle("Please Wait");
                LoadingBar.setMessage("Please wait while your vote is submitting in our database..");
                LoadingBar.setCanceledOnTouchOutside(false);
                /**LoadingBar.show();
                mref.child("Users").child(Phone).child("Vote").setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        LoadingBar.dismiss();
                    }
                });
                 **/

                Intent i =new Intent(CameraActivity.this,SelectParty.class);
                i.putExtra("phone",Phone);
                startActivity(i);
            }
        });

        alertDialog=new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please wait, Image is Processing..")
                .setCancelable(false)
                .build();

        faceDetectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();
            }
        });
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                //alertDialog.show();
                LoadingBar.setTitle("Please Wait");
                LoadingBar.setMessage("Please wait, Image is Processing..");
                LoadingBar.setCanceledOnTouchOutside(false);
                LoadingBar.show();

                Bitmap bitmap =cameraKitImage.getBitmap();
                bitmap =Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                cameraView.stop();

                processFaceDetection(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }

    private void processFaceDetection(Bitmap bitmap) {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions=new FirebaseVisionFaceDetectorOptions.Builder().build();
        FirebaseVisionFaceDetector firebaseVisionFaceDetector = FirebaseVision.getInstance()
                .getVisionFaceDetector(firebaseVisionFaceDetectorOptions);
        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        getFaceResult(firebaseVisionFaces);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CameraActivity.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFaceResult(List<FirebaseVisionFace> firebaseVisionFaces) {
        int counter=0;
        for(FirebaseVisionFace face:firebaseVisionFaces)
        {
            Rect rect = face.getBoundingBox();
            RectOverlay rectOverlay=new RectOverlay(graphicOverlay,rect);
            graphicOverlay.add(rectOverlay);

            counter=counter+1;

        }
        LoadingBar.dismiss();
        //alertDialog.dismiss();
        check = ""+counter;
        if(check.equals("1"))
        {
            InstantVote.setVisibility(View.VISIBLE);
            faceDetectionButton.setVisibility(View.INVISIBLE);
        }
        else if(check.equals("0"))
        {
            Toast.makeText(this, "No Face Found", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this, "Place only one face for vote", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
        graphicOverlay.clear();
    }
}

