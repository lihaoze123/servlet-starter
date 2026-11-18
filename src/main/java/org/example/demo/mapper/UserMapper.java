package org.example.demo.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.demo.model.User;

import java.util.List;

public interface UserMapper {
    User selectById(@Param("id") Integer id);

    List<User> selectAll();

    List<User> selectPage(@Param("offset") long offset, @Param("size") int size);

    long countAll();

    int insert(User user);
}
