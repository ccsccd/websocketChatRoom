package org.redrock.demo.mapper;

import org.apache.ibatis.annotations.*;
import org.redrock.demo.entity.User;
import org.springframework.stereotype.Component;

@Mapper
@Component(value = "mainMapper")
public interface MainMapper {
    @Insert("insert into user(username,password) value (#{username},#{password})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    void insertUser(User user);

    @Select("select count(id) from user where username= #{username}")
    int checkUser(@Param("username")String username);

    @Select("select * from user where username= #{username} and password= #{password}")
    User checkLogin(@Param("username")String username, @Param("password")String password);

    //错误:嵌套找不到参数
//    @Select("select count(id) from friend where (a_user_id = (select id from user where username= #{fromUser})and b_user_id = (select id from user where username= #{toUser}))or(b_user_id = (select id from user where username= #{fromUser})and a_user_id = (select id from user where username= #{toUser}))")
//    int checkFriend(@Param("fronUser")String fromUser,@Param("toUser")String toUser);
}
