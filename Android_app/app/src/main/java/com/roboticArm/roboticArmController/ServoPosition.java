package com.roboticArm.roboticArmController;

public class ServoPosition {
    public int rotateServoPosition;
    public int pitchServoPosition;
    public int yawServoPosition;
    public int clawServoPosition;

    ServoPosition(int rotate, int pitch, int yaw, int claw){
        this.rotateServoPosition = rotate;
        this.pitchServoPosition = pitch;
        this.yawServoPosition = yaw;
        this.clawServoPosition = claw;
    }

    ServoPosition(){
        this.rotateServoPosition = MainActivity.INITIAL_POSITION_ROTATE;
        this.pitchServoPosition = MainActivity.INITIAL_POSITION_PITCH;
        this.yawServoPosition = MainActivity.INITIAL_POSITION_YAW;
        this.clawServoPosition = MainActivity.INITIAL_POSITION_CLAW;
    }
}
