package jiyue;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

public class CsvWriter {

	public static void main(String[] args) {
		try {
			FileOutputStream fos = new FileOutputStream("file/writerTest.csv");
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			String content = "111,222,333";
			bos.write(content.getBytes(), 0, content.getBytes().length);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
