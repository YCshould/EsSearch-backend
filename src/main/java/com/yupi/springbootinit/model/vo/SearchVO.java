package com.yupi.springbootinit.model.vo;

import com.yupi.springbootinit.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;

import java.util.List;

/**
 * 已登录用户视图（脱敏）
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 **/
@Data
public class SearchVO implements Serializable {

    private List<Picture> pictureList;

    private List<UserVO> UserList;

    private List<PostVO> postList;


    private static final long serialVersionUID = 1L;
}