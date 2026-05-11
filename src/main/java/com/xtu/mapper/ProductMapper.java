package com.xtu.mapper;

import com.xtu.pojo.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper {
    @Select("SELECT * FROM products ORDER BY id ASC LIMIT #{offset}, #{pageSize}")
    List<Product> selectByPage(Integer offset, Integer pageSize);

    @Select("SELECT COUNT(*) FROM products")
    Integer countProducts();
}
