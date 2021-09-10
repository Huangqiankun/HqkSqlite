package com.hqk.hqksqlite.db.model;


import com.hqk.hqksqlite.db.annotation.DbFiled;
import com.hqk.hqksqlite.db.annotation.DbTable;

@DbTable("tb_photo")
public class Photo {
    @DbFiled("tb_time")
    private String time;
    @DbFiled("id")
    private Long id;

    public void setTime(String time) {
        this.time = time;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DbFiled("tb_path")
    private String path;

    public Photo(String time, Long id, String path, String name) {
        this.time = time;
        this.id = id;
        this.path = path;
        this.name = name;
    }

    @DbFiled("tb_name")
    private String name;

    public Photo() {
    }

}
