package com.roboticArm.roboticArmController;

public class ServoPosition {
    public int rotateServoPosition = MainActivity.INITIAL_POSITION_ROTATE;
    public int pitchServoPosition = MainActivity.INITIAL_POSITION_PITCH;
    public int yawServoPosition = MainActivity.INITIAL_POSITION_YAW;
    public int clawServoPosition = MainActivity.INITIAL_POSITION_CLAW;

    ServoPosition(int rotate, int pitch, int yaw, int claw){
        this.clawServoPosition = rotate;
        this.pitchServoPosition = pitch;
        this.yawServoPosition = yaw;
        this.clawServoPosition = claw;
    }
}
