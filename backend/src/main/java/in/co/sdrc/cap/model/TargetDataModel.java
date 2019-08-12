package in.co.sdrc.cap.model;

import lombok.Data;

@Data
public class TargetDataModel {
	private Integer indicatorId;
	private Integer areaId;
	private Integer tpId;
	private String value;
	private String filedType;
	private String type;
}
