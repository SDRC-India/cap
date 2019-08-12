package in.co.sdrc.cap.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import in.co.sdrc.cap.model.DataEntryTableModel;
import in.co.sdrc.cap.model.FrequencyModel;

public interface IDataEntryService {

	String generateTemplate();

	ResponseEntity<String> uploadDataEntryTemplate(MultipartFile file);

	ResponseEntity<String> saveTargetData(DataEntryTableModel dataEntryTableModel);

	List<FrequencyModel> getFrequencyModel();

	List<FrequencyModel> getTimePeriodModel(Integer id);

	DataEntryTableModel getData(Integer id);
}
