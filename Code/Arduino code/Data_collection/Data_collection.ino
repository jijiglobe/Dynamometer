#include <Arduino.h>

const unsigned int quadrature_hz = 500000;
unsigned long previousMicros = 0;
unsigned long previousMillis = 0;

//input pins for IC chip
const int state_transition = 10;
const int overflow_signal = 11;
const int counter_direction = 12;

//counter bits 0-7
const int D0 = 2;
const int D1 = 3;
const int D2 = 4;
const int D3 = 5;
const int D4 = 6;
const int D5 = 7;
const int D6 = 8;
const int D7 = 9;

//output pins for IC chip
const int CLK = 53;
const int select = 51;
const int reset = 49;
const int output_enabler = 47;

//use these channels strictly for testing
int quadrature_a = 24;
int quadrature_b = 22;

int quadrature_state;
int quad_channel_a;
int quad_channel_b;

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
  pinMode(reset,OUTPUT);
  digitalWrite(reset,LOW);
  pinMode(output_enabler,OUTPUT);
  digitalWrite(output_enabler,HIGH);

  //use these pins strictly for testing
  pinMode(quadrature_a,OUTPUT);
  pinMode(quadrature_b,OUTPUT);


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
  attachInterrupt(digitalPinToInterrupt(state_transition),CNT_DCDR,CHANGE); 
}
void CNT_DCDR(){
  Serial.println("state change detected");
}

void loop() {
  // put your main code here, to run repeatedly:
  if (micros() - previousMicros >= quadrature_hz) {
    //Serial.println("state change");
    previousMicros = micros();
    switch (quadrature_state) {
      case 0:
        // statements
        quad_channel_a = HIGH;
        quad_channel_b = LOW;
        quadrature_state = 0;
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

  }

  if(millis() - previousMillis >= 1000){
    digitalWrite(select,HIGH);
    sprintf(buffer,"Counter: %d%d%d%d%d%d%d%d",
          digitalRead(D0),digitalRead(D1),digitalRead(D2),digitalRead(D3),
          digitalRead(D4),digitalRead(D5),digitalRead(D6),digitalRead(D7));
    Serial.println(buffer);
    digitalWrite(select,HIGH);
    sprintf(buffer,"         %d%d%d%d%d%d%d%d",
          digitalRead(D0),digitalRead(D1),digitalRead(D2),digitalRead(D3),
          digitalRead(D4),digitalRead(D5),digitalRead(D6),digitalRead(D7));
    Serial.println(buffer);
    previousMillis = millis();
  }
}
