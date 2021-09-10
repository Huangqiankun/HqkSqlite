package com.hqk.hqksqlite.tools;

import android.os.Environment;

import java.io.File;

public class ContUtils {

    /**
     * 数据库最外层目录
     */
    public static final File parentFile = new File(Environment.getExternalStorageDirectory(),
            "tencentHqk");

    /**
     * 正在使用中的数据库对应的绝对路径
     */
    public static final String sqliteDatabasePath = parentFile.getAbsolutePath() + "/hqk.db";

    /**
     * 数据库备份目录
     */
    public static final File bakFile = new File(parentFile, "backDb");

    /**
     * 备份数据库对应的绝对路径
     */
    public static final String copySqliteDatabasePath = bakFile.getAbsolutePath() + "/hqk.db";


}
