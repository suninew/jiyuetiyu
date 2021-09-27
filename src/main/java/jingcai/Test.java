package jingcai;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

	public static void main(String[] args) throws ParseException {
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

		System.out.println(date.format(new Date()));
		System.exit(0);
		String dateStart = "2018-01-01";
		String dateEnd = "2019-12-31";
		long startTime = date.parse(dateStart).getTime();// start
		long endTime = date.parse(dateEnd).getTime();// end
		long day = 1000 * 60 * 60 * 24;
		for (long i = startTime; i <= endTime; i += day) {
			System.out.println(date.format(new Date(i)));
		}

	}

}
