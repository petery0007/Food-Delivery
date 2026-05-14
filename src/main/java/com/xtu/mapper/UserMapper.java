package com.xtu.mapper;

import com.xtu.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

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
    @Update("update user set money = money + #{amount} where id = #{id}")
    void updateUserMoney(Integer id, BigDecimal amount);
    @Select("select * from user WHERE role = 'user' limit #{offset},#{pageSize}")
    List<User> selectAllUserByPage(Integer offset, Integer pageSize);
}
