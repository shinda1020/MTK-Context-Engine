
JNI_HDRS = "-I/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers"

all : test

test : imu.o
	g++ -framework JavaVM -o test imu.o

imu.o : imu.cpp
	g++ $(JNI_HDRS) -c imu.cpp

clean :
	rm *.o test

.PHONY : clean