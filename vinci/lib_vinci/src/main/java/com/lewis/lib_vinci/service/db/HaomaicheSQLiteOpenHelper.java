
package com.lewis.lib_vinci.service.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.lewis.lib_vinci.utils.LogUtil;

/**
 * @author Lihailun
 * @date 2015/05/22
 */

public class HaomaicheSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "OnlineSQLiteOpenHelper==>";

    public static final int VERSION = 1;

    public static final String ONLINE_DB_NAME = "online_db.db";


    public HaomaicheSQLiteOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public HaomaicheSQLiteOpenHelper(Context context) {
        super(context, ONLINE_DB_NAME, null, VERSION);
        LogUtil.i(TAG, "AidSQLiteOpenHelper");
    }

    //此处创建表的操 作
    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.i(TAG, "onCreate");
        createTable(db);
    }

    private void createTable(SQLiteDatabase db) {
        db.execSQL(AccountModel.CREATE_TABLE_SQL);
    }

    /*
       当数据库有升级时，在此操作
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        LogUtil.i(TAG, "onUpgrade");

		/*if (oldVersion == 1 && newVersion == 2) {
            db.execSQL("ALTER TABLE "+AccountModel.TABLE_NAME+" ADD COLUMN code char");
		}
		if(oldVersion == 2 && newVersion == 3){
			db.execSQL(MessageModel.CREATE_TABLE_SQL);
			db.execSQL(MessageTypeModel.CREATE_TABLE_SQL);
		}
		
		if(oldVersion == 3 && newVersion == 4){
			db.execSQL(GroupModel.CREATE_TABLE_SQL);
			db.execSQL(ChildModel.CREATE_TABLE_SQL);
		}*/

    }

}
