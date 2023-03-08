package com.itao.community.service;

import com.itao.community.dao.elasticsearch.DiscussPostRepository;
import com.itao.community.entity.DiscussPost;
import com.itao.community.entity.Page;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shkstart
 * @create 2023--05 18:21
 */
@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticTemplate;

    public void saveDiscussPost(DiscussPost post){
        discussRepository.save(post);
    }

    public void deleteDiscussPost(int id){
        discussRepository.deleteById(id);
    }

    public Map<String, Object> searchDiscussPost(String keyword, int current, int limit){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))  // 搜索词条
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))                   // 排序
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))                 // 排序
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))           // 排序 - 倒序
                .withPageable(PageRequest.of(current, limit))        // 分页， 第几页 每页几行数据
                .withHighlightFields(       //  设置关键词高亮
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        // 聚合数据 包含总记录数，当前页的数据
        Map<String, Object> map  = new HashMap<>();

        SearchHits<DiscussPost> search = elasticTemplate.search(searchQuery, DiscussPost.class);

        // 总记录数
        map.put("count", search.getTotalHits());

        // 得到查询结果返回的内容
        List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();
        // 设置一个需要返回的实体类集合
        List<DiscussPost> discussPosts = new ArrayList<>();
        // 遍历返回的内容进行处理
        for (SearchHit<DiscussPost> searchHit : searchHits) {
            // 高亮的内容
            Map<String, List<String>> highLightFields = searchHit.getHighlightFields();
            // 将高亮的内容填充到content中
            searchHit.getContent().setTitle(highLightFields.get("title") == null ? searchHit.getContent().getTitle() : highLightFields.get("title").get(0));
            searchHit.getContent().setContent(highLightFields.get("content") == null ? searchHit.getContent().getContent() : highLightFields.get("content").get(0));
            // 放到实体类中
            discussPosts.add(searchHit.getContent());
        }
        map.put("discussPosts", discussPosts);

        return map;

    }

}
