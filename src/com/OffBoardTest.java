/**
 * 
 */
package com;

import java.util.Scanner;

import com.ctxengine.sensors.offboard.Activity;

/**
 * @author shinda
 * 
 */
public class OffBoardTest {

	/**
	 * 
	 */
	public OffBoardTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Activity act1 = new Activity("localhost");
		Activity act2 = new Activity("localhost");
		act1.startSensor();
		act2.startSensor();

		Scanner s = new Scanner(System.in);
		if (s.nextLine() != null) {
			System.out.println("New line");
			act1.stopSensor();
			act2.stopSensor();
		}
	}

}
