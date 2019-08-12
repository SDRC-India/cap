package in.co.sdrc.cap.dataentry.model;

import java.util.List;

import in.co.sdrc.cap.domain.Department;
import in.co.sdrc.cap.domain.Indicator;
import in.co.sdrc.cap.domain.Sector;
import in.co.sdrc.cap.domain.Source;
import lombok.Data;

@Data
public class DepartmentDE {
	
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public Sector getSector() {
		return sector;
	}
	public void setSector(Sector sector) {
		this.sector = sector;
	}
	public Indicator getIndicator() {
		return indicator;
	}
	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}
	public Source getSrc() {
		return src;
	}
	public void setSrc(Source src) {
		this.src = src;
	}
	public String getTp() {
		return tp;
	}
	public void setTp(String tp) {
		this.tp = tp;
	}
	public List<AreaDE> getAreaList() {
		return areaList;
	}
	public void setAreaList(List<AreaDE> areaList) {
		this.areaList = areaList;
	}
	public double getAvg() {
		return avg;
	}
	public void setAvg(double avg) {
		this.avg = avg;
	}
	private Department department;
	private Sector sector;
	private Indicator indicator;
	private Source src;
	private String tp;
	private List<AreaDE> areaList;
	private double avg;

}
