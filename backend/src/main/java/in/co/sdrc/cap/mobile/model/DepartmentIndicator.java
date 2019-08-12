package in.co.sdrc.cap.mobile.model;

import java.util.List;

public class DepartmentIndicator {
	private Department department;
	private List<Indicator> indicators;
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public List<Indicator> getIndicators() {
		return indicators;
	}
	public void setIndicators(List<Indicator> indicators) {
		this.indicators = indicators;
	}
}
