package in.co.sdrc.cap.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(indexes = { @Index(columnList = "indicator_id_fk,area_id_fk,tp", name = "indicator_tp_area_index"),
		@Index(columnList = "indicator_id_fk,sector_id_fk,source_id_fk,periodicity,area_id_fk,is_live", name = "ius_periodicity_index") })
public class Data {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long slugiddata;

	@ManyToOne
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "slugidindicator")
	@JsonIdentityReference(alwaysAsId = true)
	@JoinColumn(name = "indicator_id_fk")
	private Indicator indicator;// indicator with percentage

	@ManyToOne
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "slugidsubgroup")
	@JsonIdentityReference(alwaysAsId = true)
	@JoinColumn(name = "subgroup_id_fk")
	private Subgroup subgrp;// total

	@ManyToOne
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "slugidsource")
	@JsonIdentityReference(alwaysAsId = true)
	@JoinColumn(name = "source_id_fk")
	private Source src;// source

	@ManyToOne
	@JoinColumn(name = "sector_id_fk")
	private Sector sector; // theme is sector

	@ManyToOne
	@JoinColumn(name = "department_id_fk")
	private Department department;

	private String ius;// concatenate indicator,unit,subgroup,timperiod (ids or names) as string
						// separated by : (index this column)

	private String periodicity;

	@ManyToOne
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "code")
	@JsonIdentityReference(alwaysAsId = true)
	@JoinColumn(name = "area_id_fk")
	private Area area;

	private String value;// eneterd value

	private String target;// prefetched value

	private Integer rank;// nothing

	private String trend;// nothing

	@ManyToMany(cascade = CascadeType.ALL)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "code")
	@JsonIdentityReference(alwaysAsId = true)
	@JoinTable(name = "data_top_mapping", joinColumns = {
			@JoinColumn(name = "data_id_fk", referencedColumnName = "id") }, inverseJoinColumns = @JoinColumn(name = "area_id_fk", referencedColumnName = "id"))
	private List<Area> top;// nothing

	@ManyToMany(cascade = CascadeType.ALL)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "code")
	@JsonIdentityReference(alwaysAsId = true)
	@JoinTable(name = "data_below_mapping", joinColumns = {
			@JoinColumn(name = "data_id_fk", referencedColumnName = "id") }, inverseJoinColumns = @JoinColumn(name = "area_id_fk", referencedColumnName = "id"))
	private List<Area> below;// nothing

	private String tp;

	private String tps;// nothing

	@CreationTimestamp
	private Date createdDate;

	@UpdateTimestamp
	private Date lastModified;

	@Column(name = "dKPIRSrs", columnDefinition = "boolean DEFAULT false")
	private Boolean dKPIRSrs = false;

	@Column(name = "dNITIRSrs", columnDefinition = "boolean DEFAULT false")
	private Boolean dNITIRSrs = false;

	@Column(name = "dTHEMATICRSrs", columnDefinition = "boolean DEFAULT false")
	private Boolean dTHEMATICRSrs = false;// added 14.08.2018

	@Column(name = "is_baseline", columnDefinition = "boolean DEFAULT false")
	private Boolean isBaseline = false;

	@Column(name = "is_live", columnDefinition = "boolean DEFAULT true")
	private boolean isLive = true;

	// new column added
	@ManyToOne
	@JoinColumn(name = "timePeriod_id_fk")
	private TimePeriod timePeriod;

	@Column(name = "is_mrt", columnDefinition = "boolean DEFAULT false")
	private Boolean isMRT = false;

	public Long getSlugiddata() {
		return slugiddata;
	}

	public void setSlugiddata(Long slugiddata) {
		this.slugiddata = slugiddata;
	}

	public Indicator getIndicator() {
		return indicator;
	}

	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}

	public Subgroup getSubgrp() {
		return subgrp;
	}

	public void setSubgrp(Subgroup subgrp) {
		this.subgrp = subgrp;
	}

	public Source getSrc() {
		return src;
	}

	public void setSrc(Source src) {
		this.src = src;
	}

	public Sector getSector() {
		return sector;
	}

	public void setSector(Sector sector) {
		this.sector = sector;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public String getIus() {
		return ius;
	}

	public void setIus(String ius) {
		this.ius = ius;
	}

	public String getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(String periodicity) {
		this.periodicity = periodicity;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getTrend() {
		return trend;
	}

	public void setTrend(String trend) {
		this.trend = trend;
	}

	public List<Area> getTop() {
		return top;
	}

	public void setTop(List<Area> top) {
		this.top = top;
	}

	public List<Area> getBelow() {
		return below;
	}

	public void setBelow(List<Area> below) {
		this.below = below;
	}

	public String getTp() {
		return tp;
	}

	public void setTp(String tp) {
		this.tp = tp;
	}

	public String getTps() {
		return tps;
	}

	public void setTps(String tps) {
		this.tps = tps;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getIsBaseline() {
		return isBaseline;
	}

	public void setIsBaseline(Boolean isBaseline) {
		this.isBaseline = isBaseline;
	}

	@Override
	public String toString() {
		return "\nData [id=" + id + ", indicator=" + indicator.getiName() + ", subgrp=" + subgrp.getSubgroupName()
				+ ", src=" + src.getSourceName() + ", ius=" + ius + ", area=" + area.getAreaname() + ":"
				+ area.getCode() + ":" + area.getActAreaLevel().getAreaLevelName() + ", parentArea="
				+ area.getParentAreaCode() + ", value=" + value + ", target=" + target + ", rank=" + rank + ", trend="
				+ trend + ", top=" + top + ", below=" + below + ", tp=" + tp + ", tps=" + tps + "]";
	}

	public Boolean getdKPIRSrs() {
		return dKPIRSrs;
	}

	public void setdKPIRSrs(Boolean dKPIRSrs) {
		this.dKPIRSrs = dKPIRSrs;
	}

	public Boolean getdNITIRSrs() {
		return dNITIRSrs;
	}

	public void setdNITIRSrs(Boolean dNITIRSrs) {
		this.dNITIRSrs = dNITIRSrs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((area == null) ? 0 : area.hashCode());
		result = prime * result + ((below == null) ? 0 : below.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((dKPIRSrs == null) ? 0 : dKPIRSrs.hashCode());
		result = prime * result + ((dNITIRSrs == null) ? 0 : dNITIRSrs.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((indicator == null) ? 0 : indicator.hashCode());
		result = prime * result + ((ius == null) ? 0 : ius.hashCode());
		result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
		result = prime * result + ((periodicity == null) ? 0 : periodicity.hashCode());
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((slugiddata == null) ? 0 : slugiddata.hashCode());
		result = prime * result + ((src == null) ? 0 : src.hashCode());
		result = prime * result + ((subgrp == null) ? 0 : subgrp.hashCode());
		result = prime * result + ((top == null) ? 0 : top.hashCode());
		result = prime * result + ((tp == null) ? 0 : tp.hashCode());
		result = prime * result + ((tps == null) ? 0 : tps.hashCode());
		result = prime * result + ((trend == null) ? 0 : trend.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Data other = (Data) obj;
		if (area == null) {
			if (other.area != null)
				return false;
		} else if (!area.equals(other.area))
			return false;
		if (below == null) {
			if (other.below != null)
				return false;
		} else if (!below.equals(other.below))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (dKPIRSrs == null) {
			if (other.dKPIRSrs != null)
				return false;
		} else if (!dKPIRSrs.equals(other.dKPIRSrs))
			return false;
		if (dNITIRSrs == null) {
			if (other.dNITIRSrs != null)
				return false;
		} else if (!dNITIRSrs.equals(other.dNITIRSrs))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (indicator == null) {
			if (other.indicator != null)
				return false;
		} else if (!indicator.equals(other.indicator))
			return false;
		if (ius == null) {
			if (other.ius != null)
				return false;
		} else if (!ius.equals(other.ius))
			return false;
		if (lastModified == null) {
			if (other.lastModified != null)
				return false;
		} else if (!lastModified.equals(other.lastModified))
			return false;
		if (periodicity == null) {
			if (other.periodicity != null)
				return false;
		} else if (!periodicity.equals(other.periodicity))
			return false;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		if (slugiddata == null) {
			if (other.slugiddata != null)
				return false;
		} else if (!slugiddata.equals(other.slugiddata))
			return false;
		if (src == null) {
			if (other.src != null)
				return false;
		} else if (!src.equals(other.src))
			return false;
		if (subgrp == null) {
			if (other.subgrp != null)
				return false;
		} else if (!subgrp.equals(other.subgrp))
			return false;
		if (top == null) {
			if (other.top != null)
				return false;
		} else if (!top.equals(other.top))
			return false;
		if (tp == null) {
			if (other.tp != null)
				return false;
		} else if (!tp.equals(other.tp))
			return false;
		if (tps == null) {
			if (other.tps != null)
				return false;
		} else if (!tps.equals(other.tps))
			return false;
		if (trend == null) {
			if (other.trend != null)
				return false;
		} else if (!trend.equals(other.trend))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	public Boolean getdTHEMATICRSrs() {
		return dTHEMATICRSrs;
	}

	public void setdTHEMATICRSrs(Boolean dTHEMATICRSrs) {
		this.dTHEMATICRSrs = dTHEMATICRSrs;
	}

	public Boolean getIsLive() {
		return isLive;
	}

	public void setIsLive(Boolean isLive) {
		this.isLive = isLive;
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

	public Boolean getIsMRT() {
		return isMRT;
	}

	public void setIsMRT(Boolean isMRT) {
		this.isMRT = isMRT;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

}
