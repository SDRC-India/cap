package in.co.sdrc.cap.mobile.model;


import java.util.List;

import lombok.Data;

@Data
public class Indicator {
	
	private int id;
	private String name;
	private Sector recSector;
	private Unit unit;
	private Subgroup subgroup;
	private List<Sector> sectors;
}
