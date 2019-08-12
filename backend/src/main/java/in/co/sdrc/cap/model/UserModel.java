package in.co.sdrc.cap.model;

import lombok.Data;

@Data
public class UserModel {

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private int id;
}
