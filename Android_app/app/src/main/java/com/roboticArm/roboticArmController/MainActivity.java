package com.roboticArm.roboticArmController;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    static final int INITIAL_POSITION_ROTATE = 90;
    static final int INITIAL_POSITION_PITCH = 90;
    static final int INITIAL_POSITION_ELBOW = 90;
    static final int INITIAL_POSITION_CLAW = 90;

    SeekBar rotateSlider = null;
    SeekBar pitchSlider = null;
    SeekBar elbowSlider = null;
    SeekBar clawSlider = null;

    TextView rotateAngle = null;
    TextView pitchAngle = null;
    TextView elbowAngle = null;
    TextView clawAngle = null;

    ImageButton startBtn = null;
    ImageButton addBtn = null;
    ImageButton clearBtn = null;
    ImageButton restorePositionBtn = null;
    ImageButton bluetoothBtn = null;
    ImageButton infoBtn = null;

    LinkedList<ServoPosition> automatedArmPositions = new LinkedList<>();
    LinkedList<ServoPosition> automatedArmPositionsReversed = new LinkedList<>();
    boolean isAutomatedActive = false;
    static int automationStep = 1;
    static int automationIndex = 0;

    // Bluetooth connection components
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket = null;
    static final int REQUEST_ENABLE_BT = 0;
    static final int REQUEST_DISCOVER_BT = 1;
    Set<BluetoothDevice> pairedDevices;
    BluetoothDevice hc05BluetoothModule;
    boolean isBluetoothConnected = false;
    private final UUID bluetoothModuleUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    OutputStream outputStream = null;

    @SuppressLint({"DefaultLocale", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rotateSlider = findViewById(R.id.rotate_slider);
        pitchSlider = findViewById(R.id.pitch_slider);
        elbowSlider = findViewById(R.id.elbow_slider);
        clawSlider = findViewById(R.id.claw_slider);

        rotateAngle = findViewById(R.id.rotate_angle);
        pitchAngle = findViewById(R.id.pitch_angle);
        elbowAngle = findViewById(R.id.elbow_angle);
        clawAngle = findViewById(R.id.claw_angle);

        startBtn = findViewById(R.id.start_btn);
        restorePositionBtn = findViewById(R.id.restore_pos_btn);
        addBtn = findViewById(R.id.add_btn);
        clearBtn = findViewById(R.id.clear_btn);
        bluetoothBtn = findViewById(R.id.bt_disconnected_btn);
        infoBtn = findViewById(R.id.info_btn);

        rotateSlider.setProgress(INITIAL_POSITION_ROTATE + 1000);
        pitchSlider.setProgress(INITIAL_POSITION_PITCH + 2000);
        elbowSlider.setProgress(INITIAL_POSITION_ELBOW + 3000);
        clawSlider.setProgress(INITIAL_POSITION_CLAW + 4000);

        // Live angle update based on sliders' positions
        rotateAngle.setText(String.format("%d\u00B0", rotateSlider.getProgress() - 1000));
        pitchAngle.setText(String.format("%d\u00B0", pitchSlider.getProgress() - 2000));
        elbowAngle.setText(String.format("%d\u00B0", elbowSlider.getProgress() - 3000));
        clawAngle.setText(String.format("%d\u00B0", clawSlider.getProgress() - 4000));

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



        // Slider listeners
        rotateSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChangedValue = i;
                rotateAngle.setText(String.format("%d\u00B0", progressChangedValue - 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(isBluetoothConnected && !isAutomatedActive){
                    try {
                        String writeValue = String.format("%d\n", progressChangedValue);
                        outputStream.write(writeValue.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        pitchSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChangedValue = i;
                pitchAngle.setText(String.format("%d\u00B0", progressChangedValue - 2000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(isBluetoothConnected && !isAutomatedActive){
                    try {
                        String writeValue = String.format("%d\n", progressChangedValue);
                        outputStream.write(writeValue.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        elbowSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChangedValue = i;
                elbowAngle.setText(String.format("%d\u00B0", progressChangedValue - 3000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(isBluetoothConnected && !isAutomatedActive){
                    try {
                        String writeValue = String.format("%d\n", progressChangedValue);
                        outputStream.write(writeValue.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        clawSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChangedValue = i;
                clawAngle.setText(String.format("%d\u00B0", progressChangedValue - 4000));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(isBluetoothConnected && !isAutomatedActive){
                    try {
                        String writeValue = String.format("%d\n", progressChangedValue);
                        outputStream.write(writeValue.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
                if (automatedArmPositions.isEmpty()) {
                    Toast.makeText(MainActivity.this, "There are no memorised positions", Toast.LENGTH_SHORT).show();
                } else if (!isAutomatedActive) {
                    automatedArmPositions.clear();
                    automatedArmPositionsReversed.clear();
                    Toast.makeText(MainActivity.this, "Positions cleared", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (isAutomatedActive) {
                    // TO DO: opresc automatizarea
                    isAutomatedActive = false;
                    automatedArmPositions.clear();
                    automatedArmPositionsReversed.clear();
                    Toast.makeText(MainActivity.this, "Positions cleared", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAutomatedActive) {
                    Toast.makeText(MainActivity.this, "Stop the automation first", Toast.LENGTH_SHORT).show();
                } else {
                    automatedArmPositions.add(new ServoPosition(rotateSlider.getProgress(), pitchSlider.getProgress(), elbowSlider.getProgress(), clawSlider.getProgress()));
                    automatedArmPositionsReversed.add(0, new ServoPosition(rotateSlider.getProgress(), pitchSlider.getProgress(), elbowSlider.getProgress(), clawSlider.getProgress()));
                    Toast.makeText(MainActivity.this, "Added " + rotateSlider.getProgress() + "/" + pitchSlider.getProgress() + "/" + elbowSlider.getProgress() + "/" + clawSlider.getProgress(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        restorePositionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAutomatedActive) {
                    Toast.makeText(MainActivity.this, "Stop the automation first", Toast.LENGTH_SHORT).show();
                } else {
                    rotateSlider.setProgress(INITIAL_POSITION_ROTATE + 1000);
                    pitchSlider.setProgress(INITIAL_POSITION_PITCH + 2000);
                    elbowSlider.setProgress(INITIAL_POSITION_ELBOW + 3000);
                    clawSlider.setProgress(INITIAL_POSITION_CLAW + 4000);

                    if(isBluetoothConnected){
                        try {
                            String writeValue = String.format("%d\n", rotateSlider.getProgress());
                            outputStream.write(writeValue.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            String writeValue = String.format("%d\n", pitchSlider.getProgress());
                            outputStream.write(writeValue.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            String writeValue = String.format("%d\n", elbowSlider.getProgress());
                            outputStream.write(writeValue.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            String writeValue = String.format("%d\n", clawSlider.getProgress());
                            outputStream.write(writeValue.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        bluetoothBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public boolean onLongClick(View view) {
                if (bluetoothAdapter == null) {
                    Toast.makeText(MainActivity.this, "Bluetooth not available", Toast.LENGTH_SHORT).show();
                } else {
                    if (!bluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                    if (!isBluetoothConnected) {
                        pairedDevices = bluetoothAdapter.getBondedDevices();
                        if (pairedDevices.size() > 0) {
                            for (BluetoothDevice device : pairedDevices) {
                                String deviceName = device.getName();
                                String deviceHardwareAddress = device.getAddress(); // MAC address
                            }
                        }
                        System.out.println(pairedDevices);

                        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        startActivityForResult(discoverableIntent, REQUEST_DISCOVER_BT);

                        hc05BluetoothModule = bluetoothAdapter.getRemoteDevice("00:20:08:00:31:55");
                        System.out.println(hc05BluetoothModule.getName());

                        int connectTrialCounter = 0;
                        do {
                            try {
                                bluetoothSocket = hc05BluetoothModule.createRfcommSocketToServiceRecord(bluetoothModuleUUID);
                                System.out.println(bluetoothSocket);
                                bluetoothSocket.connect();
                                System.out.println(bluetoothSocket.isConnected());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            connectTrialCounter++;
                        } while (!bluetoothSocket.isConnected() && (connectTrialCounter < 3));

                        if(bluetoothSocket.isConnected()){
                            Toast.makeText(MainActivity.this, "HC-05 connected", Toast.LENGTH_SHORT).show();
                            isBluetoothConnected = true;
                            bluetoothBtn = findViewById(R.id.bt_connected_btn);
                            findViewById(R.id.bt_connected_btn).setVisibility(View.VISIBLE);
                            findViewById(R.id.bt_disconnected_btn).setVisibility(View.INVISIBLE);
                            try {
                                outputStream = bluetoothSocket.getOutputStream();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to connect to HC-05", Toast.LENGTH_SHORT).show();
                        }

                    } else if(isBluetoothConnected){
                        try {
                            bluetoothSocket.close();
                            bluetoothBtn = findViewById(R.id.bt_disconnected_btn);
                            findViewById(R.id.bt_connected_btn).setVisibility(View.INVISIBLE);
                            findViewById(R.id.bt_disconnected_btn).setVisibility(View.VISIBLE);
                            System.out.println(bluetoothSocket.isConnected());
                            Toast.makeText(MainActivity.this, "HC-05 disconnected", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        isBluetoothConnected = false;
                    }
                }
                return false;
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAutomatedActive = !isAutomatedActive;
                AutomationThread autoThread = new AutomationThread();
                autoThread.start();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            bluetoothSocket.close();
            System.out.println(bluetoothSocket.isConnected());
            Toast.makeText(MainActivity.this, "HC-05 disconnected", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isBluetoothConnected = false;
    }

    @SuppressLint("DefaultLocale")
    private void automateMovement(){
        ServoPosition currentPosition = automatedArmPositions.get(automationIndex);
        rotateSlider.setProgress(currentPosition.rotateServoPosition);
        pitchSlider.setProgress(currentPosition.pitchServoPosition);
        elbowSlider.setProgress(currentPosition.elbowServoPosition);
        clawSlider.setProgress(currentPosition.clawServoPosition);

        if(isBluetoothConnected){
            try {
                String writeValue = String.format("%d\n", currentPosition.pitchServoPosition);
                outputStream.write(writeValue.getBytes());
                writeValue = String.format("%d\n", currentPosition.rotateServoPosition);
                outputStream.write(writeValue.getBytes());
                writeValue = String.format("%d\n", currentPosition.elbowServoPosition);
                outputStream.write(writeValue.getBytes());
                writeValue = String.format("%d\n", currentPosition.clawServoPosition);
                outputStream.write(writeValue.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if ((automationIndex == automatedArmPositions.size() - 1) || (automationIndex == 0 && automationStep == -1)) {
            automationStep = -automationStep;
        }

        automationIndex += automationStep;
    }

    private class AutomationThread extends Thread {
        LinkedList<ServoPosition> armPositions = new LinkedList<>();

        @SuppressLint("DefaultLocale")
        public void run(){
            while(isAutomatedActive) {
                ServoPosition currentPosition = automatedArmPositions.get(automationIndex);
                rotateSlider.setProgress(currentPosition.rotateServoPosition);
                pitchSlider.setProgress(currentPosition.pitchServoPosition);
                elbowSlider.setProgress(currentPosition.elbowServoPosition);
                clawSlider.setProgress(currentPosition.clawServoPosition);

                if(isBluetoothConnected){
                    try {
                        String writeValue = String.format("%d\n", currentPosition.pitchServoPosition);
                        outputStream.write(writeValue.getBytes());
                        try {
                            sleep(150);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        writeValue = String.format("%d\n", currentPosition.rotateServoPosition);
                        outputStream.write(writeValue.getBytes());
                        try {
                            sleep(150);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        /*
                        writeValue = String.format("%d\n", currentPosition.elbowServoPosition);
                        outputStream.write(writeValue.getBytes());
                        try {
                            sleep(150);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        writeValue = String.format("%d\n", currentPosition.clawServoPosition);
                        outputStream.write(writeValue.getBytes());
                        try {
                            sleep(150);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        */

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if ((automationIndex == automatedArmPositions.size() - 1) || (automationIndex == 0 && automationStep == -1)) {
                    automationStep = -automationStep;
                }

                automationIndex += automationStep;


            }
        }
    }

    private class ConnectThread extends Thread {
        private static final String TAG = "BT_CONNECTION";
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = mmDevice.createRfcommSocketToServiceRecord(bluetoothModuleUUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                isBluetoothConnected = true;
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
                isBluetoothConnected = false;
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

}