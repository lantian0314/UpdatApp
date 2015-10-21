package com.example.subway.application;

import com.example.config.Global;

import android.app.Application;


public class SubwayApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		initGlobal();
	}

	private void initGlobal() {
		try {
			Global.localVersion=getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			Global.serverVersion=2;//假定服务器版本是2 本地是1
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
