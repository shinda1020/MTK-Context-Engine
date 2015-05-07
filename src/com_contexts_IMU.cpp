#include <jni.h>
#include <iostream>
#include "com_contexts_IMU.h"
using namespace std;
 
JNIEXPORT void JNICALL 
Java_com_contexts_IMU_updateContext(JNIEnv *, jobject){
  cout << "Context updated!\n";
  return;
}
