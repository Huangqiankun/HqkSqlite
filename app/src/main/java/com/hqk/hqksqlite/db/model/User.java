package com.hqk.hqksqlite.db.model;


import com.hqk.hqksqlite.db.annotation.DbFiled;
import com.hqk.hqksqlite.db.annotation.DbPrimaryKey;
import com.hqk.hqksqlite.db.annotation.DbTable;

@DbTable("tb_user")
public class User {
    //    是不是 变量名
    @DbPrimaryKey(value = "tb_id", isAuto = true)
    @DbFiled("tb_id")
    public Integer id;
    @DbFiled("tb_name")
    public String name;//

    public User(String name, Integer age) {

        this.name = name;
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @DbFiled("tb_age")
    public Integer age;


    public User() {
    }


}
