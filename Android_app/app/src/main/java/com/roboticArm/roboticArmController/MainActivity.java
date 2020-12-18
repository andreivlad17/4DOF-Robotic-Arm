package com.roboticArm.roboticArmController;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    static final int INITIAL_POSITION_ROTATE = 90;
    static final int INITIAL_POSITION_PITCH = 90;
    static final int INITIAL_POSITION_YAW = 90;
    static final int INITIAL_POSITION_CLAW = 90;

    SeekBar rotateSlider = null;
    SeekBar pitchSlider = null;
    SeekBar yawSlider = null;
    SeekBar clawSlider = null;

    TextView rotateAngle = null;
    TextView pitchAngle = null;
    TextView yawAngle = null;
    TextView clawAngle = null;

    ImageButton startBtn = null;
    ImageButton addBtn = null;
    ImageButton clearBtn = null;
    ImageButton restorePositionBtn = null;
    ImageButton bluetoothBtn = null;
    ImageButton infoBtn = null;

    LinkedList<ServoPosition> automatedArmPositions = new LinkedList<>();
    boolean isAutomatedActive = false;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rotateSlider = findViewById(R.id.rotate_slider);
        pitchSlider = findViewById(R.id.pitch_slider);
        yawSlider = findViewById(R.id.yaw_slider);
        clawSlider = findViewById(R.id.claw_slider);

        rotateAngle = findViewById(R.id.rotate_angle);
        pitchAngle = findViewById(R.id.pitch_angle);
        yawAngle = findViewById(R.id.yaw_angle);
        clawAngle = findViewById(R.id.claw_angle);

        startBtn = findViewById(R.id.start_btn);
        restorePositionBtn = findViewById(R.id.restore_pos_btn);
        addBtn = findViewById(R.id.add_btn);
        clearBtn = findViewById(R.id.clear_btn);
        bluetoothBtn = findViewById(R.id.bt_btn);
        infoBtn = findViewById(R.id.info_btn);

        rotateSlider.setProgress(INITIAL_POSITION_ROTATE);
        pitchSlider.setProgress(INITIAL_POSITION_PITCH*3);
        yawSlider.setProgress(INITIAL_POSITION_YAW*5);
        clawSlider.setProgress(INITIAL_POSITION_CLAW*7);

        // Live angle update based on sliders' positions
        rotateAngle.setText(String.format("%d\u00B0", rotateSlider.getProgress()));
        pitchAngle.setText(String.format("%d\u00B0", pitchSlider.getProgress() - 180));
        yawAngle.setText(String.format("%d\u00B0", yawSlider.getProgress() - 360));
        clawAngle.setText(String.format("%d\u00B0", clawSlider.getProgress() - 540));

        // Slider listeners
        rotateSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChangedValue = i;
                rotateAngle.setText(String.format("%d\u00B0", progressChangedValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //TO DO: send
            }
        });

        pitchSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChangedValue = i;
                pitchAngle.setText(String.format("%d\u00B0", progressChangedValue - 180));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //TO DO: send
            }
        });

        yawSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChangedValue = i;
                yawAngle.setText(String.format("%d\u00B0", progressChangedValue - 360));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //TO DO: send
            }
        });

        clawSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChangedValue = i;
                clawAngle.setText(String.format("%d\u00B0", progressChangedValue - 540));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //TO DO: send
            }
        });

        // Button listeners
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Please hold to clear positions", Toast.LENGTH_SHORT).show();
            }
        });

        clearBtn.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                if(automatedArmPositions.isEmpty()){
                    Toast.makeText(MainActivity.this, "There are no memorised positions", Toast.LENGTH_SHORT).show();
                } else if(!isAutomatedActive){
                    automatedArmPositions.clear();
                    Toast.makeText(MainActivity.this, "Positions cleared", Toast.LENGTH_SHORT).show();
                    return true;
                } else if(isAutomatedActive){
                    // TO DO: opresc automatizarea
                    isAutomatedActive = false;
                    automatedArmPositions.clear();
                    Toast.makeText(MainActivity.this, "Positions cleared", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAutomatedActive){
                    Toast.makeText(MainActivity.this, "Stop the automation first!", Toast.LENGTH_SHORT).show();
                } else {
                    automatedArmPositions.add(new ServoPosition(rotateSlider.getProgress(), pitchSlider.getProgress(), yawSlider.getProgress(), clawSlider.getProgress()));
                    Toast.makeText(MainActivity.this, "Added " + rotateSlider.getProgress() + "/" + pitchSlider.getProgress() + "/" + yawSlider.getProgress() + "/" + clawSlider.getProgress(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        restorePositionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAutomatedActive){
                    Toast.makeText(MainActivity.this, "Stop the automation first!", Toast.LENGTH_SHORT).show();
                } else {
                    rotateSlider.setProgress(INITIAL_POSITION_ROTATE);
                    pitchSlider.setProgress(INITIAL_POSITION_PITCH*3);
                    yawSlider.setProgress(INITIAL_POSITION_YAW*5);
                    clawSlider.setProgress(INITIAL_POSITION_CLAW*7);
                }
            }
        });

        bluetoothBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // TO DO: schimb culoarea in functie de status, pop-up cu devices daca e deconectat, disconnect daca e conectat + toast pt confirmare
                return false;
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAutomatedActive = !isAutomatedActive;
            }
        });
    }

    public void onClick(View view){

    }


}