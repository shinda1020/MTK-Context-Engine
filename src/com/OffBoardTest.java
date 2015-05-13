/**
 * 
 */
package com;

import java.util.Scanner;

import com.ctxengine.ContextEngine;
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

		Activity act = new Activity("localhost");
		act.startSensor();

		Scanner s = new Scanner(System.in);
		if (s.nextLine() != null) {
			System.out.println("New line");
			act.stopSensor();
		}
	}

}
