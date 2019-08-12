package in.co.sdrc.cap.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class TargetModel {
	List<Map<String, Object>> targetDatas;
	String parentAreaCode; 
	Integer sectorId;
	Integer timePeridId;
}
