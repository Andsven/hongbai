package test;

public class Item {

	
	private String info;
	private String timeBefore;
	private String timeAfter;
	private boolean inPart2;
	public Item() {
		super();
	}
	public Item(String timeBefore, String info,boolean inPart2) {
		super();
		this.info = info;
		this.timeBefore = timeBefore;
		this.inPart2=inPart2;
	}
	
	public boolean isInPart2() {
		return inPart2;
	}
	public void setInPart2(boolean inPart2) {
		this.inPart2 = inPart2;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getTimeBefore() {
		return timeBefore;
	}
	public void setTimeBefore(String timeBefore) {
		this.timeBefore = timeBefore;
	}
	public String getTimeAfter() {
		return timeAfter;
	}
	public void setTimeAfter(String timeAfter) {
		this.timeAfter = timeAfter;
	}
	@Override
	public String toString() {
		return "Item [info=" + info + ", timeBefore=" + timeBefore + ", timeAfter=" + timeAfter + "]";
	}
	
}
