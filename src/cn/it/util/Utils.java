package cn.it.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import cn.it.entity.Block;
import cn.it.entity.Config;
import cn.it.entity.ReferencePoint;

public class Utils {
	private static int transferedProgramStartTime; // data文件列表节目开始时间转换为秒
	private static int transferedCorrectingTime;// 校正时间转换为秒
	private static Properties p = new Properties();

	/**
	 * 導入配置文件,另外保存正確的校正后時間對應的秒數
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Config initialConfig(File file) throws FileNotFoundException, IOException {
		p.load(new FileReader(file));
		Config config = new Config();
		//获取config文件中的设置信息
		config.setProgramStartTime(p.getProperty("data.time.programStartTime"));
		config.setCorrectingTime(p.getProperty("data.time.correctingTime"));
		config.setYIndexOfBlockArtistText(Integer.valueOf(p.getProperty("paint.block.YIndexOfBlockArtistText")));
		config.setYIndexOfBlockDurationText(Integer.valueOf(p.getProperty("paint.block.YIndexOfBlockDurationText")));
		config.setYIndexOfTimeMarker(Integer.valueOf(p.getProperty("paint.timeMark.YIndexOfTimeMark")));
		config.setSizeOfArtistText(Integer.valueOf(p.getProperty("paint.block.sizeOfArtistText")));
		config.setSizeOfDurationText(Integer.valueOf(p.getProperty("paint.block.sizeOfDurationText")));
		//获取参照点列表
		ArrayList<ReferencePoint> rpList = new ArrayList<ReferencePoint>();
		Enumeration<?> propertyNames = p.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String name = (String) propertyNames.nextElement();
			if (name.contains("referencePoint.time")) {
				String id = name.substring(name.indexOf('_'));
				String time = p.getProperty(name);
				String pixel = p.getProperty("referencePoint.index" + id);
				rpList.add(new ReferencePoint(id, time, Integer.valueOf(pixel)));
			}
		}
		Collections.sort(rpList);		//列表排序
		calPixelsOfPerSecond(rpList);	//计算正序列表中多个参照点两两之间的PixelsOfPerSecond
		// 在参照点列表排序后添加根据情况添加开始/结束点
		int startTimeOfTimeMaker = transferTime2Second(p.getProperty("paint.timeMark.startTime"));
		int endTimeOfTimeMaker = transferTime2Second(p.getProperty("paint.timeMark.endTime"));
		addRP2List(rpList, startTimeOfTimeMaker, endTimeOfTimeMaker);
		Collections.sort(rpList);
		config.setReferencePointList(rpList);
		transferedProgramStartTime = transferTime2Second(config.getProgramStartTime());
		transferedCorrectingTime = transferTime2Second(config.getCorrectingTime());
		return config;
	}
/**
 * 添加start和end两个参照点,用于扩展时间刻度线的范围
 * @param rpList
 * @param startTimeOfTimeMaker
 * @param endTimeOfTimeMaker
 */
	private static void addRP2List(ArrayList<ReferencePoint> rpList, int startTimeOfTimeMaker, int endTimeOfTimeMaker) {
		ReferencePoint firstRP = rpList.get(0);
		if (startTimeOfTimeMaker < firstRP.getTransferedTime()) {
			ReferencePoint rp = new ReferencePoint();
			rp.setId("startTimeOfTimeMark");
			rp.setTime(p.getProperty("paint.timeMark.startTime"));
			rp.setTransferedTime(startTimeOfTimeMaker);
			rp.setRightPixelsPerSecond(firstRP.getRightPixelsPerSecond());
			int differ = (int) ((firstRP.getTransferedTime() - startTimeOfTimeMaker)
					* firstRP.getRightPixelsPerSecond());
			rp.setPixel(firstRP.getPixel() - differ);
			rpList.add(rp);
		}
		ReferencePoint lastRP = rpList.get(rpList.size() - 1);
		if (endTimeOfTimeMaker > lastRP.getTransferedTime()) {
			ReferencePoint rp = new ReferencePoint();
			rp.setId("endTimeOfTimeMark");
			rp.setTime(p.getProperty("paint.timeMark.endTime"));
			rp.setTransferedTime(endTimeOfTimeMaker);
			rp.setRightPixelsPerSecond(lastRP.getLeftPixelsPerSecond());
			int differ = (int) ((endTimeOfTimeMaker - lastRP.getTransferedTime()) * lastRP.getLeftPixelsPerSecond());
			rp.setPixel(lastRP.getPixel() + differ);
			rpList.add(rp);
		}
	}

	/**
	 * 计算每两个参照点之间单位秒所占的像素数量 设置参照点list中 前提:list按从小到大排序
	 * 
	 * @param list 參照點列表
	 *
	 */
	private static void calPixelsOfPerSecond(List<ReferencePoint> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			float result = calPixelsOfPerSecondBtw2Point(list.get(i), list.get(i + 1));
			list.get(i).setRightPixelsPerSecond(result);
			list.get(i + 1).setLeftPixelsPerSecond(result);
		}
		if (list.get(0).getLeftPixelsPerSecond() == 0.0f) {
			list.get(0).setLeftPixelsPerSecond(list.get(0).getRightPixelsPerSecond());
		}
		if (list.get(list.size() - 1).getRightPixelsPerSecond() == 0.0f) {
			list.get(0).setRightPixelsPerSecond(list.get(list.size() - 1).getLeftPixelsPerSecond());
		}
	}

	private static float calPixelsOfPerSecondBtw2Point(ReferencePoint a, ReferencePoint b) {
		return (float) (a.getPixel() - b.getPixel()) / (float) (a.getTransferedTime() - b.getTransferedTime());
	}

	/**
	 * 導入節目單文件
	 * 
	 * @param data 導入的節目單文件
	 * @return 節目組塊列表
	 * @throws IOException
	 */
	public static List<Block> initialData(File data) throws IOException {
		String[] line = new String[4];
		List<Block> list = new ArrayList<Block>();
		String temp = null;
		BufferedReader br = new BufferedReader(new FileReader(data));
		br.readLine();// 修正时间,忽略首行,从第二行开始读取
		while ((temp = br.readLine()) != null) {
			line = temp.split("<");
			if (line.length >= 4) {
				Block block = new Block(line[0], line[1], line[2]);
				if ("红".equals(line[3]))
					block.setTeam(Block.Team.RED);
				if ("白".equals(line[3]))
					block.setTeam(Block.Team.WHITE);
				if ("黃".equals(line[3]))
					block.setTeam(Block.Team.YELLOW);
				block.setDuration(calDuration(block.getStartTime(), block.getEndTime()));
				list.add(block);
			}
		}
		return list;
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
			int m = Integer.valueOf(buffer[0]).intValue();
			int s = Integer.valueOf(buffer[1]).intValue();
			return m * 60 + s;
		} else if (buffer.length == 3) {
			int h = Integer.valueOf(buffer[0]).intValue();
			int m = Integer.valueOf(buffer[1]).intValue();
			int s = Integer.valueOf(buffer[2]).intValue();
			return h * 60 * 60 + m * 60 + s;
		} else {
			throw new RuntimeException("Error data!");
		}
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

	private static String calDuration(String start, String end) {
		int startSecond = transferTime2Second(start);
		int endSecond = transferTime2Second(end);
		return transferSecond2String(endSecond - startSecond);
	}

	/**
	 * 计算当前时间点(秒数)对应的x坐标
	 * 
	 * @param transferedTime
	 * @param list
	 * @return
	 */
	private static int calXIndex(int transferedTime, List<ReferencePoint> list) {
		ReferencePoint point = getClosetPoint(transferedTime, list);
		float pixelsOfPerSecond;
		if (transferedTime < point.getTransferedTime()) {
			pixelsOfPerSecond = point.getLeftPixelsPerSecond();
		} else {
			pixelsOfPerSecond = point.getRightPixelsPerSecond();
		}
		if (pixelsOfPerSecond <= 0.0f) {
			throw new RuntimeException("ERROR! pixelsOfPerSecond value has wrong");
		}
		return point.getPixel() + Math.round((transferedTime - point.getTransferedTime()) * pixelsOfPerSecond);
	}

	/**
	 * 寻找离当前时间点最靠近的参照点
	 * 
	 * @param time
	 * @param list
	 * @param pixelsOfPerSecond
	 * @return
	 */
	private static ReferencePoint getClosetPoint(int transferedTime, List<ReferencePoint> list) {
		int min = 0;
		int index = 0;
		boolean hasStartRP = false;
		if (list.size() > 2) {
			for (ReferencePoint rp : list) {
				//过滤手工添加的start参照点
				if (rp.getId().contains("start")) {
					hasStartRP = true;
					continue;
				} else {
					min = Math.abs(rp.getTransferedTime() - transferedTime);
					break;
				}
			}
		}
		for (int i = hasStartRP?2:1; i < list.size(); i++) {
			//过滤手工添加的start和end参照点
			if (list.get(i).getId().contains("start") || list.get(i).getId().contains("end")) {
				continue;
			}
			int distance = Math.abs(transferedTime - list.get(i).getTransferedTime());
			if (min > distance) {
				min = distance;
				index = i;
			}
		}
		return list.get(index);
	}

	public static void calStartAndEndIndexOfAllBlock(List<Block> listBlock, List<ReferencePoint> listRP) {
		for (int i = 0; i < listBlock.size(); i++) {
			int correctTransStartTime = transferTime2Second(listBlock.get(i).getStartTime()) - transferedCorrectingTime
					+ transferedProgramStartTime;
			int correctTransEndTime = transferTime2Second(listBlock.get(i).getEndTime()) - transferedCorrectingTime
					+ transferedProgramStartTime;
			listBlock.get(i).setCorrectStartXIndex(calXIndex(correctTransStartTime, listRP));
			listBlock.get(i).setCorrectEndXIndex(calXIndex(correctTransEndTime, listRP));
		}
	}
}
