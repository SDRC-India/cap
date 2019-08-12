package in.co.sdrc.cap.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import in.co.sdrc.cap.domain.TimePeriod;
import in.co.sdrc.cap.repository.PeriodicityRepository;
import in.co.sdrc.cap.repository.TimePeriodRepository;

@Service
public class TimePeriodServiceImpl implements TimePeriodService {
	
	@Autowired
	private TimePeriodRepository timePeriodRepository;
	
	@Autowired
	private PeriodicityRepository periodicityRepository;
	
	@Scheduled(cron="0 2 0 1 1/1 ?")
	@Override
	public void createMonthlyTimePeriod() {
		
		LocalDateTime time = LocalDateTime.now();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM");
		int month = cal.get(Calendar.MONTH);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		
		TimePeriod timePeriod = new TimePeriod();
		
		Timestamp startDate = new Timestamp(cal.getTime().getTime());
		
		timePeriod.setStartDate(startDate);
		
		String sDate = sdf.format(startDate);
		cal.add(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Timestamp endDate = new Timestamp(cal.getTime().getTime());
		String eDate = sdf.format(endDate);
		
		timePeriod.setEndDate(endDate);
		
		timePeriod.setTimePeriod(sDate.equals(eDate) ? sDate : sDate+"-"+eDate);
		timePeriod.setYear(time.getYear()); // for year
		int preYear =0, nextYear =0;
		if(month > 2){
			preYear = cal.get(Calendar.YEAR);
			cal.add(Calendar.YEAR, 1);
			nextYear = cal.get(Calendar.YEAR);
		} else {
			cal.add(Calendar.YEAR, -1);
			preYear = cal.get(Calendar.YEAR);
			cal.add(Calendar.YEAR, 1);
			nextYear = cal.get(Calendar.YEAR);
		}
		timePeriod.setFinancialYear(preYear+"-"+nextYear);
		timePeriod.setShortName(timePeriod.getTimePeriod()+"-"+timePeriod.getYear());
		timePeriod.setPeriodicity(periodicityRepository.findByPeriodicity(1));
		timePeriodRepository.save(timePeriod);
	}
	
	@Scheduled(cron="0 3 0 1 1/3 ?")
	@Override
	public void createQuarterlyTimePeriod() {
		
		LocalDateTime time = LocalDateTime.now();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM");
		int month = cal.get(Calendar.MONTH);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		
		TimePeriod timePeriod = new TimePeriod();
		
		Timestamp startDate = new Timestamp(cal.getTime().getTime());
		
		timePeriod.setStartDate(startDate);
		
		String sDate = sdf.format(startDate);
		cal.add(Calendar.MONTH, 2);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Timestamp endDate = new Timestamp(cal.getTime().getTime());
		String eDate = sdf.format(endDate);
		
		timePeriod.setEndDate(endDate);
		
		timePeriod.setTimePeriod(sDate.equals(eDate)? sDate : sDate+"-"+eDate);
		timePeriod.setYear(time.getYear()); // for year
		int preYear =0, nextYear =0;
		if(month > 2){
			preYear = cal.get(Calendar.YEAR);
			cal.add(Calendar.YEAR, 1);
			nextYear = cal.get(Calendar.YEAR);
		} else {
			cal.add(Calendar.YEAR, -1);
			preYear = cal.get(Calendar.YEAR);
			cal.add(Calendar.YEAR, 1);
			nextYear = cal.get(Calendar.YEAR);
		}
		timePeriod.setFinancialYear(preYear+"-"+nextYear);
		timePeriod.setShortName(getQuarter()+"("+(timePeriod.getTimePeriod().split("-")[0]+" "+timePeriod.getYear()
		 		+ "-" + timePeriod.getTimePeriod().split("-")[1]+" "+timePeriod.getYear())+")");
		
		timePeriod.setPeriodicity(periodicityRepository.findByPeriodicity(3));
		timePeriodRepository.save(timePeriod);
	}
	
	@Scheduled(cron="0 4 0 1 4/12 ?")
	@Override
	public void createYearlyTimePeriod() {
		
		LocalDateTime time = LocalDateTime.now();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM");
		cal.set(Calendar.DAY_OF_MONTH, 1);
		
		TimePeriod timePeriod = new TimePeriod();
		
		Timestamp startDate = new Timestamp(cal.getTime().getTime());
		
		timePeriod.setStartDate(startDate);
		
		String sDate = sdf.format(startDate);
		cal.add(Calendar.MONTH, 11);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Timestamp endDate = new Timestamp(cal.getTime().getTime());
		String eDate = sdf.format(endDate);
		
		timePeriod.setEndDate(endDate);
		
		timePeriod.setTimePeriod(sDate.equals(eDate) ? sDate : sDate+"-"+eDate);
		timePeriod.setYear(time.getYear()); // for year
		int preYear =0, nextYear =0;
		cal.add(Calendar.YEAR, -1);
		preYear = cal.get(Calendar.YEAR);
		cal.add(Calendar.YEAR, 1);
		nextYear = cal.get(Calendar.YEAR);
		timePeriod.setFinancialYear(preYear+"-"+nextYear);
		timePeriod.setShortName(timePeriod.getTimePeriod().split("-")[0]+" "+preYear
 		+ "-" + timePeriod.getTimePeriod().split("-")[1]+" "+nextYear);
		timePeriod.setPeriodicity(periodicityRepository.findByPeriodicity(12));
		timePeriodRepository.save(timePeriod);
	}
	
	public static String getQuarter() {
		
	    int month = LocalDate.now().get(ChronoField.MONTH_OF_YEAR);
	    switch (Month.of(month)) {
		    case JANUARY:
		    case FEBRUARY:
		    case MARCH:
		    default:
		      return "Q4";
		    case APRIL:
		    case MAY:
		    case JUNE:
		      return "Q1";
		    case JULY:
		    case AUGUST:
		    case SEPTEMBER:
		      return "Q2";
		    case OCTOBER:
		    case NOVEMBER:
		    case DECEMBER:
		      return "Q3";
	    }
	  }

}
