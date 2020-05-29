#include <Arduino.h>

const unsigned int quadrature_hz = 500000;
unsigned long previousMicros = 0;
unsigned long previousMillis = 0;

//input pins for IC chip
const int state_transition = 10;
const int overflow_signal = 11;
const int counter_direction = 12;

//counter bits 0-7
const int D0 = 26;
const int D1 = 28;
const int D2 = 30;
const int D3 = 32;
const int D4 = 34;
const int D5 = 36;
const int D6 = 38;
const int D7 = 40;

//output pins for IC chip
const int CLK = 7;
const int select = 51;
const int reset = 49;
const int output_enabler = 47;

//use these channels strictly for testing
int quadrature_a = 24;
int quadrature_b = 22;

int quadrature_state;
int quad_channel_a;
int quad_channel_b;

int test_clock = 52;
int test_clock_state;

char buffer[100];
void setup() {
  
  // put your setup code here, to run once:
  pinMode(state_transition,INPUT);
  pinMode(overflow_signal,INPUT);
  pinMode(counter_direction,INPUT);

  pinMode(D0,INPUT);
  pinMode(D1,INPUT);
  pinMode(D2,INPUT);
  pinMode(D3,INPUT);
  pinMode(D4,INPUT);
  pinMode(D5,INPUT);
  pinMode(D6,INPUT);
  pinMode(D7,INPUT);

  pinMode(CLK,OUTPUT);
  pinMode(select,OUTPUT);
  digitalWrite(select,HIGH);  
  pinMode(reset,OUTPUT);
  digitalWrite(reset,HIGH);
  pinMode(output_enabler,OUTPUT);
  digitalWrite(output_enabler,LOW);

  //use these pins strictly for testing
  pinMode(quadrature_a,OUTPUT);
  pinMode(quadrature_b,OUTPUT);
  pinMode(test_clock,OUTPUT);
  test_clock_state = LOW;

 
  Serial.begin(115200);
  Serial.println("Serial on");
  int32_t mask_PWM_pin = digitalPinToBitMask(7);
  REG_PMC_PCER1 = 1 << 4;                         // activate clock for PWM controller
  REG_PIOC_PDR |= mask_PWM_pin;                   // activate peripheral functions for pin (disables all PIO functionality)
  REG_PIOC_ABSR |= mask_PWM_pin;                  // choose peripheral option B
  REG_PWM_CLK = 0;                                // choose clock rate, 0 -> full MCLK as reference 84MHz
  REG_PWM_CMR6 = 0 << 9;                          // select clock and polarity for PWM channel (pin7) -> (CPOL = 0)
  REG_PWM_CPRD6 = 10;                             // initialize PWM period -> T = value/84MHz (value: up to 16bit), value=10 -> 8.4MHz
  REG_PWM_CDTY6 = 5;                              // initialize duty cycle, REG_PWM_CPRD6 / value = duty cycle, for 10/5 = 50%
  REG_PWM_ENA = 1 << 6;                           // enable PWM on PWM channel (pin 7 = PWML6)

  quadrature_state = 0;
  quad_channel_a = LOW;
  quad_channel_b = LOW;
  attachInterrupt(digitalPinToInterrupt(overflow_signal),CNT_DCDR,CHANGE); 
}
void CNT_DCDR(){
  Serial.println("state change detected");
}

void loop() {
  // put your main code here, to run repeatedly:
  /*if (micros() - previousMicros >= quadrature_hz) {
    //Serial.println("state change");
    previousMicros = micros();
    switch (quadrature_state) {
      case 0:
        // statements
        quad_channel_a = HIGH;
        quad_channel_b = LOW;
        quadrature_state = 1;
      break;
      case 1:
        // statements
        quad_channel_a = HIGH;
        quad_channel_b = HIGH;
        quadrature_state = 2;
      break;
       case 2:
        // statements
        quad_channel_a = LOW;
        quad_channel_b = HIGH;
        quadrature_state = 3;
      break;
      case 3:
        // statements
        quad_channel_a = LOW;
        quad_channel_b = LOW;
        quadrature_state = 0;
      break;
    }
    digitalWrite(quadrature_a,quad_channel_a);
    digitalWrite(quadrature_b,quad_channel_b);
    //Serial.println(quadrature_state);
    //Serial.println("a:" + String(quadrature_a) + "b: " + String(quadrature_b));
  }*/

  if(millis() - previousMillis >= 1000){
    int microList[10];
    microList[0] = micros();
    microList[2] = digitalRead(D0);
    microList[3] = digitalRead(D1);
    microList[4] = digitalRead(D2);
    microList[5] = digitalRead(D3);
    microList[6] = digitalRead(D4);
    microList[7] = digitalRead(D5);
    microList[8] = digitalRead(D6);
    microList[9] = digitalRead(D7);
    microList[1] = micros();
    sprintf(buffer,"Times: %d, %d, %d, %d, %d, %d, %d, %d",microList[0],microList[1],microList[2],microList[3],microList[4],microList[5],microList[6],microList[7]);
    Serial.println(buffer);
    /*
    sprintf(buffer,"Counter: %d%d%d%d%d%d%d%d",
          digitalRead(D0),digitalRead(D7),digitalRead(D6),digitalRead(D5),
          digitalRead(D4),digitalRead(D3),digitalRead(D2),digitalRead(D1));
    Serial.println(buffer);

    previousMillis = millis();*/
  }
  /*
  if(test_clock_state == LOW){
    test_clock_state = HIGH;
  }else{
    test_clock_state = LOW;
  }
  digitalWrite(test_clock,test_clock_state);
  */
}
