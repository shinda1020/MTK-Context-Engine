# Face Detection w/ Raspberry Pi Camera

## Dependencies

This sensor service is done with Robidouille's RaspiCamCV. For detailed information on dependencies and how to build the code, see the website below:

https://github.com/robidouille/robidouille/tree/master/raspicam_cv

## How to build

Besides the prerequisites mentioned above, you also need "Userland". The detailed setup can be found from the website below:

https://thinkrpi.wordpress.com/2013/05/22/opencvpi-cam-step-2-compilation/

Once you have configured userland, edit the Makefile to change USERLAND_ROOT to corresponding directory on your Pi. Then type "make" and the binary file would be moved to "sensorbin" directory automatically.

## Acknowledgement

Special thanks to Robidouille's RaspiCamCV code!
