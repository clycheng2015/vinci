package com.lewis.lib_vinci.service.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lewis.lib_vinci.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lihailun
 * @date 2015/05/22
 */

public class HaomaicheDbMgr {
    private HaomaicheSQLiteOpenHelper mAidSQLiteOpenHelper;
    public SQLiteDatabase mSQLiteDatabase;

    private static HaomaicheDbMgr mAidDbMgr = null;

    private HaomaicheDbMgr() {

    }

    public static HaomaicheDbMgr getInstance() {
        if (null == mAidDbMgr)
            mAidDbMgr = new HaomaicheDbMgr();
        return mAidDbMgr;
    }

    /**
     * 在应用入口处，初始化
     *
     * @param context
     */
    public void init(Context context) {
        LogUtil.i("HaomaicheDbMgr", "******************QmDbMgr******************");
        mAidSQLiteOpenHelper = new HaomaicheSQLiteOpenHelper(context);
        mSQLiteDatabase = mAidSQLiteOpenHelper.getWritableDatabase();
    }

    /**
     * 关闭数据库
     */
    public void closeDb() {
        mAidSQLiteOpenHelper.close();
        mSQLiteDatabase.close();
    }

    /**
     * ContentValues 存一条记录
     */
    public boolean insertInfo(String tablename, ContentValues content) {

        boolean flag = false;

        if (null == content)
            return flag;

        try {
            mSQLiteDatabase.insert(tablename, "", content);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;

    }

    /**
     * 存一条记录
     */
    public long insertInfos(String tablename, ContentValues content) {

        long flag = -1;
        if (null == content)
            return flag;
        try {
            flag = mSQLiteDatabase.insert(tablename, "", content);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * Object 存一条记录
     */
    public long insertInfos(String tablename, Object info) {
        long flag = -1;
        flag = insertInfos(tablename, RefUtil.getContentValues(info));
        return flag;
    }

    /**
     * 存一条记录
     */
    public boolean insertInfo(String tablename, Object info) {

        boolean flag = false;

        flag = insertInfo(tablename, RefUtil.getContentValues(info));

        return flag;

    }

    /**
     * 存一条记录 根据ID判断如果记录存在 删除原来的记录 替换为新的记录
     * <p>
     * id 大于等于1为替换 等于-1为新建
     */
    public boolean saveInfo(String tablename, Object info, Integer id) {

        boolean refinfo = false;
        String deletesql = "delete from " + tablename + " where id=" + id;

        if (id == -1) {
            insertInfo(tablename, info);
        } else {
            delInfo(deletesql);
            ContentValues value = RefUtil.getContentValues(info);
            value.put("id", id);
            insertInfo(tablename, value);

        }

        return refinfo;

    }

    /**
     * 存一条记录 根据ID判断如果记录存在 删除原来的记录 替换为新的记录
     * <p>
     * id 大于等于1为替换 等于-1为新建
     */
    public long saveInfos(String tablename, Object info, String key, long value) {
        long refinfo = -1;
        String deletesql = "delete from " + tablename + " where " + key + "="
                + value;
        if (value == -1) {
            refinfo = insertInfos(tablename, info);
        } else {
            delInfo(deletesql);
            ContentValues contentValue = RefUtil.getContentValues(info);
            contentValue.put(key, value);
            refinfo = insertInfos(tablename, contentValue);
        }
        return refinfo;
    }

    public void deleteDB() {
        mSQLiteDatabase.delete(AccountModel.TABLE_NAME, null, null);
    }

    public long insertInfoObjects(String tablename, List<Object> object) {
        long refinfo = -1;
        mSQLiteDatabase.beginTransaction(); // 手动设置开始事务

        // 数据插入操作循环
        for (Object obj : object) {
            insertInfos(tablename, RefUtil.getContentValues(obj));
        }

        mSQLiteDatabase.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交

        mSQLiteDatabase.endTransaction(); // 处理完成

        return refinfo;
    }

    public long insertInfoStrings(String tablename, List<String> object) {
        long refinfo = -1;
        mSQLiteDatabase.beginTransaction(); // 手动设置开始事务

        // 数据插入操作循环
        for (String sql : object) {
            // LogUtil.info("sql==>"+sql);
            mSQLiteDatabase.execSQL(sql);
        }

        mSQLiteDatabase.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交

        mSQLiteDatabase.endTransaction(); // 处理完成

        return refinfo;
    }

    /**
     * 存多条记录
     */
    public boolean insertInfos(String tablename, List<ContentValues> contents) {

        boolean flag = false;

        for (ContentValues contentValues : contents) {

            mSQLiteDatabase.insert(tablename, "", contentValues);

        }

        return flag;

    }

    /**
     * 取多条记录
     */
    public List getInfosbySql(String sql, String[] parms, Class object) {

        if (null == parms) {
            parms = new String[]{};
        }

        List arrList = new ArrayList();

        try {

            Cursor cursor = mSQLiteDatabase.rawQuery(sql, parms);

            while (cursor.moveToNext()) {

                arrList.add(RefUtil.crateObjectByCursor(object, cursor));

            }

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();

        }

        return arrList;

    }

    /**
     * 取单条记录
     */
    public Object getInfobySql(String sql, String[] parms, Class object) {

        Object reinfo = null;
        if (null == parms) {
            parms = new String[]{};
        }
        try {
            Cursor cursor = mSQLiteDatabase.rawQuery(sql, parms);
            if (cursor.moveToNext()) {
                reinfo = RefUtil.crateObjectByCursor(object, cursor);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reinfo;
    }

    /**
     * 取符合条件的记录数
     */
    public int getInfoCount(String sql, String[] parms) {

        int reinfo = 0;
        if (null == parms) {
            parms = new String[]{};
        }

        try {

            Cursor cursor = mSQLiteDatabase.rawQuery(sql, parms);

            if (cursor.moveToNext()) {
                reinfo = cursor.getInt(0);
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reinfo;

    }

    /**
     * 删除记录
     */
    public boolean delInfo(String tablename, String key, String value) {
        boolean reinfo = false;
        try {

            String sql = "delete from " + tablename + " where " + key + "="
                    + value;

            reinfo = delInfo(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reinfo;

    }

    /**
     * 删除记录
     */
    public boolean delInfo(String sql) {
        boolean reinfo = false;
        try {
            mSQLiteDatabase.execSQL(sql);
            reinfo = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reinfo;
    }

    /**
     * 执行sql
     */
    public boolean execSQL(String sql) {
        boolean reinfo = false;
        try {
            mSQLiteDatabase.execSQL(sql);
            reinfo = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reinfo;

    }

    /**
     * 更新记录
     */
    public boolean updateInfo(String sql, Object[] object) {

        boolean reinfo = false;

        try {
            if (null == object) {
                object = new Object[]{};
            }
            mSQLiteDatabase.execSQL(sql, object);
            reinfo = true;
        } catch (Exception e) {
        }
        return reinfo;
    }

    /**
     * 是否存在该条记录
     */
    public boolean hasObject(String sql, String[] parms) {
        boolean reinfo = false;
        try {
            Cursor cursor = mSQLiteDatabase.rawQuery(sql, parms);
            if (cursor.moveToNext()) {
                reinfo = true;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reinfo;
    }

}
