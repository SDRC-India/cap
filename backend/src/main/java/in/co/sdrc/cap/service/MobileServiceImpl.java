package in.co.sdrc.cap.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.co.sdrc.cap.mobile.model.Area;
import in.co.sdrc.cap.mobile.model.AreaLevel;
import in.co.sdrc.cap.mobile.model.Department;
import in.co.sdrc.cap.mobile.model.DepartmentIndicator;
import in.co.sdrc.cap.mobile.model.DepartmentSector;
import in.co.sdrc.cap.mobile.model.Indicator;
import in.co.sdrc.cap.mobile.model.Sector;
import in.co.sdrc.cap.mobile.model.Source;
import in.co.sdrc.cap.mobile.model.Subgroup;
import in.co.sdrc.cap.mobile.model.Unit;
import in.co.sdrc.cap.repository.AreaJpaRepository;
import in.co.sdrc.cap.repository.AreaLevelJpaRepository;
import in.co.sdrc.cap.repository.DataJpaRepository;
import in.co.sdrc.cap.repository.IndicatorJpaRepository;
import in.co.sdrc.cap.repository.SectorJpaRepository;
import in.co.sdrc.cap.repository.SourceJpaRepository;
import in.co.sdrc.cap.repository.SubgroupJpaRepository;
import in.co.sdrc.cap.util.Constant;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MobileServiceImpl implements MobileService{
	
	
	@Autowired
	private SectorJpaRepository sectorJpaRepository;
	
	@Autowired
	private IndicatorJpaRepository indicatorJpaRepository;
	
	@Autowired
	private AreaLevelJpaRepository areaLevelJpaRepository;
	
	@Autowired
	private AreaJpaRepository areaJpaRepository;
		
	@Autowired
	private DataJpaRepository dataJpaRepository;
	
	@Autowired
	private SourceJpaRepository sourceJpaRepository;
	
	@Autowired
	private SubgroupJpaRepository subgroupJpaRepository;

	
	private final Path rootLocation = Paths.get("/cap-data");
	private final Path jsonDataLocation = Paths.get("/cap-json-data");
	
	
	
	
	@Override
	public boolean createJsonAndZip() throws IOException {
		try {
			
			log.info("Inside zipping method");
			
			List<Sector> sectorList = new ArrayList<Sector>();
			List<Indicator> indicatorList = new ArrayList<>();
			List<AreaLevel> areaLevelList = new ArrayList<>();
			List<Area> areaList = new ArrayList<>();
			List<Source> sourceList = new ArrayList<>();
			List<Subgroup> subgroupList = new ArrayList<>();
			List<DepartmentSector> departmentSectorList = new ArrayList<>();
			
			Map<Integer, List<in.co.sdrc.cap.mobile.model.Data>> indicatorWiseData = new HashMap<>();
			Map<String, List<in.co.sdrc.cap.mobile.model.Data>> areaWiseData = new HashMap<>();
			
			List<Object[]> dataList = dataJpaRepository.findJsonData();
			
			log.info("Fetched data size " + dataList.size());
					
			Map<Integer, List<Object[]>> departSectorList = dataJpaRepository.getDepartmentSector().stream().collect(Collectors.groupingBy(item-> (Integer)item[2], Collectors.toList()));
			
			
			log.info("Department sector list" + departmentSectorList.size());
			List<Sector> sectorsList = null;
			for(Integer keys: departSectorList.keySet()) {
				 List<Object[]> objList = departSectorList.get(keys);
				 
				 DepartmentSector departmentSectorObj = new DepartmentSector();
				 
				 for (Object[] sector : objList) {
					 if(departmentSectorObj.getDepartmentId() == null) {
						 departmentSectorObj.setDepartmentId((Integer)sector[2]);
						 departmentSectorObj.setDepartmentName(sector[3].toString());
					     sectorsList = new ArrayList<>();
					    
					 } else {
						 sectorsList = departmentSectorObj.getSectors();
					 }
					 Sector s = new Sector();
					 s.setId((Integer)sector[0]);
					 s.setName(sector[1].toString());				
					 sectorsList.add(s);
					 departmentSectorObj.setSectors(sectorsList);
				}
				 departmentSectorList.add(departmentSectorObj); 
			}
			
			List<in.co.sdrc.cap.domain.Sector> sectors = sectorJpaRepository.findAll();
			
			for(in.co.sdrc.cap.domain.Sector sector: sectors) {
				Sector s = new Sector();
				s.setId(sector.getId());
				s.setName(sector.getSectorName());				
				sectorList.add(s);
			}
			
			
			List<in.co.sdrc.cap.domain.Indicator> indicators = indicatorJpaRepository.findAll();
			for(in.co.sdrc.cap.domain.Indicator indicator: indicators) {
				Indicator i = new Indicator();
				i.setId(indicator.getId());
				i.setName(indicator.getiName());
				
				in.co.sdrc.cap.domain.Sector sec = indicator.getRecSector();
				
				Sector s = new Sector();
				s.setId(sec.getId());
				s.setName(sec.getSectorName());
				i.setRecSector(s);
				List<Sector> sectorListIndicator = new ArrayList<>(); 
				sectorListIndicator.add(s);
//				if(sec.getSectorName().equals("Promoting Safe Drinking Water")) {
//					in.co.sdrc.cap.domain.Sector sectorOne = sectorJpaRepository.findBySectorName("Promoting Sanitation");
//					s = new Sector();
//					s.setId(sectorOne.getId());
//					s.setName(sectorOne.getSectorName());
//					sectorListIndicator.add(s);					
//				}
				i.setSectors(sectorListIndicator);
				in.co.sdrc.cap.domain.Subgroup sbg_d = indicator.getSubgroup();
				Subgroup sbg_m =new Subgroup();
				sbg_m.setId(sbg_d.getId());
				sbg_m.setSubgroupName(sbg_d.getSubgroupName());
				i.setSubgroup(sbg_m);
				
				in.co.sdrc.cap.domain.Unit uni = indicator.getUnit();
				Unit u = new Unit();
				u.setId(uni.getId());
				u.setUnitName(uni.getUnitName());
				i.setUnit(u);
				
				indicatorList.add(i);
			}
			
			List<in.co.sdrc.cap.domain.AreaLevel> areaLevels = areaLevelJpaRepository.findAll();
			for(in.co.sdrc.cap.domain.AreaLevel areaLevel: areaLevels) {
				AreaLevel al = new AreaLevel();
				al.setId(areaLevel.getId());
				al.setName(areaLevel.getAreaLevelName());	
				al.setLevel(areaLevel.getLevel());
				al.setStateAvailable(areaLevel.getIsStateAvailable());
				al.setDistrictAvailable(areaLevel.getIsDistrictAvailable());
				areaLevelList.add(al);
			}
			
			List<in.co.sdrc.cap.domain.Area> area = areaJpaRepository.findAll();
			for (in.co.sdrc.cap.domain.Area areas: area) {
				Area a = new Area();
				a.setId(areas.getId());
				a.setAreaname(areas.getAreaname());
				a.setCode(areas.getCode());
				a.setParentAreaCode(areas.getParentAreaCode());
				
				List<AreaLevel> als = new ArrayList<>();
				for(in.co.sdrc.cap.domain.AreaLevel areaLevel : areas.getAreaLevel()) {
					AreaLevel al = new AreaLevel();
					al.setId(areaLevel.getId());
					al.setName(areaLevel.getAreaLevelName());	
					al.setLevel(areaLevel.getLevel());
					al.setStateAvailable(areaLevel.getIsStateAvailable());
					al.setDistrictAvailable(areaLevel.getIsDistrictAvailable());
					als.add(al);
				}
				
				a.setAreaLevel(als);
				areaList.add(a);
			}
			
			
			List<in.co.sdrc.cap.domain.Source> sources = sourceJpaRepository.findAll();
			for(in.co.sdrc.cap.domain.Source source: sources) {
				Source s = new Source();
				s.setId(source.getId());
				s.setSourceName(source.getSourceName());	
				
				sourceList.add(s);
			}
			
			List<in.co.sdrc.cap.domain.Subgroup> subgroups = subgroupJpaRepository.findAll();
			
			for (in.co.sdrc.cap.domain.Subgroup subgroup: subgroups) {
				Subgroup sb = new Subgroup();
				sb.setId(subgroup.getId());
				sb.setSubgroupName(subgroup.getSubgroupName());
				subgroupList.add(sb);
			}
			
			List<DepartmentIndicator> diList = new ArrayList<>();
			//sourceIndicator.json
			for(in.co.sdrc.cap.domain.Indicator indicator:  indicators) {
				
				
				
				//set indicator model
				Indicator i = new Indicator();
				i.setId(indicator.getId());
				i.setName(indicator.getiName());
				
				in.co.sdrc.cap.domain.Sector sec = indicator.getRecSector();
				Sector s = new Sector();
				s.setId(sec.getId());
				s.setName(sec.getSectorName());
				i.setRecSector(s);
				
				in.co.sdrc.cap.domain.Subgroup sbg_d = indicator.getSubgroup();
				Subgroup sbg_m =new Subgroup();
				sbg_m.setId(sbg_d.getId());
				sbg_m.setSubgroupName(sbg_d.getSubgroupName());
				i.setSubgroup(sbg_m);
				
				in.co.sdrc.cap.domain.Unit uni = indicator.getUnit();
				Unit u = new Unit();
				u.setId(uni.getId());
				u.setUnitName(uni.getUnitName());
				i.setUnit(u);
				//set indicator end
				
				
				
				List<in.co.sdrc.cap.domain.Sector> sectorsOfIndicator = indicator.getSc();
				
				for(in.co.sdrc.cap.domain.Sector sector: sectorsOfIndicator) {
					
					List<in.co.sdrc.cap.domain.Department> departments = sector.getDepartments();
					
					for(in.co.sdrc.cap.domain.Department department:departments) {
						
						Department dept = new Department();
						dept.setId(department.getId());
						dept.setName(department.getDepartmentName());
						if(!isPresentInDIList(dept, diList)) {
							////department is not present in the diList
							DepartmentIndicator di = new DepartmentIndicator();
							di.setDepartment(dept);
							List<Indicator> indList = new ArrayList<>();
							indList.add(i);
							di.setIndicators(indList);
							diList.add(di);
						}else {
							//department is present in the diList
							if(!checkIndicatorPresentIntheDiList(dept, i, diList)) {
								//indicator not present
								diList = putIndicatorInDIList(dept, i, diList);
							}
						}
						
						
					}
					
					
				}
				
				
			}
			
			
			
			
			for (int i = 0; i < dataList.size(); i++) {

				Object[] data = dataList.get(i);

				// domain to model

				in.co.sdrc.cap.mobile.model.Data dataModel = new in.co.sdrc.cap.mobile.model.Data();

				dataModel.set_id(data[0].toString());
				dataModel.setArea(data[14].toString());


				dataModel.setIndicator(Integer.parseInt(data[15].toString()));
				dataModel.setIus(data[5].toString());
				dataModel.setSrc(Integer.parseInt(data[16].toString()));
				dataModel.setSubgrp(Integer.parseInt(data[17].toString()));
				dataModel.setTp((data[10].toString()));
				dataModel.setTrend((data[12].toString()));
				dataModel.setValue(data[13].toString());
				dataModel.setTps((data[11].toString()));
				dataModel.setBaseline(((boolean) data[20]));
				dataModel.setTarget(data[21] != null ? data[21].toString() : "-");
				dataModel.setSectorId(Integer.parseInt(data[22].toString()));

				// indicator work
				List<in.co.sdrc.cap.mobile.model.Data> existingIndicatorDataList = indicatorWiseData
						.get(dataModel.getIndicator());
				if (existingIndicatorDataList == null) {
					existingIndicatorDataList = new ArrayList<>();
				}

				existingIndicatorDataList.add(dataModel);
				indicatorWiseData.put(dataModel.getIndicator(), existingIndicatorDataList);

				// area work
				List<in.co.sdrc.cap.mobile.model.Data> existingAreaDataList = areaWiseData.get(dataModel.getArea());
				if (existingAreaDataList == null) {
					existingAreaDataList = new ArrayList<>();
				}
				existingAreaDataList.add(dataModel);
				areaWiseData.put(dataModel.getArea(), existingAreaDataList);

			}
			
			
			// Sector json file creation
			String fileName = "sector";
			ObjectMapper mapper = new ObjectMapper();
			File file = this.jsonDataLocation.resolve(fileName + ".json").toFile();
			mapper.writeValue(file, sectorList);
			
			// Indicator json file creation
			fileName = "indicator";
			mapper = new ObjectMapper();
			file = this.jsonDataLocation.resolve(fileName + ".json").toFile();
			mapper.writeValue(file, indicatorList);
			
			
			// Area Level json file creation
			fileName = "arealevel";
			mapper = new ObjectMapper();
			file = this.jsonDataLocation.resolve(fileName + ".json").toFile();
			mapper.writeValue(file, areaLevelList);
			
			// Area json file creation
			fileName = "area";
			mapper = new ObjectMapper();
			file = this.jsonDataLocation.resolve(fileName + ".json").toFile();
			mapper.writeValue(file, areaList);
			
			// Department json file creation
			fileName = "departmentIndicator";
			mapper = new ObjectMapper();
			file = this.jsonDataLocation.resolve(fileName + ".json").toFile();
			mapper.writeValue(file, diList);
			
			// Department json file creation
			fileName = "subgroup";
			mapper = new ObjectMapper();
			file = this.jsonDataLocation.resolve(fileName + ".json").toFile();
			mapper.writeValue(file, subgroupList);
			
			
			// Source json file creation
			fileName = "source";
			mapper = new ObjectMapper();
			file = this.jsonDataLocation.resolve(fileName + ".json").toFile();
			mapper.writeValue(file, sourceList);
			
			// Source json file creation
			fileName = "departmentSector";
			mapper = new ObjectMapper();
			file = this.jsonDataLocation.resolve(fileName + ".json").toFile();
			mapper.writeValue(file, departmentSectorList);

			// Indicator json file creation
			for (Map.Entry<Integer, List<in.co.sdrc.cap.mobile.model.Data>> entry : indicatorWiseData.entrySet()) {
				String fileNameInner = entry.getKey().toString();
				List<in.co.sdrc.cap.mobile.model.Data> fileContent = entry.getValue();

				ObjectMapper mapperInner = new ObjectMapper();
				File fileInner = this.jsonDataLocation.resolve(fileNameInner + ".json").toFile();
				mapperInner.writeValue(fileInner, fileContent);
			}

			// Area json file creation
			for (Map.Entry<String, List<in.co.sdrc.cap.mobile.model.Data>> entry : areaWiseData.entrySet()) {
				String fileNameInner = entry.getKey().toString();
				List<in.co.sdrc.cap.mobile.model.Data> fileContent = entry.getValue();

				ObjectMapper mapperInner = new ObjectMapper();

				File fileInner = this.jsonDataLocation.resolve(fileNameInner + ".json").toFile();
				mapperInner.writeValue(fileInner, fileContent);
			}
			
			
			
			
			
			
			

			// code to zip the folder
			// create byte buffer
			byte[] buffer = new byte[1024];

			File zipFile = this.rootLocation.resolve(Constant.ZIP_FILE_NAME).toFile();
			FileOutputStream fos = new FileOutputStream(zipFile.getAbsolutePath());

			ZipOutputStream zos = new ZipOutputStream(fos);

			File dir = this.jsonDataLocation.toFile();

			File[] files = dir.listFiles();
			
			log.info("File size " + files.length);

			for (int i = 0; i < files.length; i++) {


				FileInputStream fis = new FileInputStream(files[i]);

				// begin writing a new ZIP entry, positions the stream to the start of the entry
				// data
				zos.putNextEntry(new ZipEntry(files[i].getName()));

				int length;

				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}

				zos.closeEntry();

				// close the InputStream
				fis.close();
			}

			// close the ZipOutputStream
			zos.close();
			log.info("Method finished");
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private List<DepartmentIndicator> putIndicatorInDIList(Department dept, Indicator i,
			List<DepartmentIndicator> diList) {
		
		List<DepartmentIndicator> dList = diList.stream().filter(di -> di.getDepartment().getId() == dept.getId()).collect(Collectors.toList());
		
		List<Indicator> list = dList.get(0).getIndicators();
		
//		List<Indicator> list = diList.stream().filter(di -> di.getDepartment().getId() == dept.getId()).collect(Collectors.toList()).get(0).getIndicators().stream().filter(ind->ind.getId() == i.getId()).collect(Collectors.toList());
		list.add(i);
		DepartmentIndicator di = new DepartmentIndicator();
		di.setDepartment(dept);
		di.setIndicators(list);
		
		for(int j = 0; j < diList.size();j++) {
			if(diList.get(j).getDepartment().getId() == dept.getId()) {
				diList.remove(j);
				break;
			}
		}
		
		diList.add(di);
		
		return diList;
	}

	private boolean checkIndicatorPresentIntheDiList(Department dept, Indicator i, List<DepartmentIndicator> diList) {
		return diList.stream().filter(di -> di.getDepartment().getId() == dept.getId()).collect(Collectors.toList()).get(0).getIndicators().stream().filter(ind->ind.getId() == i.getId()).collect(Collectors.toList()).size() > 0?true:false;
	}

	private boolean isPresentInDIList(Department dept, List<DepartmentIndicator> diList) {
		return diList.stream().filter(di -> di.getDepartment().getId() == dept.getId()).collect(Collectors.toList()).size()>0?true:false;
	}

	
}
