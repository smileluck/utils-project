package top.zsmile.runner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import top.zsmile.utils.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 网页读取内容，然后转成需要的json格式
 */
public class PageLoadApplication {


    public static void main(String[] args) {
//        ctad();
        zhihu();
    }


    public static void ctad() {
        String[] urlArr = new String[]{"http://www.ctad.org.cn/index/view/index.html?a_id=151",
                "http://www.ctad.org.cn/index/view/index.html?a_id=263",
                "http://www.ctad.org.cn/index/view/index.html?a_id=150",
                "http://www.ctad.org.cn/index/view/index.html?a_id=239"};

        JSONArray jsonArray = new JSONArray();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "text/html; charset=utf-8");

        for (String url : urlArr) {
            String res = OkHttpUtils.get(url, "");

            if (res != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("hot", true);
                jsonObject.put("uid", UUID.randomUUID().toString().replaceAll("-", ""));
                Document parse = Jsoup.parse(res);

                Elements viewh1 = parse.getElementsByClass("viewh1");
                jsonObject.put("name", viewh1.text());

                Element notetext = parse.getElementById("notetext");
                Element img = notetext.getElementsByTag("img").get(0);
                Element span = notetext.getElementsByTag("span").get(0);
                if (!span.text().isEmpty()) {
                    jsonObject.put("intro", span.text());
                } else {
                    Element p = notetext.getElementsByTag("p").get(0);
                    if (!p.text().isEmpty()) {
                        jsonObject.put("intro", p.text());
                    }
                }
                jsonObject.put("imgSrc", img.attr("src"));
                jsonObject.put("content", notetext.html());

                jsonArray.add(jsonObject);
            }
        }
        System.out.println(jsonArray);
    }


    public static void zhihu() {
        String[] urlArr = new String[]{"https://zhuanlan.zhihu.com/p/101635307"};

        JSONArray jsonArray = new JSONArray();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "text/html; charset=utf-8");

        for (String url : urlArr) {
            String res = OkHttpUtils.get(url, "");

            if (res != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("hot", true);
                jsonObject.put("uid", UUID.randomUUID().toString().replaceAll("-", ""));
                Document parse = Jsoup.parse(res);


                Elements viewh1 = parse.getElementsByClass("Post-Title");
                jsonObject.put("name", viewh1.text());

                Element notetext = parse.getElementsByClass("Post-RichTextContainer").get(0);
                Element img = notetext.getElementsByTag("img").get(0);
                Element span = notetext.getElementsByTag("span").get(0);
                if (!span.text().isEmpty()) {
                    jsonObject.put("intro", span.text());
                } else {
                    Element p = notetext.getElementsByTag("p").get(0);
                    if (!p.text().isEmpty()) {
                        jsonObject.put("intro", p.text());
                    }
                }
                jsonObject.put("imgSrc", img.attr("src"));

                Elements figures = notetext.getElementsByTag("figure");
                for (Element figure : figures) {
                    Elements childrens = figure.children();
                    for (Element children : childrens) {
                        if (children.tagName().equalsIgnoreCase("img")) {
                            children.remove();
                        }

                    }
                    Element noscript = figure.getElementsByTag("noscript").get(0);
                    Node node = noscript.childNode(0);
                    figure.appendChild(node);
                    noscript.remove();
                }
                jsonObject.put("content", notetext.html());

                jsonArray.add(jsonObject);

            }
        }
        System.out.println(jsonArray);
    }
}

