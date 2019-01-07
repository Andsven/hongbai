package cn.it.entity;

/**
 * 每组歌手的区间对象
 * 
 * @author Administrator
 *
 */
public class ExportBlock {
	private String startTime;
	private String endTime;
	private String duration;
	private String artist;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public ExportBlock(String startTime, String endTime, String artist, String duration) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.artist = artist;
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "[" + startTime + " - " + endTime + "] [" +duration+"] "+ artist;
	}
}
