package jingcai;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class FetchMatchResults {
	private static String url;
	private static String content;
	private static String lostRate;
	private static String drawRate;
	private static String winRate;
	private static String matchNumStr;
	private static String homeTeam;
	private static String awayTeam;
	private static String goalLine;
	private static Integer total;
	private static Logger logger = Logger.getLogger(FetchMatchResults.class);
	private static ArrayList<String> dayLists = new ArrayList<String>();

	// sectionsNo999
	private static String finalMatchResult;

	public static void main(String[] args) throws IOException, ParseException {
		// ��ʼ��һ��csv
		FileWriter fw = new FileWriter("file/��������������.csv");

		content = "��������,���±��,���ӣ�����vs�Ͷ�,ȫ���ȷ�,ʤ,ƽ,��,ʤ������";
		fw.write(content);
		fw.write("\r\n");

		// �����������ں���
		generateAllDates("2020-01-01");
		for (String date : dayLists) {
			url = "https://webapi.sporttery.cn/gateway/jc/football/getMatchResultV1.qry?matchPage=1&matchBeginDate="
					+ date + "&matchEndDate=" + date + "&pageSize=10000000";
			// Fetch Result from URL
			JSONObject responseJsonObject = JSONObject.parseObject(fetchResults(url));
			total = responseJsonObject.getInteger("total");

			JSONObject jsonObjectDataJsonObject = responseJsonObject.getJSONObject("value");

			Integer total = jsonObjectDataJsonObject.getInteger("total");

			JSONArray jsonArray = jsonObjectDataJsonObject.getJSONArray("matchResult");

			if (total != 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);

					lostRate = jsonObjectItem.getString("a").equals("") ? " -- " : jsonObjectItem.getString("a");
					drawRate = jsonObjectItem.getString("d").equals("") ? " -- " : jsonObjectItem.getString("d");
					winRate = jsonObjectItem.getString("h").equals("") ? " -- " : jsonObjectItem.getString("h");

					matchNumStr = jsonObjectItem.getString("matchNumStr");
					homeTeam = jsonObjectItem.getString("homeTeam");
					awayTeam = jsonObjectItem.getString("awayTeam");
					matchNumStr = jsonObjectItem.getString("matchNumStr");
					goalLine = jsonObjectItem.getString("goalLine");
					finalMatchResult = jsonObjectItem.getString("sectionsNo999");
					content = " " + date + " ," + matchNumStr + "," + homeTeam + "(" + goalLine + ") VS " + awayTeam
							+ ", " + finalMatchResult + " ," + winRate + "," + drawRate + "," + lostRate;

					if (finalMatchResult.contentEquals("ȡ��"))
						logger.info("���� " + date + " ���� " + content + " ȡ����");
					else {
						content = content + "," + anaylstMatchResult();
						logger.info("��ץȡ�����ݣ� " + content + " !");
						fw.write(content);
						fw.write("\r\n");
					}
				}
				logger.info("���� " + date + " ���б���(�ܹ�" + total + "��)ץȡ������");
			} else {
				System.out.println("���� " + date + " û�б�����");
				logger.info("���� " + date + " û�б�����");

			}
		}
		fw.close();
		System.out.println("��������������ץȡ������");

	}

	private static String anaylstMatchResult() {
		int homeResult = Integer.valueOf(finalMatchResult.split(":")[0]);
		int awayResult = Integer.valueOf(finalMatchResult.split(":")[1]);
		if (homeResult > awayResult) {
			return "��ʤ";
		} else if (homeResult < awayResult) {
			return "��ʤ";
		} else {
			return "ƽ��";

		}
	}

	private static void generateAllDates(String startDate) throws ParseException {
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
		String today = date.format(new Date());
		long startTime = date.parse(startDate).getTime();// start
		long endTime = date.parse(today).getTime();// end
		long day = 1000 * 60 * 60 * 24;
		for (long i = startTime; i <= endTime; i += day)
			dayLists.add(date.format(new Date(i)));

	}

	private static String fetchResults(String url) {
		StringBuffer sb = new StringBuffer();
		try {
			URL urls = new URL(url);
			HttpURLConnection uc = (HttpURLConnection) urls.openConnection();
			uc.setRequestMethod("GET");
			uc.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Safari/537.36");
			uc.setRequestProperty("Accept-Language", "h-CN,zh;q=0.9");

			uc.setDoOutput(true);
			uc.setDoInput(true);
			uc.setReadTimeout(50000);
			uc.setConnectTimeout(50000);//

			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), "utf-8"));
			String readLine = "";
			while ((readLine = in.readLine()) != null) {
				sb.append(readLine);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
