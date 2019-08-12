package in.co.sdrc.cap.domain;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


/**
 * @author Subham Ashish
 *
 */
@lombok.Data
@Entity
@Table(name = "time_period", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "time_period_duration", "financial_year", "year" }) })
public class TimePeriod {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "timeperiod_id_pk")
	private Integer timePeriodId;

	@Column(name = "time_period_duration", length = 30)
	private String timePeriod;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date", nullable = false)
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date", nullable = false)
	private Date endDate;

//	@Column(name = "periodicity", length = 30)
//	private String periodicity;

	@Column(name = "created_date", nullable = false)
	@CreationTimestamp
	private Timestamp createdDate;

	@Column(name = "updated_date")
	@UpdateTimestamp
	private Timestamp updatedDate;

	@Column(name = "financial_year", length = 30)
	private String financialYear;

	@Column(name = "year")
	private Integer year;
	
	private String shortName;
	
	@ManyToOne
	@JoinColumn(name = "periodicity_id_fk")
	private Periodicity periodicity;
	
	public TimePeriod(int id) {
		this.timePeriodId = id;
	}
	
	public TimePeriod() {
	}
}
