# IMU Shake Detection

This module is implemented to detect shake gesture on Raspberry Pi.

## How to setup

To interface an IMU (MPU6050 or GY-521 in this case), check the following website for detailed information.

http://blog.bitify.co.uk/2013/11/interfacing-raspberry-pi-and-mpu-6050.html

## How to build

Once you have done all the things mentioned in the above page, run "make" in command line to build this module. However, you might need "sudo" privilege to access the IMU from the code. That is because you have not granted the user with the authority to access IMU. To fix this issue, run "sudo adduser pi i2c" to add user to I2C group to run IMU from the code.

## Acknowledgement

Special thanks to Richard Hirst's implementation on reading the IMU data. The full git repository can be access via:

https://github.com/richardghirst/PiBits/tree/master/MPU6050-Pi-Demo
