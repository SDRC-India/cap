package in.co.sdrc.cap.util;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class CAPUtil {

	public static XSSFCellStyle getStyleForLeftMiddle(XSSFWorkbook workbook) {

		XSSFCellStyle styleForLeftMiddle = workbook.createCellStyle();
		styleForLeftMiddle.setVerticalAlignment(VerticalAlignment.CENTER);
		styleForLeftMiddle.setAlignment(HorizontalAlignment.CENTER);
		styleForLeftMiddle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleForLeftMiddle.setFillForegroundColor(new XSSFColor(new java.awt.Color(248, 181, 32)));
		styleForLeftMiddle.setFont(getStyleForFont(workbook).getFont());
		return styleForLeftMiddle;
	}

	public static XSSFCellStyle getStyleForFont(XSSFWorkbook workbook) {

		// Create a new font and alter it.
		XSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 15);
		font.setBold(true);

		XSSFCellStyle tyleForWrapFont = workbook.createCellStyle();
		tyleForWrapFont.setFont(font);

		return tyleForWrapFont;
	}
	
	public static XSSFCellStyle getHeaderStyle(XSSFWorkbook workbook) {

		// Create a new font and alter it.
		XSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 13);
		font.setBold(true);

		XSSFCellStyle tyleForWrapFont = workbook.createCellStyle();		
		tyleForWrapFont.setFont(font);
		tyleForWrapFont.setFillForegroundColor(new XSSFColor(new java.awt.Color(169,169,169)));
		tyleForWrapFont.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		tyleForWrapFont.setWrapText(true);
		//
		tyleForWrapFont.setBorderBottom(BorderStyle.THIN);
		tyleForWrapFont.setBorderTop(BorderStyle.THIN);
		tyleForWrapFont.setBorderLeft(BorderStyle.THIN);
		tyleForWrapFont.setBorderRight(BorderStyle.THIN);

		return tyleForWrapFont;
	}
	
	public static XSSFCellStyle getIndicatorCell(XSSFWorkbook workbook) {


		XSSFCellStyle tyleForWrapFont = workbook.createCellStyle();
		tyleForWrapFont.setWrapText(true);
		tyleForWrapFont.setVerticalAlignment(VerticalAlignment.TOP);
		//
		tyleForWrapFont.setBorderBottom(BorderStyle.THIN);
		tyleForWrapFont.setBorderTop(BorderStyle.THIN);
		tyleForWrapFont.setBorderLeft(BorderStyle.THIN);
		tyleForWrapFont.setBorderRight(BorderStyle.THIN);
		return tyleForWrapFont;
	}
	
	public static XSSFCellStyle getThemeDepartmentCell(XSSFWorkbook workbook) {


		XSSFFont font = workbook.createFont();
		font.setBold(true);
		
		XSSFCellStyle tyleForWrapFont = workbook.createCellStyle();
		tyleForWrapFont.setWrapText(true);
		tyleForWrapFont.setFont(font);
		tyleForWrapFont.setVerticalAlignment(VerticalAlignment.CENTER);
		tyleForWrapFont.setAlignment(HorizontalAlignment.CENTER);
		
		//
		tyleForWrapFont.setBorderBottom(BorderStyle.THIN);
		tyleForWrapFont.setBorderTop(BorderStyle.THIN);
		tyleForWrapFont.setBorderLeft(BorderStyle.THIN);
		tyleForWrapFont.setBorderRight(BorderStyle.THIN);
		return tyleForWrapFont;
	}
	
	public static XSSFCellStyle getEnableEditCell(XSSFWorkbook workbook) {

		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setLocked(false);
		
		//
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		return cellStyle;
		
	}
	
	public static XSSFCellStyle getEnableEditCellForTimePeriod(XSSFWorkbook workbook) {

		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setLocked(false);
		
		//
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setDataFormat((short)BuiltinFormats.getBuiltinFormat("text"));
		return cellStyle;
		
	}
	
	public static XSSFCellStyle wrap(XSSFWorkbook workbook) {


		XSSFCellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		
		return style;
	}
	
	public static XSSFCellStyle bold(XSSFWorkbook workbook) {


		XSSFFont font = workbook.createFont();
		font.setBold(true);
		
		XSSFCellStyle tyleForWrapFont = workbook.createCellStyle();
		tyleForWrapFont.setFont(font);
		tyleForWrapFont.setBorderBottom(BorderStyle.THIN);
		tyleForWrapFont.setBorderTop(BorderStyle.THIN);
		tyleForWrapFont.setBorderLeft(BorderStyle.THIN);
		tyleForWrapFont.setBorderRight(BorderStyle.THIN);
		
		return tyleForWrapFont;
	}
	
	/**
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @param rowSpan
	 * @param columnSpan
	 * @param sheet
	 * @return
	 */
	public static XSSFSheet doMerge(int rowIndex, int columnIndex, int rowSpan, int columnSpan, XSSFSheet sheet) {

		Cell cell = sheet.getRow(rowIndex).getCell(rowSpan);
		CellRangeAddress range = new CellRangeAddress(rowIndex, columnIndex, rowSpan, columnSpan);

		sheet.addMergedRegion(range);

		RegionUtil.setBorderBottom(BorderStyle.THIN, range, sheet);
		RegionUtil.setBorderTop(BorderStyle.THIN, range, sheet);
		RegionUtil.setBorderLeft(BorderStyle.THIN, range, sheet);
		RegionUtil.setBorderRight(BorderStyle.THIN, range, sheet);

		RegionUtil.setBottomBorderColor(cell.getCellStyle().getBottomBorderColor(), range, sheet);
		RegionUtil.setTopBorderColor(cell.getCellStyle().getTopBorderColor(), range, sheet);
		RegionUtil.setLeftBorderColor(cell.getCellStyle().getLeftBorderColor(), range, sheet);
		RegionUtil.setRightBorderColor(cell.getCellStyle().getRightBorderColor(), range, sheet);

		return sheet;
	}

}
