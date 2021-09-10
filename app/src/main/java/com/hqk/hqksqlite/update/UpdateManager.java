package com.hqk.hqksqlite.update;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;


import com.hqk.hqksqlite.db.dao.UserDao;
import com.hqk.hqksqlite.db.dao.base.BaseDaoFactory;
import com.hqk.hqksqlite.db.model.User;
import com.hqk.hqksqlite.tools.ContUtils;
import com.hqk.hqksqlite.tools.DomUtils;
import com.hqk.hqksqlite.tools.FileUtil;
import com.hqk.hqksqlite.update.bean.CreateDb;
import com.hqk.hqksqlite.update.bean.CreateVersion;
import com.hqk.hqksqlite.update.bean.UpdateDb;
import com.hqk.hqksqlite.update.bean.UpdateDbXml;
import com.hqk.hqksqlite.update.bean.UpdateStep;

import java.io.File;
import java.util.List;

public class UpdateManager {
    private File parentFile = ContUtils.parentFile;
    private File bakFile = ContUtils.bakFile;
    //App  2年 记录 数据  用户
    private List<User> userList;

    public void startUpdateDb(Context context) {
        UpdateDbXml updateDbxml = DomUtils.readDbXml(context);
//    下载 上一个版本  --》下一个版本
        String[] versions = FileUtil.getLocalVersionInfo(new File(parentFile,
                "update.txt"));
        //拿到上一个版本
        String lastVersion = versions[0];  //拿到当前版本
        String thisVersion = versions[1];

        String userfile = ContUtils.sqliteDatabasePath;
        String user_bak = ContUtils.copySqliteDatabasePath;
        FileUtil.CopySingleFile(userfile, user_bak);
        UpdateStep updateStep = DomUtils.findStepByVersion(updateDbxml, lastVersion, thisVersion);
        if (updateStep == null) {
            return;
        }

        List<UpdateDb> updateDbs = updateStep.getUpdateDbs();
        try {
            //    第二步   将原始数据库中所有的表名 更改成 bak_表名(数据还在)
            executeBeforesSql(updateDbs);
            // 第三步:检查新表，创建新表
            CreateVersion createVersion = DomUtils.findCreateByVersion(updateDbxml, thisVersion);
            executeCreateVersion(createVersion);
//          第四步  将原来bak_表名  的数据迁移到 新表中
            executeAftersSql(updateDbs);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void executeAftersSql(List<UpdateDb> updateDbs) throws Exception {
        for (UpdateDb db : updateDbs) {
            if (db == null || db.getName() == null) {
                throw new Exception("db or dbName is null;");
            }
            List<String> sqls = db.getSqlAfters();
            SQLiteDatabase sqlitedb = getDb();
            //执行数据库语句
            executeSql(sqlitedb, sqls);
            sqlitedb.close();
        }
    }


    private void executeCreateVersion(CreateVersion createVersion) throws Exception {
        if (createVersion == null || createVersion.getCreateDbs() == null) {
            throw new Exception("createVersion or createDbs is null;");
        }
        for (CreateDb cd : createVersion.getCreateDbs()) {
            if (cd == null || cd.getName() == null) {
                throw new Exception("db or dbName is null when createVersion;");
            }
            // 创建数据库表sql
            List<String> sqls = cd.getSqlCreates();
            SQLiteDatabase sqlitedb = getDb();
            executeSql(sqlitedb, sqls);
            sqlitedb.close();

        }
    }


    //所有的表名 更改成 bak_表名(数据还在)
    private void executeBeforesSql(List<UpdateDb> updateDbs) throws Exception {
        for (UpdateDb db : updateDbs) {
            if (db == null || db.getName() == null) {
                throw new Exception("db or dbName is null;");
            }
            List<String> sqls = db.getSqlBefores();
            SQLiteDatabase sqlitedb = getDb();
            //执行数据库语句
            executeSql(sqlitedb, sqls);
            sqlitedb.close();

        }
    }

    private SQLiteDatabase getDb() {
        String dbfilepath = null;
        SQLiteDatabase sqlitedb = null;

        dbfilepath = ContUtils.sqliteDatabasePath;// logic对应的数据库路径
        if (dbfilepath != null) {
            File f = new File(dbfilepath);
            f.mkdirs();
            if (f.isDirectory()) {
                f.delete();
            }
            sqlitedb = SQLiteDatabase.openOrCreateDatabase(dbfilepath, null);
        }
        return sqlitedb;
    }


    private void executeSql(SQLiteDatabase sqlitedb, List<String> sqls) {
        // 检查参数
        if (sqls == null || sqls.size() == 0) {
            return;
        }
        sqlitedb.beginTransaction();
        for (String sql : sqls) {
            sql = sql.replaceAll("\r\n", " ");
            sql = sql.replaceAll("\n", " ");
            if (!"".equals(sql.trim())) {
                try {
                    // Logger.i(TAG, "执行sql：" + sql, false);
                    sqlitedb.execSQL(sql);
                } catch (SQLException e) {
                }
            }
        }

        sqlitedb.setTransactionSuccessful();
        sqlitedb.endTransaction();
    }

}
