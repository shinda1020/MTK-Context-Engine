#include <jni.h>
#include <iostream>

using namespace std;

JNIEnv* create_vm(JavaVM ** jvm) {
    
    JNIEnv *env;
    JavaVMInitArgs vm_args;
    
    JavaVMOption options;
    //Path to the java source code
    options.optionString = "-Djava.class.path=./src";
    vm_args.version = JNI_VERSION_1_6; //JDK version. This indicates version 1.6
    vm_args.nOptions = 1;
    vm_args.options = &options;
    vm_args.ignoreUnrecognized = 0;
    
    int ret = JNI_CreateJavaVM(jvm, (void**)&env, &vm_args);
    if(ret < 0)
        printf("\nUnable to Launch JVM\n");
    return env;
}

int main(int args, char** argv)
{
    
    JavaVM* jvm;
    
    JNIEnv* env = create_vm(&jvm);
    
    jclass myClass = env->FindClass("com.contexts.IMU");
    jmethodID mid = env->GetStaticMethodID(myClass, "updateContext", "(I)I");
    
    env->CallStaticIntMethod(myClass, mid, (jint)10);
    
}