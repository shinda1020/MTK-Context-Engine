# The library paths
LIB_PATH = /usr/local/Cellar/libfreenect/0.5.1/lib/ /opt/local/lib
# The libraries
LIBS = -lopencv_core -lopencv_highgui -lopencv_photo -lopencv_imgproc -lfreenect

# The include paths
INCLUDE_PATH = /usr/local/include/libfreenect /opt/local/include/

# Build flags
LDFLAGS = $(foreach d, $(LIB_PATH), -L$d)
CFLAGS  = $(foreach d, $(INCLUDE_PATH), -I$d)

activity : main.cpp
	g++ $(LDFLAGS) $(LIBS) $(CFLAGS) $< -o $@
	mv ./activity ../../sensorbin

clean :
	rm activity

.PHONY : clean
