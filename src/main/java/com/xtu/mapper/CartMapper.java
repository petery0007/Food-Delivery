package com.xtu.mapper;

import com.xtu.pojo.Cart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CartMapper {
    @Select("SELECT * FROM cart WHERE user_id = #{userId}")
    Cart getCart(Integer userId);

    @Update("UPDATE cart SET quantity = #{quantity} WHERE id = #{id}")
    void updateCart(Integer id, Integer quantity);

    @Delete("DELETE FROM cart WHERE id = #{id}")
    void deleteCart(Integer id);
}
