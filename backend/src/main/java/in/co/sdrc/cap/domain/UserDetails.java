package in.co.sdrc.cap.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.sdrc.usermgmt.domain.Account;

import lombok.Data;

@Data
@Entity
@Table(name = "mst_user_details")
public class UserDetails implements Serializable{


	private static final long serialVersionUID = -6142358483948073924L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_details_id")
	private Integer userDetailsId;

	@Column
	private String name;
	
	@OneToOne
	@JoinColumn(name = "acc_id_fk", unique=true)
	private Account account;
	
	@ManyToOne
	@JoinColumn(name = "department_id_fk")
	private Department department;
	
	@ManyToOne
	@JoinColumn(name = "area_id_fk")
	private Area area;
	
	
}

