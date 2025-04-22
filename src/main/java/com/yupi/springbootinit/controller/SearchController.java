
package com.yupi.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.picture.PictureQueryRequest;
import com.yupi.springbootinit.model.dto.picture.SearchQueryRequest;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.model.enums.SearchTypeEnum;
import com.yupi.springbootinit.model.vo.PostVO;
import com.yupi.springbootinit.model.vo.SearchVO;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.service.PictureService;
import com.yupi.springbootinit.service.PostService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 图片接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {


    @Resource
    private PictureService pictureService;

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    /**
     * 聚合搜索
     *
     * @param searchQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/all")
    public BaseResponse<SearchVO> searchOnAll(@RequestBody SearchQueryRequest searchQueryRequest,
                                              HttpServletRequest request) {
        long current = searchQueryRequest.getCurrent();
        long size = searchQueryRequest.getPageSize();
        String searchText = searchQueryRequest.getSearchText();
        String category = searchQueryRequest.getCategory();
        SearchVO searchVO = new SearchVO();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        SearchTypeEnum enumByValue = SearchTypeEnum.getEnumByValue(category);
        if(category == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"类型不能为空");
        }
        if(enumByValue==null){
            // 图片搜索
            Page<Picture> picturePage = pictureService.getPicturePage(searchText, current, size);
            // 用户搜索
            UserQueryRequest userQueryRequest = new UserQueryRequest();
            userQueryRequest.setUserName(searchText);
            Page<UserVO> userVOPage = userService.searchUserVO(userQueryRequest);
            // 帖子搜索
            PostQueryRequest postQueryRequest = new PostQueryRequest();
            postQueryRequest.setSearchText(searchText);
            Page<Post> postPage=postService.page(new Page<>(current, size), postService.getQueryWrapper(postQueryRequest));
            Page<PostVO> postVOPage = postService.getPostVOPage(postPage, request);
            searchVO.setPictureList(picturePage.getRecords());
            searchVO.setPostList(postVOPage.getRecords());
            searchVO.setUserList(userVOPage.getRecords());
        }else {
            switch (enumByValue) {
                case POST:
                    // 帖子搜索（基于数据库的like查询）
                    PostQueryRequest postQueryRequest = new PostQueryRequest();
                    postQueryRequest.setSearchText(searchText);
                    //Page<Post> postPage=postService.page(new Page<>(current, size), postService.getQueryWrapper(postQueryRequest));
                    //Page<PostVO> postVOPage = postService.getPostVOPage(postPage, request);
                    Page<Post> postPageByEs = postService.searchFromEs(postQueryRequest);
                    List<Post> records = postPageByEs.getRecords();
                    List<PostVO> postVO=new ArrayList<>();
                    for (Post record : records) {
                        PostVO postVO1 = new PostVO();
                        postVO1.setId(record.getId());
                        postVO1.setTitle(record.getTitle());
                        postVO1.setContent(record.getContent());
                        postVO1.setTagList(Collections.singletonList(record.getTags()));
                        postVO1.setFavourNum(record.getFavourNum());
                        postVO1.setThumbNum(record.getThumbNum());
                        postVO1.setCreateTime(record.getCreateTime());
                        postVO1.setUpdateTime(record.getUpdateTime());
                        postVO.add(postVO1);
                    }

                    searchVO.setPostList(postVO);
                    break;
                case USER:
                    // 用户搜索
                    UserQueryRequest userQueryRequest = new UserQueryRequest();
                    userQueryRequest.setUserName(searchText);
                    Page<UserVO> userVOPage = userService.searchUserVO(userQueryRequest);
                    searchVO.setUserList(userVOPage.getRecords());
                    break;
                case PICTURE:
                    // 图片搜索
                    Page<Picture> picturePage = pictureService.getPicturePage(searchText, current, size);
                    searchVO.setPictureList(picturePage.getRecords());
                    break;
                default:
            }
        }

        return ResultUtils.success(searchVO);

    }



}
