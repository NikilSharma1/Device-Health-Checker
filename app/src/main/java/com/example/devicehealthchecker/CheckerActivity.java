package com.example.devicehealthchecker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class CheckerActivity extends AppCompatActivity implements DetailInterface {
    CustomAdapter customAdapter;
    ArrayList<Checks> arrayList;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    View recyclerview_item;
    Button check;
    Button skip;
    TextView test_textview;
    TextView desc_textview;
    private static final int PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checker);
        arrayList = new ArrayList<>();
        fillThelist();

        recyclerView = findViewById(R.id.chec_recyclerview);
        layoutManager = new LinearLayoutManager(this);
        //arrayList.add("ROOT");
        customAdapter = new CustomAdapter(this, arrayList, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();

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
    }

    @Override
    public void sendDetail(int position) {
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
                } else if (name.equals("MAIN CAMERA")) {
                    checkMainCamera();
                } else if (name.equals("SELFIE CAMERA")) {
                    checkSelfieCamera();
                } else if (name.equals("PRIMARY MICROPHONE")) {
                    checkPrimaryMicrophone();
                } else if (name.equals("SECONDARY MICROPHONE")) {
                    checkSecondaryMircophone();
                } else if (name.equals("BLUETOOTH")) {
                    checkBluetooth();
                } else if (name.equals("ACCELEROMETER")) {
                    checkAccelerometer();
                } else if (name.equals("GYROSCOPE")) {
                    checkGyroscope();
                } else if (name.equals("GPS")) {
                    checkGPS();
                }
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
                MaterialCardView check_card = recyclerview_item.findViewById(R.id.card_view);
                check_card.setStrokeColor(getResources().getColor(R.color.green));
                desc_textview.setText("GPS is enabled");
                desc_textview.setTextColor(getColor(R.color.green));
                check.setVisibility(View.GONE);
                skip.setVisibility(View.GONE);
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
                                TestSuccessful("Gyroscope Test PASSED.");
                            }else{
                                Testfailed("Gyroscope Test Failed");
                            }
                        } catch (Exception e) {
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
                                TestSuccessful("Accelerometer Test PASSED.");
                            }else{
                                Testfailed("Acceleromter Test Failed");
                            }
                        } catch (Exception e) {
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
                        //Toast.makeText(getApplicationContext(),String.valueOf(result.getResultCode())+" + "+String.valueOf(Activity.RESULT_OK),Toast.LENGTH_SHORT).show();
                        TestSuccessful("Bluetooth works fine.");
                    }else{
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
                        if(test_textview.getText().toString().equals("SECONDARY MICROPHONE"))
                        TestSuccessful("External Mic Test PASSED");
                        break;
                    default:
                        Testfailed("External Mic Test FAILED");
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
                                TestSuccessful("Primary Mic Test PASSED.");
                            }else{
                                Testfailed("Primary Mic Test Failed");
                            }
                        } catch (Exception e) {
                            Testfailed("Primary Mic Test Failed");
                        }

                    }else{
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
                                TestSuccessful("Front Camera Test PASSED.");
                            }else{
                                Testfailed("Front Camera Test Failed");
                            }
                        } catch (Exception e) {
                            Testfailed("Front Camera Test Failed");
                        }

                    }else{
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
                                TestSuccessful("Rear Camera Test PASSED.");
                            }else{
                                Testfailed("Rear Camera Test Failed");
                            }
                        } catch (Exception e) {
                            Testfailed("Rear Camera Test Failed");
                        }

                    }else{
                        Testfailed("Rear Camera Test Failed");
                    }
                }
            });

    private void checkRootStatus() {
    }
//    private void genericAlertDialog(String Sensor){
//        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(CheckerActivity.this)
//                .setTitle("Suggestions")
//                .setMessage("This Device Doesn't Support "+sensor+" sensor");
//        builder.show();
//    }
}