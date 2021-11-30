package top.zsmile.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.Request;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.helper.StringUtil;
import top.zsmile.utils.OkHttpUtils;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *  @author: B.Smile
 *  @Date: 2021/11/30 9:10
 *  @Description: Meituan Crawler tool
 */
public class MeituanDemo {

    private String url = "http://jianxian.meituan.com/";

    private String urlMeishi = "http://jianxian.meituan.com/meishi/{}";


    private static int page = 1;

    private static int rowCount = 1;

    private static int totalCount = 0;

    private static int offsetCount = 0;


    //http://www.javashuo.com/article/p-awrsbkvz-et.html
    //http://www.javashuo.com/article/p-qxgpqqbz-gn.html

    public static void getMeishiList(XSSFSheet sheet) throws UnsupportedEncodingException, InterruptedException {
        if (page == 1) {
            XSSFRow row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue("PoiId");
            row.createCell(1).setCellValue("店名");
            row.createCell(2).setCellValue("品牌名");
            row.createCell(3).setCellValue("地址");
            row.createCell(4).setCellValue("开放时间");
            row.createCell(5).setCellValue("电话");
            row.createCell(6).setCellValue("经纬度");
            row.createCell(7).setCellValue("平均评分");
        }

        Map<String, String> header = new HashMap<>();

        header.put("Cookie", "uuid=b66e0f2c11c0428da6d8.1638172759.1.0.0; mtcdn=K; userTicket=PaVdEneOlyLsqelcwIxcKMaWcXdJEoRGzFvEKhDF; _yoda_verify_resp=7NAE%2BDaPaDgJ17jKpCxzGe%2BruU3rHei%2FN7CYgRFPa%2FmR%2FU%2BXsAM6Cu2RO7FL5st04yAZOxDb1oG6WnpeOSbcmJsJVRAGvOoAzxZ2GPcyDewGQ8rMSzR%2BGiv0nvt6Yr%2FNCWsBr83Udur4bHsdiUh8CZdfxz1CrEOjkNq89BYZtBMiGh6bAHQTV55blqGF1bBgSS5FcN%2Fc5y%2B0niSbkJxJ%2BnXzgqIGdKXWLsufjrhyHAEpRNHTVXW93RN2lhLbr2YTLZu6KGscUQIUIXKqyf3vU3nIeETifFZfDJuQJOCeCSHZoDeVk4tGg3k8e8dCJm0anxuV8OO6xsu9GgKni8FO%2Fg%3D%3D; _yoda_verify_rid=14558a6689423049; u=334950208; n=%E4%B8%80%E4%B8%80%E4%B8%80%E4%B8%80%E4%B8%AB; lt=yj_ANgdLNQdK27xaTsAwDZ2RakQAAAAAVg8AAH11lpeOKap900dIbGTwq8z6YTvpDEQ0fW53EkW1AKzqFTTQgrOMv3P0ksrPN5MgKw; mt_c_token=yj_ANgdLNQdK27xaTsAwDZ2RakQAAAAAVg8AAH11lpeOKap900dIbGTwq8z6YTvpDEQ0fW53EkW1AKzqFTTQgrOMv3P0ksrPN5MgKw; token=yj_ANgdLNQdK27xaTsAwDZ2RakQAAAAAVg8AAH11lpeOKap900dIbGTwq8z6YTvpDEQ0fW53EkW1AKzqFTTQgrOMv3P0ksrPN5MgKw; lsu=; token2=yj_ANgdLNQdK27xaTsAwDZ2RakQAAAAAVg8AAH11lpeOKap900dIbGTwq8z6YTvpDEQ0fW53EkW1AKzqFTTQgrOMv3P0ksrPN5MgKw; unc=%E4%B8%80%E4%B8%80%E4%B8%80%E4%B8%80%E4%B8%AB; _lxsdk_cuid=17d6ab6b8a37d-0d037bb5de391a-4343363-1fa400-17d6ab6b8a4c8; _lxsdk=17d6ab6b8a37d-0d037bb5de391a-4343363-1fa400-17d6ab6b8a4c8; webloc_geo=22.538069%2C113.917659%2Cwgs84; ci=30; firstTime=1638172905505; _lxsdk_s=17d6ab6b8a5-2a5-afb-33a%7C%7C5");
//        header.put("Content-Type", convertStr("application/json; charset=utf-8"));
        header.put("Accept", "*/*");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Accept-Language", "zh-CN,zh;q=0.9");
        header.put("Connection", "keep-alive");
        header.put("Host", "jianxian.meituan.com");
        header.put("Referer", "https://jianxian.meituan.com/meishi/pn" + page + "/");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36");
//        header.put("sec-ch-ua", convertStr("\"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"92\""));
//        header.put("sec-ch-ua-mobile", convertStr("?0"));
//        header.put("Sec-Fetch-Dest", convertStr("empty"));
//        header.put("Sec-Fetch-Mode", convertStr("cors"));
//        header.put("Sec-Fetch-Site", convertStr("same-origin"));

        Request.Builder builder = new Request.Builder();

        if (header != null && header.keySet().size() > 0) {
            header.forEach(builder::addHeader);
        }

        System.out.println("==========第" + page + "页==========");
        Request request = builder.url("https://jianxian.meituan.com/meishi/api/poi/getPoiList?cityName=%E5%90%89%E5%AE%89%E5%8E%BF&cateId=0&areaId=0&sort=&dinnerCountAttrId=&page="+page+"&userId=334950208&uuid=b66e0f2c11c0428da6d8.1638172759.1.0.0&platform=1&partner=126&originUrl=https%3A%2F%2Fjianxian.meituan.com%2Fmeishi%2Fpn1%2F&riskLevel=1&optimusCode=10&" +
                "_token=eJxVT9tyqkAQ%2FJd9lZJdboJvalRE4oEInJBTeeAmCyygsAvBVP49m0rycKpmqnt6uqtm3kF3SMESQWhAKIAh68ASoDmca0AAtOcbTdbRQkaKqmuSAJL%2FNQMaAoi74AEs%2FyFDgoKh6a9fyhMXvhUEdfgq%2FHKFc0nh9eU6cBPAlF77pSiWRdS88Z7XWUEZx6StRc57XIjXBon8HsBDtcdDHKsfjH6Q%2Fs6P%2FAHu7Yu84SyzRlL6lK3K7SNm4ubQM33dW%2B7RHbY3YgfP7sz18jG0cp14eWCGOwdZNtp4d0eOji27NNLFPg3ihIPxbqjHU1lvLnCvDYt2lQ5Es08zR738MbNwfJqKPTqbrIA0Daf2jJHvTKEVVLdsZSmIvSxeqhZNCbZZWXRhYrr7W%2BqPtyK7S%2BW021qEStdGDn2tn1HYxkRKt28kfTDX2YDWnpoviOodEhrhWKzDoBxOxLar%2BO9e7pR4JR5bS%2FNrlkZlqkfOs0VwsGOGnE2KcrVtdNbPuDIC8PEJ6diX7g%3D%3D").build();
        String body1 = OkHttpUtils.getBody(request, true);
        if (!StringUtil.isBlank(body1)) {
            JSONObject resJson = JSON.parseObject(body1);
            System.out.println(resJson);
            JSONObject data = resJson.getJSONObject("data");
            totalCount = data.getIntValue("totalCounts");
            JSONArray poiInfos = data.getJSONArray("poiInfos");
            offsetCount += poiInfos.size();
            for (int i = 0; i < poiInfos.size(); i++) {
                String poiId = poiInfos.getJSONObject(i).getString("poiId");
                Thread.sleep(200);
                getShopInfo(sheet, poiId);
            }
            if (offsetCount < totalCount) {
                page++;
                getMeishiList(sheet);
            }
        }
    }

    public static void getShopInfo(XSSFSheet sheet, String poiId) {
        Map<String, String> header = new HashMap<>();

        header.put("Cookie", "client-id=4af25ac0-4ff9-4efe-a9a2-1f15b619a67e; uuid=b66e0f2c11c0428da6d8.1638172759.1.0.0; mtcdn=K; userTicket=PaVdEneOlyLsqelcwIxcKMaWcXdJEoRGzFvEKhDF; _yoda_verify_resp=7NAE%2BDaPaDgJ17jKpCxzGe%2BruU3rHei%2FN7CYgRFPa%2FmR%2FU%2BXsAM6Cu2RO7FL5st04yAZOxDb1oG6WnpeOSbcmJsJVRAGvOoAzxZ2GPcyDewGQ8rMSzR%2BGiv0nvt6Yr%2FNCWsBr83Udur4bHsdiUh8CZdfxz1CrEOjkNq89BYZtBMiGh6bAHQTV55blqGF1bBgSS5FcN%2Fc5y%2B0niSbkJxJ%2BnXzgqIGdKXWLsufjrhyHAEpRNHTVXW93RN2lhLbr2YTLZu6KGscUQIUIXKqyf3vU3nIeETifFZfDJuQJOCeCSHZoDeVk4tGg3k8e8dCJm0anxuV8OO6xsu9GgKni8FO%2Fg%3D%3D; _yoda_verify_rid=14558a6689423049; u=334950208; n=%E4%B8%80%E4%B8%80%E4%B8%80%E4%B8%80%E4%B8%AB; lt=yj_ANgdLNQdK27xaTsAwDZ2RakQAAAAAVg8AAH11lpeOKap900dIbGTwq8z6YTvpDEQ0fW53EkW1AKzqFTTQgrOMv3P0ksrPN5MgKw; mt_c_token=yj_ANgdLNQdK27xaTsAwDZ2RakQAAAAAVg8AAH11lpeOKap900dIbGTwq8z6YTvpDEQ0fW53EkW1AKzqFTTQgrOMv3P0ksrPN5MgKw; token=yj_ANgdLNQdK27xaTsAwDZ2RakQAAAAAVg8AAH11lpeOKap900dIbGTwq8z6YTvpDEQ0fW53EkW1AKzqFTTQgrOMv3P0ksrPN5MgKw; lsu=; token2=yj_ANgdLNQdK27xaTsAwDZ2RakQAAAAAVg8AAH11lpeOKap900dIbGTwq8z6YTvpDEQ0fW53EkW1AKzqFTTQgrOMv3P0ksrPN5MgKw; unc=%E4%B8%80%E4%B8%80%E4%B8%80%E4%B8%80%E4%B8%AB; _lxsdk_cuid=17d6ab6b8a37d-0d037bb5de391a-4343363-1fa400-17d6ab6b8a4c8; _lxsdk=17d6ab6b8a37d-0d037bb5de391a-4343363-1fa400-17d6ab6b8a4c8; webloc_geo=22.538069%2C113.917659%2Cwgs84; ci=30; firstTime=1638173073228; _lxsdk_s=17d6ab6b8a5-2a5-afb-33a%7C%7C13");
//        header.put("Content-Type", convertStr("application/json; charset=utf-8"));
        header.put("Accept", "*/*");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Accept-Language", "zh-CN,zh;q=0.9");
        header.put("Connection", "keep-alive");
        header.put("Host", "jianxian.meituan.com");
        header.put("Referer", "https://jianxian.meituan.com/meishi/pn" + page + "/");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36");
        Request.Builder builder = new Request.Builder();

        if (header != null && header.keySet().size() > 0) {
            header.forEach(builder::addHeader);
        }

        Request request = builder.url("https://jianxian.meituan.com/meishi/" + poiId + "/").build();
        String body1 = OkHttpUtils.getBody(request, true);
        Pattern p = Pattern.compile("window._appState = (.*?);</script>");
        Matcher m = p.matcher(body1);
        if (m.find()) {
            System.out.println("poiId " + poiId + ", " + m.group());
            JSONObject group = JSON.parseObject(m.group(1));
            JSONObject detailInfo = group.getJSONObject("detailInfo");

            String id = detailInfo.getString("poiId");
            String name = detailInfo.getString("name");
            String brandName = detailInfo.getString("brandName");
            String address = detailInfo.getString("address");
            String openTime = detailInfo.getString("openTime");
            String phone = detailInfo.getString("phone");
            String latitude = detailInfo.getString("latitude");
            String longitude = detailInfo.getString("longitude");
            String avgScore = detailInfo.getString("avgScore");

            XSSFRow row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(id);
            row.createCell(1).setCellValue(name);
            row.createCell(2).setCellValue(brandName);
            row.createCell(3).setCellValue(address);
            row.createCell(4).setCellValue(openTime);
            row.createCell(5).setCellValue(phone);
            row.createCell(6).setCellValue(latitude + "," + longitude);
            row.createCell(7).setCellValue(avgScore);

        }
    }

    public static void loop() throws IOException, InterruptedException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");
        getMeishiList(sheet);
        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd HHmmss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        workbook.write(new FileOutputStream(new File("美团-吉安美食-" + sdf.format(date) + ".xlsx")));
        workbook.close();
        System.out.println("爬取完成");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        loop();

    }


}
