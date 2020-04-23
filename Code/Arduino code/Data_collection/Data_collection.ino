#include <Arduino.h>

const unsigned long serialPeriodMillis = 2000;
unsigned long previousMillis = 0;

void setup() {
  // put your setup code here, to run once:
  int state_transition = 10;
  int overflow_signal = 11;
  int counter_direction = 12;

  int D0 = 2;
  int D1 = 3;
  int D2 = 4;
  int D3 = 5;
  int D4 = 6;
  int D5 = 7;
  int D6 = 8;
  int D7 = 9;


  int CLK = 53;
  int select = 51;
  int reset = 49;
  int output_enabler = 47;

  int32_t mask_PWM_pin = digitalPinToBitMask(7);
  REG_PMC_PCER1 = 1 << 4;                         // activate clock for PWM controller
  REG_PIOC_PDR |= mask_PWM_pin;                   // activate peripheral functions for pin (disables all PIO functionality)
  REG_PIOC_ABSR |= mask_PWM_pin;                  // choose peripheral option B
  REG_PWM_CLK = 0;                                // choose clock rate, 0 -> full MCLK as reference 84MHz
  REG_PWM_CMR6 = 0 << 9;                          // select clock and polarity for PWM channel (pin7) -> (CPOL = 0)
  REG_PWM_CPRD6 = 10;                             // initialize PWM period -> T = value/84MHz (value: up to 16bit), value=10 -> 8.4MHz
  REG_PWM_CDTY6 = 5;                              // initialize duty cycle, REG_PWM_CPRD6 / value = duty cycle, for 10/5 = 50%
  REG_PWM_ENA = 1 << 6;                           // enable PWM on PWM channel (pin 7 = PWML6)

  Serial.begin(115200);
  Serial.println("Serial on");
  
}

void loop() {
  // put your main code here, to run repeatedly:
  if (millis() - previousMillis > serialPeriodMillis) {
    Serial.println("TIC");
    previousMillis = millis();
  }
}
