package in.co.sdrc.cap.dataentry.model;

import in.co.sdrc.cap.domain.Area;
import lombok.Data;

@Data
public class AreaDE {
	
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public double getTarget() {
		return target;
	}
	public void setTarget(double target) {
		this.target = target;
	}
	private Area area;
	private double value;
	private double target;

}
