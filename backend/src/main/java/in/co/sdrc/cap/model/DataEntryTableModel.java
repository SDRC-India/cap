package in.co.sdrc.cap.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author Subham Ashish
 *
 */
@Data
public class DataEntryTableModel {

	private List<Map<String, Object>> tableHead;

	private List<String> tableColumns;

	private List<Map<String, Object>> tableData;

}
