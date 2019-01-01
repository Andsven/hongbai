package test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import cn.it.util.TimeUtil;

public class MyTest {

	@Test
	public void test1() {
		String a="67:23";
		String b="33:45";
		String[] as = a.split(":");
		String[] bs = b.split(":");
		if(as.length==2 && bs.length==2) {
		int secondSUM  = Integer.valueOf(as[1]) + Integer.valueOf(bs[1]);
		int MinuteSUM= Integer.valueOf(as[0]) + Integer.valueOf(bs[0]);
			if(secondSUM>=60) {
				MinuteSUM+=1;
				secondSUM-=60;
			}
			System.out.println( String.format("%d:%02d", MinuteSUM,secondSUM));
		}
	}
	
	
	public static void main(String[] args) {
		int m = Integer.valueOf("00").intValue();
		System.out.println(m);
	}
	
	@Test
	public void test2() {
		String str="part1";
		int pno = Character.getNumericValue(str.charAt(str.length() - 1));
		System.out.println(pno);
	}
	

}
