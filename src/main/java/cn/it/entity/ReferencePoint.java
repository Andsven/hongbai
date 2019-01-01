package cn.it.entity;

import cn.it.util.TimeUtil;

/**
 * 参照点对象
 * @author Administrator
 *
 */
public class ReferencePoint implements Comparable<ReferencePoint>{

	
	private String id;
	//参照坐标对应的时间
	private String time;
	//参照坐标在原始走势图中的x坐标
	private int pixel;
	private int transferedTime;
	private float leftPixelsPerSecond=-1.0f;
	private float rightPixelsPerSecond=-1.0f;
	
	public ReferencePoint() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ReferencePoint(String id, String time, int pixel) {
		this.id = id;
		this.time = time;
		this.pixel = pixel;
		transferedTime=TimeUtil.transferTime2Second(time);
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getPixel() {
		return pixel;
	}
	public void setPixel(int pixel) {
		this.pixel = pixel;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getTransferedTime() {
		return transferedTime;
	}
	public void setTransferedTime(int transferedTime) {
		this.transferedTime = transferedTime;
	}
	@Override
	public int compareTo(ReferencePoint o) {
		return this.getTransferedTime()-o.getTransferedTime();
	}
	public float getLeftPixelsPerSecond() {
		return leftPixelsPerSecond;
	}
	public void setLeftPixelsPerSecond(float leftPixelsPerSecond) {
		this.leftPixelsPerSecond = leftPixelsPerSecond;
	}
	public float getRightPixelsPerSecond() {
		return rightPixelsPerSecond;
	}
	public void setRightPixelsPerSecond(float rigthPixelsPerSecond) {
		this.rightPixelsPerSecond = rigthPixelsPerSecond;
	}
}
