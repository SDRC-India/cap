package in.co.sdrc.cap.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

/**
 * @author Subham Ashish(subham@sdrc.co.in)
 *
 */
@Data
@Entity
@Table(name = "uuid_generator")
public class UUIdGenerator {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uuid_generator_id_pk")
	private Integer id;

	private Integer accountId;

	private Integer month;

	private Integer year;

	private String uuid;

	@CreationTimestamp
	private Date createdDate;

}
