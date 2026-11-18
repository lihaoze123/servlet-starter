package org.example.demo.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.example.demo.mapper.UserMapper;
import org.example.demo.model.PageResult;
import org.example.demo.model.User;

import java.util.List;

@RequiredArgsConstructor
public class UserDAO {
    @NonNull
    private final SqlSessionFactory sqlSessionFactory;

    public User getById(Integer id) {
        if (id == null) {
            return null;
        }
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.selectById(id);
        }
    }

    public List<User> listAll() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.selectList(Wrappers.<User>lambdaQuery().orderByAsc(User::getId));
        }
    }

    public PageResult<User> listPage(int page, int size) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            Page<User> mpPage = mapper.selectPage(
                    new Page<>(page, size),
                    Wrappers.<User>lambdaQuery().orderByAsc(User::getId)
            );
            return new PageResult<>(mpPage.getRecords(), mpPage.getTotal(), page, size);
        }
    }

    public User create(User user) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User entity = new User(null, user.getUsername(), user.getEmail());
            mapper.insert(entity);
            session.commit();
            return entity;
        }
    }
}
