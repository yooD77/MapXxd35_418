package com.example.mapxxd35_418;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;



public class SharePreferencesManager {
private final static String TAG = "SharePreferenceManager";
	
	/** SharePreferenceManager唯一实例对象 */
	private static SharePreferencesManager sSpManager = new SharePreferencesManager();
	
	/** 默认的SharePreferences文件名 */
	private final String defaultSpName = "user";
	
	/** 创建SharePreference文件默认的操作模式,私有模式,只能
	 *  由当前应用和与当前应用相同UserID的Application使用 */
	private final int defaultSpMode = Context.MODE_PRIVATE;
	
	/**
	 * <p>
	 * 	获取SharePreferenceManager唯一实例对象
	 * </p>
	 * 
	 * @return 返回SharePreferenceManager唯一实例对象
	 * */
	public static SharePreferencesManager getInstance() {
		return sSpManager;
	}
	
	/**
	 * <p>
	 * 	添加一个键值对到默认的SharePreference文件中.
	 * </p>
	 * 
	 * @param context 上下文 
	 * @param key  键,不能为空
	 * @param value 值
	 * 
	 * @return 添加成功返回true,失败返回false.
	 * */
	public boolean putBoolean(Context context, String key, boolean value) {
		return putBoolean(context, defaultSpName, defaultSpMode, key, value);
	}
	
	/**
	 * <p>
	 * 	添加一个键值对到指定的SharePreference文件中.注意如果该文件不存在会被自动创建.
	 * </p>
	 * 
	 * @param context 上下文 
	 * @param name 指定的SharePreference文件名,不能为空
	 * @param mode 创建的SharePreference文件模式
	 * @param key  键,不能为空
	 * @param value 值
	 * 
	 * @return 添加成功返回true,失败返回false.
	 * */
	public boolean putBoolean(Context context, String name, int mode, String key, boolean value) {
		if(!checkParameters(context, name, mode, key)) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(name, mode);
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}
	
	/**
	 * <p>
	 * 	添加一个键值对到默认的SharePreference文件中.
	 * </p>
	 * 
	 * @param context 上下文 
	 * @param key  键,不能为空
	 * @param value 值
	 * 
	 * @return 添加成功返回true,失败返回false.
	 * */
	public boolean putFloat(Context context, String key, float value) {
		return putFloat(context, defaultSpName, defaultSpMode, key, value);
	}
	
	/**
	 * <p>
	 * 	添加一个键值对到指定的SharePreference文件中.注意如果该文件不存在会被自动创建.
	 * </p>
	 * 
	 * @param context 上下文 
	 * @param name 指定的SharePreference文件名,不能为空
	 * @param mode 创建的SharePreference文件模式
	 * @param key  键,不能为空
	 * @param value 值
	 * 
	 * @return 添加成功返回true,失败返回false.
	 * */
	public boolean putFloat(Context context, String name, int mode, String key, float value) {
		if(!checkParameters(context, name, mode, key)) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(name, mode);
		Editor editor = sp.edit();
		editor.putFloat(key, value);
		return editor.commit();
	}
	
	/**
	 * <p>
	 * 	添加一个键值对到默认的SharePreference文件中.
	 * </p>
	 * 
	 * @param context 上下文 
	 * @param key  键,不能为空
	 * @param value 值
	 * 
	 * @return 添加成功返回true,失败返回false.
	 * */
	public boolean putInt(Context context, String key, int value) {
		return putInt(context, defaultSpName, defaultSpMode, key, value);
	}
	
	/**
	 * <p>
	 * 	添加一个键值对到指定的SharePreference文件中.注意如果该文件不存在会被自动创建.
	 * </p>
	 * 
	 * @param context 上下文 
	 * @param name 指定的SharePreference文件名,不能为空
	 * @param mode 创建的SharePreference文件模式
	 * @param key  键,不能为空
	 * @param value 值
	 * 
	 * @return 添加成功返回true,失败返回false.
	 * */
	public boolean putInt(Context context, String name, int mode, String key, int value) {
		if(!checkParameters(context, name, mode, key)) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(name, mode);
		Editor editor = sp.edit();
		editor.putInt(key, value);
		return editor.commit();
	}
	
	/**
	 * <p>
	 * 	添加一个键值对到默认的SharePreference文件中.
	 * </p>
	 * 
	 * @param context 上下文 
	 * @param key  键,不能为空
	 * @param value 值
	 * 
	 * @return 添加成功返回true,失败返回false.
	 * */
	public boolean putLong(Context context, String key, long value) {
		return putLong(context, defaultSpName, defaultSpMode, key, value);
	}
	
	/**
	 * <p>
	 * 	添加一个键值对到指定的SharePreference文件中.注意如果该文件不存在会被自动创建.
	 * </p>
	 * 
	 * @param context 上下文 
	 * @param name 指定的SharePreference文件名,不能为空
	 * @param mode 创建的SharePreference文件模式
	 * @param key  键,不能为空
	 * @param value 值
	 * 
	 * @return 添加成功返回true,失败返回false.
	 * */
	public boolean putLong(Context context, String name, int mode, String key, long value) {
		if(!checkParameters(context, name, mode, key)) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(name, mode);
		Editor editor = sp.edit();
		editor.putLong(key, value);
		return editor.commit();
	}
	
	/**
	 * <p>
	 * 	添加一个键值对到默认的SharePreference文件中.
	 * </p>
	 * 
	 * @param context 上下文 
	 * @param key  键,不能为空
	 * @param value 值
	 * 
	 * @return 添加成功返回true,失败返回false.
	 * */
	public boolean putString(Context context, String key, String value) {
		return putString(context, defaultSpName, defaultSpMode, key, value);
	}
	
	/**
	 * <p>
	 * 	添加一个键值对到指定的SharePreference文件中.注意如果该文件不存在会被自动创建.
	 * </p>
	 * 
	 * @param context 上下文 
	 * @param name 指定的SharePreference文件名,不能为空
	 * @param mode 创建的SharePreference文件模式
	 * @param key  键,不能为空
	 * @param value 值
	 * 
	 * @return 添加成功返回true,失败返回false.
	 * */
	public boolean putString(Context context, String name, int mode, String key, String value) {
		if(!checkParameters(context, name, mode, key)) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(name, mode);
		Editor editor = sp.edit();
		editor.putString(key, value);
		return editor.commit();
	}
	
	//检查参数的合法性
		@SuppressWarnings("deprecation")
		private boolean checkParameters(Context context, String name, int mode, String key) {
			if(context == null) {
				return false;
			}
			if(TextUtils.isEmpty(name)) {
				return false;
			}
			if(mode != Context.MODE_PRIVATE
					&& mode != Context.MODE_WORLD_READABLE
					&& mode != Context.MODE_WORLD_WRITEABLE) {
				return false;
			}
			//不允许存空键
			if(TextUtils.isEmpty(key)) {
				return false;
			}
			return true;
		}
		
		public int getInt(Context context, String name, int mode, String key){
			if(!checkParameters(context, name, mode, key)){
				return -1;
			}
			SharedPreferences sp = context.getSharedPreferences(name, mode);
			return sp.getInt(key, 0);
		}
		
		public String getString(Context context, String name, int mode, String key){
			if(!checkParameters(context, name, mode, key)){
				return "";
			}
			SharedPreferences sp = context.getSharedPreferences(name, mode);
			return sp.getString(key, "");
		}
}
