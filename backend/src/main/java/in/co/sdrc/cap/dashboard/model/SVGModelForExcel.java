package in.co.sdrc.cap.dashboard.model;

import java.util.List;

import lombok.Data;


@Data
public class SVGModelForExcel {
	
	private String districtName;
	private String blockName;
	private String themeName;
	private List<String> svg;
	private List<DataForExcel> dataForExcel;
	private List<DataForExcel> outcome;

}
