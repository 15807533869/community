package com.itao.community.dao;

import com.itao.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shkstart
 * @create 2023--14 20:36
 */
@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPost(int userId, int offset, int limit);

    // @Param注解用于给参数取别名
    // 如果只有一个参数，并且在<if>里使用，则必须加别名。
    int selectDiscussPostRows(@Param("userId") int userId);

}
