package in.co.sdrc.cap.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;

/**
 * @author Subham Ashish
 *
 */
@Entity
@Table(name="target_data", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "area_id_fk", "indicator_id_fk", "timePeriod_id_fk", "target"})})
@Data
public class TargetData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String target;
	
	@ManyToOne
	@JoinColumn(name = "area_id_fk")
	private Area area;
	
	@ManyToOne
	@JoinColumn(name = "indicator_id_fk")
	private Indicator indicator;
	
	@ManyToOne
	@JoinColumn(name = "timePeriod_id_fk")
	private TimePeriod timePeriod;
	
	private boolean isLive;
}
