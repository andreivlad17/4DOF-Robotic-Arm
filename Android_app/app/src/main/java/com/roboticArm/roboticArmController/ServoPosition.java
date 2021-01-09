package com.roboticArm.roboticArmController;

public class ServoPosition {
    public int rotateServoPosition;
    public int pitchServoPosition;
    public int elbowServoPosition;
    public int clawServoPosition;

    ServoPosition(int rotate, int pitch, int elbow, int claw){
        this.rotateServoPosition = rotate;
        this.pitchServoPosition = pitch;
        this.elbowServoPosition = elbow;
        this.clawServoPosition = claw;
    }

    ServoPosition(){
        this.rotateServoPosition = MainActivity.INITIAL_POSITION_ROTATE;
        this.pitchServoPosition = MainActivity.INITIAL_POSITION_PITCH;
        this.elbowServoPosition = MainActivity.INITIAL_POSITION_ELBOW;
        this.clawServoPosition = MainActivity.INITIAL_POSITION_CLAW;
    }
}
