package in.co.sdrc.cap.mobile.model;

import java.util.List;



public class Area {

	private int id;
	
	private String areaname;
	
	private String code;
	
	private String parentAreaCode;
	
	private List<AreaLevel> areaLevel;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getParentAreaCode() {
		return parentAreaCode;
	}

	public void setParentAreaCode(String parentAreaCode) {
		this.parentAreaCode = parentAreaCode;
	}

	public List<AreaLevel> getAreaLevel() {
		return areaLevel;
	}

	public void setAreaLevel(List<AreaLevel> areaLevel) {
		this.areaLevel = areaLevel;
	}

}
