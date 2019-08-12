package in.co.sdrc.cap.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import in.co.sdrc.cap.domain.Area;
import in.co.sdrc.cap.domain.Data;
import in.co.sdrc.cap.domain.UserDetails;
import in.co.sdrc.cap.model.UserModel;
import in.co.sdrc.cap.repository.AreaJpaRepository;
import in.co.sdrc.cap.repository.DataJpaRepository;
import in.co.sdrc.cap.repository.UserDetailsRepository;
import in.co.sdrc.cap.util.CAPUtil;
import in.co.sdrc.cap.util.TokenInfoExtracter;

@Service
public class ReportServiceImpl implements IReportService{
	
	
	@Autowired
	private AreaJpaRepository areaJpaRepository;
	

	
	@Autowired
	private UserDetailsRepository userDetailsRepository;
	
	@Autowired
	private DataJpaRepository dataJpaRepository;
	
	@Autowired
	private TokenInfoExtracter tokenInfoExtracter;
	
	
	private final Path outputPath = Paths.get("/cap/capTemp");
	
	@Override
	public String generateReport(String areaCode) {
 
		
		// TODO Auto-generated method stub
		String outputPathExcel = outputPath.toAbsolutePath().toString();
		
		
		
		List<Integer> areaIds = new ArrayList<>();
		
		
		OAuth2Authentication authentication = (OAuth2Authentication)SecurityContextHolder.getContext().getAuthentication();

		UserModel userModel = tokenInfoExtracter.getUserModelInfo(authentication);
		
		UserDetails userDetails = userDetailsRepository.findOne(userModel.getId());
		boolean isAdmin = userDetails.getDepartment() == null?true:false;
		Area district = null;
		List<Data> data = new ArrayList<>();
		if(isAdmin) {
			data = dataJpaRepository.findAllByIsLiveTrue();
		}else {
			district = userDetails.getArea();
			
			if(district != null) {
				areaCode = district.getCode();
			}else {
				district = areaJpaRepository.findByCode(areaCode);
			}
			
			//Getting blocks
			List<Area> areas = areaJpaRepository.findAllByParentAreaCodeOrderById(areaCode);
			areas.add(district);
			areas.stream().forEach(a->areaIds.add(a.getId()));
			data = dataJpaRepository.findAllByDepartmentIdAndAreaIdInAndIsLiveTrue(userDetails.getDepartment().getId(), areaIds);
			
		}

		
		
		
		try {
			
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			URL url = loader.getResource("images/cap-header.jpg");
			String path1 = url.getPath().replaceAll("%20", " ");
			File file = new File(path1);
			
			BufferedImage bImage = ImageIO.read(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(bImage, "jpg", bos);

			String date = new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date());
			String path = outputPathExcel;
			outputPathExcel = path + "_" + date + ".xlsx";

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("cap");
			sheet.createFreezePane(0, 5);
			
			
			XSSFRow row = null;
			XSSFCell cell = null;
			int rowNum = 4;
			int cellNum = 0;
			int rowHeaderMergeVaue=0;
			XSSFCellStyle cellstyleFont = CAPUtil.getHeaderStyle(workbook);
			XSSFCellStyle wrap = CAPUtil.wrap(workbook);
			XSSFCellStyle border = CAPUtil.getEnableEditCell(workbook);
			
			
			//1st row, the header
			row = sheet.createRow(rowNum);
			
			cell = row.createCell(cellNum);
			cell.setCellValue("SL.NO");
			cell.setCellStyle(cellstyleFont);
			rowHeaderMergeVaue++;
			
			cell = row.createCell(++cellNum);
			cell.setCellValue("Department");
			cell.setCellStyle(cellstyleFont);
			sheet.autoSizeColumn(cellNum);
			sheet.setColumnWidth(cellNum, 7000);
			rowHeaderMergeVaue++;
			
			cell = row.createCell(++cellNum);
			cell.setCellValue("Theme");
			cell.setCellStyle(cellstyleFont);
			sheet.setColumnWidth(cellNum, 8000);
			rowHeaderMergeVaue++;
			
			cell = row.createCell(++cellNum);
			cell.setCellValue("Indicator");
			cell.setCellStyle(cellstyleFont);
			sheet.autoSizeColumn(cellNum);
			sheet.setColumnWidth(cellNum, 12000);
			rowHeaderMergeVaue++;
			
			
			cell = row.createCell(++cellNum);
			cell.setCellValue("Source");
			cell.setCellStyle(cellstyleFont);
			rowHeaderMergeVaue++;
			
			cell = row.createCell(++cellNum);
			cell.setCellValue("Time period");
			cell.setCellStyle(cellstyleFont);
			sheet.autoSizeColumn(cellNum);
			rowHeaderMergeVaue++;
			
			cell = row.createCell(++cellNum);
			cell.setCellValue("Area");
			cell.setCellStyle(cellstyleFont);
			sheet.autoSizeColumn(cellNum);
			sheet.setColumnWidth(cellNum, 3000);
			rowHeaderMergeVaue++;
			
			cell = row.createCell(++cellNum);
			cell.setCellValue("Value");
			cell.setCellStyle(cellstyleFont);
			sheet.autoSizeColumn(cellNum);
			rowHeaderMergeVaue++;
			
			
			
			//initial row headings
			sheet = workbook.getSheet("cap");
			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("CAP : Raw Data Report");
			cell.setCellStyle(cellstyleFont);
			sheet = CAPUtil.doMerge(0, 0, 0, rowHeaderMergeVaue-1, sheet);
			
			row = sheet.createRow(1);
			cell = row.createCell(0);
			cell.setCellStyle(cellstyleFont);
			if(isAdmin) {
				cell.setCellValue("STATE : Bihar, DISTRICT : All Districts");
			}else {
				cell.setCellValue("STATE : Bihar, DISTRICT : " + district.getAreaname());
			}
			
			sheet = CAPUtil.doMerge(1, 1, 0, rowHeaderMergeVaue-1, sheet);
			
			row = sheet.createRow(2);
			cell = row.createCell(0);
			cell.setCellStyle(cellstyleFont);
			if(isAdmin) {
				cell.setCellValue("DEPARTMENT : All Departments");
			}else {
				cell.setCellValue("DEPARTMENT :" +userDetails.getDepartment().getDepartmentName());
			}
			
			sheet = CAPUtil.doMerge(2, 2, 0, rowHeaderMergeVaue-1, sheet);
			
			
			row = sheet.createRow(3);
			cell = row.createCell(0);
			cell.setCellStyle(cellstyleFont);
			cell.setCellValue("Date Of Report Generation : "+new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
			sheet = CAPUtil.doMerge(3, 3, 0, rowHeaderMergeVaue-1, sheet);
			
			row = sheet.createRow(rowNum+1);
			
			int i = 0;
			for(Data d: data) {
				
				cellNum = 0;
				row = sheet.createRow(++rowNum);
				
				//slno
				cell = row.createCell(cellNum++);
				cell.setCellValue(++i);
				cell.setCellStyle(border);
				
				cell = row.createCell(cellNum++);
				cell.setCellValue(d.getDepartment().getDepartmentName());
				cell.setCellStyle(wrap);
				
				cell = row.createCell(cellNum++);
				cell.setCellValue(d.getSector().getSectorName());
				cell.setCellStyle(wrap);
				
				cell = row.createCell(cellNum++);
				cell.setCellValue(d.getIndicator().getiName());
				cell.setCellStyle(wrap);
				
				cell = row.createCell(cellNum++);
				cell.setCellValue(d.getSrc().getSourceName());
				cell.setCellStyle(wrap);
				
				cell = row.createCell(cellNum++);
				cell.setCellValue(d.getTp());
				cell.setCellStyle(wrap);
				
				cell = row.createCell(cellNum++);
				cell.setCellValue(d.getArea().getAreaname());
				cell.setCellStyle(wrap);
				
				cell = row.createCell(cellNum++);
				cell.setCellValue(d.getValue());
				cell.setCellStyle(border);
				
				
			}
			
			
			
			FileOutputStream fileOutputStream = new FileOutputStream(new File(outputPathExcel));
			workbook.write(fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
			workbook.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outputPathExcel;
	
	}

}
