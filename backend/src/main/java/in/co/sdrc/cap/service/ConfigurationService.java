package in.co.sdrc.cap.service;

import java.util.Date;

public interface ConfigurationService {

	String createOneAutherity(String name, String description);

	String createOneDesignation(String name, String code);

	String createOneDesignationAutherityMapping(String designationCode, String autherityName);

	boolean configureSQLDb();

	boolean importAreaIntoSQL(Date currentDate);

	boolean configurePeriodicity();

	boolean configureTargetValue();

	boolean importDataIntoSQLDb(Date currentDate);

}