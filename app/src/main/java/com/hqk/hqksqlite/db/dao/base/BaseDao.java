package com.hqk.hqksqlite.db.dao.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.hqk.hqksqlite.db.annotation.DbFiled;
import com.hqk.hqksqlite.db.annotation.DbPrimaryKey;
import com.hqk.hqksqlite.db.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseDao<T> implements IBaseDao<T> {

    private static final String TAG = "hqk";

    /**
     * 持有数据库操作类的引用
     */
    private SQLiteDatabase database;
    /**
     * 持有操作数据库表所对应的java类型
     * User
     */
    private Class<T> entityClass;
    /**
     * 保证实例化一次
     */
    private boolean isInit = false;

    private String tableName;

    //    检查表
    private HashMap<String, Field> cacheMap;

    protected BaseDao() {
    }

    protected synchronized boolean init(Class<T> entity, SQLiteDatabase sqLiteDatabase) {
        if (!isInit) {
            //初始化完了  自动建表
            entityClass = entity;
            database = sqLiteDatabase;
            if (entity.getAnnotation(DbTable.class) == null) {
                tableName = entity.getClass().getSimpleName();
            } else {
                tableName = entity.getAnnotation(DbTable.class).value();
            }
            if (!database.isOpen()) {
                return false;
            }
            String sql = createTable();
            database.execSQL(sql);
            //建立好映射关系
            initCacheMap();
            isInit = true;
        }
        return true;
    }

    /**
     * 将真实表中的列名  + 成员变量进行 映射
     * 缓存对应的 表 Model里的属性名以及对应表列名
     */
    private void initCacheMap() {
        cacheMap = new HashMap<>();
        //这里没有必要查询 对应表中的任何数据，只想要对应表列名，所以 这 limit 0
        String sql = "select * from " + tableName + " limit 0";
        Cursor cursor = database.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        Field[] columnFields = entityClass.getDeclaredFields();
        //获取对应表中的列名数组，以及对应表Model里面的属性数组
        for (String columnName : columnNames) {
            Field resultField = null;
            for (Field field : columnFields) {
                //拿到对应属性的注解值
                String fieldAnnotationName = field.getAnnotation(DbFiled.class).value();
                //如果对应的属性注解值与数据库表列名相同，则拿到对应属性值
                if (columnName.equals(fieldAnnotationName)) {
                    resultField = field;
                    break;
                }
            }
            if (resultField != null) {
                cacheMap.put(columnName, resultField);
            }
        }

    }

    /**
     * 组装 创建表的SQL语句
     *
     * @return
     */
    private String createTable() {
        StringBuffer stringBuffer = new StringBuffer();
        //开始组装 SQL语句
        stringBuffer.append("create table if not exists ");
        stringBuffer.append(tableName + " (");
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            Class type = field.getType();
            String primaryKey = null;
            try {
                primaryKey = field.getAnnotation(DbPrimaryKey.class).value();
            } catch (Exception e) {

            }
            Log.i(TAG, "createTable primaryKey " + primaryKey);
            Log.i(TAG, "createTable type " + type);
            if (type == String.class) {
                if (null == primaryKey) {
                    stringBuffer.append(field.getAnnotation(DbFiled.class).value() + " TEXT,");
                } else {
                    stringBuffer.append(field.getAnnotation(DbFiled.class).value() + " TEXT PRIMARY KEY,");
                }
            } else if (type == Double.class) {
                if (null == primaryKey) {
                    stringBuffer.append(field.getAnnotation(DbFiled.class).value() + "  DOUBLE,");
                } else {
                    stringBuffer.append(field.getAnnotation(DbFiled.class).value() + "  DOUBLE PRIMARY KEY,");
                }
            } else if (type == Integer.class) {
                if (null == primaryKey) {
                    stringBuffer.append(field.getAnnotation(DbFiled.class).value() + "  INTEGER,");
                } else {
                    boolean isAuto = field.getAnnotation(DbPrimaryKey.class).isAuto();
                    if (isAuto) {
                        stringBuffer.append(field.getAnnotation(DbFiled.class).value() + "  INTEGER PRIMARY KEY AUTOINCREMENT,");
                    } else {
                        stringBuffer.append(field.getAnnotation(DbFiled.class).value() + "  INTEGER PRIMARY KEY,");
                    }
                }
            } else if (type == Long.class) {
                if (null == primaryKey) {
                    stringBuffer.append(field.getAnnotation(DbFiled.class).value() + "  BIGINT,");
                } else {
                    stringBuffer.append(field.getAnnotation(DbFiled.class).value() + "  BIGINT PRIMARY KEY,");
                }
            } else if (type == byte[].class) {
                if (null == primaryKey) {
                    stringBuffer.append(field.getAnnotation(DbFiled.class).value() + "  BLOB,");
                } else {
                    stringBuffer.append(field.getAnnotation(DbFiled.class).value() + "  BLOB PRIMARY KEY,");
                }
            } else {
                  /*
                不支持的类型
                 */
                continue;
            }
        }
        //循环完成后，最后一项会有 逗号 ，如果最后一个是逗号，则删除最后一个字符
        if (stringBuffer.charAt(stringBuffer.length() - 1) == ',') {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        //SQL 语句 收尾
        stringBuffer.append(")");
        Log.i(TAG, "createTable: " + stringBuffer.toString());
        return stringBuffer.toString();
    }

    @Override
    public Long insert(T entity) {
        Map<String, String> map = getValues(entity);
        ContentValues contentValues = getContentValues(map);
        return database.insert(tableName, null, contentValues);
    }

    /**
     * 获取对应 model 属性以及对应的注解值（表列名值）
     *
     * @param entity 对应 表结构的model
     * @return 返回 key= 列名，value=属性的值          map集合
     */
    private Map<String, String> getValues(T entity) {
        HashMap<String, String> map = new HashMap<>();
        //获取对应缓存 model 里面的属性键
        Iterator<Field> fieldIterator = cacheMap.values().iterator();
        while (fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            field.setAccessible(true);
            try {
                Object object = field.get(entity);
                if (object == null) {
                    continue;
                }
                String value = object.toString();
                String key = field.getAnnotation(DbFiled.class).value();
                //遍历 取出对应 属性的值 以及对应的 注解值，并添加至Map里
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    map.put(key, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 数据库数据结构的封装
     *
     * @param map 带有 以表列名为键，的map
     * @return 数据库需要的封装格式
     */
    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if (value != null) {
                contentValues.put(key, value);
            }
        }
        return contentValues;
    }

    @Override
    public int update(T entity, T where) {
        Map values = getValues(entity);
        ContentValues contentValues = getContentValues(values);
        //条件
        Map whereMap = getValues(where);
        Condition condition = new Condition(whereMap);
        return database.update(tableName, contentValues, condition.whereClause, condition.whereArgs);
    }

    class Condition {
        String whereClause;
        String[] whereArgs;

        public Condition(Map<String, String> whereClause) {
            boolean flag = false;
            if (true && flag) {

            }
            ArrayList list = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            // 这里之所以先添加 1=1 这个条件 是因为
            // SQL  where  后面需要给条件判断，而下面 while 循环 直接添加了 and
            // SQL 语句就变成了 where and  这显然不符合SQL语句
            // 因此 加上 1=1 就变成了  where 1=1 and xx。起了一个呈上去下的作用

            stringBuilder.append("1=1");
            Set keys = whereClause.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = whereClause.get(key);
                if (value != null) {
                    stringBuilder.append(" and " + key + " =?");
                    list.add(value);
                }
            }
            this.whereClause = stringBuilder.toString();
            this.whereArgs = (String[]) list.toArray(new String[list.size()]);
        }
    }

    @Override
    public int delete(T where) {
        Map map = getValues(where);
        Condition condition = new Condition(map);
        return database.delete(tableName, condition.whereClause, condition.whereArgs);
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null, null, null
        );
    }
    //所有  条件
    @Override
    public List<T> query(T where, String groupBy, String orderBy, String having,Integer startIndex,
                         Integer limit) {
        String limitString=null;
        if(startIndex!=null&&limit!=null)
        {
            limitString=startIndex+" , "+limit;
        }

        Map map=getValues(where);
        Condition condition=new Condition(map);
        Cursor cursor=  database.query(tableName, null, condition.whereClause,
                condition.whereArgs,
                groupBy, having,
                orderBy, limitString
        );
//        封装   --返回
        List<T> result = getResult(cursor, where);
        cursor.close();
        return result;
    }





    private List<T> getResult(Cursor cursor, T where) {
        ArrayList  list=new ArrayList();
        Object item;
        while (cursor.moveToNext()) {
            try {
//                cachmap        ---对象中的成员变量    Filed    annotion-- tb_name
//cacheMap    name  ---Filed       1
//            tb_name       ---Filed  2
                item=where.getClass().newInstance();
                Iterator iterator=cacheMap.entrySet().iterator();
                while (iterator.hasNext())
                {
                    Map.Entry entry= (Map.Entry) iterator.next();
                    //tb_name
                    /**
                     * 得到列名
                     */
                    String colomunName= (String) entry.getKey();
//                    通过列名查找到游标的索性
                    Integer colmunIndex= cursor.getColumnIndex(colomunName);
//                    Filed
//反射的成员 cursor
                    Field field= (Field) entry.getValue();
                    Class type=field.getType();
                    if(colmunIndex!=-1)
                    {
//
                        if (type == String.class) {
                            field.set(item, cursor.getString(colmunIndex));
                        }else if(type==Double.class)
                        {
                            field.set(item,cursor.getDouble(colmunIndex));
                        }else  if(type==Integer.class)
                        {
                            field.set(item,cursor.getInt(colmunIndex));
                        }else if(type==Long.class)
                        {
                            field.set(item,cursor.getLong(colmunIndex));
                        }else  if(type==byte[].class)
                        {
                            field.set(item,cursor.getBlob(colmunIndex));
                            /*
                            不支持的类型
                             */
                        }else {
                            continue;
                        }

                    }

                }
                list.add(item);
            } catch ( Exception e) {
                e.printStackTrace();
            }


        }

        return list;
    }
}
