package com.xtu.mapper;

import com.xtu.pojo.Product;
import com.xtu.pojo.ProductInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {
    @Select("SELECT * FROM products ORDER BY id ASC LIMIT #{offset}, #{pageSize}")
    List<Product> selectByPage(Integer offset, Integer pageSize);

    @Select("SELECT COUNT(*) FROM products")
    Integer countProducts();

    @Select("SELECT * FROM products WHERE name LIKE CONCAT('%', #{keywords}, '%') AND specification LIKE CONCAT('%', #{specification}, '%') ORDER BY id ASC LIMIT #{offset}, #{pageSize}")
    List<Product> selectByKeywordsAndSpecification(int offset, Integer pageSize, String specification, String keywords);

    @Select("SELECT COUNT(*) FROM products WHERE name LIKE CONCAT('%', #{keywords}, '%') AND specification LIKE CONCAT('%', #{specification}, '%')")
    Integer countProductsByKeywordsAndSpecification(String specification, String keywords);

    @Select("SELECT * FROM products WHERE specification LIKE CONCAT('%', #{specification}, '%') ORDER BY id ASC LIMIT #{offset}, #{pageSize}")
    List<Product> selectBySpecification(int offset, Integer pageSize, String specification);

    @Select("SELECT COUNT(*) FROM products WHERE specification LIKE CONCAT('%', #{specification}, '%')")
    Integer countProductsBySpecification(String specification);

    @Select("SELECT * FROM products WHERE name LIKE CONCAT('%', #{keywords}, '%') ORDER BY id ASC LIMIT #{offset}, #{pageSize}")
    List<Product> selectByKeywords(int offset, Integer pageSize, String keywords);

    @Select("SELECT COUNT(*) FROM products WHERE name LIKE CONCAT('%', #{keywords}, '%')")
    Integer countProductsByKeywords(String keywords);

    @Update("UPDATE products SET status = #{status} WHERE id = #{id}")
    Boolean upProductsStatusById(Integer id, String status);

    @Delete("DELETE FROM products WHERE id = #{id}")
    void deleteProductById(Integer id);

    @Insert("INSERT INTO products (name, image_url, specification, stock, price, click_count, status, producer, description) VALUES (#{name}, #{imageUrl}, #{specification}, #{stock}, #{price}, #{clickCount}, #{status}, #{producer}, #{description})")
    void addProduct(Product product);
}