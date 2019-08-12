package in.co.sdrc.cap.dashboard.model;

import java.util.List;

import lombok.Data;

@Data
public class DepartmentModel {

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<DashboardThemeModel> getThemes() {
		return themes;
	}
	public void setThemes(List<DashboardThemeModel> themes) {
		this.themes = themes;
	}
	private int id;
	private String name;
	private List<DashboardThemeModel> themes;
}
