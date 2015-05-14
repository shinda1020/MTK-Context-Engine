# Source files
SRC = I2Cdev.cpp MPU6050.cpp imu.cpp

# Object files
OBJS = $(SRC:.cpp=.o)

# Compiler definition and flags
GCC = g++
CXXFLAGS = -DDMP_FIFO_RATE=9 -Wall -g -O2 

TARGET = IMU

all : $(OBJS) main.o
	$(GCC) -o $(TARGET) $(OBJS) main.o -lpthread

%.o : %.cpp
	$(GCC) -c $< -o $@

main.o : main.c
	$(GCC) -c main.c

clean:
	rm *.o $(TARGET)

.PHONY : clean