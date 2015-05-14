#ifndef _IMU_HPP_
#define _IMU_HPP_  

#ifdef __cplusplus
#define EXTERNC extern "C"
#else
#define EXTERNC
#endif


/* External function for context engine to start the IMU service */
EXTERNC void start_imu();

#endif /* _IMU_HPP_ */
