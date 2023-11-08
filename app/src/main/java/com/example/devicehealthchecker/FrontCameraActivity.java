package com.example.devicehealthchecker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class FrontCameraActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST = 1;
    private static final int CAMERA_REQUEST = 1888;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;
    private Preview preview;
    private Camera camera;
    private ImageCapture imageCapture=null;
    private CameraSelector cameraSelector;
    private ImageAnalysis imageAnalysis;
    private PreviewView viewFinder;
    private CardView cardView;
    private ImageView cameraimageview;
    private AppCompatButton ImageButton;
    private Button yesbutton;
    private Button nobutton;
    private TextView questionview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_camera);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                //We can show our custom dialog here

            }
            else {
                //It will show android's default dialog box
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST);
                //This will show a permission dialog and onRequestPermissionResult function will be called
            }
        }
        else {
            startCamera();
            //intentfunc();
        }
        initialiseViews();
        onclick();
    }


    public void onclick(){
        ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.i("info","came here1");
                String name = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                ContentValues contentValues = new ContentValues();

                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {

                    contentValues.put(MediaStore.Images.Media.DATE_TAKEN, name);

                    contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,
                            "Pictures/Camera-X");


                }
                //Log.i("info","came here2");
                ImageCapture.OutputFileOptions outputFileOptions =
                        new ImageCapture.OutputFileOptions.Builder(getContentResolver(),MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues).build();
                imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(FrontCameraActivity.this),
                        new ImageCapture.OnImageSavedCallback() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {

                                try {
                                    Bitmap photo = (Bitmap)  MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), outputFileResults.getSavedUri());
//                                    Matrix matrix = new Matrix();
//                                    cameraimageview.setScaleType(ImageView.ScaleType.MATRIX);   //required
//                                    matrix.postRotate((float) 90.0f);
//                                    cameraimageview.setImageMatrix(matrix);
                                    Matrix matrix = new Matrix();

                                    matrix.postRotate(270);

                                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(photo, photo.getWidth(), photo.getHeight(), true);

                                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                                    cardView.setVisibility(View.VISIBLE);
                                    cameraimageview.setVisibility(View.VISIBLE);
                                    cameraimageview.setImageBitmap(rotatedBitmap);
                                    viewFinder.setVisibility(View.GONE);
                                    yesbutton.setVisibility(View.VISIBLE);
                                    nobutton.setVisibility(View.VISIBLE);
                                    questionview.setVisibility(View.VISIBLE);

                                    //exiffunc(photo,outputFileResults.getSavedUri());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
//                        cameraimageview.setImageBitmap(photo);
                                //Toast.makeText(FrontCameraActivity.this, "Photo Captured", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onError(ImageCaptureException error) {
                                Toast.makeText(FrontCameraActivity.this, "Failed to Capture", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                //Log.i("info","came here3");
            }
        });
        yesbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fields.Sensor_Status=true;
                Intent intent=new Intent();
                intent.putExtra("RESULT_OK","true");
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        nobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fields.Sensor_Status=false;
                Intent intent=new Intent();
                intent.putExtra("RESULT_OK","false");
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
    private void initialiseViews(){
        cameraimageview=findViewById(R.id.imageView2);
        viewFinder=findViewById(R.id.viewFinder);
        ImageButton=findViewById(R.id.ImageButton);
        cardView=findViewById(R.id.card_view);
        yesbutton=findViewById(R.id.YesButton);
        nobutton=findViewById(R.id.NoButton);
        questionview=findViewById(R.id.question_view);
    }
    public void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                //bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
            // Preview
            preview = new Preview.Builder()
                    .build();
            preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

            imageCapture = new ImageCapture.Builder()
                    .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                    .build();

            imageAnalysis = new ImageAnalysis.Builder()
                    // enable the following line if RGBA output is needed.
                    //.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .setTargetResolution(new Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                imageAnalysis.setAnalyzer(getMainExecutor(), new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy imageProxy) {
                        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                        // insert your code here.

                        // after done, release the ImageProxy object
                        imageProxy.close();
                    }
                });
            }
            cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build();
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture, imageAnalysis);

            } catch(Exception e) {
                Log.i( "Use case binding failed", "error");
            }
        }, ContextCompat.getMainExecutor(this));

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        startCamera();
                        //intentfunc();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("RESULT_OK","false");
        setResult(RESULT_OK,intent);
        super.onBackPressed();

    }
}