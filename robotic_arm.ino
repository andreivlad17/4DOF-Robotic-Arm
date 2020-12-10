#include <Servo.h>

Servo rotate_servo;
Servo pitch_servo;
Servo incline_servo;
//Servo claw_servo;

const int incline_pot_pin = A0;
//const int claw_pot_pin = A1;
const int joystick_x_key = A2;
const int joystick_y_key = A3;
int joystick_x_pos;
int joystick_y_pos;
int incline_pot_pos;
//int claw_pot_pos;
int joystick_x_value;
int joystick_y_value;
int incline_pot_value;
//int claw_pot_value;

const int rotate_servo_pin = 10;
const int pitch_servo_pin = 9;
const int incline_servo_pin = 11;
//int claw_servo_pin = 8;

int initial_position_rotate = 120;
int initial_position_pitch = 90;
int initial_position_incline = 90;
//int initial_position_claw = 0;

void setup ( ) {
  Serial.begin (9600) ;
  rotate_servo.attach (10 ) ;
  pitch_servo.attach (9 ) ;
  incline_servo.attach (11 ) ;
  //claw_servo.attach (8 ) ;

  rotate_servo.write (initial_position_rotate);
  pitch_servo.write (initial_position_pitch);
  incline_servo.write (initial_position_incline);
  //claw_servo.write (initial_position_claw);

  //pinMode (joystick_x_key, INPUT) ;
  //pinMode (joystick_y_key, INPUT) ;
  //pinMode (incline_pot_pin, INPUT) ;
  //pinMode (claw_pot_pin, INPUT) ;
}

void loop ( ) {
  joystick_x_value = analogRead (joystick_y_key) ;
  joystick_y_value = analogRead (joystick_x_key) ;
  incline_pot_value = analogRead (incline_pot_pin) ;
  //claw_pot_value = analogRead (claw_pot_pin) ;

  joystick_x_pos = map(joystick_x_value, 0, 1023, 179, 0) ;
  joystick_y_pos = map(joystick_y_value, 0, 1023, 50, 179) ;
  incline_pot_pos = map(incline_pot_value, 0, 1023, 0, 150) ;
  //claw_pot_pos = map(claw_pot_value, 0, 1023, 0, 179) ;

  rotate_servo.write(joystick_x_pos) ;
  pitch_servo.write(joystick_y_pos) ;
  incline_servo.write(incline_pot_pos) ;
  //claw_servo.write(claw_pot_pos) ;
  delay(100);

  /*
  if (joystick_x_value < 300) {
    if (initial_position_rotate < 10) { } else {
      initial_position_rotate = initial_position_rotate - 20;
      rotate_servo.write ( initial_position_rotate ) ;
      delay (100) ;
    }
  } if (joystick_x_value > 700) {
    if (initial_position_rotate > 180)
    {
    }
    else {
      initial_position_rotate = initial_position_rotate + 20;
      rotate_servo.write ( initial_position_rotate ) ;
      delay (100) ;
    }
  }

  if (joystick_y_value < 300) {
    if (initial_position_pitch < 10) { } else {
      initial_position_pitch = initial_position_pitch - 20;
      pitch_servo.write ( initial_position_pitch ) ;
      delay (100) ;
    }
  } if (joystick_y_value > 700) {
    if (initial_position_pitch > 180)
    {
    }
    else {
      initial_position_pitch = initial_position_pitch + 20;
      pitch_servo.write ( initial_position_pitch ) ;
      delay (100) ;
    }
  }*/
}
