package in.co.sdrc.cap.mobile.model;

public class AreaLevel {
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public boolean isStateAvailable() {
		return isStateAvailable;
	}
	public void setStateAvailable(boolean isStateAvailable) {
		this.isStateAvailable = isStateAvailable;
	}
	public boolean isDistrictAvailable() {
		return isDistrictAvailable;
	}
	public void setDistrictAvailable(boolean isDistrictAvailable) {
		this.isDistrictAvailable = isDistrictAvailable;
	}
	private int id;
	private String name;
	private int level;
	private boolean isStateAvailable;
	private boolean isDistrictAvailable;
	
}
