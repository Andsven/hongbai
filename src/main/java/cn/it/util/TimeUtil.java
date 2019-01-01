package cn.it.util;

public class TimeUtil {

	
	public static String addTimeStringFormat(String a, String b) {
		String[] as = a.split(":");
		String[] bs = b.split(":");
		if(as.length==2 && bs.length==2) {
		int secondSUM  = Integer.valueOf(as[1]) + Integer.valueOf(bs[1]);
		int MinuteSUM= Integer.valueOf(as[0]) + Integer.valueOf(bs[0]);
			if(secondSUM>=60) {
				MinuteSUM+=1;
				secondSUM-=60;
			}
			return String.format("%d:%02d", MinuteSUM,secondSUM);
		}
		return null;
	}
	
	public static String transferSecond2String(int seconds) {
		if (seconds < 0) {
			throw new RuntimeException("Error seconds");
		}
		int temp = seconds;
		int hour = temp / 3600;
		temp = temp % 3600;
		int minute = temp / 60;
		int second = temp % 60;
		if (hour > 0) {
			return String.format("%02d:%02d:%02d", hour, minute, second);
		}
		if (minute > 0) {
			return String.format("%2d:%02d", minute, second);
		} else {
			return String.format("%02d", second);
		}
	}
	
	public static String calDuration(String start, String end) {
		int startSecond = transferTime2Second(start);
		int endSecond = transferTime2Second(end);
		return transferSecond2String(endSecond - startSecond);
	}
	/**
	 * 時間字符串换算为当天00点到当前的秒数
	 * 
	 * @param time
	 * @return
	 */
	public static int transferTime2Second(String time) {
		String[] buffer = time.split(":");
		if (buffer.length == 2) {
			int m = Integer.valueOf(buffer[0]);
			int s = Integer.valueOf(buffer[1]);
			return m * 60 + s;
		} else if (buffer.length == 3) {
			int h = Integer.valueOf(buffer[0]);
			int m = Integer.valueOf(buffer[1]);
			int s = Integer.valueOf(buffer[2]);
			return h * 60 * 60 + m * 60 + s;
		} else {
			System.out.println(buffer.length);
			System.out.println(buffer[0]);
			throw new RuntimeException("Error data!");
		}
	}
}
