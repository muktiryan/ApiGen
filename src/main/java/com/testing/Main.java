package com.testing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main {
	public static void main(String[] args) {
		String startTime = "08:00:00";
		Main app = new Main();
		try {
			System.out.println(app.timeToDecimal(startTime));
		} catch (ParseException e) {
			System.out.println("ParseException : "+e.getMessage());
		}
	}
	public Integer timeToString(String timeInput) throws ParseException {
		Integer result = 0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date userDate = dateFormat.parse(timeInput);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(userDate);
		result = (int) calendar.getTimeInMillis();
		return result;
	}
	public String timeToStringStr(String timeInput) throws ParseException {
		String result = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date userDate = dateFormat.parse(timeInput);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(userDate);
		result = calendar.getTimeInMillis()+"";		
		return result;
	}
	public Integer timeToDecimal(String timeInput) throws ParseException {
		Integer result = 0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date userDate = dateFormat.parse(timeInput);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(userDate);
		
		
		int hours = calendar.getTime().getHours() ;
		int minutes = calendar.getTime().getMinutes();
		int second = calendar.getTime().getSeconds();
		result = ((hours*60)*60)+(minutes*60)+(second);
		
//		result = second;
		
		
		return result;
	}
}
