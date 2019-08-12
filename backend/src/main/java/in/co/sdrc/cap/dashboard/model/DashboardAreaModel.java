package in.co.sdrc.cap.dashboard.model;

import lombok.Data;

@Data
public class DashboardAreaModel {

	private String areaname;
	private String code;
	private String parentAreaCode;
	private String areaLevelName;
	private Integer areaLevelId;
	private Integer areaId;
}
