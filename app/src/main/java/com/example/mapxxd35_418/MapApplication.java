package com.example.mapxxd35_418;



import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.BMapManager;
import com.baidu.trace.LBSTraceClient;
import com.xxd.service.MapService;


public class MapApplication extends Application {
	public MapService healthService;
    private static MapApplication mInstance = null;
    public BMapManager mBMapManager = null;
    @Override
    public void onCreate() {
        super.onCreate();
        healthService = new MapService(getApplicationContext());
        mInstance = this;
        initEngineManager(this);
    }

    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager();
        }

        Log.d("ljx", "initEngineManager");
    }

    public static MapApplication getInstance() {
        return mInstance;
    }

}