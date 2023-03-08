package com.itao.community.controller;

import com.itao.community.entity.DiscussPost;
import com.itao.community.entity.Page;
import com.itao.community.service.ElasticsearchService;
import com.itao.community.service.LikeService;
import com.itao.community.service.UserService;
import com.itao.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shkstart
 * @create 2023--05 21:45
 */
@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    // search?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        // 搜索帖子 包含总记录数 和 每页数据
        Map<String, Object> postMap = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

        // 总记录数
        long count = (long) postMap.get("count");
        // 当前页的帖子数据
        List<DiscussPost> posts = (List<DiscussPost>) postMap.get("discussPosts");

        // 分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows((int) count);

        // 聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (postMap != null) {
            for (DiscussPost post : posts) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                map.put("post", post);
                // 作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        return "site/search";
    }

}
