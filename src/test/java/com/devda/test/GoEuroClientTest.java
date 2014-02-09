package com.devda.test;

import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

import com.devda.java.GoEuroClient;

public class GoEuroClientTest {
	@SuppressWarnings("static-access")
	@Test
	public void testGoEuro(){
		GoEuroClient euroClient = new GoEuroClient();
		System.out.println("Enter same string two times");
		System.out.println("Enter first string");
		Scanner scanner = new Scanner(System.in);
		Assert.assertEquals("Same String from keyboard must be Same",scanner.next(), euroClient.getString());
		scanner.close();
	}
}
