/**
 * 
 */
package com;

import java.util.Scanner;

import com.ctxengine.ContextEngine;

/**
 * @author shinda
 * 
 */
public class OnBoardTest {

	/**
	 * 
	 */
	public OnBoardTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ContextEngine ce = new ContextEngine();
		ce.startActivity();
		// ce.startIMU();
		// ce.startCamera();

		Scanner s = new Scanner(System.in);
		if (s.nextLine() != null) {
			System.out.println("New line");
			ce.stopActivity();
			// ce.stopIMU();
			// ce.stopCamera();
		}
	}

}
