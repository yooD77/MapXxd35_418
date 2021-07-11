package com.example.mapxxd35_418;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.Trace;
import com.baidu.trace.TraceLocation;
import com.xxd.config.MsgConfig;
import com.xxd.service.MapService;
import com.xxd.ui.BaseActivity;
import com.xxd.ui.RecordActivity;
import com.xxd.utils.DateUtils;
import com.xxd.utils.LocationEntry;
import com.xxd.utils.Utils;

public class MainActivity extends BaseActivity implements OnClickListener {
	private final static String TAG = "MainActivity";
	
	private ViewPager mViewPager;
	private final int tabCount = 3;/** Tab数 */
	private TextView[] tabTextView = new TextView[tabCount];/** TextView数组存储 */
	private String[] tabText = new String[tabCount];/** 底部Tab文字 */
	private List<View> mViews; // ViewPager包含的View
	private View view_home;
	private View view_run;
	private View view_mine;
	private LayoutInflater mInflater; 
	private TextView tv_tab_mine;
	private TextView tv_tab_run;
	private TextView tv_tab_home;
	private int showTabIndex = 0;/**要显示的Tab*/
	private int currentTabIndex = 0;/** 当前选择Tab索引 */
	private TextView rightTitle;
    private PagerAdapter mViewPagerAdapter;//mViewPager的适配器 
    
    private BDLocation tempLocation;//最新位置缓存
    
	private Handler locHander = new Handler() {// 接收定位结果消息，并显示在地图上
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case MsgConfig.MSG_MODE_MAP:
				try {
					Log.i(TAG,"开启定位");
					BDLocation location = msg.getData().getParcelable("loc");
					int iscal = msg.getData().getInt("iscalculate");
					if (location != null) {
						LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
						// 构建Marker图标
						BitmapDescriptor bitmap = null;
						if (iscal == 0) {
							bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark); // 非推算结果
						} else {
							bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark); // 推算结果
						}
						// 构建MarkerOption，用于在地图上添加Marker
						OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
						mBaiduMap.addOverlay(option);// 在地图上添加Marker，并显示
						mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
					}
					else{
						Log.i(TAG,"location为空");
					}
				} catch (Exception e) {
				}
				break;
            case RUN_STOP:
            	bt_start.setText("开启轨迹服务");
            	break;
            case MsgConfig.SHOW_INFO:
            	String text = (String) msg.getData().get("content");
            	Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
            	break;
            default:
                super.handleMessage(msg);
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_main);
		mInflater = LayoutInflater.from(this); 
		initView();
		initMapView();
		initRunView();
		initMineView();
	}
	@Override
	protected void onStart() {
		super.onStart();
		resetTab(showTabIndex);//开始时显示首页
		//setActionBar(showTabIndex);//初始化ActionBar
	}
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.id_ViewPager1);
		tabText[0] = getResources().getString(R.string.home);
		tabText[1] = getResources().getString(R.string.run);
		tabText[2] = getResources().getString(R.string.mine);
		tv_tab_home = (TextView) findViewById(R.id.tab_home_btn);
		tv_tab_home.setClickable(true);
		tv_tab_home.setOnClickListener(this);
		tv_tab_mine = (TextView) findViewById(R.id.tab_mine_btn);
		tv_tab_mine.setClickable(true);
		tv_tab_mine.setOnClickListener(this);
		tv_tab_run = (TextView) findViewById(R.id.tab_run_btn);
		tv_tab_run.setClickable(true);
		tv_tab_run.setOnClickListener(this);
		//便于设置选择状态
		tabTextView[0] = tv_tab_home;
		tabTextView[1] = tv_tab_run;
		tabTextView[2] = tv_tab_mine;
		mViews = new ArrayList<View>();  
	    view_home = mInflater.inflate(R.layout.tab_map, null);  
        view_run = mInflater.inflate(R.layout.tab_run, null);  
        view_mine = mInflater.inflate(R.layout.tab_mine, null);  
        mViews.add(view_home);  
        mViews.add(view_run);  
        mViews.add(view_mine);  
		mViewPagerAdapter = new PagerAdapter()  {  
            @Override  
            public void destroyItem(ViewGroup container, int position, Object object){  
                container.removeView(mViews.get(position));  
            }  
            @Override  
            public Object instantiateItem(ViewGroup container, int position){  
                View view = mViews.get(position);  
                container.addView(view);  
                return view;  
            }  
            @Override  
            public boolean isViewFromObject(View arg0, Object arg1){  
                return arg0 == arg1;  
            }  
            @Override  
            public int getCount(){  
                return mViews.size();  
            }  
        }; 
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener(){ 
            @Override  
            public void onPageSelected(int position){  
            	resetTab(position);
            	//setActionBar(position);
            }  
            @Override  
            public void onPageScrolled(int arg0, float arg1, int arg2){  
            }  
            @Override  
            public void onPageScrollStateChanged(int arg0){  
            }  
        });  
	}
	@Override
	public void onClick(View v) {
		int selectTabIndex = -1;
		switch(v.getId()) {
		case R.id.tab_home_btn://点击底部Tab地图
			selectTabIndex = 0;
			break;
		case R.id.tab_run_btn://点击底部Tab轨迹
			selectTabIndex = 1;
			break;
		case R.id.tab_mine_btn://点击底部Tab我的
			selectTabIndex = 2;
			break;
		}
		if(selectTabIndex != -1 && selectTabIndex != currentTabIndex) {
			if(selectTabIndex < 0 || selectTabIndex >= tabCount) {
				Log.e(TAG, "selectTabIndex is " + selectTabIndex + " and out of 0 ~ " + (tabCount-1));
			} else {
				resetTab(selectTabIndex);//重新设置Tab
				//setActionBar(selectTabIndex);//重新设置ActionBar
			}
		}
		mViewPager.setCurrentItem(selectTabIndex);
	}
	
	private void resetTab(int selectTabIndex) {
		tabTextView[currentTabIndex].setSelected(false);//原先选择的Tab设置为不选择
		tabTextView[selectTabIndex].setSelected(true);//设置新的Tab
		currentTabIndex = selectTabIndex;
		TextView tv = setCenterTitle(tabText[currentTabIndex]);
		tv.setCompoundDrawables(null, null, null, null);
		showTabIndex = currentTabIndex;
	}
	
	//首页地图相关控件
	private BaiduMap mBaiduMap;
    private MapService locService;
    private MapView mMapView = null;    
	private LinkedList<LocationEntry> locationList = new LinkedList<LocationEntry>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果
	public TextView tv_location;
	
	private void initMapView() {
		//获取地图控件引用  
        mMapView = (MapView) view_home.findViewById(R.id.bmapView);  
        tv_location = (TextView) view_home.findViewById(R.id.tv_location);
        mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));
		locService = ((MapApplication) getApplication()).healthService;
		LocationClientOption mOption = locService.getDefaultLocationClientOption();
		mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving); 
		mOption.setCoorType("bd09ll");
		locService.setLocationOption(mOption);
		locService.registerListener(mListener);
		locService.start();
	}
	/***
	 * 定位结果回调，在此方法中处理定位结果
	 */
	BDLocationListener mListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.i(TAG,"定位返回信息");
			if (location != null && (location.getLocType() == 161 || location.getLocType() == 66)) {
				tempLocation = location;
				locate(location);
			}
		}
	};
	//记录轨迹模块相关控件
	private TextView run_time;
	private TextView run_step;
	private TextView run_distance;
	private Boolean isRunning = false;
	private Button bt_start;
	private static BaiduMap mRunBaiduMap;
    private MapView bmapView = null;
    LBSTraceClient client = new LBSTraceClient(MainActivity.this);//实例化轨迹服务客户端
    protected static Trace trace = null; // 轨迹服务
    protected static OnEntityListener entityListener = null;// Entity监听器
    protected OnStartTraceListener  startTraceListener;
    protected OnStopTraceListener stopTraceListener;
    
    long serviceId  = 116488;//鹰眼服务ID
    private int gatherInterval = 5; // 采集周期（单位 : 秒）
    private int packInterval = 60;//打包周期（单位 : 秒）
	String entityName = "mycar";//entity标识
	//轨迹服务类型（0 : 不上传位置数据，也不接收报警信息； 1 : 不上传位置数据，但接收报警信息；2 : 上传位置数据，且接收报警信息）
	int traceType = 2;
	RefreshThread refreshThread;//该线程用来查询实时位置
	private static List<LatLng> pointList = new ArrayList<LatLng>();
	private static BitmapDescriptor realtimeBitmap; //图标
	protected static OverlayOptions overlay;// 覆盖物
	private static PolylineOptions polyline = null;// 路线覆盖物 
	protected static MapStatusUpdate msUpdate = null;
	long start_run;
	long stop_run;
	private static int run_time_show = 0;
	Timer timer = new Timer();  
	 private static final int RUN_STOP = 15;
    private void initRunView() {
    	initTrace();
		bt_start = (Button) view_run.findViewById(R.id.run_start);
		bt_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startRun();
			}
		});
	}
    private void  initTrace(){
	    trace = new Trace(getApplicationContext(), serviceId, entityName,traceType);
        initOnEntityListener();
        initStartListener();
        initStopListener();
        // 设置采集周期
        client.setInterval(gatherInterval, packInterval);
        bmapView = (MapView) view_run.findViewById(R.id.run_mapView);  
        mRunBaiduMap = bmapView.getMap();
        bmapView.showZoomControls(false);
	}
    
    /**
     * 初始化OnEntityListener
     */
    private void initOnEntityListener() {
        entityListener = new OnEntityListener() {
            @Override
            public void onRequestFailedCallback(String arg0) {// 请求失败回调接口
                Looper.prepare();
                Log.i(TAG,"entity请求失败回调接口消息 : " + arg0);
                Looper.loop();
            }
            @Override
            public void onAddEntityCallback(String arg0) {// 添加entity回调接口
                Looper.prepare();
                Log.i(TAG,"添加entity回调接口消息 : " + arg0);
                Looper.loop();
            }
            @Override
            public void onQueryEntityListCallback(String message) {// 查询entity列表回调接口
            }
            @Override
            public void onReceiveLocation(TraceLocation location) {
            	// showMessage("onReceiveLocation拿到回调位置");
            	showRealLocation(location);
            }
        };
    }
    private void initStartListener() {
		//实例化开启轨迹服务回调接口
		startTraceListener = new OnStartTraceListener() {       
		    //开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
		     @Override
		     public void onTraceCallback(int arg0, String arg1) {   
		    	 Log.i(TAG,"onTraceCallback开启轨迹服务回调" + arg0 + "，消息内容 : " + arg1 + "]");
		    	 showMessage("开启轨迹服务");
	                if (0 == arg0 || 10006 == arg0 || 10008 == arg0) {
	                    isRunning = true;
	                    startRefreshThread(true);
	                }
		     }
		    //轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
		     @Override
		     public void onTracePushCallback(byte arg0, String arg1) {
		    	 Log.i(TAG,"轨迹服务推送接口消息 [消息类型 : " + arg0 + "，消息内容 : " + arg1 + "]");
		     }
		};
	}
    private void initStopListener() {
		 stopTraceListener = new OnStopTraceListener() {
           public void onStopTraceSuccess() {// 轨迹服务停止成功
           	Log.i(TAG,"onStopTraceSuccess关闭轨迹服务");
               showMessage("停止轨迹服务成功");
               locHander.sendEmptyMessage(RUN_STOP);
               isRunning = false;
               startRefreshThread(false);
           }
           // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
           public void onStopTraceFailed(int arg0, String arg1) {
           	}
           };
	}
    //用来展示当前的运动轨迹
    private void showRealLocation(TraceLocation location) {
    	Log.i(TAG,"showRealLocation展示当前运动轨迹");
    	 if (null == refreshThread || !refreshThread.refresh) {
    		 return;
         }
         double latitude = location.getLatitude();
         double longitude = location.getLongitude();
         if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) < 0.000001) {
         } else {
             LatLng latLng = new LatLng(latitude, longitude);
             pointList.add(latLng);
             drawRealtimePoint(latLng);
         }
	}

    private void drawRealtimePoint(LatLng point) {
    	Log.i(TAG,"绘制路线的点");
        mRunBaiduMap.clear();
        MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(18).build();
        msUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        realtimeBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);
        overlay = new MarkerOptions().position(point)
                .icon(realtimeBitmap).zIndex(9).draggable(true);
        if (pointList.size() >= 2 && pointList.size() <= 10000) {
            polyline = new PolylineOptions().width(10)// 添加路线（轨迹）
                    .color(Color.RED).points(pointList);
        }
        addMarker();
    }
    /**
     * 添加地图覆盖物
     */
    protected static void addMarker() {
        if (null != msUpdate) {
        	Log.i(TAG,"设置mRunBaiduMap的状态");
            mRunBaiduMap.setMapStatus(msUpdate);
        }
        if (null != polyline) {// 路线覆盖物
        	Log.i(TAG,"添加mRunBaiduMap的路线覆盖物");
            mRunBaiduMap.addOverlay(polyline);
        }
        if (null != overlay) {// 实时点覆盖物
        	Log.i(TAG,"添加mRunBaiduMap的实时点覆盖物");
            mRunBaiduMap.addOverlay(overlay);
        }
    }
    
    protected void startRefreshThread(boolean isStart) {
    	Log.i(TAG,"开启监控线程");
        if (null == refreshThread) {
            refreshThread = new RefreshThread();
        }
        refreshThread.refresh = isStart;
        if (isStart) {
            if (!refreshThread.isAlive()) {
                refreshThread.start();
            }
        } else {
            refreshThread = null;
        }
    }
    protected class RefreshThread extends Thread {
        protected boolean refresh = true;
        @Override
        public void run() {
            while (refresh) {
                queryRealtimeTrack();// 查询实时轨迹
                try {
                    Thread.sleep(gatherInterval * 1000);
                } catch (InterruptedException e) {
                    System.out.println("线程休眠失败");
                }
            }
        }
    }
    /**
     * 查询实时轨迹
     */
    private void queryRealtimeTrack() {
        client.queryRealtimeLoc(serviceId, entityListener);
    }
    long temp_time = 0;
	private void startRun() {
		if(!isRunning){
			start_run = System.currentTimeMillis();
			bt_start.setText("停止轨迹服务");
			startService();
		}
		else{ 
			String curDate = DateUtils.getCurrentDate();
			stop_run = System.currentTimeMillis();
			temp_time = stop_run-start_run+temp_time;
			stop_run = 0;
			start_run = 0;
			//updateInfo(curDate,temp_time,mStepValue,mDistanceValue);
			bt_start.setText("正在停止轨迹服务...");
			stopService();
		}
	}
	
	private void startService() {
        if (! isRunning) {
            Log.i(TAG, "Start StepService");
            isRunning = true;
            client.startTrace(trace, startTraceListener);//开启轨迹服务
        }
    }
    private void stopService() {
    	 Log.i(TAG, "stopService");
        client.stopTrace(trace, stopTraceListener);
    }
    
    
    //初始化我的模块
	private void initMineView() {
		view_mine.findViewById(R.id.mine_record).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intend = new Intent(MainActivity.this,RecordActivity.class);
				startActivity(intend);
			}
		});
		view_mine.findViewById(R.id.exit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isRunning){
					showExit();
				}else{
					MainActivity.this.finish();
				}
			}
		});
	}
	private void locate(BDLocation location){
		if (location != null && (location.getLocType() == 161 || location.getLocType() == 66)) {
			Message locMsg = new Message();
			locMsg.what = MsgConfig.MSG_MODE_MAP;
			Bundle locData;
			locData = Algorithm(location);
			if (locData != null) {
				locData.putParcelable("loc", location);
				locMsg.setData(locData);
				locHander.sendMessage(locMsg);
			}
			String str = "";
			if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
				for (int i = 0; i < location.getPoiList().size(); i++) {
					Poi poi = (Poi) location.getPoiList().get(i);
					str = ("当前位置:"+poi.getName() + ";");
				}
			}
			if(str!=""){
				tv_location.setVisibility(View.VISIBLE);
				tv_location.setText(str);
			}
		}
	}
	 @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        locService.unregisterListener(mListener);
		locService.stop();
		stopService();
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
    }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
    }  
	/***
	 * 平滑策略代码实现方法，主要通过对新定位和历史定位结果进行速度评分，
	 * 来判断新定位结果的抖动幅度，如果超过经验值，则判定为过大抖动，进行平滑处理,若速度过快，
	 * 则推测有可能是由于运动速度本身造成的，则不进行低速平滑处理 ╭(●｀∀´●)╯
	 */
	private Bundle Algorithm(BDLocation location) {
		Bundle locData = new Bundle();
		double curSpeed = 0;
		if (locationList.isEmpty() || locationList.size() < 2) {
			LocationEntry temp = new LocationEntry();
			temp.location = location;
			temp.time = System.currentTimeMillis();
			locData.putInt("iscalculate", 0);
			locationList.add(temp);
		} else {
			if (locationList.size() > 5)
				locationList.removeFirst();
			double score = 0;
			for (int i = 0; i < locationList.size(); ++i) {
				LatLng lastPoint = new LatLng(locationList.get(i).location.getLatitude(),
						locationList.get(i).location.getLongitude());
				LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
				double distance = DistanceUtil.getDistance(lastPoint, curPoint);
				curSpeed = distance / (System.currentTimeMillis() - locationList.get(i).time) / 1000;
				score += curSpeed * Utils.EARTH_WEIGHT[i];
			}
			if (score > 0.00000999 && score < 0.00005) { // 经验值,开发者可根据业务自行调整，也可以不使用这种算法
				location.setLongitude((locationList.
						get(locationList.size() - 1).location.getLongitude() + location.getLongitude())/ 2);
				location.setLatitude((locationList.
						get(locationList.size() - 1).location.getLatitude() + location.getLatitude())/ 2);
				locData.putInt("iscalculate", 1);
			} else {
				locData.putInt("iscalculate", 0);
			}
			LocationEntry newLocation = new LocationEntry();
			newLocation.location = location;
			newLocation.time = System.currentTimeMillis();
			locationList.add(newLocation);
		}
		return locData;
	}
	
	//点击后退键弹出对话框提示
	@Override
	public void onBackPressed() {
		if(isRunning){
			showExit();	
		}else{
			this.finish();
		}
	}
	private void showExit() {
		AlertDialog.Builder normalDia=new AlertDialog.Builder(MainActivity.this);  
        normalDia.setIcon(R.drawable.ic_launcher);  
        normalDia.setMessage("退出程序会停止轨迹服务,你确定要退出程序吗");  
        normalDia.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();
            	MainActivity.this.finish();  
            }  
        });  
        normalDia.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
            	dialog.dismiss();
            }  
        });  
        normalDia.create().show();  
    }  
	private void showMessage(String text){
		Message msg = new Message();
    	Bundle data = new Bundle();
    	data.putString("content",text);
    	msg.setData(data);
    	msg.what = MsgConfig.SHOW_INFO;
    	locHander.sendMessage(msg);
    }
}
