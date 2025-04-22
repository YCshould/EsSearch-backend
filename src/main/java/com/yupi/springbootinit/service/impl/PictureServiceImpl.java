package com.yupi.springbootinit.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PictureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PictureServiceImpl implements PictureService {
    @Override
    public Page<Picture> getPicturePage(String searchTest, long pageNum, long pageSize) {
        long current = pageSize * (pageNum - 1);
        String url=String.format("https://cn.bing.com/images/search?q=迪迦&first=%s",current);
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取图片失败");
        }

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
            if(pictures.size()>=pageSize){
                break;
            }
        }
        System.out.println(pictures.size());
        Page<Picture> page = new Page<>();
        page.setRecords(pictures);
        page.setPages(pageNum);
        page.setSize(pageSize);
        return page;
    }
}
