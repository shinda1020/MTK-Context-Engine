#include <iostream>
#include <cstdlib>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdint.h>
#include <math.h>
#include "I2Cdev.h"
#include "MPU6050_6Axis_MotionApps20.h"
#include <time.h>
#include <sys/time.h>
#include <fcntl.h>
#include "imu.hpp"

#ifdef __cplusplus
extern "C" {
#endif

#include <pthread.h>

#ifdef __cplusplus
}
#endif

// ================================================================
// ===                   VIRABLE DECLARATION                    ===
// ================================================================

// Shake detection related constants
#define MIN_SHAKE_COUNT  10
#define SHAKE_THRESHOLD  6000.0f
#define INTERVAL_THRESHOLD  1000  // the interval between two shaking should be 1 second

// class default I2C address is 0x68
// specific I2C addresses may be passed as a parameter here
// AD0 low = 0x68 (default for SparkFun breakout and InvenSense evaluation board)
// AD0 high = 0x69
MPU6050 mpu;

// MPU control/status vars
bool dmpReady;  // set true if DMP init was successful
uint8_t mpuIntStatus;   // holds actual interrupt status byte from MPU
uint8_t devStatus;      // return status after each device operation (0 = success, !0 = error)
uint16_t packetSize;    // expected DMP packet size (default is 42 bytes)
uint16_t fifoCount;     // count of all bytes currently in FIFO
uint8_t fifoBuffer[64]; // FIFO storage buffer

// orientation/motion vars
Quaternion q;           // [w, x, y, z]         quaternion container
VectorInt16 aa;         // [x, y, z]            accel sensor measurements
VectorInt16 aaReal;     // [x, y, z]            gravity-free accel sensor measurements
VectorInt16 aaWorld;    // [x, y, z]            world-frame accel sensor measurements
VectorFloat gravity;    // [x, y, z]            gravity vector
float euler[3];         // [psi, theta, phi]    Euler angle container
float ypr[3];           // [yaw, pitch, roll]   yaw/pitch/roll container and gravity vector

// shake detection vars
int shakeCount;
long prevTime;

// loop control
bool stopLoop; // set true if destructor is called to quit infinite loops

// IMU thread
pthread_t imu_thread;


// ================================================================
// ===                  FUNCTION DECLARATION                    ===
// ================================================================

EXTERNC void start_imu();
void setup();
void loop();
void shake_detected();
long get_current_time();
void* start_imu_thread(void *arg);  // This is the IMU thread function


// ================================================================
// ===                 FUNCTION DEFINITION                      ===
// ================================================================

/**
 * This is the extern function for the context engine to start
 * the IMU sensing service. Since there is an infinite loop in the
 * IMU service, this function primarily starts an IMU thread to
 * avoid blocking the main thread.
 */

EXTERNC void start_imu()
{
  // Terminate the IMU thread if exists
  if (imu_thread) {
    pthread_cancel(imu_thread);
  }

  // Create a new thread for IMU sensing
  if (pthread_create(&imu_thread, NULL, &start_imu_thread, NULL) != 0) {
    printf("Error creating IMU thread\n");
  }

  pthread_join(imu_thread, NULL);
}

/**
 * This is the thread function that sets up the IMU module and
 * configures it to run on loops
 */
void* start_imu_thread(void *arg)
{
  // Local variable initialization
  shakeCount = 0;
  prevTime = 0;
  dmpReady = false;
  stopLoop = false;

  setup();
  usleep(100000);
  while(!stopLoop)
    loop();
}

/**
 * This event to handle shaken gesture. Here we call the extern
 * function from context engine to update the context
 */
void shake_detected()
{
  printf("shake\n");
  fflush(stdout);
}

/**
 * The setup function configures the IMU module and initiates parameters
 */
void setup()
{
  // initialize device
  printf("Initializing I2C devices...\n");
  mpu.initialize();

  // verify connection
  printf("Testing device connections...\n");
  printf(mpu.testConnection() ? "MPU6050 connection successful\n" : "MPU6050 connection failed\n");

  // load and configure the DMP
  printf("Initializing DMP...\n");
  devStatus = mpu.dmpInitialize();

  // make sure it worked (returns 0 if so)
  if (devStatus == 0) {
    // turn on the DMP, now that it's ready
    printf("Enabling DMP...\n");
    mpu.setDMPEnabled(true);

    // enable Arduino interrupt detection
    //Serial.println(F("Enabling interrupt detection (Arduino external interrupt 0)..."));
    //attachInterrupt(0, dmpDataReady, RISING);
    mpuIntStatus = mpu.getIntStatus();

    // set our DMP Ready flag so the main loop() function knows it's okay to use it
    printf("DMP ready!\n");
    dmpReady = true;

    // get expected DMP packet size for later comparison
    packetSize = mpu.dmpGetFIFOPacketSize();
  } else {
    // ERROR!
    // 1 = initial memory load failed
    // 2 = DMP configuration updates failed
    // (if it's going to break, usually the code will be 1)
    printf("DMP Initialization failed (code %d)\n", devStatus);
  }
}

/**
 * The main loop of the IMU service
 */
void loop()
{
  // if programming failed, don't try to do anything
  if (!dmpReady) return;
  // get current FIFO count
  fifoCount = mpu.getFIFOCount();

  if (fifoCount == 1024) {
    // reset so we can continue cleanly
    mpu.resetFIFO();
    printf("FIFO overflow!\n");

    // otherwise, check for DMP data ready interrupt (this should happen frequently)
  } else if (fifoCount >= 42) {
    // read a packet from FIFO
    mpu.getFIFOBytes(fifoBuffer, packetSize);

    mpu.dmpGetQuaternion(&q, fifoBuffer);
    mpu.dmpGetGravity(&gravity, &q);
    mpu.dmpGetAccel(&aa, fifoBuffer);
    mpu.dmpGetLinearAccel(&aaReal, &aa, &gravity);
    mpu.dmpGetLinearAccelInWorld(&aaWorld, &aaReal, &q);

#define sqr(a)    a*a

    float sqrSum = sqrt( sqr(aaWorld.x) + sqr(aaWorld.y) + sqr(aaWorld.z) );
    if (sqrSum >= SHAKE_THRESHOLD) {

      shakeCount++;

      if (shakeCount >= MIN_SHAKE_COUNT) {

	long curTime = get_current_time();

	if ( abs(curTime - prevTime) > INTERVAL_THRESHOLD ) {

	  // This is where we handle the shaken even.
	  shake_detected();
	}

	prevTime = curTime;
	shakeCount = 0;
      }
    } else {
      shakeCount = 0;
    }
  }
}

/**
 * Get the current time and converts into number of mini-seconds.
 */
long get_current_time()
{
  struct timeval tv;
  gettimeofday(&tv, NULL);
  return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}
