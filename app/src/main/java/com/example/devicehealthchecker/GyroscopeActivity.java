package com.example.devicehealthchecker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class GyroscopeActivity extends AppCompatActivity implements SensorEventListener {

    ImageView imageView;
    ImageView sucessview;
    private SensorManager sensorManager;
    private Sensor sensor;
    Button button;
    TextView textView;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        animation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.gyroscope_anim);

        imageView=findViewById(R.id.imageView);

        sucessview=findViewById(R.id.success);

        button=findViewById(R.id.Gyrobutton);
        onclick();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        imageView.startAnimation(animation);

        if(sensorManager!=null){
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if(sensor!=null){
                sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
            }else{
                genericAlertDialog("Gyroscope");
            }
        }else{
            genericAlertDialog("Gyroscope");
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
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(GyroscopeActivity.this)
                .setTitle("Suggestions")
                .setMessage("This Device Doesn't Support "+sensor+" sensor");
        builder.show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType()==Sensor.TYPE_GYROSCOPE){
            //textView.setText(String.valueOf(sensorEvent.values[0])+" "+String.valueOf(sensorEvent.values[1])+" "+String.valueOf(sensorEvent.values[2]));
            if (sensorEvent.values[2] > 1.0f) {
                //Toast.makeText(this,"x rotated",Toast.LENGTH_SHORT).show();
                successfull();
            }else if (sensorEvent.values[2]< -1.0f) {
                //Toast.makeText(this,"y rotated",Toast.LENGTH_SHORT).show();
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