package in.co.sdrc.cap.model;

import lombok.Data;


/**
 * @author Subham Ashish(subham@sdrc.co.in)
 *
 */
@Data
public class TargetEventModel {

	private Integer areaId;
	
	private String dataValue;
	
	private String target;
	
	private Integer indicatorId;
	
	private Integer timePeriodId;
}
