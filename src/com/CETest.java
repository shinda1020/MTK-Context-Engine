package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
		System.out.println("Context engine started");

//		Runtime rt = Runtime.getRuntime();
		Process pr = Runtime.getRuntime().exec("sensorbin/test");

		InputStream stdin = pr.getInputStream();
		InputStreamReader isr = new InputStreamReader(stdin);
		BufferedReader br = new BufferedReader(isr);

		String line = null;
		while ((line = br.readLine()) != null)
			System.out.println(line);
		
		pr.waitFor();
	}

}
