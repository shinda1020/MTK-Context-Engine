# Activity sensing

This is the activity sensing module with Kinect depth sensor. At this moment, this module is implemented on Mac platform.

## How it works

This module reads depth data from the Kinect depth sensor and smoothes the image using impainting. Then the program computes number of significantly different pixels between frames, and uses this number to infer activity level (none, low, high).

## Dependencies

This program requires libopencv_core, libopencv_imgproc, libopencv_photo and libopencv_highgui (for debugging) from OpenCV. Also, it requires libfreenect to allow Mac to work with Kinect.

## How to build

To build this program, first edit Makefile to configure search paths for includes and libraries. Then run "make" to build it. The sensor binary would automatically be moved to the "sensorbin" directory.
