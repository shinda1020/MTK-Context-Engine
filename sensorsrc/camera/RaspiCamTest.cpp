/*

 Copyright (c) by Emil Valkov,
 All rights reserved.

 License: http://www.opensource.org/licenses/bsd-license.php

*/

//#include <cv.h>
//#include <highgui.h>
//#include <objdetect.h>

#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/opencv.hpp"

#include <iostream>

using namespace std;
using namespace cv;

#include <stdio.h>
#include <unistd.h>
#include "RaspiCamCV.h"

/** Global variables */
#define CAM_WIDTH  1280
#define CAM_HEIGHT 960
#define SCALE_FACTOR 0.10

String face_cascade_name = "./haarcascade_frontalface_alt.xml";
String eyes_cascade_name = "./haarcascade_eye_tree_eyeglasses.xml";
CascadeClassifier face_cascade;
CascadeClassifier eyes_cascade;
string window_name = "Capture - Face detection";
RNG rng(12345);

/** Function Headers */
void detectAndDisplay(Mat frame);

int prev_face_num = 0;

int main(int argc, char *argv[]){

  // Initialize video writer
  //if (argc != 2) { cout << "Usage: raspicam [filename]" << endl; return -1; }

  //VideoWriter video(string(argv[1]), CV_FOURCC('M','J','P','G'), 15, Size(CAM_WIDTH,CAM_HEIGHT));

  /*
   * Load the cascades
   */

  if( !face_cascade.load( face_cascade_name ) ){ printf("--(!)Error loading\n"); return -1; };
  if( !eyes_cascade.load( eyes_cascade_name ) ){ printf("--(!)Error loading\n"); return -1; };
  
  /*                                                                                                                                                                 
   * Initialize the camera                                                                                                                                           
   */

  RASPIVID_CONFIG * config = (RASPIVID_CONFIG*)malloc(sizeof(RASPIVID_CONFIG));
  
  config->width      = CAM_WIDTH;
  config->height     = CAM_HEIGHT;
  config->bitrate    = 0;	// zero: leave as default
  config->framerate  = 30;
  config->monochrome = 0;
	
  int opt;
	
  while ((opt = getopt(argc, argv, "lxm")) != -1)
    {
      switch (opt)
	{
	case 'l':					// large
	  config->width = 640;
	  config->height = 480;
	  break;
	case 'x':	   				// extra large
	  config->width = 960;
	  config->height = 720;
	  break;
	case 'm':					// monochrome
	  config->monochrome = 1;
	  break;
	default:
	  fprintf(stderr, "Usage: %s [-x] [-l] [-m] \n", argv[0], opt);
	  fprintf(stderr, "-l: Large mode\n");
	  fprintf(stderr, "-x: Extra large mode\n");
	  fprintf(stderr, "-l: Monochrome mode\n");
	  exit(EXIT_FAILURE);
	}
    }

  /*
    Could also use hard coded defaults method: raspiCamCvCreateCameraCapture(0)
  */
  RaspiCamCvCapture * capture = (RaspiCamCvCapture *) raspiCamCvCreateCameraCapture2(0, config); 
  free(config);
	
  CvFont font;
  double hScale=0.4;
  double vScale=0.4;
  int    lineWidth=1;

  cvInitFont(&font, CV_FONT_HERSHEY_SIMPLEX|CV_FONT_ITALIC, hScale, vScale, 0, lineWidth, 8);

  int exit =0;

  do {
    IplImage* image = raspiCamCvQueryFrame(capture);

    /*
    char text[200];
    sprintf(
	    text
	    , "w=%.0f h=%.0f fps=%.0f bitrate=%.0f monochrome=%.0f"
	    , raspiCamCvGetCaptureProperty(capture, RPI_CAP_PROP_FRAME_WIDTH)
	    , raspiCamCvGetCaptureProperty(capture, RPI_CAP_PROP_FRAME_HEIGHT)
	    , raspiCamCvGetCaptureProperty(capture, RPI_CAP_PROP_FPS)
	    , raspiCamCvGetCaptureProperty(capture, RPI_CAP_PROP_BITRATE)
	    , raspiCamCvGetCaptureProperty(capture, RPI_CAP_PROP_MONOCHROME)
	    );
    cvPutText (image, text, cvPoint(05, 40), &font, cvScalar(255, 255, 0, 0));
		
    sprintf(text, "Press ESC to exit");
    cvPutText (image, text, cvPoint(05, 80), &font, cvScalar(255, 255, 0, 0));
		
    cvShowImage("RaspiCamTest", image);
    */

    Mat frame(image);
    //video.write(frame); // Write the frame
    detectAndDisplay(frame);

    char key = cvWaitKey(10);
		
    switch(key)	
      {
      case 27:		// Esc to exit
	exit = 1;
	break;
      case 60:		// < (less than)
	raspiCamCvSetCaptureProperty(capture, RPI_CAP_PROP_FPS, 25);	// Currently NOOP
	break;
      case 62:		// > (greater than)
	raspiCamCvSetCaptureProperty(capture, RPI_CAP_PROP_FPS, 30);	// Currently NOOP
	break;
      }
		
  } while (!exit);

  raspiCamCvReleaseCapture(&capture);
  return 0;
}


/** @function detectAndDisplay */
void detectAndDisplay(Mat frame)
{
  std::vector<Rect> faces;
  Mat frame_gray_raw;
    
  cvtColor( frame, frame_gray_raw, CV_BGR2GRAY );
  equalizeHist( frame_gray_raw, frame_gray_raw );
    
  Mat frame_gray;
  // Need to resize that. Otherwise it's running very slowly!
  resize(frame_gray_raw, frame_gray, cv::Size(), SCALE_FACTOR, SCALE_FACTOR);
    
  //-- Detect faces
  face_cascade.detectMultiScale( frame_gray, faces, 1.1, 2, 0|CV_HAAR_SCALE_IMAGE, Size(30, 30) );
    
  for( size_t i = 0; i < faces.size(); i++ )
    {
      float face_x = faces[i].x + faces[i].width*0.5;
      float face_y = faces[i].y + faces[i].height*0.5;
      Point center( face_x/SCALE_FACTOR, face_y/SCALE_FACTOR );
      ellipse( frame_gray, center, Size( faces[i].width*0.5/SCALE_FACTOR, faces[i].height*0.5/SCALE_FACTOR ), 0, 0, 360, Scalar( 255, 0, 255 ), 4, 8, 0 );
      
      /*  
      Mat faceROI = frame_gray( faces[i] );
      std::vector<Rect> eyes;
      
      //-- In each face, detect eyes
      eyes_cascade.detectMultiScale( faceROI, eyes, 1.1, 2, 0 |CV_HAAR_SCALE_IMAGE, Size(30, 30) );
        
      for( size_t j = 0; j < eyes.size(); j++ )
        {
	  Point center( faces[i].x + eyes[j].x + eyes[j].width*0.5, faces[i].y + eyes[j].y + eyes[j].height*0.5 );
	  int radius = cvRound( (eyes[j].width + eyes[j].height)*0.25 );
	  circle( frame_gray, center, radius, Scalar( 255, 0, 0 ), 4, 8, 0 );
        }
*/
    }

  if (faces.size() > 0 && faces.size() != prev_face_num) {
    printf("face\n");
    fflush(stdout);
  }

  prev_face_num = faces.size();

  //-- Show what you got
  //imshow( window_name, frame_gray );
  imshow( "RGB", frame_gray );
 // imshow( window_name, frame );
}
