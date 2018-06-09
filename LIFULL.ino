#include <Adafruit_GFX.h>
#include <Adafruit_SH1106.h>
#include <SoftwareSerial.h>
#define OLED_RESET 4
 unsigned long currentHr = 0;
 unsigned long horn = 0;
 unsigned long currentSec = 0;
 char v1='a';
 char v2='b';
 char v3='c';
 String myStHr;
 boolean hB=0;
 boolean tc=0;
  boolean ts=0;
  int cs=0;
 char bt;
 int value = 0;
  String myStsec;
 unsigned long currentMin = 0;
 String myStmin;
int blueTx=2;   //Tx (보내는핀 설정)at
int blueRx=3;   //Rx (받는핀 설정)
const int motorPin  = 4; 
SoftwareSerial mySerial(blueTx, blueRx);  //시리얼 통신을 위한 객체선언

Adafruit_SH1106 display(OLED_RESET);
void setup() {
  // put your setup code here, to run once:
  
  display.begin(SH1106_SWITCHCAPVCC, 0x3C);
   
    mySerial.begin(9600); //블루투스 시리얼 개방
     pinMode(motorPin, OUTPUT);
    Serial.begin(9600);
    digitalWrite(motorPin, LOW);
}

  void loop() {
    // put your main code here, to run repeatedly:
   
if(mySerial.available()){
     
       
          byte data;
          data = mySerial.read();
          delay(5);
          if(data=='a'){
            
              
              switch(v1)
              {
                case 'a' : {
                  digitalWrite(motorPin,HIGH);
                  delay(500);
                  break;
                }

                case 'b' : {
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  delay(100);
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  break;
                }

                case 'c' :{
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  delay(100);
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  delay(100);
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  break;
                }
               
              }
               display.clearDisplay();
              display.setTextColor(WHITE);
              display.setTextSize(2);
              display.setCursor(5,15);
              display.print("Name");    
              display.display();
              delay(2000);
              
              
            }
            else if(data == 'b')
            {
              v1 = 'a';
            }
            else if(data == 'c')
            {
              v1 = 'b';
            }
            else if(data == 'd')
            {
              v1 = 'c';
            }
            else if(data == 'e')
            {
              v2 = 'a';
            }
            else if(data == 'f')
            {
              v2 = 'b';
            }
            else if(data == 'g')
            {
              v2 = 'c';
            }
            else if(data == 'h')
            {
              v3 = 'a';
            }
            else if(data == 'i')
            {
              v3 = 'b';
            }
            else if(data == 'j')
            {
              v3 = 'c';
            }
            else
            {
              digitalWrite(motorPin, LOW); 
               bt = (char)data;
              if(!tc)
              {
           
                  myStHr += bt;
                  
                  if(myStHr.length() == 2)
                  {
                    currentHr = myStHr.toInt();
                    tc = 1;
                  }
              }      
               else
              {
                myStmin += bt;
               
                if(myStmin.length() == 2)
                {
                  currentMin = myStmin.toInt();
                  ts=1;
              
                }
             }
            if(ts)
            {
              myStsec += bt;
             
              if(myStsec.length()==2)
              {
                currentSec = millis();
                cs = myStsec.toInt();
                tc=0;
              }
            }
            }
          }
       
     
    value = analogRead(0);
   delay(20);
      Serial.println(value);
      if(value>=60)
      {
        if(hB)
        {
            if(millis()-horn>=200)
            {
              if(value>=120)
              {
                switch(v2)
              {
                case 'a' : {
                  digitalWrite(motorPin,HIGH);
                  delay(500);
                  break;
                }

                case 'b' : {
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  delay(100);
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  break;
                }

                case 'c' :{
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  delay(100);
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  delay(100);
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  break;
                }
               
              }
               
              display.clearDisplay();
              display.setTextColor(WHITE);
              display.setTextSize(2);
              display.setCursor(5,15);
              display.print("Car Horn");    
              display.display();
              delay(2000);
              hB=0;
              }
              else if(value<=80||value>=70)
              {
               
               switch(v3)
              {
                case 'a' : {
                  digitalWrite(motorPin,HIGH);
                  delay(500);
                  break;
                }

                case 'b' : {
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  delay(100);
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  break;
                }

                case 'c' :{
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  delay(100);
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  delay(100);
                  digitalWrite(motorPin,HIGH);
                  delay(100);
                  digitalWrite(motorPin,LOW);
                  break;
                }
              }
              
              display.clearDisplay();
              display.setTextColor(WHITE);
              display.setTextSize(2);
              display.setCursor(5,15);
              display.print("Emergency");    
              display.display();
              delay(2000);
              hB=0;
              }
              else
              {
                hB=0;
               
              }
            }
            
        }
        else
        {
          hB=1;
          horn=millis();
        }
      }
    unsigned long currentMillis = millis()-currentSec;
    unsigned long hr = currentHr  + currentMillis/3600000;
    currentMillis = currentMillis%3600000;
    unsigned long minute = currentMin + currentMillis/60000;
    unsigned long second = currentMillis%60000;
    unsigned long sec = cs+second/1000;
    if(ts)
    {
      if(sec>=60)
    {
      cs=0;
      currentMin = currentMin+1;
      currentSec = millis();
      ts=0;
    }
      
    }
    display.clearDisplay();
    display.setTextColor(WHITE);
    display.setTextSize(2);
    display.setCursor(5,15);
    if( hr <12 ) {
      display.println("AM");
    }
    else if( hr <24 ) {
      display.println("PM");
    }
    else {
      hr = hr -24;
      display.println("AM");
    }
    display.setTextSize(2);
    display.setCursor(5,35);
    if( minute >= 60 ) {
      hr = hr + 1;
      minute = minute - 60;
    }
    display.print((unsigned long)hr);
    display.print(":");
    display.print((unsigned long)minute);
    display.print(":");
    display.print((unsigned long)sec);    
    display.display();
}
