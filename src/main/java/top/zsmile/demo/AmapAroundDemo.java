package top.zsmile.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import top.zsmile.utils.OkHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AmapAroundDemo {
    private final static String API_KEY = "";

    //当前页
    private static int page = 1;
    //每页显示数据
    private static int offset = 25;

    public static void main(String[] args) {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");
        int rowCount = 0;
        searchPoi(sheet, rowCount, page, offset);
        try {
            workbook.write(new FileOutputStream(new File(UUID.randomUUID() + ".xlsx")));
            workbook.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    /**
     * 搜索周边服务
     * api文档：https://lbs.amap.com/api/webservice/guide/api/search
     * 操作步骤：
     * 1. 替换API_KEY，需要使用绑定web服务
     * 2. 使用坐标拾取器，获取经纬度坐标>https://lbs.amap.com/tools/picker，并将searchPoi里面的location修改
     */
    public static void searchPoi(XSSFSheet sheet, int rowCount, int page, int offset) {
        if (page == 1) {
//            System.out.println("省份\t城市\t区域\t名称\t电话\tPOI编号\tPOI种类\t距离中心位置\t经纬度");
            XSSFRow row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue("省份");
            row.createCell(1).setCellValue("城市");
            row.createCell(2).setCellValue("区域");
            row.createCell(3).setCellValue("地址");
            row.createCell(4).setCellValue("名称");
            row.createCell(5).setCellValue("电话");
            row.createCell(6).setCellValue("POI编号");
            row.createCell(7).setCellValue("POI种类");
            row.createCell(8).setCellValue("距离中心位置");
            row.createCell(9).setCellValue("经纬度");
        }
        Map<String, String> params = new HashMap<>();
        params.put("key", API_KEY); // 开发API
        params.put("keywords", "医院"); //多个关键字用“|”分割
        params.put("location", "113.936683,22.532681"); // 经度和纬度用","分割，经度在前，纬度在后，经纬度小数点后不得超过6位
        params.put("radius", "5000"); // 0-50000。规则：大于50000按默认值，单位：米
        params.put("sortrule", "distance"); // 按距离排序：distance；综合排序：weight
        params.put("offset", offset + "");
        params.put("page", page + "");
        String resStr = OkHttpUtils.get("https://restapi.amap.com/v3/place/around", params);
        if (resStr != null) {
            JSONObject resJson = JSON.parseObject(resStr);
            if (resJson.getIntValue("status") == 1) {
                int count = resJson.getIntValue("count");
//                System.out.println("请求成功，总行数：" + count);

                //遍历信息
                JSONArray pois = resJson.getJSONArray("pois");
                for (int i = 0; i < pois.size(); i++) {
                    JSONObject info = pois.getJSONObject(i);
                    String pname = info.getString("pname");//省份
                    String cityname = info.getString("cityname");//城市
                    String adname = info.getString("adname");//区域
                    String address = info.getString("address");//区域
                    String name = info.getString("name");//名称
                    String tel = info.getString("tel");//电话
                    String type = info.getString("type");//POI编号
                    String typecode = info.getString("typecode");//POI种类
                    String distance = info.getString("distance");//距离中心位置
                    String location = info.getString("location");//经纬度
//                    System.out.println(pname + "\t" + cityname + "\t" + adname + "\t" + name + "\t" + tel + "\t" + type + "\t" + typecode + "\t" + distance + "\t" + location);

                    XSSFRow row = sheet.createRow(rowCount++);
                    row.createCell(0).setCellValue(pname);
                    row.createCell(1).setCellValue(cityname);
                    row.createCell(2).setCellValue(adname);
                    row.createCell(3).setCellValue(address);
                    row.createCell(4).setCellValue(name);
                    row.createCell(5).setCellValue(tel);
                    row.createCell(6).setCellValue(type);
                    row.createCell(7).setCellValue(typecode);
                    row.createCell(8).setCellValue(distance + "米");
                    row.createCell(9).setCellValue(location);
                }
                if (page * offset < count) {
                    searchPoi(sheet, rowCount, page + 1, offset);
                }

            } else {
                System.out.println("请求失败：" + resJson.getString("info"));
            }
        } else {
            System.out.println("查找失败：" + resStr);
        }
    }
}
