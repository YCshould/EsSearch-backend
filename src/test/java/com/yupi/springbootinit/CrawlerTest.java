package com.yupi.springbootinit;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.service.PostService;
import com.yupi.springbootinit.service.UserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class CrawlerTest {

    @Resource
    private PostService postService;


    @Test
    void picturetest() throws IOException {
        int current = 0;
        String url=String.format("https://cn.bing.com/images/search?q=迪迦&first=%s",current);
        Document doc = Jsoup.connect(url).get();

        Elements imgs = doc.select(".iuscp");
        List<Picture> pictures = new ArrayList<>();
        for (Element headline : imgs) {
            String imgpt = headline.select(".iusc").get(0).attr("m");
            String title = headline.select(".inflnk").get(0).attr("aria-label");
            Map<String,Object> map = JSONUtil.toBean(imgpt, Map.class);
            String imgurl = (String) map.get("murl");
            System.out.println(imgurl);
            System.out.println(title);
            Picture picture = new Picture();
            picture.setTitle(title);
            picture.setPictureUrl(imgurl);
            pictures.add(picture);
        }
        System.out.println(pictures.size());
    }

    @Test
    void testCrawler() {
        String json = "{\"reviewStatus\":1,\"needNotInterests\":true,\"hiddenContent\":true,\"needCursor\":true,\"needFilterVipContent\":true,\"needOnlyRecommend\":true,\"cursorList\":[{\"field\":\"recommendScore\",\"asc\":false},{\"field\":\"id\",\"asc\":false}],\"queryType\":\"hot\"}";
        String url = "https://api.codefather.cn/api/recommend/list/page/vo";
        String result2 = HttpRequest
                .post(url)
                .body(json)
                .execute()
                .body();
        System.out.println(result2);
        Map<String, Object> resultMap = JSONUtil.toBean(result2, Map.class);
        JSONObject resultJson = (JSONObject) resultMap.get("data");
        JSONArray resultArray = (JSONArray) resultJson.get("records");

        List<Post> posts = new ArrayList<>();
        for (Object o : resultArray) {
            JSONObject tempobj = (JSONObject) o;
            Post post = new Post();
            String content = (String) tempobj.get("content");
            System.out.println(content);
            post.setContent(tempobj.getStr("content"));
            post.setTitle(tempobj.getStr("category"));
            post.setUserId(1L);
            Object tags = tempobj.get("tags");
            if (tags!= null) {
                post.setTags(tags.toString());
            }
            posts.add(post);
        }
        postService.saveBatch(posts);
    }
}
