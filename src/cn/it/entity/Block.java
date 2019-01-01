package cn.it.entity;

/**
 *每组歌手的区间对象
 * @author Administrator
 *
 */
public class Block {
	private String startTime;
	private String endTime;
	private String artist;
	private Team team;
	private String duration;
	private int correctStartXIndex;
	private int correctEndXIndex;
	public enum Team{
		RED,WHITE,YELLOW,GRAY;
	}

	public Block(String startTime, String endTime, String artist) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.artist = artist;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getArtist() {
		return artist;
	}



	public void setArtist(String artist) {
		this.artist = artist;
	}



	public int getCorrectStartXIndex() {
		return correctStartXIndex;
	}



	public void setCorrectStartXIndex(int correctStartXIndex) {
		this.correctStartXIndex = correctStartXIndex;
	}



	public int getCorrectEndXIndex() {
		return correctEndXIndex;
	}



	public void setCorrectEndXIndex(int correctEndXIndex) {
		this.correctEndXIndex = correctEndXIndex;
	}



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

	public Team getTeam() {
		return team;
	}


	public void setTeam(Team team) {
		this.team = team;
	}



	@Override
	public String toString() {
		return "Block [startTime=" + startTime + ", endTime=" + endTime + ", artist=" + artist + ", team=" + team
				+ ", correctStartXIndex=" + correctStartXIndex + ", correctEndXIndex=" + correctEndXIndex + "]";
	}

	
}
