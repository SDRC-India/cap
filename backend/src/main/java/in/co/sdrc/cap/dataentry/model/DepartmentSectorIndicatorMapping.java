package in.co.sdrc.cap.dataentry.model;

import lombok.Data;

@Data
public class DepartmentSectorIndicatorMapping {
	
	public int getDepartment() {
		return department;
	}
	public void setDepartment(int department) {
		this.department = department;
	}
	public int getSector() {
		return sector;
	}
	public void setSector(int sector) {
		this.sector = sector;
	}
	public int getIndicator() {
		return indicator;
	}
	public void setIndicator(int indicator) {
		this.indicator = indicator;
	}
	private int department;
	private int sector;
	private int indicator;

}
