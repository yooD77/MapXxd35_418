package com.xxd.ui;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.example.mapxxd35_418.R;
import com.xxd.config.MsgConfig;
import com.xxd.utils.DateDialog;
import com.xxd.utils.DateDialog.CallBack;
import com.xxd.utils.DateDialog.PriorityListener;
import com.xxd.utils.DateUtils;
import com.xxd.utils.GsonService;
import com.xxd.utils.HistoryTrackData;

public class RecordActivity extends Activity {
	private String TAG = "RecordActivity1";
	private Button btn_record;
	private MapView mapView;
	private static BaiduMap mBaiduMap;
    private static PolylineOptions polyline = null;// 路线覆盖物
    private static MarkerOptions startMarker = null;// 起点图标覆盖物
    private static MarkerOptions endMarker = null;// 终点图标覆盖物
    protected static OverlayOptions overlay;// 覆盖物
    private static BitmapDescriptor bmStart;// 起点图标
    private static BitmapDescriptor bmEnd;// 终点图标
    protected static MapStatusUpdate msUpdate = null;
	LBSTraceClient client = new LBSTraceClient(RecordActivity.this);//实例化轨迹服务客户端
	protected static Trace trace = null;// 轨迹服务
	long serviceId  = 116488;//鹰眼服务ID
	String entityName = "mycar";//entity标识
	int traceType = 2;//轨迹服务类型（0 : 不上传位置数据，也不接收报警信息； 1 : 不上传位置数据，但接收报警信息；2 : 上传位置数据，且接收报警信息）
    protected static OnTrackListener trackListener = null;//Track监听器
	private int startTime = 0;
    private int endTime = 0;
    private int year = 0;//查询日期的年月日
    private int month = 0;
    private int day = 0;
    String record_date;
    private static int isProcessed = 0;
    
    
    
    private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch(msg.what){
    		case MsgConfig.SHOW_INFO:
    			String text = (String) msg.getData().get("content");
        		btn_record.setText("选择日期");
        		Toast.makeText(RecordActivity.this, text, Toast.LENGTH_LONG).show();
        		break;
    		case MsgConfig.SHOW_HISTORY:
        		btn_record.setText("查询完毕");
        		break;
    		}
    	};
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG,"onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_record);
		initView();
		initTrace();
	}
	private void  initTrace(){
	    trace = new Trace(RecordActivity.this, serviceId, entityName,traceType);
        initOnTrackListener();
        client.setOnTrackListener(trackListener);
        mapView = (MapView) findViewById(R.id.record_bmapView);
        mBaiduMap = mapView.getMap();
        mapView.showZoomControls(false);
	}
	 /**
     * 初始化OnTrackListener
     */
    private void initOnTrackListener() {
        trackListener = new OnTrackListener() {
            @Override
            public void onRequestFailedCallback(String arg0) { // 请求失败回调接口
                Looper.prepare();
                Looper.loop();
            }
            @Override
            public void onQueryHistoryTrackCallback(String arg0) {// 查询历史轨迹回调接口
            	Log.i(TAG, "track请求成功回调接口消息 : " + arg0);
            	showHistoryTrack(arg0);
            	Message msg = new Message();
            	msg.what = MsgConfig.SHOW_HISTORY;
            	mHandler.sendMessage(msg);
                super.onQueryHistoryTrackCallback(arg0);
            }
        };
    }
  //用来展示历史轨迹
	private void showHistoryTrack(String arg0){
		Log.i(TAG,"查询回调展示，拿到回调的数据1");
		Log.i(TAG,"查询回调展示，拿到回调的数据2");
		HistoryTrackData historyTrackData = GsonService.parseJson(arg0,
	             HistoryTrackData.class);
	    List<LatLng> latLngList = new ArrayList<LatLng>();
	    if (historyTrackData != null && historyTrackData.getStatus() == 0) {
	    	Log.i(TAG,"查询回调展示，拿到回调的数据，数据不为空且数据status为0");
	    	if (historyTrackData.getListPoints() != null) {
	            latLngList.addAll(historyTrackData.getListPoints());
	        }
	        drawHistoryTrack(latLngList, historyTrackData.distance);// 绘制历史轨迹
	    }else{
	    	Log.i(TAG,"查询回调展示，拿到回调的数据，数据为空或数据status为0");
	    	Message msg = new Message();
	    	Bundle data = new Bundle();
	    	data.putString("content", "当天没有进行走动");
	    	msg.setData(data);
	    	msg.what = MsgConfig.SHOW_INFO;
	    	mHandler.sendMessage(msg);
	    	Log.i(TAG,"查询回调展示，拿到回调的数据，数据为空或数据status为0，在toast后面");
	    }
	}
    /**
     * 绘制历史轨迹
     */
    private void drawHistoryTrack(final List<LatLng> points, final double distance) {
    	Log.i(TAG,"查询回调展示，拿到回调的数据，画出轨迹");
    	mBaiduMap.clear();// 绘制新覆盖物前，清空之前的覆盖物
        if (points == null || points.size() == 0) {
            Looper.prepare();
            Message msg = new Message();
            Bundle data = new Bundle();
	    	data.putString("content", "当天没有进行走动");
	    	msg.setData(data);
            msg.what = MsgConfig.SHOW_INFO;
            mHandler.sendMessage(msg);
            Looper.loop();
            resetMarker();
        } else if (points.size() > 1) {
            LatLng llC = points.get(0);
            LatLng llD = points.get(points.size() - 1);
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(llC).include(llD).build();
            msUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);
            bmStart = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
            bmEnd = BitmapDescriptorFactory.fromResource(R.drawable.icon_end);
            // 添加起点图标
            startMarker = new MarkerOptions()
                    .position(points.get(points.size() - 1)).icon(bmStart)
                    .zIndex(9).draggable(true);
            // 添加终点图标
            endMarker = new MarkerOptions().position(points.get(0))
                    .icon(bmEnd).zIndex(9).draggable(true);
            // 添加路线（轨迹）
            polyline = new PolylineOptions().width(10)
                    .color(Color.RED).points(points);
            addMarker();
            Message msg = new Message();
            msg.what = MsgConfig.SHOW_HISTORY;
            mHandler.sendMessage(msg);
            Looper.prepare();
            Looper.loop();
        }else{
        	Message msg = new Message();
        	Bundle data = new Bundle();
 	    	data.putString("content", "当天的走动量太少,被系统忽略");
 	    	msg.setData(data);
            msg.what = MsgConfig.SHOW_INFO;
            mHandler.sendMessage(msg);
        }
     }
	/**
     * 重置覆盖物
     */
    private void resetMarker() {
        startMarker = null;
        endMarker = null;
        polyline = null;
    }
    /**
     * 添加地图覆盖物
     */
    protected static void addMarker() {
        if (null != msUpdate) {
            mBaiduMap.setMapStatus(msUpdate);
        }
        // 路线覆盖物
        if (null != polyline) {
            mBaiduMap.addOverlay(polyline);
        }
        // 实时点覆盖物
        if (null != overlay) {
            mBaiduMap.addOverlay(overlay);
        }
    }
	private void initView() {
		btn_record = (Button) findViewById(R.id.record_date);
		btn_record.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btn_record.setText("正在查询历史轨迹，请稍候");
				queryTrack();
			}
		});
	}
	 /**
     * 轨迹查询(先选择日期，再根据是否纠偏，发送请求)
     */
    private void queryTrack() {
    	Log.i(TAG, "开始查询轨迹，选择日期");
        // 选择日期
        int[] date = null;
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        if (year == 0 && month == 0 && day == 0) {
            String curDate = DateUtils.getCurrentDate();
            date = DateUtils.getYMDArray(curDate, "-");
        }
        if (date != null) {
            year = date[0];
            month = date[1];
            day = date[2];
        }
        DateDialog dateDiolog = new DateDialog(RecordActivity.this, new PriorityListener() {
            public void refreshPriorityUI(String sltYear, String sltMonth,
                    String sltDay, CallBack back) {
                Log.i(TAG, sltYear + sltMonth + sltDay);
                year = Integer.parseInt(sltYear);
                month = Integer.parseInt(sltMonth);
                day = Integer.parseInt(sltDay);
                if(month>9){
                	if(day>9){
                    	record_date = year + "-" + month + "-" + day;
                    }else{
                    	record_date = year + "-"+month + "-" + "0" + day;
                    }
                }else{
                	if(day>9){
                		record_date = year + "-" + "0"+month + "-"+ day;
                    }else{
                    	record_date = year + "-" + "0" +month + "-" + "0" + day;
                    }
                }
                String st = year + "年" + month + "月" + day + "日0时0分0秒";
                String et = year + "年" + month + "月" + day + "日23时59分59秒";
                startTime = Integer.parseInt(DateUtils.getTimeToStamp(st));
                endTime = Integer.parseInt(DateUtils.getTimeToStamp(et));
                back.execute();
            }
        }, new CallBack() {
            public void execute() {
            	Log.i(TAG, "执行查询轨迹");
                queryHistoryTrack();
            }
        }, year, month, day, width, height, "选择日期", 1);
        Window window = dateDiolog.getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        dateDiolog.setCancelable(true);
        dateDiolog.show();
    }
    /**
     * 查询历史轨迹
     */
    private void queryHistoryTrack() {
        int simpleReturn = 0;// 是否返回精简的结果（0 : 否，1 : 是）
        // 开始时间
        if (startTime == 0) {
            startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        }
        if (endTime == 0) {
            endTime = (int) (System.currentTimeMillis() / 1000);
        }
        int pageSize = 1000;// 分页大小
        int pageIndex = 1; // 分页索引
        client.queryHistoryTrack(serviceId, entityName, simpleReturn, startTime, endTime,
                pageSize,pageIndex,trackListener);
        Log.i(TAG, "调用client开始查询");
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	client.onDestroy();
    	mapView.onDestroy();  
    }
}
