
#include <SPI.h>
#include <Ethernet.h>
#include "LedControl.h"

LedControl lc = LedControl(4, 3, 2, 1);

byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
char server[] = "192.168.1.43";

char success[] = "Status: f";
char failed[] = "Status: s";
int arraySize;

char bufferResponse[] = "Status: b";

boolean found = false;
boolean foundValue = true;
boolean ignoreData = false;

int frame = 0;

unsigned long lastRun = 0;
unsigned long lastCheck = 0;

IPAddress ip(192, 168, 1, 42);

byte on[5][8] = {
  { B00000011,
    B10000111,
    B01000000,
    B01011000,
    B01011000,
    B01000000,
    B10000011,
    B00000011
  },
  { B00000011,
    B10001011,
    B01000000,
    B01011000,
    B01011000,
    B01000000,
    B10000011,
    B00000011
  },
  { B00000011,
    B10010011,
    B01000000,
    B01011000,
    B01011000,
    B01000000,
    B10000011,
    B00000011
  },
  { B00000011,
    B10100011,
    B01000000,
    B01011000,
    B01011000,
    B01000000,
    B10000011,
    B00000011
  }, {
    B00000011,
    B10000011,
    B01000000,
    B01011000,
    B01011000,
    B01000000,
    B10000011,
    B00000011
  }
};

byte off[8] = {
  B00000011,
  B01000011,
  B10000000,
  B10011000,
  B10011000,
  B10000000,
  B01000011,
  B00000011
};


EthernetClient client;

void setup() {

  pinMode(8, OUTPUT);

  lc.shutdown(0, false);
  lc.setIntensity(0, 8);
  lc.clearDisplay(0);

  arraySize = sizeof(success);
  Serial.begin(9600);
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    Ethernet.begin(mac, ip);
  }
  delay(1000);
  Serial.println("connecting...");

  checkServer();


}

void checkServer() {
  if (client.connect(server, 80)) {
    Serial.println("connected");
    client.println("GET /rss_feed/59 HTTP/1.1");
    client.println("Host: 192.168.1.42");
    client.println("Connection: close");
    client.println();
  }
  else {
    Serial.println("connection failed");
  }
}

void turnOn() {

  lc.setRow(0, 7, on[frame][0]);
  lc.setRow(0, 6, on[frame][1]);
  lc.setRow(0, 5, on[frame][2]);
  lc.setRow(0, 4, on[frame][3]);
  lc.setRow(0, 3, on[frame][4]);
  lc.setRow(0, 2, on[frame][5]);
  lc.setRow(0, 1, on[frame][6]);
  lc.setRow(0, 0, on[frame][7]);

  Serial.println(frame);

  if (frame == 4) {
    frame = 0;
  } else {
    frame++;
  }
  lastRun = millis();
}

void turnOff() {
  lc.setRow(0, 7, off[0]);
  lc.setRow(0, 6, off[1]);
  lc.setRow(0, 5, off[2]);
  lc.setRow(0, 4, off[3]);
  lc.setRow(0, 3, off[4]);
  lc.setRow(0, 2, off[5]);
  lc.setRow(0, 1, off[6]);
  lc.setRow(0, 0, off[7]);
}

void addChar(char c) {

  bufferResponse[arraySize - 1] = c;

  for (int i = 1; i < arraySize - 1; i++) {
    bufferResponse[i] = bufferResponse[i + 1];
  }
}

boolean checkFailed() {
  for (int i = 1; i < arraySize - 1; i++) {
    if ( bufferResponse[i] != failed[i]) {
      return false;
    }
  }
  return true;
}

boolean checkSuccess() {
  for (int i = 1; i < arraySize - 1; i++) {
    if ( bufferResponse[i] != success[i]) {
      return false;
    }
  }
  return true;
}

void loop()
{

  if (found && foundValue == false && millis() - lastRun > 1000) {
    turnOn();
  }

  if (client.available()) {
    char c = client.read();

    if (!ignoreData) {
      addChar(c);

      boolean checkF = checkFailed();
      boolean checkS = checkSuccess();

      if (checkF == true) {
        Serial.println();
        Serial.println("FOUND FAILED");
        Serial.println();
        turnOn();


        if (foundValue == true) {
          digitalWrite(8, HIGH);
          delay(1000);
          digitalWrite(8, LOW);

        }

        found = true;
        foundValue = false;
        ignoreData = true;

      }

      if (checkS  == true) {
        Serial.println();
        Serial.println("FOUND SUCCCESS");
        Serial.println();

        turnOff();
        found = true;
        foundValue = true;
        ignoreData = true;
      }
    }


    if (!client.connected()) {
      Serial.println();
      Serial.println("disconnecting.");
      client.stop();
      lastCheck = millis();
    }
  }

  if (!client.connected() && millis() - lastCheck > 20000) {
    Serial.println("reseting ignoredata");
    ignoreData = false;
    checkServer();
  }
}

