package com.hqk.hqksqlite.db.dao.base;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.hqk.hqksqlite.tools.ContUtils;

public class BaseDaoFactory {

    private final String TAG = "hqk";
    private SQLiteDatabase sqLiteDatabase;

    private String sqliteDatabasePath;

    private static BaseDaoFactory instance = new BaseDaoFactory();

    //饿汉单例模式
    public static BaseDaoFactory getInstance() {
        return instance;
    }

    public BaseDaoFactory() {
        //读者可随意更改路径以及对应数据库名，这里演示暂时放在根目录
        sqliteDatabasePath = ContUtils.sqliteDatabasePath;
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath, null);
        Log.i(TAG, "sqliteDatabasePath : " + sqliteDatabasePath);
        Log.i(TAG, "sqLiteDatabase : " + sqLiteDatabase.getPath());
    }


    /**
     * @param clazz
     * @param entityClass
     * @param <R>         我们在这可以把它看成某一个对象，它继承与 BaseDao<T> ，而里面的T 就是下面的那个空对象
     * @param <T>         我们在这可以吧它看成某一个空对象 T
     * @return
     */
    public synchronized <R extends BaseDao<T>, T> R createBaseDao(Class<R> clazz, Class<T> entityClass) {
        BaseDao baseDao = null;
        try {
            baseDao = clazz.newInstance();
            baseDao.init(entityClass, sqLiteDatabase);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return (R) baseDao;
    }
}

