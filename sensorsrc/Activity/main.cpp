//
//  main.cpp
//  Activity
//
//  Created by Shinda Zeng on 2/14/15.
//  Copyright (c) 2015 Shinda Zeng. All rights reserved.
//

#include <iostream>

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <assert.h>
#include "libfreenect.h"

#include <pthread.h>

#include <opencv2/opencv.hpp>
using namespace cv;

#define _USE_MATH_DEFINES
#include <math.h>

pthread_t freenect_thread;
volatile int die = 0;

pthread_mutex_t gl_backbuf_mutex = PTHREAD_MUTEX_INITIALIZER;

// back: owned by libfreenect (implicit for depth)
// mid: owned by callbacks, "latest frame ready"
// front: owned by GL, "currently being drawn"
uint8_t *depth_mid, *depth_front;
uint8_t *rgb_back, *rgb_mid, *rgb_front;

freenect_context *f_ctx;
freenect_device *f_dev;
int freenect_angle = 0;
int freenect_led;

freenect_video_format requested_format = FREENECT_VIDEO_RGB;
freenect_video_format current_format = FREENECT_VIDEO_RGB;

pthread_cond_t gl_frame_cond = PTHREAD_COND_INITIALIZER;
int got_rgb = 0;
int got_depth = 0;

uint16_t t_gamma[2048];
Mat1s depth(480,640); // 16 bit depth (in millimeters)
Mat1b depth8(480,640);
Mat1b prevDepth8(480,640);
Mat1b diffDepth8(480,640);
Mat3b debug(480, 640); // debug visualization

int totalContour = 0;

int frameCount = 0;

String prevState = "";
String curState = "";

void countActivity()
{
    if (totalContour < 50000) {
        curState = "ActivityNone";
    } else if (totalContour >= 50000 && totalContour < 300000) {
        curState = "ActivityLow";
    } else if (totalContour >= 300000) {
        curState = "ActivityHigh";
    }
    
    if (prevState.compare(curState) != 0) {
        printf("%s\n", curState.c_str());
        fflush(stdout);
    }
    
    prevState = curState;
    
    totalContour = 0;
}

void depth_cb(freenect_device *dev, void *v_depth, uint32_t timestamp)
{
    uint16_t *u_depth = (uint16_t*)v_depth;
    
    pthread_mutex_lock(&gl_backbuf_mutex);
    
    depth.data = (uchar*) u_depth;
    
    // Interpolation & inpainting
    {
        Mat _tmp,_tmp1; //minimum observed value is ~440. so shift a bit
        Mat(depth - 400.0).convertTo(_tmp1,CV_64FC1);
        
        cv::Point minLoc; double minval,maxval;
        minMaxLoc(_tmp1, &minval, &maxval, NULL, NULL);
        _tmp1.convertTo(depth8, CV_8UC1, 255.0/maxval);  //linear interpolation
        
        //use a smaller version of the image
        Mat small_depthf;
        resize(depth8, small_depthf, cv::Size(), 0.2, 0.2);
        
        //inpaint only the "unknown" pixels
        inpaint(small_depthf,(small_depthf == 255),_tmp1,5.0,INPAINT_TELEA);
        
        resize(_tmp1, _tmp, depth8.size());
        _tmp.copyTo(depth8, (depth8 == 255));  //add the original signal back over the inpaint
    }
    
    // Count number of significantly different pixels between frames
    {
        absdiff(depth8, prevDepth8, diffDepth8);
        diffDepth8 = diffDepth8 > 30.0;
        
        totalContour += cv::countNonZero(diffDepth8);
        
        depth8.copyTo(prevDepth8);
    }
    
    // Update frame count.
    // If it reaches threshold value, call countActivity
    frameCount++;
    
    if (frameCount >= 5) {
        countActivity();
        frameCount = 0;
    }
    
    // Slow down the FPS
    usleep(100000);
    
    got_depth++;
    pthread_cond_signal(&gl_frame_cond);
    pthread_mutex_unlock(&gl_backbuf_mutex);
}

void *freenect_threadfunc(void *arg)
{
    int accelCount = 0;
    
    freenect_set_tilt_degs(f_dev,freenect_angle);
    freenect_set_led(f_dev,LED_RED);
    freenect_set_depth_callback(f_dev, depth_cb);
    freenect_set_depth_mode(f_dev, freenect_find_depth_mode(FREENECT_RESOLUTION_MEDIUM, FREENECT_DEPTH_11BIT));
    
    freenect_start_depth(f_dev);
    
    while (!die && freenect_process_events(f_ctx) >= 0) {
        //Throttle the text output
        if (accelCount++ >= 2000)
        {
            accelCount = 0;
            freenect_raw_tilt_state* state;
            freenect_update_tilt_state(f_dev);
            state = freenect_get_tilt_state(f_dev);
            double dx,dy,dz;
            freenect_get_mks_accel(state, &dx, &dy, &dz);
        }
    }
    
    printf("\nshutting down streams...\n");
    
    freenect_stop_depth(f_dev);
    
    freenect_close_device(f_dev);
    freenect_shutdown(f_ctx);
    
    printf("-- done!\n");
    return NULL;
}


int main(int argc, const char * argv[]) {
    
    int res;
    
//    namedWindow("depth");
//    namedWindow("prevdepth");
//    namedWindow("diffdepth");
    
    depth_mid = (uint8_t*)malloc(640*480*3);
    depth_front = (uint8_t*)malloc(640*480*3);
    rgb_back = (uint8_t*)malloc(640*480*3);
    rgb_mid = (uint8_t*)malloc(640*480*3);
    rgb_front = (uint8_t*)malloc(640*480*3);
    
    int i;
    for (i=0; i<2048; i++) {
        float v = i/2048.0;
        v = powf(v, 3)* 6;
        t_gamma[i] = v*6*256;
    }
    
    if (freenect_init(&f_ctx, NULL) < 0) {
        exit(EXIT_FAILURE);
    }
    
    freenect_set_log_level(f_ctx, FREENECT_LOG_DEBUG);
    freenect_select_subdevices(f_ctx, (freenect_device_flags)(FREENECT_DEVICE_MOTOR | FREENECT_DEVICE_CAMERA));
    
    int nr_devices = freenect_num_devices (f_ctx);
    
    int user_device_number = 0;
    
    if (nr_devices < 1) {
        freenect_shutdown(f_ctx);
        exit(EXIT_FAILURE);
    }
    
    if (freenect_open_device(f_ctx, &f_dev, user_device_number) < 0) {
        freenect_shutdown(f_ctx);
        exit(EXIT_FAILURE);
    }
    
    res = pthread_create(&freenect_thread, NULL, freenect_threadfunc, NULL);
    if (res) {
        freenect_shutdown(f_ctx);
        exit(EXIT_FAILURE);
    }
    
    pthread_join(freenect_thread, NULL);

    return 0;
}
