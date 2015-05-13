package com;

import java.util.Scanner;
import com.ctxengine.ContextEngine;

/**
 * @author shinda
 * 
 */
public class CETest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ContextEngine ce = new ContextEngine();
		ce.startIMU();
		ce.startCamera();

		Scanner s = new Scanner(System.in);
		if (s.nextLine() != null) {
			System.out.println("New line");
			ce.stopIMU();
			ce.stopCamera();
		}

	}

}
