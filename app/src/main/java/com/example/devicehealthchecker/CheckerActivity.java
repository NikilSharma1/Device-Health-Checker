package com.example.devicehealthchecker;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CheckerActivity extends AppCompatActivity implements DetailInterface {
    CustomAdapter customAdapter;
    ResultsAdapter resultsAdapter;

    ArrayList<Checks> arrayList;
    ArrayList<String> resultList;
    RecyclerView recyclerView;
    RecyclerView test_recyclerview;
    LinearLayoutManager layoutManager;
    LinearLayoutManager reslayoutManager;
    View recyclerview_item;
    Button check;
    Button skip;
    TextView test_textview;
    TextView desc_textview;
    private static final int PERMISSION_REQUEST = 1;

    Button firebase;
    Button pdf;

    FloatingActionButton floatingActionButton;

    ArrayList<String> staticarraylist_Result;
    ArrayList<String>testnames;

    MutableLiveData<Integer> listen = new MutableLiveData<>();

    int array_pos=8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checker);

        ColorDrawable colorDrawable
                = new ColorDrawable(getColor(R.color.red));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        askPermissions();
        firebase=findViewById(R.id.firebase);
        pdf=findViewById(R.id.pdf);
        floatingActionButton=findViewById(R.id.floating);

        arrayList = new ArrayList<>();
        resultList=new ArrayList<>();
        staticarraylist_Result=new ArrayList<>();
        testnames=new ArrayList<>();

        fillThelist();

        recyclerView = findViewById(R.id.chec_recyclerview);
        test_recyclerview=findViewById(R.id.results_recyclerview);

        layoutManager = new LinearLayoutManager(this);
        reslayoutManager = new LinearLayoutManager(this);
        //arrayList.add("ROOT");
        customAdapter = new CustomAdapter(this, arrayList, this);
        resultsAdapter=new ResultsAdapter(this,resultList);

        recyclerView.setLayoutManager(layoutManager);
        test_recyclerview.setLayoutManager(reslayoutManager);

        recyclerView.setAdapter(customAdapter);
        test_recyclerview.setAdapter(resultsAdapter);

        customAdapter.notifyDataSetChanged();
        Fields.COUNT=0;
//        listen.setValue(Fields.COUNT); //Initilize with a value
//
//        listen.observe(this,new Observer<Integer>() {
//            @Override
//            public void onChanged(Integer changedValue) {
//                //Toast.makeText(getApplicationContext(),String.valueOf(changedValue),Toast.LENGTH_SHORT).show();
//                if(changedValue==9){
//
//                    floatingActionButton.performClick();
//                }
//            }
//        });
        //resultsAdapter.notifyDataSetChanged();
        onclick();

    }
    private void askPermissions(){
        ActivityCompat.requestPermissions(CheckerActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
    }
    private void onclick(){
        firebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String ,Object> hashMap=new HashMap<>();
                for(int i=0; i<9; i++){
                    hashMap.put(testnames.get(i),staticarraylist_Result.get(i));
                }
                FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                firebaseDatabase.getReference().setValue(hashMap);
                Intent intent=new Intent(CheckerActivity.this,PerformActivity.class);
                startActivity(intent);
                finish();
            }
        });
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(CheckerActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);




                PdfDocument pdfDocument = new PdfDocument();
                PdfDocument.PageInfo pageInfo=new PdfDocument.PageInfo.Builder(1040,1980,1).create();
                PdfDocument.Page page=pdfDocument.startPage(pageInfo);
                Canvas canvas=page.getCanvas();
                Paint paint=new Paint();
                paint.setColor(getResources().getColor(R.color.black));
                paint.setTextSize(62);
                String tes_repo="TEST REPORT";
                float x=400;
                float y=100;
                canvas.drawText(tes_repo,x,y,paint);
                x=100;
                y=300;
                String text = "";
                paint.setTextSize(42);
                for(int i=0; i<9; i++){
                    text=testnames.get(i)+" : "+staticarraylist_Result.get(i);
                    canvas.drawText(text,x,y,paint);
                    y+=100;
                }

                pdfDocument.finishPage(page);
                File downloaddir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String filename="New.pdf";
                File file=new File(downloaddir,filename);
                try {
                    FileOutputStream fileOutputStream=new FileOutputStream(file);
                    pdfDocument.writeTo(fileOutputStream);
                    pdfDocument.close();
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),"Some Error Occured. Please try again.",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),"Some Error Occured. Please try again.",Toast.LENGTH_SHORT).show();
                }
                Intent intent=new Intent(CheckerActivity.this,PerformActivity.class);
                startActivity(intent);
                finish();
            }

        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Fields.COUNT>=9){
                    for(int i=0; i<9; i++){
                        resultList.add(testnames.get(i)+" : "+staticarraylist_Result.get(i));
                    }
                    recyclerView.setVisibility(View.GONE);
                    test_recyclerview.setVisibility(View.VISIBLE);
                    firebase.setVisibility(View.VISIBLE);
                    pdf.setVisibility(View.VISIBLE);
                    floatingActionButton.setVisibility(View.INVISIBLE);
                }else{
                    genericAlertDialog("Please perform either of the two actions for all the tests. 1.CHECK or 2.SKIP");
                }
            }
        });
    }
    private void fillThelist() {
        if (arrayList.size() > 0) return;
        arrayList.add(new Checks("ROOT", "Check if your device is Rooted or not"));
        arrayList.add(new Checks("MAIN CAMERA", "Check if your main camera is working fine or not"));
        arrayList.add(new Checks("SELFIE CAMERA", "Check if your main camera is working fine or not"));
        arrayList.add(new Checks("PRIMARY MICROPHONE", "Check if your primary microphone is working fine or not"));
        arrayList.add(new Checks("SECONDARY MICROPHONE", "Check if your secondary microphone is working fine or not"));
        arrayList.add(new Checks("BLUETOOTH", "Check if your bluetooth is working fine or not"));
        arrayList.add(new Checks("ACCELEROMETER", "Check if your accerlerometer is working fine or not"));
        arrayList.add(new Checks("GYROSCOPE", "Check if your gyroscope is working fine or not"));
        arrayList.add(new Checks("GPS", "Check if your GPS is working fine or not"));

        staticarraylist_Result.add("STATUS UNKNOWN");
        staticarraylist_Result.add("STATUS UNKNOWN");
        staticarraylist_Result.add("STATUS UNKNOWN");
        staticarraylist_Result.add("STATUS UNKNOWN");
        staticarraylist_Result.add("STATUS UNKNOWN");
        staticarraylist_Result.add("STATUS UNKNOWN");
        staticarraylist_Result.add("STATUS UNKNOWN");
        staticarraylist_Result.add("STATUS UNKNOWN");
        staticarraylist_Result.add("STATUS UNKNOWN");

        testnames.add("ROOT");
        testnames.add("MAIN CAMERA");
        testnames.add("SELFIE CAMERA");
        testnames.add("PRIMARY MICROPHONE");
        testnames.add("SECONDARY MICROPHONE");
        testnames.add("BLUETOOTH");
        testnames.add("ACCELEROMETER");
        testnames.add("GYROSCOPE");
        testnames.add("GPS");

    }

    @Override
    public void sendDetail(int position) {
        array_pos=position;
        recyclerview_item = recyclerView.getLayoutManager().findViewByPosition(position);
        skip = recyclerview_item.findViewById(R.id.skip);
        test_textview = recyclerview_item.findViewById(R.id.check_name);
        desc_textview = recyclerview_item.findViewById(R.id.check_desc);
        String name = test_textview.getText().toString();

        skip.setVisibility(View.VISIBLE);
        check = recyclerview_item.findViewById(R.id.check);
        check.setVisibility(View.VISIBLE);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.equals("ROOT")) {
                    checkRootStatus();
                    if(staticarraylist_Result.get(position).equals("STATUS UNKNOWN"));
                    Fields.COUNT++;
                    //listen.setValue(Fields.COUNT);
                } else if (name.equals("MAIN CAMERA")) {

                    checkMainCamera();
                    if(staticarraylist_Result.get(position).equals("STATUS UNKNOWN"));
                    Fields.COUNT++;
                    //listen.setValue(Fields.COUNT);
                } else if (name.equals("SELFIE CAMERA")) {

                    checkSelfieCamera();
                    if(staticarraylist_Result.get(position).equals("STATUS UNKNOWN"));
                    Fields.COUNT++;
                    //listen.setValue(Fields.COUNT);
                } else if (name.equals("PRIMARY MICROPHONE")) {

                    checkPrimaryMicrophone();
                    if(staticarraylist_Result.get(position).equals("STATUS UNKNOWN"));
                    Fields.COUNT++;
                    //listen.setValue(Fields.COUNT);
                } else if (name.equals("SECONDARY MICROPHONE")) {

                    checkSecondaryMircophone();
                    if(staticarraylist_Result.get(position).equals("STATUS UNKNOWN"));
                    Fields.COUNT++;
                    //listen.setValue(Fields.COUNT);
                } else if (name.equals("BLUETOOTH")) {

                    checkBluetooth();
                    if(staticarraylist_Result.get(position).equals("STATUS UNKNOWN"));
                    Fields.COUNT++;
                    //listen.setValue(Fields.COUNT);
                } else if (name.equals("ACCELEROMETER")) {

                    checkAccelerometer();
                    if(staticarraylist_Result.get(position).equals("STATUS UNKNOWN"));
                    Fields.COUNT++;
                    //listen.setValue(Fields.COUNT);
                } else if (name.equals("GYROSCOPE")) {

                    checkGyroscope();
                    if(staticarraylist_Result.get(position).equals("STATUS UNKNOWN"));
                    Fields.COUNT++;
                    //listen.setValue(Fields.COUNT);
                } else if (name.equals("GPS")) {

                    checkGPS();
                    if(staticarraylist_Result.get(position).equals("STATUS UNKNOWN"));
                    Fields.COUNT++;
                    //listen.setValue(Fields.COUNT);
                }
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Testfailed(name+" FAILED");
                if(staticarraylist_Result.get(position).equals("STATUS UNKNOWN"));
                Fields.COUNT++;
                //listen.setValue(Fields.COUNT);
                staticarraylist_Result.set(position,"SKIPPED");
            }
        });
    }

    private void checkGPS() {
        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                if(locationManager.isLocationEnabled()){
//                    Toast.makeText(getApplicationContext(),"Yes location is enabled ",Toast.LENGTH_SHORT).show();
//                }else Toast.makeText(getApplicationContext(),"No ",Toast.LENGTH_SHORT).show();
//            }
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //Toast.makeText(getApplicationContext(),"Yes Gps is enabled ",Toast.LENGTH_SHORT).show();
                staticarraylist_Result.set(8,"PASSED");
                TestSuccessful("GPS IS ENABLED");
            } else {
                //Toast.makeText(getApplicationContext(),"No Gps is not enabled ",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new MaterialAlertDialogBuilder(CheckerActivity.this)
                        .setTitle("Suggestions")
                        .setMessage("Please try to turn on your location by swiping down the notification bar and clicking on the location icon.")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Fail", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                staticarraylist_Result.set(8,"FAILED");
                                Testfailed("GPS TEST FAILED");
                            }
                        });

                builder.show();
            }
        } catch (Exception ex) {
        }

    }
    private void Testfailed(String Desc){
        MaterialCardView check_card = recyclerview_item.findViewById(R.id.card_view);
        check_card.setStrokeColor(getResources().getColor(R.color.red));
        desc_textview.setText(Desc);
        desc_textview.setTextColor(getColor(R.color.red));
        check.setVisibility(View.GONE);
        skip.setVisibility(View.GONE);
    }

    private void checkGyroscope() {
        Fields.Sensor_Status=false;
        //Toast.makeText(getApplicationContext(),String.valueOf(Fields.Accel_Status),Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(CheckerActivity.this,GyroscopeActivity.class);
        intent.putExtra("SENSOR_STATUS","FALSE");
        gyroscopeActivitylauncher.launch(intent);
    }
    ActivityResultLauncher<Intent>gyroscopeActivitylauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode()==RESULT_OK){
                        try {
//                            Log.i("infoooo",o.getData().getStringExtra("RESULT_OK"));
//                            Toast.makeText(getApplicationContext(),o.getData().getStringExtra("RESULT_OK"),Toast.LENGTH_SHORT).show();
                            if(o.getData().getStringExtra("RESULT_OK").equals("true")){
                                staticarraylist_Result.set(7,"PASSED");
                                TestSuccessful("Gyroscope Test PASSED.");
                            }else{
                                staticarraylist_Result.set(7,"FAILED");
                                Testfailed("Gyroscope Test Failed");
                            }
                        } catch (Exception e) {
                            staticarraylist_Result.set(7,"FAILED");
                            Testfailed("Gyroscope Test Failed");
                        }

                    }
                }
            });
    private void checkAccelerometer() {
        Fields.Sensor_Status=false;
        //Toast.makeText(getApplicationContext(),String.valueOf(Fields.Accel_Status),Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(CheckerActivity.this,AccelerometerActivity.class);
        intent.putExtra("SENSOR_STATUS","FALSE");
        accelerometerActivitylauncher.launch(intent);
    }
    ActivityResultLauncher<Intent>accelerometerActivitylauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode()==RESULT_OK){
                        try {
//                            Log.i("infoooo",o.getData().getStringExtra("RESULT_OK"));
//                            Toast.makeText(getApplicationContext(),o.getData().getStringExtra("RESULT_OK"),Toast.LENGTH_SHORT).show();
                            if(o.getData().getStringExtra("RESULT_OK").equals("true")){
                                staticarraylist_Result.set(6,"PASSED");
                                TestSuccessful("Accelerometer Test PASSED.");
                            }else{
                                staticarraylist_Result.set(6,"FAILED");
                                Testfailed("Acceleromter Test Failed");
                            }
                        } catch (Exception e) {
                            staticarraylist_Result.set(6,"FAILED");
                            Testfailed("Acceleromter Test Failed");
                        }

                    }
                }
            });
    private void checkBluetooth() {

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothstatus = bluetoothManager.getAdapter();

        if (bluetoothstatus == null) {
                AlertDialog.Builder builder = new MaterialAlertDialogBuilder(CheckerActivity.this)
                        .setTitle("Suggestions")
                        .setMessage("This Device doesn't Support Bluetooth Functionality.");
                builder.show();
                bluetoothError();
            } else if (!bluetoothstatus.isEnabled()) {
                //Toast.makeText(getApplicationContext(), "Yes  ", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[] { android.Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_REQUEST);
                    //return;
                }else{
                    openSomeActivityForResult();
                }
            } else {
               staticarraylist_Result.set(5,"PASSED");
               TestSuccessful("Bluetooth is working fine.");
            }

        }
        private void displayDialogForDeclinedBluetoothPerm(){
            AlertDialog.Builder builder = new MaterialAlertDialogBuilder(CheckerActivity.this)
                        .setTitle("Suggestions")
                        .setMessage("Please grant bluetooth permissions for this test to be successful.")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Fail", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                staticarraylist_Result.set(5,"FAILED");
                                bluetoothError();
                            }
                        });
                builder.show();
        }

        private void bluetoothError(){
            MaterialCardView check_card=recyclerview_item.findViewById(R.id.card_view);
            check_card.setStrokeColor(getResources().getColor(R.color.red));
            desc_textview.setText("Bluetooth Test FAILED");
            desc_textview.setTextColor(getColor(R.color.red));
            check.setVisibility(View.GONE);
            skip.setVisibility(View.GONE);
        }
        private void TestSuccessful(String Desc){
            MaterialCardView check_card=recyclerview_item.findViewById(R.id.card_view);
            check_card.setStrokeColor(getResources().getColor(R.color.green));
            desc_textview.setText(Desc);
            desc_textview.setTextColor(getColor(R.color.green));
            check.setVisibility(View.GONE);
            skip.setVisibility(View.GONE);
        }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(CheckerActivity.this,
                            android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        openSomeActivityForResult();
                    }
                } else {
//                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    displayDialogForDeclinedBluetoothPerm();
                }
                return;
            }
        }
    }
    public void openSomeActivityForResult() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 1) {
                        staticarraylist_Result.set(5,"PASSED");
                        //Toast.makeText(getApplicationContext(),String.valueOf(result.getResultCode())+" + "+String.valueOf(Activity.RESULT_OK),Toast.LENGTH_SHORT).show();
                        TestSuccessful("Bluetooth works fine.");
                    }else{
                        staticarraylist_Result.set(5,"FAILED");
//                        Toast.makeText(getApplicationContext(),String.valueOf(result.getResultCode())+" + "+String.valueOf(Activity.RESULT_OK),Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(),"2",Toast.LENGTH_SHORT).show();
                        displayDialogForDeclinedBluetoothPerm();
                    }
                }
            });

    private void checkSecondaryMircophone() {
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        HeadsetIntentReceiver receiver = new HeadsetIntentReceiver();
        registerReceiver( receiver, receiverFilter );
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(CheckerActivity.this)
                .setTitle("Suggestions")
                .setMessage("Please plugin a headset for detecting external microphone. If you are unable to do so click on FAIL.")
                        .setNegativeButton("FAIL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                staticarraylist_Result.set(4,"FAILED");
                                Testfailed("Secondary Mic Test FAILED");
                                dialogInterface.dismiss();
                            }
                        });
        builder.show();

    }
    public class HeadsetIntentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch(state) {
                    case(0):
                        //Log.d(TAG, "Headset unplugged");
                        break;
                    case(1):
                        //Log.d(TAG, "Headset plugged");
                        if(test_textview.getText().toString().equals("SECONDARY MICROPHONE")) {
                            staticarraylist_Result.set(4,"PASSED");
                            TestSuccessful("External Mic Test PASSED");
                        }
                        break;
                    default: {
                        staticarraylist_Result.set(4,"FAILED");
                        Testfailed("External Mic Test FAILED");
                    }
                }
            }
        }
    }

    private void checkPrimaryMicrophone() {
        Fields.Sensor_Status=false;
        //Toast.makeText(getApplicationContext(),String.valueOf(Fields.Accel_Status),Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(CheckerActivity.this,PrimaryMicrophoneActivity.class);
        intent.putExtra("SENSOR_STATUS","FALSE");
        PrimMicActivityResultLauncher.launch(intent);
    }
    ActivityResultLauncher<Intent> PrimMicActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK){
                        try {
//                            Log.i("infoooo",o.getData().getStringExtra("RESULT_OK"));
//                            Toast.makeText(getApplicationContext(),o.getData().getStringExtra("RESULT_OK"),Toast.LENGTH_SHORT).show();
                            if(result.getData().getStringExtra("RESULT_OK").equals("true")){
                                staticarraylist_Result.set(3,"PASSED");
                                TestSuccessful("Primary Mic Test PASSED.");
                            }else{
                                staticarraylist_Result.set(3,"FAILED");
                                Testfailed("Primary Mic Test Failed");
                            }
                        } catch (Exception e) {
                            staticarraylist_Result.set(3,"FAILED");
                            Testfailed("Primary Mic Test Failed");
                        }

                    }else{
                        staticarraylist_Result.set(3,"FAILED");
                        Testfailed("Primary Mic Failed");
                    }
                }
            });

    private void checkSelfieCamera() {
        Fields.Sensor_Status=false;
        //Toast.makeText(getApplicationContext(),String.valueOf(Fields.Accel_Status),Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(CheckerActivity.this,FrontCameraActivity.class);
        intent.putExtra("SENSOR_STATUS","FALSE");
        FrontCameraActivityResultLauncher.launch(intent);
    }
    ActivityResultLauncher<Intent> FrontCameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK){
                        try {
//                            Log.i("infoooo",o.getData().getStringExtra("RESULT_OK"));
//                            Toast.makeText(getApplicationContext(),o.getData().getStringExtra("RESULT_OK"),Toast.LENGTH_SHORT).show();
                            if(result.getData().getStringExtra("RESULT_OK").equals("true")){
                                staticarraylist_Result.set(2,"PASSED");
                                TestSuccessful("Front Camera Test PASSED.");
                            }else{
                                staticarraylist_Result.set(2,"FAILED");
                                Testfailed("Front Camera Test Failed");
                            }
                        } catch (Exception e) {
                            staticarraylist_Result.set(2,"FAILED");
                            Testfailed("Front Camera Test Failed");
                        }

                    }else{
                        staticarraylist_Result.set(2,"FAILED");
                        Testfailed("Front Camera Test Failed");
                    }
                }
            });

    private void checkMainCamera() {
        Fields.Sensor_Status=false;
        //Toast.makeText(getApplicationContext(),String.valueOf(Fields.Accel_Status),Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(CheckerActivity.this,BackCameraActivity.class);
        intent.putExtra("SENSOR_STATUS","FALSE");
        BackCameraActivityResultLauncher.launch(intent);
    }
    ActivityResultLauncher<Intent> BackCameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK){
                        try {
//                            Log.i("infoooo",o.getData().getStringExtra("RESULT_OK"));
//                            Toast.makeText(getApplicationContext(),o.getData().getStringExtra("RESULT_OK"),Toast.LENGTH_SHORT).show();
                            if(result.getData().getStringExtra("RESULT_OK").equals("true")){
                                staticarraylist_Result.set(1,"PASSED");
                                TestSuccessful("Rear Camera Test PASSED.");
                            }else{
                                staticarraylist_Result.set(1,"FAILED");
                                Testfailed("Rear Camera Test Failed");
                            }
                        } catch (Exception e) {
                            staticarraylist_Result.set(1,"FAILED");
                            Testfailed("Rear Camera Test Failed");
                        }

                    }else{
                        staticarraylist_Result.set(1,"FAILED");
                        Testfailed("Rear Camera Test Failed");
                    }
                }
            });

    private void checkRootStatus() {
        if( RootUtil.isDeviceRooted()){
            staticarraylist_Result.set(0,"PASSED");
            TestSuccessful("DEVICE IS ROOTED");
        }else{
            staticarraylist_Result.set(0,"PASSED");
            TestSuccessful("DEVICE IS NOT ROOTED");
        }
    }
    private void genericAlertDialog(String string){
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(CheckerActivity.this)
                .setTitle("Suggestions")
                .setMessage(string);
        builder.show();
    }
}