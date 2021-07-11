package com.example.mapxxd35_418;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.xxd.bean.User;


/**
  * <p>
 * 	数据库操作帮助类,该类继承自{@link OrmLiteSqliteOpenHelper},使用的是ORM开源
 * 	框架Ormlite.
 * </p>
 * <p>
 * 	该数据库帮助类提供了一系列的getXXXDao方法,用于对相应的数据库表进行操作,可以操作的数据库表包括:
 * 	<li>
 * 		info
 *  </li>
 *	<pre>
 *	Example:											
 *		OrmDatabaseHelper helper = new OrmDatabaseHelper(); 
 *		User user = new User();<br />
 *		Dao<User, Integer> userDao = helper.getUserDao();   
 *		try {												
 *			userDao.create(user);							
 *  	} catch(Exception e) {								
 *		e.printStackTrace();							
 *	}													
 *	</pre>
 * </p>
 * 
 * @see #getUserDao()
 * 
 * */
public class OrmDatabaseHelper extends OrmLiteSqliteOpenHelper{

	/** 数据库名 */
	private final static String DB_NAME = "DBmap_xxd.db";
	/**数据库版本 */
	private final static int DB_VERSION = 1;
	
	/**run_info表 */
	private Dao<User, Integer> T_infoDxxd;
	
	
	public OrmDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	public OrmDatabaseHelper(Context context, String databaseName, CursorFactory factory, int databaseVersion) {
		super(context, databaseName, factory, databaseVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			TableUtils.createTable(connectionSource, User.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2, int arg3) {
		
	}
	
	/**
	 * <p>
	 * 	获取run_info表DAO
	 * </p>
	 * 
	 * @return 获取成功返回相应的Dao,获取失败返回null
	 * */
	public Dao<User, Integer> getUserDao() {
		if(T_infoDxxd == null) {
			try {
				T_infoDxxd = getDao(User.class);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return T_infoDxxd;
	}
	
	
	/**
	 * 清除指定表的所有记录
	 * 
	 * @return 成功返回true,失败返回false.
	 * */
	public <T> boolean clearTable(Class<T> clazz) {
		ConnectionSource cs = getConnectionSource();
		try {
			TableUtils.clearTable(cs, clazz);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void close() {
		super.close();
	}
}
