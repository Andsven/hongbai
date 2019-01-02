package cn.it.entity;

import java.util.List;
import java.util.Map;

public class Config {
	// 节目开始时间(视频中的开始时间)
	private String programStartTime;
	// 校正时间
	private String correctingTime;
	// 参照点列表
	private List<ReferencePoint> referencePointList;
	private int YIndexOfBlockArtistText;
	private int YIndexOfBlockDurationText;
	//上方时间刻度Y坐标
	private int YIndexOfTimeMarker;
	private float transparencyOfRECT;
	private int sizeOfArtistText;
	private int sizeOfDurationText;

	private int numOfDataPart;
	private Map DataPartDurationMap;
	
	public float getTransparencyOfRECT() {
		return transparencyOfRECT;
	}

	public void setTransparencyOfRECT(float transparencyOfRECT) {
		this.transparencyOfRECT = transparencyOfRECT;
	}

	public int getNumOfDataPart() {
		return numOfDataPart;
	}

	public Map getDataPartDurationMap() {
		return DataPartDurationMap;
	}

	public void setDataPartDurationMap(Map dataPartDurationMap) {
		DataPartDurationMap = dataPartDurationMap;
	}

	public void setNumOfDataPart(int numOfDataPart) {
		this.numOfDataPart = numOfDataPart;
	}


	public int getYIndexOfTimeMarker() {
		return YIndexOfTimeMarker;
	}

	public int getSizeOfArtistText() {
		return sizeOfArtistText;
	}

	public void setSizeOfArtistText(int sizeOfArtistText) {
		this.sizeOfArtistText = sizeOfArtistText;
	}

	public int getSizeOfDurationText() {
		return sizeOfDurationText;
	}

	public void setSizeOfDurationText(int sizeOfDurationText) {
		this.sizeOfDurationText = sizeOfDurationText;
	}

	public void setYIndexOfTimeMarker(int yIndexOfTimeMarker) {
		YIndexOfTimeMarker = yIndexOfTimeMarker;
	}

	public String getProgramStartTime() {
		return programStartTime;
	}

	public void setProgramStartTime(String programStartTime) {
		this.programStartTime = programStartTime;
	}

	public String getCorrectingTime() {
		return correctingTime;
	}

	public void setCorrectingTime(String correctingTime) {
		this.correctingTime = correctingTime;
	}


	public List<ReferencePoint> getReferencePointList() {
		return referencePointList;
	}

	public void setReferencePointList(List<ReferencePoint> referencePointList) {
		this.referencePointList = referencePointList;
	}

	public int getYIndexOfBlockArtistText() {
		return YIndexOfBlockArtistText;
	}

	public void setYIndexOfBlockArtistText(int yIndexOfBlockArtistText) {
		YIndexOfBlockArtistText = yIndexOfBlockArtistText;
	}

	public int getYIndexOfBlockDurationText() {
		return YIndexOfBlockDurationText;
	}

	public void setYIndexOfBlockDurationText(int yIndexOfBlockDurationText) {
		YIndexOfBlockDurationText = yIndexOfBlockDurationText;
	}

	
}
