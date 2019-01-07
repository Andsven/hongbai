package test;

import java.util.ArrayList;
import java.util.HashMap;

import cn.it.util.TimeUtil;

public class CalRealTime {

	
	public static void main(String[] args) {
		
		ArrayList<Item> list = new ArrayList();
		
		list.add(new Item("113:16","VTR开始",false));
		list.add(new Item("114:04","VTR结束，AKB登场",false));
		list.add(new Item("115:39","表演开始",false));
		list.add(new Item("02:14","表演结束",true));
		list.add(new Item("02:23","AKB终了",true));
		
		list.add(new Item("23:26","欅坂登场",true));
		list.add(new Item("24:25","表演开始",true));
		list.add(new Item("27:02","表演结束",true));
		list.add(new Item("27:13","欅坂终了",true));
		
		list.add(new Item("41:04","乃木坂登场",true));
		list.add(new Item("42:12","念信完毕",true));
		list.add(new Item("43:14","表演开始",true));
		list.add(new Item("45:42","表演结束",true));
		list.add(new Item("46:05","乃木坂终了",true));
		for(Item item:list) {
			String time=item.getTimeBefore();
			if(item.isInPart2()) {
				time = TimeUtil.addTimeStringFormat(time,"116:22");
			}
			int correctSecond = TimeUtil.transferTime2Second(time)- TimeUtil.transferTime2Second("104:59") + TimeUtil.transferTime2Second("21:00:00");
			item.setTimeAfter(TimeUtil.transferSecond2String(correctSecond));
		}
		for(Item item:list) {
			System.out.println("["+item.getTimeAfter()+"]"+item.getInfo());
		}
	}
	
	
}
