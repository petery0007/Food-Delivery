package com.xtu.mapper;

import com.xtu.pojo.Cart;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CartMapper {
    @Select("SELECT * FROM cart WHERE user_id = #{userId}")
    Cart getCart(Integer userId);

    @Update("UPDATE cart SET quantity = #{quantity} WHERE id = #{id}")
    void updateCart(Integer id, Integer quantity);

    @Delete("DELETE FROM cart WHERE id = #{id}")
    void deleteCart(Integer id);

    @Select("SELECT count(*) FROM cart WHERE user_id = #{id} AND product_id = #{productId}")
    int check(Integer id, Integer productId);

    @Insert("INSERT INTO cart (user_id, product_id, image_url, specification, price, quantity) VALUES (#{userId}, #{productId}, #{imageUrl}, #{specification}, #{price}, #{quantity})")
    void addCart(Cart cart);
}
