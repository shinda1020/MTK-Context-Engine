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
		
		OnBoardSensor imu = new IMU(ce);
		imu.startSensor();
		
		Scanner s = new Scanner(System.in);
		if (s.nextLine() != null) {
			System.out.println("New line");
			imu.stopSensor();
		}
		
//		imu.startSensor();
		
//		this.shutdown();
		
		
//		while(true){}
		
//		pr.waitFor();
		
//		Process process = Runtime.getRuntime().exec("sensorbin/test");
//		LogStreamReader lsr = new LogStreamReader(process.getInputStream());
//		Thread thread = new Thread(lsr, "LogStreamReader");
//		thread.start();
		
	}


	public static class LogStreamReader implements Runnable {

	    private BufferedReader reader;

	    public LogStreamReader(InputStream is) {
	        this.reader = new BufferedReader(new InputStreamReader(is));
	    }

	    public void run() {
	        try {
	            String line = reader.readLine();
	            while (line != null) {
	                System.out.println(line);
	                line = reader.readLine();
	            }
	            reader.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

}
