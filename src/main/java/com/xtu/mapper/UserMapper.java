package com.xtu.mapper;

import com.xtu.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username}")
    User findByUsername(String username);
    @Insert("insert into user(username,password,phone,id_number,role) values(#{username},#{password},#{phone},#{idNumber},#{role})")
    void insert(User user);
    @Select("select * from user where phone = #{phone}")
    User selectByPhone(String phone);
    @Select("select * from user where id = #{id}")
    User selectById(Integer id);
    @Update("update user set username = #{username},phone = #{phone} where id = #{id}")
    void updateInfoById(User dto);
    @Update("update user set password = #{password} where id = #{id}")
    void updatePasswordById(User dto);
}
