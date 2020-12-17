#include <Servo.h>
#include <SoftwareSerial.h>

// Hardware entities instantiation
Servo rotate_servo;
Servo pitch_servo;
Servo yaw_servo;
Servo claw_servo;

SoftwareSerial BT_module(0,1); // RX | TX 

// Control pins, positions and values
const int yaw_pot_pin = A0;
const int claw_pot_pin = A1;
const int joystick_x_key = A2;
const int joystick_y_key = A3;
int rotate_pos;
int pitch_pos;
int yaw_pos;
int claw_pos;
int joystick_x_value;
int joystick_y_value;
int yaw_pot_value;
int claw_pot_value;

// Servomotors pins
const int rotate_servo_pin = 10;
const int pitch_servo_pin = 11;
const int yaw_servo_pin = 9;
const int claw_servo_pin = 8;

int initial_position_rotate = 90;
int initial_position_pitch = 90;
int initial_position_yaw = 90;
int initial_position_claw = 90;

// Mode select 
int mode_select = 0;
const int mode_select_pin = 4;

// Function definition
void writeToServos(int rotate_pos, int pitch_pos, int yaw_pos, int claw_pos);
  

void setup ( ) {
  Serial.begin(9600);
  BT_module.begin(9600);
  Serial.println("Bluetooth ready to connect\nDefualt password is 1234 or 000"); 
  
  rotate_servo.attach(10);
  pitch_servo.attach(11);
  yaw_servo.attach(9);
  claw_servo.attach(8);

  writeToServos(initial_position_rotate, initial_position_pitch, initial_position_yaw, initial_position_claw);

  /*
  rotate_servo.write(initial_position_rotate);
  pitch_servo.write(initial_position_pitch);
  yaw_servo.write(initial_position_yaw);
  claw_servo.write(initial_position_claw);
  */
  pinMode(mode_select_pin, INPUT);

}

void loop ( ) {


  mode_select = digitalRead(mode_select_pin);

  if (mode_select == 0) {
    // Bluetooth control case
    int BT_value; 
    
    if (BT_module.available() > 0) {
      BT_value = BT_module.read(); 
      Serial.println(BT_module);

      // Based on each slider's value, each designated servo will update its position
      if(BT_value >= 0 && BT_value < 180){
        rotate_pos = map(BT_value, 0, 179, 179, 0);
      } 
      else if(BT_value >= 180 && BT_value < 360){
        pitch_pos = map(BT_value, 180, 359, 0, 179);
      }
      else if(BT_value >= 360 && BT_value < 540){
        yaw_pos = map(BT_value, 360, 539, 0, 179);
      }
      else if(BT_value > 540 && BT_value < 720){
        claw_pos = map(BT_value, 540, 719, 0, 179);
      }
      
    }
  } else {
    // Joystick and potentiometers control case
    joystick_x_value = analogRead(joystick_y_key) ;
    joystick_y_value = analogRead(joystick_x_key) ;
    yaw_pot_value = analogRead(yaw_pot_pin) ;
    claw_pot_value = analogRead(claw_pot_pin) ;

    rotate_pos = map(joystick_x_value, 0, 511, 179, 0) ;
    pitch_pos = map(joystick_y_value, 0, 511, 0, 179) ;
    yaw_pos = map(yaw_pot_value, 0, 511, 80, 179) ;
    claw_pos = map(claw_pot_value, 0, 511, 0, 179) ;
  }

  writeToServos(rotate_pos, pitch_pos, yaw_pos, claw_pos);
  /*
  rotate_servo.write(rotate_pos) ;
  pitch_servo.write(pitch_pos) ;
  yaw_servo.write(yaw_pos) ;
  claw_servo.write(claw_pos) ;
  */
  delay(100);

}

// Function declaration
void writeToServos(int rotate_pos, int pitch_pos, int yaw_pos, int claw_pos){
  rotate_servo.write(rotate_pos) ;
  delay(100);
  pitch_servo.write(pitch_pos) ;
  delay(100);
  yaw_servo.write(yaw_pos) ;
  delay(100);
  claw_servo.write(claw_pos) ;
  delay(100);
}
