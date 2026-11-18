package org.example.demo.dao;

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
            return mapper.selectAll();
        }
    }

    public PageResult<User> listPage(int page, int size) {
        long offset = (long) (page - 1) * size;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            long total = mapper.countAll();
            List<User> records = total > 0 ? mapper.selectPage(offset, size) : List.of();
            return new PageResult<>(records, total, page, size);
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
