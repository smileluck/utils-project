package top.zsmile.runner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;

public class FileLoadApplication {

    private final static String path = "D:\\project\\Senlink\\aojia\\澳嘉文化官网\\doc\\服务项目\\服务项目";

    public static void main(String[] args) {
        File root = new File(path);
        JSONObject jsonObject = new JSONObject();
        iterateFile(jsonObject, root);
        System.out.println(jsonObject.toJSONString());
    }

    public static void iterateFile(JSONObject parentObject, File root) {
        JSONArray jsonArray = new JSONArray();
        File[] files = root.listFiles();
        for (File file : files) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", file.getName());
            if (file.isDirectory()) {
                jsonObject.put("type", "directory");
                iterateFile(jsonObject, file);
            } else if (file.isFile()) {
                jsonObject.put("type", "file");
            }
            jsonArray.add(jsonObject);
        }
        parentObject.put("childrens", jsonArray);
    }
}
