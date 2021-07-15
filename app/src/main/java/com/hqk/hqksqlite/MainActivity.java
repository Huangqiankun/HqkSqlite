package com.hqk.hqksqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hqk.hqksqlite.db.dao.base.BaseDaoFactory;
import com.hqk.hqksqlite.db.dao.base.IBaseDao;
import com.hqk.hqksqlite.db.dao.PhotoDao;
import com.hqk.hqksqlite.db.dao.UserDao;
import com.hqk.hqksqlite.db.model.Photo;
import com.hqk.hqksqlite.db.model.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    UserDao<User> userDao;

    PhotoDao<Photo> photoDao;

    private ArrayList<User> listUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission(this);
        userDao = BaseDaoFactory.getInstance().createBaseDao(UserDao.class, User.class);
        photoDao = BaseDaoFactory.getInstance().createBaseDao(PhotoDao.class, Photo.class);
    }


    public void save(View view) {
        User user = new User("hqk", 18);
        long size = userDao.insert(user);
        Photo photo = new Photo("time", System.currentTimeMillis(), "path");
        long photoSize = photoDao.insert(photo);
        Toast.makeText(this, "save line :   " + size, Toast.LENGTH_LONG).show();
    }


    public void update(View view) {
        User where = new User();
        where.setAge(18);
        int size = userDao.update(new User("TOM", 99), where);
        Toast.makeText(this, "update Size :   " + size, Toast.LENGTH_LONG).show();
    }

    public void delete(View view) {
        User where = new User();
        where.setAge(18);
        int size = userDao.delete(where);
        Toast.makeText(this, "delete Size :   " + size, Toast.LENGTH_LONG).show();
    }

    public void queryList(View view) {
        listUser.clear();
        listUser.addAll(userDao.query(new User()));
        Toast.makeText(this, "查询条数为：" + listUser.size(), Toast.LENGTH_LONG).show();
    }


    public void requestPermission(
            Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }
    }

}