package com.xtu.mapper;

import com.xtu.pojo.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CartMapper {
    @Select("SELECT * FROM cart WHERE user_id = #{userId}")
    Cart getCart(Integer userId);
}
