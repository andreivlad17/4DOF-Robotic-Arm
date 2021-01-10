#include <Servo.h>
#include <SoftwareSerial.h>

// Hardware entities instantiation
Servo rotate_servo;
Servo pitch_servo;
Servo elbow_servo;
Servo claw_servo;

// Pentru a putea trimite date prin Bluetooth, pinul TX al modulului va fi
// conectat la pinul RX al placii si RX de pe modul la TX de pe placa - nu era 
// obligatoriu pentru acest proiect, deoarece nu trimitem date de pe placa
SoftwareSerial BT_module(0, 1); // RX | TX

// Control pins, positions and values
// Elementele de control (Joystick si 2 potentiometre) vor fi conectate
// la 4 pini analogici
const int elbow_pot_pin = A0;
const int claw_pot_pin = A1;
const int joystick_x_key = A2;
const int joystick_y_key = A3;
// Pozitiile in care vor si setate servo-urile
int rotate_pos;
int pitch_pos;
int elbow_pos;
int claw_pos;
// Valorile citie de pe elementele analogice, cu valori intre 0 si 1024.
// Acestea vor fi mapate pentru fiecare servo, pentru a corespunde valorilor
// corespunzatoare unui servo, intre 0 si 180 de grade.
// Joystick-ul functioneaza ca 2 potentiometre, fiecare controland cate o axa. 
// Aditional, mai este putea fi folosita optiunea de click pe joystick.
int joystick_x_value;
int joystick_y_value;
int elbow_pot_value;
int claw_pot_value;

// Servomotors pins - Digital PWM
const int rotate_servo_pin = 10;
const int pitch_servo_pin = 11;
const int elbow_servo_pin = 9;
const int claw_servo_pin = 6;

// Pozitiile in care vor fi setate servo-urile initial, inainte de a
// le controla cu potentiometre sau din aplicatia Android.
int initial_position_rotate = 90;
int initial_position_pitch = 90;
int initial_position_elbow = 90;
int initial_position_claw = 90;

// Mode select
// Daca este setat pe 0, bratul va fi controlat cu componentele fizice
// Daca este setat pe 1, acesta va astepta semnale seriale obtinute prin Bluetooth
int mode_select = 0;
const int mode_select_pin = 4; // digital

// Function definition
// writeToServos - scrierea celor 4 servo-uri
void writeToServos(int rotate_pos, int pitch_pos, int elbow_pos, int claw_pos);
// servoConnected - verifica daca toate cele 4 servo-uri sunt conectate
bool servosConnected();

static int BT_value = 0;

void setup() {
  // Setarea Baud Rate-ului la 9600 pentru ambele comunicari
  // Putem Baud Rate-ul conexiunii Bluetooth la 9600, deoarece nu avem nevoie de setari cu comenzile acestuia
  Serial.begin(9600);
  BT_module.begin(9600);
  Serial.println("Bluetooth ready to connect\nDefault password is 1234 or 000");

  rotate_servo.attach(rotate_servo_pin);
  pitch_servo.attach(pitch_servo_pin);
  elbow_servo.attach(elbow_servo_pin);
  claw_servo.attach(claw_servo_pin);

  writeToServos(initial_position_rotate, initial_position_pitch, initial_position_elbow, initial_position_claw);

  pinMode(mode_select_pin, INPUT);

  rotate_pos = initial_position_rotate;
  pitch_pos = initial_position_pitch;
  elbow_pos = initial_position_elbow;
  claw_pos = initial_position_claw;
}

void loop() {

  mode_select = digitalRead(mode_select_pin);

  // Daca toate servo-urile sunt conectate, putem controla bratul
  // Daca nu sunt conectate, afisam un mesaj
  if(servosConnected()){
    if (mode_select == 0) {
      // Bluetooth control case
      // Asteptam transmitere de date din aplicatie
      if (BT_module.available()) {
        // Datele vor fi trimise sub forma de intreg
        BT_value = BT_module.parseInt();
        Serial.println("Value: " + (int)BT_value);

        // In functie de valoarea transmisa, vom muta o anumita parte a bratului:
        // Daca cifra miilor a fiecarei valori transmise este 1, vom roti bratul,
        // daca este 2 vom inclina bratul(miscare asemanatoare cu cea a umarulul),
        // daca este 3 bratul va actiona segmentul care misca gheara,
        // daca este 4 vom inchide sau deschide gheara.
        // Pentru fiecare caz vom mapa valori de la 0 la 180 de grade(sau invers, daca dorim inversarea
        // directiei fata de cea a dispozitivului de control)
        if (BT_value >= 1000 && BT_value < 1180) {
          //Serial.println("Rotate: " + (int)BT_value);
          rotate_pos = map(BT_value, 1000, 1179, 179, 0);
        }
        else if (BT_value >= 2000 && BT_value < 2180) {
          //Serial.println("Pitch: " + (int)BT_value);
          pitch_pos = map(BT_value, 2000, 2179, 0, 179);
        }
        else if (BT_value >= 3000 && BT_value < 3180) {
          //Serial.println("elbow: " + (int)BT_value);
          elbow_pos = map(BT_value, 3000, 3179, 0, 179);
        }
        else if (BT_value >= 4000 && BT_value < 4180) {
          //Serial.println("Claw: " + (int)BT_value);
          claw_pos = map(BT_value, 4000, 4179, 0, 179);
        }

        // Valoarea va fi din nou 0, pentru a evita miscarea nedorita a servo-urilor
        BT_value = 0;
        // Este indicat sa se astepte o durata de timp intre citirile sincrone
        // Din aplicatie este transmis un delay la fiecare scriere
        delay(40);
      }
    }
    else {
      // Joystick and potentiometers control case
      // Vom citi valorile de pe pinii analogici si vom astepta 5 milisecunde intre citiri,
      // pentru a nu folosi valori gresite
      joystick_x_value = analogRead(joystick_y_key);
      delay(5);
      joystick_y_value = analogRead(joystick_x_key);
      delay(5);
      elbow_pot_value = analogRead(elbow_pot_pin);
      delay(5);
      claw_pot_value = analogRead(claw_pot_pin);
      delay(5);

      // Fiecare potentiometru(inclusiv cele din joystick) au 1024 de valori disponibile, 
      // de aceea le vom mapa la valorile corespunzatoare intre unghiurile limita pentru 
      // fiecare servo, unele dintre ele fiind limitate, in caz contrat ajungand in pozitii 
      // nedorite/neutilizate sau chiar blocarea partilor componente din cadru.
      rotate_pos = map(joystick_x_value, 0, 1023, 159, 0);
      pitch_pos = map(joystick_y_value, 0, 1023, 0, 179);
      elbow_pos = map(elbow_pot_value, 0, 1023, 0, 179);
      claw_pos = map(claw_pot_value, 0, 1023, 0, 179);
    }

    // Mutarea servo-urilor in functie de valorile calculate sau cele initiale
    writeToServos(rotate_pos, pitch_pos, elbow_pos, claw_pos);
    delay(50);
    
  } else Serial.println("Servos are not attached");
}

// Function declaration
void writeToServos(int rotate_pos, int pitch_pos, int elbow_pos, int claw_pos) {
  rotate_servo.write(rotate_pos);
  //delay(50);
  pitch_servo.write(pitch_pos);
  //delay(50);
  elbow_servo.write(elbow_pos);
  //delay(50);
  claw_servo.write(claw_pos);
  //delay(50);
}

bool servosConnected() {
  return rotate_servo.attached() && pitch_servo.attached() && elbow_servo.attached() && claw_servo.attached();
}
