package cn.it.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TimeUtil {

	/**
	 * 把两个分秒格式的时间相加
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static String addTimeStringFormat(String a, String b) {
		String[] as = a.split(":");
		String[] bs = b.split(":");
		if (as.length == 2 && bs.length == 2) {
			int secondSUM = Integer.valueOf(as[1]) + Integer.valueOf(bs[1]);
			int MinuteSUM = Integer.valueOf(as[0]) + Integer.valueOf(bs[0]);
			if (secondSUM >= 60) {
				MinuteSUM += 1;
				secondSUM -= 60;
			}
			return String.format("%d:%02d", MinuteSUM, secondSUM);
		}
		return null;
	}

	/**
	 * 秒数转换为时分秒格式字符串
	 * 
	 * @param seconds
	 * @return
	 */
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
	 * 時間字符串换算为当天00点到当前时间点的秒数
	 * 
	 * @param time 要转换为秒数的时间（时：分：秒或分：秒）
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

	/**
	 * 如果视频有分P，当前时间需要加上前几P的总共时长
	 * 
	 * @param time    当前P的时间点
	 * @param partNum 所在的P数
	 * @param map     保存所有分P的时间长度
	 * @return 合计的时间点（分：秒）
	 */
	public static String calTimeAddPartDuration(String time, String partNum, Map map) {
		String result = time;
		int pno = Utils.getLastNumFromString(partNum);
		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = (Entry<String, String>) iterator.next();
			int no = Utils.getLastNumFromString(entry.getKey());
			if (pno > no) {
				result = TimeUtil.addTimeStringFormat(result, entry.getValue());
			}
		}
		return result;
	}
}
