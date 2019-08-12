package in.co.sdrc.cap.dashboard.model;

import lombok.Data;

@Data
public class SVGModel {
	
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	private int key;
	private String value;

}
