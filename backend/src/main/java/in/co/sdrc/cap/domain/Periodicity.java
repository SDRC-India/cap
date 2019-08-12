package in.co.sdrc.cap.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import in.co.sdrc.cap.util.Frequency;

/**
 * @author Subham Ashish(subham@sdrc.co.in)
 *
 */
@lombok.Data
@Entity
@Table(name="periodicity")
public class Periodicity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Integer periodicity;

	@Enumerated(value = EnumType.STRING)
	private Frequency frequency;
	
	@Column(name="is_live",columnDefinition =  "boolean DEFAULT true")
	private Boolean isLive=true;
}
