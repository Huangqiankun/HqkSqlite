package com.hqk.hqksqlite.db.model;


import com.hqk.hqksqlite.db.annotation.DbFiled;
import com.hqk.hqksqlite.db.annotation.DbTable;

@DbTable("tb_photo")
public class Photo {
    @DbFiled("time")
    private  String time;
    @DbFiled("id")
    private  Long id;
    @DbFiled("path")
    private  String path;

    public Photo( ) {
    }

    public Photo(String time, Long id, String path) {
        this.time = time;
        this.id = id;
        this.path = path;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
