package com.example.devicehealthchecker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Timer;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {

    ImageView imageView;
    ImageView sucessview;
    private SensorManager sensorManager;
    private Sensor sensor;
    Button button;

    Animation animation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

         animation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.accelerometer_anim);

        imageView=findViewById(R.id.imageView);

        sucessview=findViewById(R.id.success);

        button=findViewById(R.id.Accelbutton);
        onclick();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        imageView.startAnimation(animation);

        if(sensorManager!=null){
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if(sensor!=null){
                sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
            }else{
                genericAlertDialog("Accelerometer");
            }
        }else{
            genericAlertDialog("Accelerometer");
        }
    }
    public void onclick(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("RESULT_OK",String.valueOf(Fields.Sensor_Status));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
    private void genericAlertDialog(String Sensor){
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(AccelerometerActivity.this)
                .setTitle("Suggestions")
                .setMessage("This Device Doesn't Support "+sensor+" sensor");
        builder.show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
//        Toast.makeText(getApplicationContext(),String.valueOf(Fields.Accel_Status)+" accel",Toast.LENGTH_SHORT).show();


        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

            if (sensorEvent.values[0] > 1.0f) {
                //Toast.makeText(this,"x axis moved",Toast.LENGTH_SHORT).show();
                successfull();
            }else if (sensorEvent.values[1]< -1.0f) {
                //Toast.makeText(this,"y axis moved",Toast.LENGTH_SHORT).show();
                successfull();
            }else{
                //
            }
        }
    }
    private void successfull(){

        Fields.Sensor_Status=true;
        imageView.clearAnimation();
        imageView.setVisibility(View.GONE);
        sucessview.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);


    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("RESULT_OK",String.valueOf(Fields.Sensor_Status));
        setResult(RESULT_OK,intent);
        super.onBackPressed();

    }
}