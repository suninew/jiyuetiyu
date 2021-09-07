package jiyue;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class FetchMatchResults {
	private static String[] companies = { "0", "293" };

	private static String baiJiaWeiLianUrl;

	private static String starttime;
	private static String gscore;
	private static String hscore;
	private static String gname;
	private static String hname;
	private static String fidString;
	private static String stateid;
	private static String content;

	public static void main(String[] args) throws IOException {

		String url = "http://p.pengyd.com/firstscore/soccer/matchlist?cid=3&d=1&t=2&token=NldheDcxdXl6b0t5RXNXTkhiTitjR3ZySUxKeDRNNUI2bFJBcC83YmE4dz0%3D&type=1";
		// 初始化一個csv
		FileWriter fw = new FileWriter("file/writerTest.csv");

		content = "日期,公司,比赛队伍,赢(最新),平(最新),输(最新),赢(最旧),平(最旧),输(最旧),最新时间,最旧时间";
		fw.write(content);
		fw.write("\r\n");

		for (int d = 0; d < 7; d++) {
			url = "http://p.pengyd.com/firstscore/soccer/matchlist?cid=3&d=" + d
					+ "&t=2&token=NldheDcxdXl6b0t5RXNXTkhiTitjR3ZySUxKeDRNNUI2bFJBcC83YmE4dz0%3D&type=1";
			// Fetch Result from URL
			JSONObject responseJsonObject = JSONObject.parseObject(fetchResults(url));

			JSONObject jsonObjectDataJsonObject = responseJsonObject.getJSONObject("data");
			JSONArray jsonArray = jsonObjectDataJsonObject.getJSONArray("list");

			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
				starttime = jsonObjectItem.getString("starttime");
				gscore = jsonObjectItem.getString("gscore");
				hscore = jsonObjectItem.getString("hscore");
				gname = jsonObjectItem.getString("gname");
				hname = jsonObjectItem.getString("hname");
				fidString = jsonObjectItem.getString("eid");
				stateid = jsonObjectItem.getString("stateid");

				if (gscore != null && hscore != null && stateid != null) {
					if ((Integer.valueOf(gscore) == Integer.valueOf(hscore)) && (Integer.valueOf(stateid) == 4)) {
						System.out.println("已抓取到数据： " + starttime + "---------" + gname + "-" + hname + " : " + gscore
								+ "-" + hscore);

						for (String compId : companies) {
							try {
								fetchOddsContent(compId);
								fw.write(content);
								fw.write("\r\n");
							} catch (IndexOutOfBoundsException e) {
								// TODO: handle exception
							}

						}
					}
				}
			}
		}
		fw.close();

	}

	private static void fetchOddsContent(String compId) {
		String tempbaiJiaWeiLianUrl = "http://p.pengyd.com/firstscore/fruit/oneapple?cid=" + compId + "&fid="
				+ fidString + "&sweet=1&token=NldheDcxdXl6b0t5RXNXTkhiTitjR3ZySUxKeDRNNUI2bFJBcC83YmE4dz0%3D";
		String companyName = "";
		if (compId.contains("0")) {
			companyName = "百家欧赔";
		} else {
			companyName = "威廉希尔";
		}
		try {
			JSONObject baiJiaJson = JSONObject.parseObject(fetchResults(tempbaiJiaWeiLianUrl));
			JSONArray baiJiaDataJsonArray = baiJiaJson.getJSONArray("data");

			JSONObject latestBaiJiaStat = (JSONObject) baiJiaDataJsonArray.get(0);

			String latestWinString = latestBaiJiaStat.getString("win");
			String latestDrawString = latestBaiJiaStat.getString("draw");
			String latestLostString = latestBaiJiaStat.getString("lost");
			String latestTimeString = latestBaiJiaStat.getString("time");

			JSONObject eldestBaiJiaStat = (JSONObject) baiJiaDataJsonArray.get(baiJiaDataJsonArray.size() - 1);

			String eldestWinString = eldestBaiJiaStat.getString("win");
			String eldestDrawString = eldestBaiJiaStat.getString("draw");
			String eldestLostString = eldestBaiJiaStat.getString("lost");
			String eldestTimeString = eldestBaiJiaStat.getString("time");

			content = "'" + starttime + "," + companyName + "," + hname + " - " + gname + "(" + hscore + "-" + gscore
					+ ")" + "," + latestWinString + "," + latestDrawString + "," + latestLostString + ","
					+ eldestWinString + "," + eldestDrawString + "," + eldestLostString + ",'" + latestTimeString + ",'"
					+ eldestTimeString;
		} catch (NullPointerException e) {
			System.out.println("导出" + tempbaiJiaWeiLianUrl + "时有异常！请试着重跑");
			System.exit(0);
		} catch (IndexOutOfBoundsException e) {
//			System.out.println("");
		}

	}

	private static String fetchResults(String url) {
		StringBuffer sb = new StringBuffer();
		try {
			URL urls = new URL(url);
			HttpURLConnection uc = (HttpURLConnection) urls.openConnection();
			uc.setRequestMethod("POST");
			uc.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			uc.setRequestProperty("charset", "UTF-8");
			uc.setRequestProperty("User-Agent", "ji yue ti yu/2.1.4 (iPad; iOS 14.4; Scale/2.00)");
			uc.setRequestProperty("platform", "1");
			uc.setRequestProperty("Connection", "keep-alive");
			uc.setRequestProperty("appfrom", "dybf");
			uc.setRequestProperty("appversion", "V2.1.4");
			uc.setRequestProperty("Accept-Language", "zh-Hans-CN;q=1, zh-Hant-CN;q=0.9, ja-CN;q=0.8");
			uc.setRequestProperty("Content-Length", "89");

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
