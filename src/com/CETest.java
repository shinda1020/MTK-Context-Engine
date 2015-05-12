package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.ctxengine.ContextEngine;
import com.ctxengine.sensors.IMU;
import com.ctxengine.sensors.OnBoardSensor;

/**
 * 
 */

/**
 * @author shinda
 * 
 */
public class CETest {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		ContextEngine ce = new ContextEngine();
		ce.startIMU();

		Scanner s = new Scanner(System.in);
		if (s.nextLine() != null) {
			System.out.println("New line");
			ce.stopIMU();
		}

	}

	
}
