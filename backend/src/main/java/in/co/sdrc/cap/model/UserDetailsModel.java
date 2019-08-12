package in.co.sdrc.cap.model;

import java.util.List;

import lombok.Data;

@Data
public class UserDetailsModel {

	private int userId;
	private String userName;
	private Boolean enable;
	private String name;
	private String email;
	private List<Integer> roleId;
	private List<String> roleName;
	private String areaCode;
	private int departmentId;
}
