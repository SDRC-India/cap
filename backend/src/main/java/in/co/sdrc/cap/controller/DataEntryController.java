package in.co.sdrc.cap.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.co.sdrc.cap.model.DataEntryTableModel;
import in.co.sdrc.cap.model.FrequencyModel;
import in.co.sdrc.cap.service.IDataEntryService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class DataEntryController {

	@Autowired
	private IDataEntryService dataEntryService;

	@PostMapping("template")
	@PreAuthorize("hasAuthority('DATA_COLLECTION')")
	public ResponseEntity<InputStreamResource> generateTemplate() {

		String filePath = "";
		try {

			log.info("Control going to data entry service");
			filePath = dataEntryService.generateTemplate();
			log.info("Control has come from data entry service");
			File file = new File(filePath);

			HttpHeaders respHeaders = new HttpHeaders();
			respHeaders.add("Content-Disposition", "attachment; filename=" + file.getName());
			InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
			log.info("Pre file deletion");

			file.delete();
			log.info("Post file deletion");
			return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	/**
	 *@author Subham Ashish (subham@sdrc.co.in)
	 */
	@PostMapping("/uploadTemplate")
	@PreAuthorize("hasAuthority('DATA_COLLECTION')")
	public ResponseEntity<String> uploadDataEntryTemplate(@RequestParam("file") MultipartFile file) {
		return dataEntryService.uploadDataEntryTemplate(file);
	}
	
	@PostMapping("/saveData")
	@PreAuthorize("hasAuthority('DATA_COLLECTION')")
	public ResponseEntity<String> saveTargetData(@RequestBody DataEntryTableModel dataEntryTableModel) {
		return dataEntryService.saveTargetData(dataEntryTableModel);
	}
	
	@GetMapping("/getFrequency")
	public List<FrequencyModel> getFrequencyModel(){
		return dataEntryService.getFrequencyModel();
	}
	
	@GetMapping("/getTimeperiod")
	public List<FrequencyModel> getTimePeriodModel(@RequestParam("id") Integer id){
		return dataEntryService.getTimePeriodModel(id);
	}
	
	@GetMapping("/getData")
	public DataEntryTableModel dataEntryTableModel(@RequestParam("id") Integer id){
		return dataEntryService.getData(id);
	}
	
}
