package com.hqk.hqksqlite.db.dao;

import com.hqk.hqksqlite.db.dao.base.BaseDao;

import java.util.List;

public class PhotoDao<Photo> extends BaseDao<Photo> {

    @Override
    public Long insert(Photo entity) {
        return super.insert(entity);
    }

    @Override
    public int update(Photo entity, Photo where) {
        return super.update(entity, where);
    }

    @Override
    public List<Photo> query(Photo where) {
        return super.query(where);
    }

    @Override
    public int delete(Photo where) {
        return super.delete(where);
    }
}
