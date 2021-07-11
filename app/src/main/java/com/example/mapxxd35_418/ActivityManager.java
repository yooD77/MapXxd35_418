package com.example.mapxxd35_418;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;

/**
 * <p>Activity管理器</p>
 * 
 * <p>采用单例模式</p>
 * 
 */
public class ActivityManager {

	private final static ActivityManager sManager = new ActivityManager();
	
	private List<Activity> mActivityList = new ArrayList<Activity>();
	
	private ActivityManager() {}
	
	public static ActivityManager getInstance() {
		return sManager;
	}
	
	public void addActivity(Activity at) {
		if(at != null) {
			mActivityList.add(at);
		}
	}
	
	public void removeActivity(Activity at) {
		if(at != null) {
			for(Activity activity : mActivityList) {
				if(at.equals(activity)) {
					mActivityList.remove(at);
				}
			}
		}
	}
	
	public void removeAllActivity() {
		mActivityList.clear();
	}
	
	public int getActivityCount() {
		return mActivityList.size();
	}
	
	/**
	 * 获取当前Activity上一个Activity的类名,如果at为null或者当前Activity
	 * 为第一个Activity则返回null.
	 * 
	 * @param at
	 * @return 获取成功返回对应的Activity的类名,否则返回null.
	 */
	public String getPreActivityName(Activity at) {
		if(at == null) {
			return null;
		}
		Activity preActivity = null;
		for(Activity activity : mActivityList) {
			if(at.equals(activity)) {
				break;
			}	
			preActivity = activity;
		}
		if(preActivity != null) {
			return preActivity.getClass().getName();
		} else {
			return null;
		}
	}
}
