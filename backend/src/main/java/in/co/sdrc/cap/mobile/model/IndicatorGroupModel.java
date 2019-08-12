package in.co.sdrc.cap.mobile.model;

import java.util.List;

import lombok.Data;

@Data
public class IndicatorGroupModel {
	
	private String indicatorName;
	private Integer indicatorId;
	private String indicatorValue;
//	private String indicatorGroupName;
	private String timeperiod;
	private String periodicity;
	private Integer timeperiodId;
	private List<String> chartsAvailable;
	private String align;
//	private String cardType;
	private String unit;
//	private String chartAlign;
	private String chartGroup;
	private String source;
	private Integer departmentId;
	private String departmentName;
	private Integer sectorId;
	private String sectorName;
}
