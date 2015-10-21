package com.example.messageservice;

import com.example.updateapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MessageActivity extends Activity {

	protected static final int MSG_STOPSERVICE = 1;
	private boolean isPush=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (isPush) {
			startService(new Intent(this, MessageService.class));
		}
		
		mHandler.sendEmptyMessageDelayed(MSG_STOPSERVICE, 15*1000);
	}
	
	private void stopService(){
		stopService(new Intent(this, MessageService.class));
		isPush=false;
	}
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_STOPSERVICE:
				stopService();
				break;

			default:
				break;
			}
		};
	};
}
