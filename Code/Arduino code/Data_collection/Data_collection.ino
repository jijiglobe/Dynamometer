#include <Arduino.h>

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
char currentPos;
int D7FlipTime = 0;
char previousD7 = 0;
char collectedData[10000][2]; //this buffer holds all of the recorded data. if the system runs for 1 million timesteps it will overflow.
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
  REG_PWM_ENA = 1 << 6;                           // enable PWM on PWM channel (pin 7 = PWML6

  //digitalWrite(reset,HIGH);
  //digitalWrite(reset,LOW);
}

void readData(){
  int i = 0;
  while(millis() - D7FlipTime < 100){
    collectedData[i][0] = micros();
    if(digitalRead(D7)){
      bitSet(collectedData[i][1],7);
    }
    if(digitalRead(D6)){
      bitSet(collectedData[i][1],6);
    }
    if(digitalRead(D5)){
      bitSet(collectedData[i][1],5);
    }
    if(digitalRead(D4)){
      bitSet(collectedData[i][1],4);
    }
    if(digitalRead(D3)){
      bitSet(collectedData[i][1],3);
    }
    if(digitalRead(D2)){
      bitSet(collectedData[i][1],2);
    }
    if(digitalRead(D1)){
      bitSet(collectedData[i][1],1);
    }
    if(digitalRead(D0)){
      bitSet(collectedData[i][1],0);
    }
    //sprintf(buffer,"%d",collectedData[i][1]);
    //Serial.println(buffer);
    delay(1);
    if(abs(collectedData[i][1] - previousD7) >= 50){
      previousD7 = collectedData[i][1];
      D7FlipTime = millis();
    }
    i++;
  }
}

void printData(){
  int i = 0;
  while(collectedData[i][0] != 0 || collectedData[i+1][0] != 0){
    sprintf(buffer,"%d,%d",collectedData[i][0],collectedData[i][1]);
    Serial.println(buffer);
    delay(1);
    collectedData[i][0] = 0;
    collectedData[i][1] = 0;
    i++;
  }
}

void loop() {
  currentPos = 0;
  if(digitalRead(D7)){
    bitSet(currentPos,7);
  }
  if(digitalRead(D6)){
    bitSet(currentPos,6);
  }
  if(digitalRead(D5)){
    bitSet(currentPos,5);
  }
  if(digitalRead(D4)){
    bitSet(currentPos,4);
  }
  if(digitalRead(D3)){
    bitSet(currentPos,3);
  }
  if(digitalRead(D2)){
    bitSet(currentPos,2);
  }
  if(digitalRead(D1)){
    bitSet(currentPos,1);
  }
  if(digitalRead(D0)){
    bitSet(currentPos,0);
  }
  //sprintf(buffer,"%d",currentPos);
  //Serial.println(buffer);
  if(currentPos > 2){
    Serial.println("Motion Started");
    D7FlipTime = millis();
    readData();
    digitalWrite(reset,0);
    digitalWrite(reset,1);
    Serial.println("Motion Ceased");
    printData();
  }
}
