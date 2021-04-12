package top.zsmile.runner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;

/**
 * 读取指定路径下的文件夹等，并生成JSON树
 */
public class FileLoadApplication {

    private final static String path = "D:\\project\\Senlink\\aojia\\澳嘉文化官网\\doc\\服务项目\\服务项目";

    public static void main(String[] args) {
        File root = new File(path);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("path", "/");
        iterateFile(jsonObject, root);
        System.out.println(jsonObject.toJSONString());
    }

    public static void iterateFile(JSONObject parentObject, File root) {
        JSONArray jsonArray = new JSONArray();
        File[] files = root.listFiles();
        for (File file : files) {
            if(file.getName().contains(".txt")||file.getName().contains(".TXT")||file.getName().contains(".BAT")||file.getName().contains(".bat")){
                continue;
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", file.getName());
            if (file.isDirectory()) {
//                jsonObject.put("type", "directory");
                jsonObject.put("path", parentObject.get("path") + file.getName() + "/");
                iterateFile(jsonObject, file);
            } else if (file.isFile()) {
//                jsonObject.put("type", "file");
                jsonObject.put("url", parentObject.get("path") + file.getName());
            }
            jsonArray.add(jsonObject);
        }
        parentObject.put("childrens", jsonArray);
    }
}
