package com.yupi.springbootinit.EStest;

import com.yupi.springbootinit.esdao.PostEsDao;
import com.yupi.springbootinit.model.dto.post.PostEsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class estest {

    @Resource
    private PostEsDao postEsDao;

    @Test
    void esAdd(){
        PostEsDTO postEsDTO = new PostEsDTO();
        postEsDTO.setId(2L);
        postEsDTO.setUserId(124L);
        postEsDTO.setTitle("伍wuwuwuwwu");
        postEsDTO.setContent("测试内容第二版");
        postEsDTO.setTags(Arrays.asList("java", "python"));
        postEsDao.save(postEsDTO);
    }

    @Test
    void select(){
        Iterable<PostEsDTO> post = postEsDao.findByTitle("伍");
        for (PostEsDTO postEsDTO : post) {
            System.out.println(postEsDTO);
        }

    }

}
