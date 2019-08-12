package in.co.sdrc.cap.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sdrc.usermgmt.domain.Authority;
import org.sdrc.usermgmt.domain.Designation;
import org.sdrc.usermgmt.domain.DesignationAuthorityMapping;
import org.sdrc.usermgmt.repository.AuthorityRepository;
import org.sdrc.usermgmt.repository.DesignationAuthorityMappingRepository;
import org.sdrc.usermgmt.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.co.sdrc.cap.domain.Area;
import in.co.sdrc.cap.domain.AreaLevel;
import in.co.sdrc.cap.domain.Data;
import in.co.sdrc.cap.domain.Department;
import in.co.sdrc.cap.domain.Indicator;
import in.co.sdrc.cap.domain.Periodicity;
import in.co.sdrc.cap.domain.Sector;
import in.co.sdrc.cap.domain.Source;
import in.co.sdrc.cap.domain.Subgroup;
import in.co.sdrc.cap.domain.TargetData;
import in.co.sdrc.cap.domain.TimePeriod;
import in.co.sdrc.cap.domain.Unit;
import in.co.sdrc.cap.repository.AreaJpaRepository;
import in.co.sdrc.cap.repository.AreaLevelJpaRepository;
import in.co.sdrc.cap.repository.DataJpaRepository;
import in.co.sdrc.cap.repository.DepartmentRepository;
import in.co.sdrc.cap.repository.IndicatorJpaRepository;
import in.co.sdrc.cap.repository.PeriodicityRepository;
import in.co.sdrc.cap.repository.SectorJpaRepository;
import in.co.sdrc.cap.repository.SourceJpaRepository;
import in.co.sdrc.cap.repository.SubgroupJpaRepository;
import in.co.sdrc.cap.repository.SynchronizationDateMasterRepository;
import in.co.sdrc.cap.repository.TargetDataRepository;
import in.co.sdrc.cap.repository.TimePeriodRepository;
import in.co.sdrc.cap.repository.UnitJpaRepository;
import in.co.sdrc.cap.util.Frequency;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	@Autowired
	private AuthorityRepository authorityRepository;

	@Autowired
	private DesignationRepository designationRepository;

	@Autowired
	private DesignationAuthorityMappingRepository designationAuthorityMappingRepository;

	@Autowired
	private SynchronizationDateMasterRepository synchronizationDateMasterRepository;

	@Autowired
	private SourceJpaRepository sourceJpaRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private SectorJpaRepository sectorJpaRepository;

	@Autowired
	private SubgroupJpaRepository subgroupJpaRepository;

	@Autowired
	private AreaLevelJpaRepository areaLevelJpaRepository;

	@Autowired
	private UnitJpaRepository unitJpaRepository;

	@Autowired
	private IndicatorJpaRepository indicatorJpaRepository;

	@Autowired
	private AreaJpaRepository areaJpaRepository;
	
	@Autowired
	private DataJpaRepository dataJpaRepository;
	
	@Autowired
	private PeriodicityRepository periodicityRepository;
	
	@Autowired
	private TimePeriodRepository timePeriodRepository;
	
	@Autowired
	private TargetDataRepository targetDataRepository;
	
	Map<String, Indicator> sqlIndicatorMap = new HashMap<>();

	Map<String, Unit> sqlUnitMap = new HashMap<>();

	Map<String, Source> sqlSourceMap = new HashMap<>();
	
	Map<String, Sector> sectorMap = new HashMap<>();
	
	Map<String, Subgroup> sqlSubgroupMap = new HashMap<>();

	Map<String, Area> sqlAreaMap = new HashMap<>();
	
	Map<String, Department> departmentMap = new HashMap<>();

	@Override
	public String createOneAutherity(String name, String description) {

		Authority authority = new Authority();
		authority.setAuthority(name);
		authority.setDescription(description);
		authorityRepository.save(authority);
		System.out.println("Autherity " + name + " created.");

		return "succes";
	}

	@Override
	public String createOneDesignation(String name, String code) {

		Designation desg = new Designation();
		desg.setCode(code);
		desg.setName(name);
		designationRepository.save(desg);
		return "success";
	}

	@Override
	public String createOneDesignationAutherityMapping(String designationCode, String autherityName) {

		DesignationAuthorityMapping dam = new DesignationAuthorityMapping();

		dam.setAuthority(authorityRepository.findByAuthority(autherityName));
		dam.setDesignation(designationRepository.findByCode(designationCode));

		designationAuthorityMappingRepository.save(dam);

		System.out.println("Autherity mapping" + designationCode + " " + autherityName + " created.");

		return "success";
	}

////////////////////////////------------------------SQL
//////////////////////////// CONFIGURATION--------------------------------------------
	@Transactional
	@Override
	public boolean configureSQLDb() {
		sqlSourceMap = new HashMap<>();
		sqlSubgroupMap = new HashMap<>();

		Date currentDate = new Date();
		System.out.println(
				"Timestamp recieved from sql::" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(currentDate));
		System.out.println(currentDate);
		System.out.println(currentDate.getTime());
		synchronizationDateMasterRepository.findAll().forEach(s -> {
			s.setLastModifiedDate(currentDate);
			s.setLastSynchronized(currentDate);
			synchronizationDateMasterRepository.save(s);
		});
		{
			Source source = new Source();
			source.setSourceName("HMIS");
			source.setSlugidsource(1);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);

			sqlSourceMap.put(source.getSourceName(), source);
//			System.out.println(source);
			

			source = new Source();
			source.setSourceName("ICDS-CAS");
			source.setSlugidsource(2);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
//			System.out.println(source);

			source = new Source();
			source.setSourceName("ASHA Cell");
			source.setSlugidsource(3);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
//			System.out.println(source);


			source = new Source();
			source.setSourceName("PMMVY");
			source.setSlugidsource(4);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
//			System.out.println(source);
			

			source = new Source();
			source.setSourceName("ICDS");
			source.setSlugidsource(5);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
//			System.out.println(source);


			source = new Source();
			source.setSourceName("PHED");
			source.setSlugidsource(6);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
//			System.out.println(source);

			
			source = new Source();
			source.setSourceName("JEEVIKA");
			source.setSlugidsource(7);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
			System.out.println(source);

			
			source = new Source();
			source.setSourceName("EDUCATION");
			source.setSlugidsource(8);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
//			System.out.println(source);//
			
			
			source = new Source();
			source.setSourceName("NHFS-4");
			source.setSlugidsource(9);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
//			System.out.println(source);
			
			
//			source = new Source();
//			source.setSourceName("MGNREGA");
//			source.setSlugidsource(24);
//			source.setCreatedDate(currentDate);
//			source.setLastModified(currentDate);
//			source = sourceJpaRepository.save(source);
//			sqlSourceMap.put(source.getSourceName(), source);
//			System.out.println(source);
			
			
//			source = new Source();
//			source.setSourceName("PHD");
//			source.setSlugidsource(25);
//			source.setCreatedDate(currentDate);
//			source.setLastModified(currentDate);
//			source = sourceJpaRepository.save(source);
//			sqlSourceMap.put(source.getSourceName(), source);
//			System.out.println(source);
			
			
//			source = new Source();
//			source.setSourceName("PHDE");
//			source.setSlugidsource(26);
//			source.setCreatedDate(currentDate);
//			source.setLastModified(currentDate);
//			source = sourceJpaRepository.save(source);
//			sqlSourceMap.put(source.getSourceName(), source);
//			System.out.println(source);
			
			
			
			Department department = new Department();
			department.setDepartmentName("Department of Social welfare-ICDS");
			department.setLive(true);
			department = departmentRepository.save(department);
			departmentMap.put(department.getDepartmentName(), department);
			
			department = new Department();
			department.setDepartmentName("Department of Health");
			department.setLive(true);
			department = departmentRepository.save(department);
			departmentMap.put(department.getDepartmentName(), department);
			
			department = new Department();
			department.setDepartmentName("Bihar Rural Livelihoods Promotion Society (BRLPS) under Rural Development");
			department.setLive(true);
			department = departmentRepository.save(department);
			departmentMap.put(department.getDepartmentName(), department);
			
			department = new Department();
			department.setDepartmentName("Department of Education");
			department.setLive(true);
			department = departmentRepository.save(department);
			departmentMap.put(department.getDepartmentName(), department);
			
			department = new Department();
			department.setDepartmentName("Department of Public Health & Engineering (PHED)");
			department.setLive(true);
			department = departmentRepository.save(department);
			departmentMap.put(department.getDepartmentName(), department);
			
			department = new Department();
			department.setDepartmentName("Department of Rural Development");
			department.setLive(true);
			department = departmentRepository.save(department);
			departmentMap.put(department.getDepartmentName(), department);
			
			department = new Department();
			department.setDepartmentName("Department of Panchayati Raj");
			department.setLive(false);
			department = departmentRepository.save(department);
			departmentMap.put(department.getDepartmentName(), department);
			
			department = new Department();
			department.setDepartmentName("Department of Food and Civil supplies");
			department.setLive(false);
			department = departmentRepository.save(department);
			departmentMap.put(department.getDepartmentName(), department);
			
//			department = new Department();
//			department.setDepartmentName("ICDS");
//			department = departmentRepository.save(department);
//			departmentMap.put(department.getDepartmentName(), department);
			
//			department = new Department();
//			department.setDepartmentName("MWCD");
//			department = departmentRepository.save(department);
//			departmentMap.put(department.getDepartmentName(), department);
			
			

// Creating Sectors

			
			
			Sector sector = new Sector();
			sector.setSectorName("Breastfeeding Promotion");
			sector.setSlugidsector(1);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			List<Department> departments = new ArrayList<>();
			departments.add(departmentMap.get("Department of Health"));
			sector.setDepartments(departments);
			sector = sectorJpaRepository.save(sector);
			sectorMap.put(sector.getSectorName(), sector);

			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Complementary Feeding");
			sector.setSlugidsector(2);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			departments = new ArrayList<>();
			departments.add(departmentMap.get("Department of Social welfare-ICDS"));
			departments.add(departmentMap.get("Department of Health"));
			sector.setDepartments(departments);
			sector = sectorJpaRepository.save(sector);
			sectorMap.put(sector.getSectorName(), sector);
			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Immunization for Children");
			sector.setSlugidsector(3);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			departments = new ArrayList<>();
			departments.add(departmentMap.get("Department of Health"));
			sector.setDepartments(departments);
			sector = sectorJpaRepository.save(sector);
			sectorMap.put(sector.getSectorName(), sector);
			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Micronutrient, Vitamin-A Supplementation and Anti-Helminth for Children");
			
			sector.setSlugidsector(4);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			departments = new ArrayList<>();
			departments.add(departmentMap.get("Department of Health"));
			sector.setDepartments(departments);
			sector = sectorJpaRepository.save(sector);
			sectorMap.put(sector.getSectorName(), sector);
			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Growth Monitoring & Promotion");
			sector.setSlugidsector(5);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			departments = new ArrayList<>();
			departments.add(departmentMap.get("Department of Social welfare-ICDS"));
//			departments.add(departmentMap.get("Department of Health"));			
			sector.setDepartments(departments);
			sector = sectorJpaRepository.save(sector);
			sectorMap.put(sector.getSectorName(), sector);
			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Diarrhoea Management with Oral Rehydration Solution and Zinc");
			sector.setSlugidsector(6);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			departments = new ArrayList<>();
			departments.add(departmentMap.get("Department of Health"));
			sector.setDepartments(departments);
			sector = sectorJpaRepository.save(sector);
			sectorMap.put(sector.getSectorName(), sector);
			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Management of Acute Malnutrition");
			sector.setSlugidsector(7);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			departments = new ArrayList<>();
			departments.add(departmentMap.get("Department of Health"));
			sector.setDepartments(departments);
			sector = sectorJpaRepository.save(sector);
			sectorMap.put(sector.getSectorName(), sector);
			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Iron and Folic Acid for Adolescents, IFA, Calcium & Albendazole for Pregnant Women");
			sector.setSlugidsector(8);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			departments = new ArrayList<>();
			departments.add(departmentMap.get("Department of Health"));
//			departments.add(departmentMap.get("Department of Education"));
//			departments.add(departmentMap.get("ICDS"));
			sector.setDepartments(departments);
			sector = sectorJpaRepository.save(sector);
			sectorMap.put(sector.getSectorName(), sector);
			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Antenatal and Postnatal Services");
			sector.setSlugidsector(9);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			departments = new ArrayList<>();
			departments.add(departmentMap.get("Department of Health"));
			departments.add(departmentMap.get("Department of Social welfare-ICDS"));
			sector.setDepartments(departments);
			sector = sectorJpaRepository.save(sector);
			sectorMap.put(sector.getSectorName(), sector);
			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Promoting Safe Drinking Water");
			sector.setSlugidsector(10);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			departments = new ArrayList<>();
			departments.add(departmentMap.get("Department of Social welfare-ICDS"));
			departments.add(departmentMap.get("Department of Public Health & Engineering (PHED)"));
			departments.add(departmentMap.get("Department of Rural Development"));
//			departments.add(departmentMap.get("Department of Education"));ghj
			sector.setDepartments(departments);
			sector = sectorJpaRepository.save(sector);
			sectorMap.put(sector.getSectorName(), sector);
			System.out.println(sector);

//			sector = new Sector();
//			sector.setSectorName("Promoting Sanitation");
//			sector.setSlugidsector(11);
//			sector.setCreatedDate(currentDate);
//			sector.setLastModified(currentDate);
//			departments = new ArrayList<>();
//			departments.add(departmentMap.get("ICDS"));
//			departments.add(departmentMap.get("Department of Health"));
//			departments.add(departmentMap.get("Department of Education"));
//			departments.add(departmentMap.get("Panchayati Raj"));
//			sector.setDepartments(departments);
//			sector = sectorJpaRepository.save(sector);
//			sectorMap.put(sector.getSectorName(), sector);
//			System.out.println(sector);

//			sector = new Sector();
//			sector.setSectorName("Promoting Personal Hygiene");
//			sector.setSlugidsector(12);
//			sector.setCreatedDate(currentDate);
//			sector.setLastModified(currentDate);
//			departments = new ArrayList<>();
//			departments.add(departmentMap.get("ICDS"));
//			departments.add(departmentMap.get("Department of Health"));
//			departments.add(departmentMap.get("Department of Education"));
//			sector.setDepartments(departments);
//			sector = sectorJpaRepository.save(sector);
//			sectorMap.put(sector.getSectorName(), sector);
//			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Enabling Environment");
			sector.setSlugidsector(11);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			departments = new ArrayList<>();
			departments.add(departmentMap.get("Department of Social welfare-ICDS"));
			departments.add(departmentMap.get("Department of Education"));
			departments.add(departmentMap.get("Bihar Rural Livelihoods Promotion Society (BRLPS) under Rural Development"));
			sector.setDepartments(departments);
			sector = sectorJpaRepository.save(sector);
			sectorMap.put(sector.getSectorName(), sector);
			System.out.println(sector);
			
			sector = new Sector();
			sector.setSectorName("Take Home Ration");
			sector.setSlugidsector(12);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			departments = new ArrayList<>();
			departments.add(departmentMap.get("Department of Social welfare-ICDS"));
			sector.setDepartments(departments);
			sector = sectorJpaRepository.save(sector);
			sectorMap.put(sector.getSectorName(), sector);
			System.out.println(sector);
			
			sector = new Sector();
			sector.setSectorName("Critical supplies 13");
			sector.setSlugidsector(13);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			departments = new ArrayList<>();
			departments.add(departmentMap.get("Department of Social welfare-ICDS"));
			sector.setDepartments(departments);
			sector = sectorJpaRepository.save(sector);
			sectorMap.put(sector.getSectorName(), sector);
			System.out.println(sector);

//			sector = new Sector();
//			sector.setSectorName("Supply Chain Management");
//			sector.setSlugidsector(14);
//			sector.setCreatedDate(currentDate);
//			sector.setLastModified(currentDate);
//			departments = new ArrayList<>();
//			departments.add(departmentMap.get("ICDS"));
//			departments.add(departmentMap.get("Department of Health"));
//			departments.add(departmentMap.get("Department of Education"));
//			sector.setDepartments(departments);
//			sector = sectorJpaRepository.save(sector);
//			sectorMap.put(sector.getSectorName(), sector);
//			System.out.println(sector);

//			sector = new Sector();
//			sector.setSectorName("Awareness Generation for Behaviour Change");
//			sector.setSlugidsector(15);
//			sector.setCreatedDate(currentDate);
//			sector.setLastModified(currentDate);
//			departments = new ArrayList<>();
//			departments.add(departmentMap.get("ICDS"));
//			departments.add(departmentMap.get("Department of Health"));
//			departments.add(departmentMap.get("Department of Education"));
//			sector.setDepartments(departments);
//			sector = sectorJpaRepository.save(sector);
//			sectorMap.put(sector.getSectorName(), sector);
//			System.out.println(sector);

//			sector = new Sector();
//			sector.setSectorName("Social Security Schemes");
//			sector.setSlugidsector(16);
//			sector.setCreatedDate(currentDate);
//			sector.setLastModified(currentDate);
//			departments = new ArrayList<>();
//			departments.add(departmentMap.get("Department of Food and Civil supplies"));
//			departments.add(departmentMap.get("Department of Livelihood"));
//			sector.setDepartments(departments);
//			sector = sectorJpaRepository.save(sector);
//			sectorMap.put(sector.getSectorName(), sector);
//			System.out.println(sector);

//subgroup
			Subgroup subgroup = new Subgroup();
			subgroup.setSubgroupName("Total");
			subgroup.setSlugidsubgroup(1);
			subgroup.setCreatedDate(currentDate);
			subgroup.setLastModified(currentDate);
			subgroup = subgroupJpaRepository.save(subgroup);

			sqlSubgroupMap.put(subgroup.getSubgroupName(), subgroup);

//			AreaLevel national = new AreaLevel();
//			national.setLevel(1);
//			national.setAreaLevelName("National");
//			national.setIsDistrictAvailable(false);
//			national.setIsStateAvailable(false);
//			national.setSlugidarealevel(1);
//			national.setCreatedDate(currentDate);
//			national.setLastModified(currentDate);
//			areaLevelJpaRepository.save(national);
//
//			AreaLevel state = new AreaLevel();
//			state.setLevel(2);
//			state.setAreaLevelName("State");
//			state.setIsDistrictAvailable(false);
//			state.setIsStateAvailable(true);
//			state.setSlugidarealevel(2);
//			state.setCreatedDate(currentDate);
//			state.setLastModified(currentDate);
//			areaLevelJpaRepository.save(state);
//
//			AreaLevel district = new AreaLevel();
//			district.setLevel(3);
//			district.setAreaLevelName("District");
//			district.setIsDistrictAvailable(true);
//			district.setIsStateAvailable(true);
//			district.setSlugidarealevel(3);
//			district.setCreatedDate(currentDate);
//			district.setLastModified(currentDate);
//			areaLevelJpaRepository.save(district);
//
//			AreaLevel block = new AreaLevel();
//			block.setLevel(4);
//			block.setAreaLevelName("Block");
//			block.setIsDistrictAvailable(true);
//			block.setIsStateAvailable(true);
//			block.setSlugidarealevel(4);
//			block.setCreatedDate(currentDate);
//			block.setLastModified(currentDate);
//			areaLevelJpaRepository.save(block);

			Unit unit = null;

			unit = new Unit();
			unit.setUnitName("Percent");
			unit.setSlugidunit(1);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Deaths per 1000 live births");
			unit.setSlugidunit(2);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Number");
			unit.setSlugidunit(3);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Rupees");
			unit.setSlugidunit(4);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Females per 1,000 males");
			unit.setSlugidunit(5);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Live births per woman");
			unit.setSlugidunit(6);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Deaths per 100,000 live births");
			unit.setSlugidunit(7);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Sq km");
			unit.setSlugidunit(8);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Per square kilometer");
			unit.setSlugidunit(9);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Males per 100 Females");
			unit.setSlugidunit(10);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Persons per sq km");
			unit.setSlugidunit(11);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

		}
//		importAreaIntoSQL(currentDate);

		XSSFWorkbook book = null;
		try {
			sqlIndicatorMap = new HashMap<>();
//			ClassLoader loader = Thread.currentThread().getContextClassLoader();
//			URL url = loader.getResource("indicator/");
//			String path = url.getPath().replaceAll("%20", " ");
//			File files[] = new File(path).listFiles();
//			book = new XSSFWorkbook(files[0]);
			book = new XSSFWorkbook(new File("C:\\Users\\SDRC_DEV\\Desktop\\CAP\\IPE_All_Indicator_List_r11.xlsx"));
			int indicatorindex = 1;
			XSSFSheet sheet = book.getSheetAt(0);
			for (int row = 1; row <= sheet.getLastRowNum(); row++) {
				XSSFRow xssfRow = sheet.getRow(row);

				Iterator<Cell> cellItr = xssfRow.cellIterator();
				String indicatorName = "";
				String sectorNameString = "";
				List<String> sectorNameList = new ArrayList<>();
				String isKPI = "", nitiaayog = "", thematicKPI = "", thematicSourceNational = "",
						thematicSourceState = "", thematicSourceDistrict = "";
				String unitC = "";
				boolean highIsGood = false, nuculor = false;
				String subgroupName = "";
				String sourceNational = "", sourceState = "", sourceDistrict = "", ssv = "", hmis = "";
				Integer periodicity = null;
				String source = "";
				String department = "";
				String information = "";

				while (cellItr.hasNext()) {
					Cell cell = cellItr.next();

					switch (cell.getColumnIndex()) {

					case 1:
						indicatorName = cell.getStringCellValue().trim();
						break;

					case 2:
						if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
							isKPI = "No";
						} else
							isKPI = cell.getStringCellValue();
						break;

					case 3:
						unitC = cell.getStringCellValue().trim();

						break;
					case 4:
						if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
							highIsGood = cell.getBooleanCellValue();
							nuculor = false;
						}
						if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {

							if (cell.getStringCellValue().trim().equalsIgnoreCase("Neutral")) {
								highIsGood = false;
								nuculor = true;
							} else {
								throw new IllegalArgumentException("Invalid argument::" + cell.toString());
							}
						}

						break;
					case 5:
						subgroupName = cell.getStringCellValue().trim();
						break;
					case 6:
						sectorNameString = cell.getStringCellValue().trim();
						sectorNameList = Arrays.asList(sectorNameString.split(":"));						
						break;
					case 7:
						if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
							sourceNational = "";
						} else
							sourceNational = cell.getStringCellValue().trim();

						break;
					case 8:
						if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
							sourceState = "";
						} else
							sourceState = cell.getStringCellValue().trim();

						break;

					case 9:
						if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
							sourceDistrict = "";
						} else
							sourceDistrict = cell.getStringCellValue().trim();

						break;
					case 10:
						if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
							nitiaayog = "No";
						} else
							nitiaayog = "Yes";
						break;
					case 11:
						if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
							thematicKPI = "No";
						} else
							thematicKPI = "Yes";

						break;

					case 12:
						if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
							thematicSourceNational = "";
						} else
							thematicSourceNational = cell.getStringCellValue().trim();

						break;
					case 13:
						if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
							thematicSourceState = "";
						} else
							thematicSourceState = cell.getStringCellValue().trim();
						break;
					case 14:
						if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
							thematicSourceDistrict = "";
						} else
							thematicSourceDistrict = cell.getStringCellValue().trim();
						break;

					case 15:
						if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
							ssv = "No";
						} else
							ssv = "Yes";

						break;
					case 16:
						if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
							hmis = "No";
						} else
							hmis = "Yes";

						break;
					case 17:
						periodicity =  (int)cell.getNumericCellValue();
						break;
					case 18:
						source = cell.getStringCellValue().trim();
						break;
					case 19:
						department = cell.getStringCellValue().trim();
						break;
					case 20:
						information = cell.getStringCellValue().trim();
						break;
					}
				}

				List<Source> national = new ArrayList<Source>();
				List<Source> state = new ArrayList<Source>();
				List<Source> district = new ArrayList<Source>();
				List<Sector> sectors = new ArrayList<Sector>();

				List<Source> thematicNational = new ArrayList<Source>();
				List<Source> thematicState = new ArrayList<Source>();
				List<Source> thematicDistrict = new ArrayList<Source>();

				if (!sourceNational.isEmpty())
					national.add(sourceJpaRepository.findBySourceName(sourceNational));

				if (!sourceState.isEmpty())
					state.add(sourceJpaRepository.findBySourceName(sourceState));

				if (!sourceDistrict.isEmpty())
					district.add(sourceJpaRepository.findBySourceName(sourceDistrict));

				if (!thematicSourceNational.isEmpty())
					thematicNational.add(sourceJpaRepository.findBySourceName(thematicSourceNational));

				if (!thematicSourceState.isEmpty())
					thematicState.add(sourceJpaRepository.findBySourceName(thematicSourceState));

				if (!thematicSourceDistrict.isEmpty())
					thematicDistrict.add(sourceJpaRepository.findBySourceName(thematicSourceDistrict));
				
				
				for(String sectorName: sectorNameList) {
					Sector sector = sectorJpaRepository.findBySectorName(sectorName);
					if(sector!= null) {
						sectors.add(sector);
					}else {
						throw new NullPointerException("sector cannot be null::" + sectorName);
					}
				}
				
				
				

				Indicator indicator = new Indicator();
				indicator.setKpi((isKPI).equalsIgnoreCase("yes") ? true : false);
				indicator.setNitiaayog((nitiaayog).equalsIgnoreCase("yes") ? true : false);
				indicator.setiName(indicatorName);
				indicator.setSubgroup(subgroupJpaRepository.findBySubgroupName(subgroupName));
				indicator.setDistrict(district);
				indicator.setNational(national);
				indicator.setState(state);
				indicator.setSc(sectors);
				indicator.setHighisgood(highIsGood);
				indicator.setNucolor(nuculor);

				Unit unit = unitJpaRepository.findByUnitName(unitC.trim());

				indicator.setUnit(unit);
				indicator.setRecSector(sectorJpaRepository.findBySectorName(sectorNameString.split(":")[0]));

				indicator.setSlugidindicator(indicatorindex++);
				indicator.setCreatedDate(currentDate);
				indicator.setLastModified(currentDate);

				indicator.setSsv((ssv).equalsIgnoreCase("yes") ? true : false);
				indicator.setHmis((hmis).equalsIgnoreCase("yes") ? true : false);
				indicator.setThematicKpi((thematicKPI).equalsIgnoreCase("yes") ? true : false);
				indicator.setPeriodicity(periodicityRepository.findByPeriodicity(periodicity));
				indicator.setSrc(sourceJpaRepository.findBySourceName(source));
				indicator.setDepartment(departmentRepository.findByDepartmentName(department));
				indicator.setInformation(information);

				indicator.setThematicDistrict(thematicDistrict);
				indicator.setThematicState(thematicState);
				indicator.setThematicNational(thematicNational);

				if (!indicatorName.isEmpty()) {
					indicator = indicatorJpaRepository.save(indicator);

					sqlIndicatorMap.put(indicator.getiName().toLowerCase(), indicator);
				}

			}

//			importDataIntoSQLDb(currentDate);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				book.close();
			} catch (Exception e2) {
			}
		}

	}

	@Override
	@Transactional
	public boolean importAreaIntoSQL(Date currentDate) {
		{
			sqlAreaMap = new HashMap<>();
			int areaindex = 1;
			AreaLevel nitiAyogAreaLevel = areaLevelJpaRepository.findByLevel(5);
			XSSFWorkbook book = null;
			try {
//				ClassLoader loader = Thread.currentThread().getContextClassLoader();
//				URL url = loader.getResource("area/");
//				String path = url.getPath().replaceAll("%20", " ");
//				File files[] = new File(path).listFiles();
//				book = new XSSFWorkbook(files[0]);
				book = new XSSFWorkbook(new File("D:\\CAP_Area.xlsx"));
				XSSFSheet sheet = book.getSheetAt(0);

				for (int row = 2; row <= sheet.getLastRowNum(); row++) {

					XSSFRow xssfRow = sheet.getRow(row);

					Iterator<Cell> cellItr = xssfRow.cellIterator();
					int cols = 0;
					String areaCode = "";
					String areaName = "";
					Integer areaLevel = null;
					String parentAreaCode = null;
					boolean nitiAyog = false;
					while (cellItr.hasNext()) {

						Cell cell = cellItr.next();

						switch (cols) {
						case 0:

							break;
						case 1:
							areaCode = cell.getStringCellValue().trim();
							break;
						case 2:
							areaName = cell.getStringCellValue().trim();
							break;
						case 3:
							parentAreaCode = cell.getStringCellValue().trim();
							break;
						case 4:
							areaLevel = (int) cell.getNumericCellValue();
							break;
						case 5:
							if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
							} else
								nitiAyog = cell.getStringCellValue().trim().equalsIgnoreCase("yes") ? true : false;
							break;
						}
						cols++;
					}

// Area area = areaRepository.findByCode(areaCode);
// if (area == null) {
					Area area = new Area();
					area.setCcode(areaCode);
					area.setCode(areaCode);
					area.setCreatedDate(currentDate);
					area.setLastModified(currentDate);
					area.setParentAreaCode(parentAreaCode.equals("null") ? "" : parentAreaCode);

					AreaLevel level = areaLevelJpaRepository.findByLevel(areaLevel);

// System.out.println("AreaLevel::" + level);
					List<AreaLevel> levels = new ArrayList<>();
					levels.add(level);
					if (nitiAyog)
						levels.add(nitiAyogAreaLevel);
					area.setAreaLevel(levels);
					area.setActAreaLevel(level);
					area.setAreaname(areaName);
					area.setSlugidarea((((int) (areaindex++))));

					area = areaJpaRepository.save(area);
					sqlAreaMap.put(area.getCode().toLowerCase(), area);
//					System.out.println(area);
				}
				return true;

			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				try {
					book.close();
				} catch (Exception e2) {
				}
			}
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean importDataIntoSQLDb(Date currentDate) {

		int dataindex = 1;

//		ClassLoader loader = Thread.currentThread().getContextClassLoader();
//		URL url = loader.getResource("data/");
//		String path = url.getPath().replaceAll("%20", " ");
//		File files[] = new File(path).listFiles();
//
//		for (int f = 0; f < files.length; f++) {
//			System.out.println(files[f].toString());
//		}

//		for (int f = 0; f < files.length; f++) {
//			System.out.println("Importing From " + files[f].toString());
		XSSFWorkbook book = null;
		List<Data> datas = new ArrayList<>();
		int row = 1;
		try {

//				book = new XSSFWorkbook(files[f]);
			book = new XSSFWorkbook(new File("C:\\Users\\subrata\\Desktop\\CAP\\Data.xlsx"));

			XSSFSheet sheet = book.getSheetAt(0);

/*			for (row = 1; row <= sheet.getLastRowNum(); row++) {

				XSSFRow xssfRow = sheet.getRow(row);
				if (xssfRow == null) {
					break;
				}

				int cols = 0;
				String indicatorName = "", unitC = "", subgroupName = "", areaCode = "", tp = "", sourceName = "",
						tpshortName = "", sectorName = "",departmentName = "" ,value = "", target = "";
				Boolean dKPIRSrs = false, dNITIRSrs = false, baseline = false;
				Boolean dTHEMATICRSrs = false;

				Unit unit = null;
			
				for (int index = 0; index < 15; index++) {

					Cell cell = xssfRow.getCell(index);
					if (cell == null) {
						break;
					}
					switch (cols) {
					case 0:
						int cellType = cell.getCellType();
						if (cellType == 0) {
							tp = String.valueOf((int) cell.getNumericCellValue());
						} else {
							tp = cell.getStringCellValue().trim();
						}

						break;
					case 1:
						areaCode = cell.getStringCellValue().trim();
						break;
					case 4:
						indicatorName = cell.getStringCellValue().trim();
						break;
					case 5:
						unitC = cell.getStringCellValue().trim();
						break;
					case 6:
						subgroupName = cell.getStringCellValue().trim();
						break;
					case 7:
						int valcell = cell.getCellType();
						if (valcell == 0) {
							value = String.valueOf(cell.getNumericCellValue());
						} else {
							value = cell.getStringCellValue().trim();
						}
						break;
					case 8:
						sourceName = cell.getStringCellValue().trim();
						break;
					case 9:
						if (cell != null)
							tpshortName = cell.getStringCellValue().trim();

						break;

					case 10:
						 sectorName = cell.getStringCellValue().trim();
						break;
					case 11:
						// highisgood = cell.getBooleanCellValue();
						break;
					case 12:
						int targetcell = cell.getCellType();
						if (targetcell == 0) {
							target = String.valueOf(cell.getNumericCellValue());
						} else {
							target = cell.getStringCellValue().trim();
						}
						break;
					case 13:
						baseline = cell.getBooleanCellValue();
						break;
					case 14:
						 departmentName = cell.getStringCellValue().trim();
						break;	

					default:
						break;

					}
					cols++;
				}
			

				Indicator indicator = sqlIndicatorMap.get(indicatorName.toLowerCase());

				if (indicator == null) {
					throw new NullPointerException("Indicator Not Found:::" + indicatorName);
				} else if (!indicatorName.toLowerCase().equals(indicator.getiName().toLowerCase())) {
					throw new NullPointerException("Indicator MisMatch \n" + "Actual Indicator:::" + indicatorName
							+ "\nFound:::" + indicator.getiName());
				}

				else {

					unit = sqlUnitMap.get(unitC);

					Area area = sqlAreaMap.get(areaCode.toLowerCase());
					if (area == null) {
						throw new NullPointerException("Area cannot be null::" + areaCode);
					}

					Subgroup subgroup = sqlSubgroupMap.get(subgroupName);

					if (subgroup == null) {
						throw new NullPointerException("subgroup cannot be null::" + subgroup);
					}

					Source source = sqlSourceMap.get(sourceName);
					
					Sector sector = sectorMap.get(sectorName);
					if (sector == null) {
						throw new NullPointerException("Sector Not Found:::" + sectorName);
					}
					
					Department department = departmentMap.get(departmentName);
					if (department == null) {
						throw new NullPointerException("Department Not Found:::" + departmentName);
					}
					

					if (source == null) {
						throw new NullPointerException("source Not Found:::" + sourceName);
					}

					if (unit == null) {
						throw new NullPointerException("Unit Not Found:::" + unitC);
					}

					String ius = indicator.getiName().concat(":").concat(unit.getUnitName()).concat(":")
							.concat(subgroup.getSubgroupName());

					 

					Data data = new Data();
					data.setdKPIRSrs(dKPIRSrs);
					data.setdNITIRSrs(dNITIRSrs);
					data.setdTHEMATICRSrs(dTHEMATICRSrs);

					data.setArea(area);
					data.setBelow(new ArrayList<Area>());
					data.setTop(new ArrayList<Area>());
					data.setIndicator(indicator);
					data.setIus(ius);
					data.setRank(1);
					data.setSrc(source);
					data.setSector(sector);
					data.setDepartment(department);
					data.setSubgrp(subgroup);
					data.setTrend("up");
					data.setValue(value);
					data.setTp(tp);
					data.setTps(tpshortName);
					data.setSlugiddata((((long) (dataindex++))));
					data.setPeriodicity("Yearly");
					data.setCreatedDate(currentDate);
					data.setLastModified(currentDate);
					data.setTarget(target);
					data.setIsBaseline(baseline);
					data.setIsLive(true);
					datas.add(data);

				}
			}*/
			int colNum = 0;
			XSSFRow xssfRow = null;
			XSSFCell cell = null;
			for (row = 1; row <= sheet.getLastRowNum(); row++) {
				xssfRow = sheet.getRow(row);

				Data data = new Data();
				
				cell = xssfRow.getCell(colNum);
				
				data.setdKPIRSrs(cell.getBooleanCellValue());
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setdNITIRSrs(cell.getBooleanCellValue());
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setdTHEMATICRSrs(cell.getBooleanCellValue());
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setIsBaseline(cell.getBooleanCellValue());
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setIsLive(cell.getBooleanCellValue());
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setIsMRT(cell.getBooleanCellValue());
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setIus(cell.getStringCellValue());
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setPeriodicity(cell.getStringCellValue());
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setRank((int)cell.getNumericCellValue());
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setSlugiddata(Long.valueOf(row));
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setTarget(String.valueOf((int)cell.getNumericCellValue()));
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setTp(cell.getStringCellValue());
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setTps(null);
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setTrend(cell.getStringCellValue());
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setValue(String.valueOf((int)cell.getNumericCellValue()));
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setArea(new Area((int)cell.getNumericCellValue()));
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setDepartment(new Department((int)cell.getNumericCellValue()));
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setIndicator(new Indicator((int)cell.getNumericCellValue()));
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setSector(new Sector((int)cell.getNumericCellValue()));
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setSrc(new Source((int)cell.getNumericCellValue()));
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setSubgrp(new Subgroup((int)cell.getNumericCellValue()));
				
				colNum++;
				cell = xssfRow.getCell(colNum);
				data.setTimePeriod(new TimePeriod((int)cell.getNumericCellValue()));
				
				datas.add(data);
				
				colNum = 0;
			}
			
			if (!datas.isEmpty())
				dataJpaRepository.save(datas);
			System.out.println("DATA LOOP FINISHED: " + dataindex);
			book.close();
			return true;
		} catch (Exception e) {
			System.out.println("Exception in row : " + row + "\n" + e.getStackTrace());
			throw new RuntimeException(e);
		} finally {
			try {
				book.close();
			} catch (Exception e2) {
				throw new RuntimeException(e2);
			}
		}

	}

	@Override
	public boolean configurePeriodicity() {
	
		
		Periodicity periodicity = new Periodicity();
		periodicity.setFrequency(Frequency.Monthly);
		periodicity.setPeriodicity(1);
		periodicity.setIsLive(true);
		periodicityRepository.save(periodicity);
		
		periodicity = new Periodicity();
		periodicity.setFrequency(Frequency.Quarterly);
		periodicity.setPeriodicity(3);
		periodicity.setIsLive(true);
		periodicityRepository.save(periodicity);
		
		periodicity = new Periodicity();
		periodicity.setFrequency(Frequency.Yearly);
		periodicity.setPeriodicity(12);
		periodicity.setIsLive(false);
		periodicityRepository.save(periodicity);
		
		
		return true;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean configureTargetValue() {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("target/");
		String path = url.getPath().replaceAll("%20", " ");
		File[] files = new File(path).listFiles();

		if (files == null) {
			throw new RuntimeException("No file found in path " + path);
		}

		List<TargetData> tdataList = new ArrayList<>();

		for (int f = 0; f < files.length; f++) {

			XSSFWorkbook workbook = null;

			try {
				workbook = new XSSFWorkbook(files[f]);
			} catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException | IOException e) {

				e.printStackTrace();
			}

			XSSFSheet targetSheet = workbook.getSheetAt(0);

			Integer id = null;
			String areaName = null;
			String indicatorName = null;
			Double targetValue = null;
			String financialYear = null;

			try {

				for (int row = 1; row <= targetSheet.getLastRowNum(); row++) {// row
					// loop
					// System.out.println("Rows::" + row);

					XSSFRow xssfRow = targetSheet.getRow(row);

					id = null;
					areaName = null;
					indicatorName = null;
					targetValue = null;
					financialYear = null;

					for (int cols = 0; cols < 5; cols++) {// column loop

						Cell cell = xssfRow.getCell(cols);

						switch (cols) {

						case 0:
							id = (int) cell.getNumericCellValue();
							break;

						case 1:
							areaName = cell.getStringCellValue();
							break;

						case 2:
							indicatorName = cell.getStringCellValue();
							break;

						case 3:
							targetValue = cell.getNumericCellValue();
							break;

						case 4:
							financialYear = cell.getStringCellValue();
							break;

						}

					}

					TargetData target = new TargetData();
					target.setArea(areaJpaRepository.findByAreaname(areaName));
					target.setId(id);
					target.setIndicator(indicatorJpaRepository.findByIName(indicatorName));
					target.setLive(true);
					target.setTarget(targetValue.toString());
					target.setTimePeriod(timePeriodRepository.findByFinancialYearAndPeriodicityFrequency(financialYear,
							Frequency.Yearly));
					tdataList.add(target);
				}
				targetDataRepository.save(tdataList);
				return true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return false;

	}
}
