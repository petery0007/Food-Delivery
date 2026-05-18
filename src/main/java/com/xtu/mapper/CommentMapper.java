package com.xtu.mapper;

import com.xtu.pojo.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Insert("INSERT INTO comment (user_id, goods_id, content, score, create_time) VALUES (#{userId}, #{goodsId}, #{content}, #{score}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "commentId")
    int insertComment(Comment comment);

    @Select("SELECT * FROM comment WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}")
    List<Comment> selectCommentsByUserId(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM comment WHERE user_id = #{userId}")
    Integer countCommentsByUserId(Integer userId);

    @Select("SELECT * FROM comment WHERE goods_id = #{goodsId} ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}")
    List<Comment> selectCommentsByGoodsId(@Param("goodsId") Integer goodsId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM comment WHERE goods_id = #{goodsId}")
    Integer countCommentsByGoodsId(Integer goodsId);

    @Delete("DELETE FROM comment WHERE comment_id = #{commentId} AND user_id = #{userId}")
    int deleteComment(Integer commentId, Integer userId);

}
