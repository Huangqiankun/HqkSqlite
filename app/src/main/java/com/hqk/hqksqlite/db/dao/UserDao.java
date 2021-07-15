package com.hqk.hqksqlite.db.dao;

import com.hqk.hqksqlite.db.dao.base.BaseDao;

import java.util.List;

public class UserDao<User> extends BaseDao<User> {

    @Override
    public Long insert(User entity) {
        return super.insert(entity);
    }

    @Override
    public List<User> query(User where) {
        return super.query(where);
    }

    @Override
    public int delete(User where) {
        return super.delete(where);
    }

    @Override
    public int update(User entity, User where) {
        return super.update(entity, where);
    }

    @Override
    public List<User> query(User where, String groupBy, String orderBy, String having, Integer startIndex, Integer limit) {
        return super.query(where, groupBy, orderBy, having, startIndex, limit);
    }
}
