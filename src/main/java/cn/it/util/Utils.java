package cn.it.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import cn.it.entity.Block;
import cn.it.entity.Config;
import cn.it.entity.ReferencePoint;

public class Utils {
	private static int transferedProgramStartTime; // data文件列表节目开始时间转换为秒
	private static int transferedCorrectingTime;// 校正时间转换为秒
	private static Properties p = new Properties();
	public static Config config = new Config();

	/**
	 * 導入配置文件,另外保存正確的校正后時間對應的秒數
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void initialConfig(File file) throws FileNotFoundException, IOException {
		p.load(new FileReader(file));
		// 获取config文件中的设置信息
		config.setProgramStartTime(p.getProperty("data.time.programStartTime"));
		config.setCorrectingTime(p.getProperty("data.time.correctingTime"));
		config.setNumOfDataPart(Integer.valueOf(p.getProperty("data.time.numOfPart")));
		if (config.getNumOfDataPart() > 1) {
			config.setDataPartDurationMap(new HashMap());
		}

		config.setYIndexOfTimeMarker(Integer.valueOf(p.getProperty("paint.timeMark.YIndexOfTimeMark")));

		config.setYIndexOfBlockArtistText(Integer.valueOf(p.getProperty("paint.block.YIndexOfBlockArtistText")));
		config.setYIndexOfBlockDurationText(Integer.valueOf(p.getProperty("paint.block.YIndexOfBlockDurationText")));
		config.setSizeOfArtistText(Integer.valueOf(p.getProperty("paint.block.sizeOfArtistText")));
		config.setSizeOfDurationText(Integer.valueOf(p.getProperty("paint.block.sizeOfDurationText")));
		config.setTransparencyOfRECT(Float.valueOf(p.getProperty("paint.block.transparencyOfRECT")));
		
		// 获取参照点列表
		ArrayList<ReferencePoint> rpList = new ArrayList<ReferencePoint>();
		Enumeration<?> propertyNames = p.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String name = (String) propertyNames.nextElement();
			// 载入参照点坐标
			if (name.contains("referencePoint.time")) {
				String id = name.substring(name.indexOf('_'));
				String time = p.getProperty(name);
				String pixel = p.getProperty("referencePoint.index" + id);
				rpList.add(new ReferencePoint(id, time, Integer.valueOf(pixel)));
			}
			// 载入分段视频的长度
			if (config.getDataPartDurationMap() != null && name.contains("data.time.part")) {
				config.getDataPartDurationMap().put(name.substring(name.lastIndexOf(".")+1), p.getProperty(name));
			}
		}
		Collections.sort(rpList); // 列表排序
		calPixelsOfPerSecond(rpList); // 计算正序列表中多个参照点两两之间的PixelsOfPerSecond
		// 在参照点列表排序后添加根据情况添加开始/结束点
		addRP2List(rpList, p.getProperty("paint.timeMark.startTime"), p.getProperty("paint.timeMark.endTime"));
		Collections.sort(rpList);
		config.setReferencePointList(rpList);
		// 设置静态属性
		transferedProgramStartTime = TimeUtil.transferTime2Second(config.getProgramStartTime());
		transferedCorrectingTime = TimeUtil.transferTime2Second(config.getCorrectingTime());
	}

	/**
	 * 添加start和end两个参照点,用于扩展时间刻度线的范围
	 * 
	 * @param rpList
	 * @param startTimeOfTimeMaker
	 * @param endTimeOfTimeMaker
	 */
	private static void addRP2List(ArrayList<ReferencePoint> rpList, String startTimeOfTimeMakerStr, String endTimeOfTimeMakerStr) {
		ReferencePoint firstRP = rpList.get(0);
		int startTimeOfTimeMaker=TimeUtil.transferTime2Second(startTimeOfTimeMakerStr);
		int endTimeOfTimeMaker=TimeUtil.transferTime2Second(endTimeOfTimeMakerStr);
		if (startTimeOfTimeMaker < firstRP.getTransferedTime()) {
			ReferencePoint rp = new ReferencePoint();
			rp.setId("startTimeOfTimeMark");
			rp.setTime(startTimeOfTimeMakerStr);
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
			rp.setTime(endTimeOfTimeMakerStr);
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
		if (list.get(0).getLeftPixelsPerSecond() <= 0.0f) {
			list.get(0).setLeftPixelsPerSecond(list.get(0).getRightPixelsPerSecond());
		}
		if (list.get(list.size() - 1).getRightPixelsPerSecond() <= 0.0f) {
			list.get(list.size() - 1).setRightPixelsPerSecond(list.get(list.size() - 1).getLeftPixelsPerSecond());
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
		List<Block> list = new ArrayList<Block>();
		String temp = null;
		BufferedReader br = new BufferedReader(new FileReader(data));
		while ((temp = br.readLine()) != null) {
			if(temp.startsWith("#")) {
				continue;
			}
			String[] line = temp.split("<");
			Block block=null;
			if (line.length == 5) { // 包含part1参数
				String startTotal = calTimeAddPartDuration(line[0], line[4], config.getDataPartDurationMap());
				String endTotal = calTimeAddPartDuration(line[1], line[4], config.getDataPartDurationMap());

				block = new Block(startTotal, endTotal, line[2]);
			} else if(line.length==4){
				block = new Block(line[0], line[1], line[2]);
			}
			if ("红".equals(line[3]))
				block.setTeam(Block.Team.RED); // 红组
			if ("白".equals(line[3]))
				block.setTeam(Block.Team.WHITE); // 白组
			if ("黃".equals(line[3]))
				block.setTeam(Block.Team.YELLOW); // 非红白组歌手
			if ("绿".equals(line[3]))
				block.setTeam(Block.Team.GREEN); // 企划、新闻
			block.setDuration(TimeUtil.calDuration(block.getStartTime(), block.getEndTime()));
			list.add(block);
		}
		return list;
	}

	private static String calTimeAddPartDuration(String time, String partNum, Map map) {
		String result = time;
		if (map.get(partNum) != null) {
			int pno = Character.getNumericValue(partNum.charAt(partNum.length() - 1));
			Iterator iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> entry = (Entry<String, String>) iterator.next();
				String key =  entry.getKey();
				int no = Character.getNumericValue(key.charAt(key.length() - 1));
				if (pno > no) {
					result = TimeUtil.addTimeStringFormat(result, entry.getValue());
				}
			}
		}
		return result;
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
				// 过滤手工添加的start参照点
				if (rp.getId().contains("start")) {
					hasStartRP = true;
					continue;
				} else {
					min = Math.abs(rp.getTransferedTime() - transferedTime);
					break;
				}
			}
		}
		for (int i = hasStartRP ? 2 : 1; i < list.size(); i++) {
			// 过滤手工添加的start和end参照点
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
			int correctTransStartTime = TimeUtil.transferTime2Second(listBlock.get(i).getStartTime())
					- transferedCorrectingTime + transferedProgramStartTime;
			int correctTransEndTime = TimeUtil.transferTime2Second(listBlock.get(i).getEndTime())
					- transferedCorrectingTime + transferedProgramStartTime;
			listBlock.get(i).setCorrectStartXIndex(calXIndex(correctTransStartTime, listRP));
			listBlock.get(i).setCorrectEndXIndex(calXIndex(correctTransEndTime, listRP));
		}
	}

}
