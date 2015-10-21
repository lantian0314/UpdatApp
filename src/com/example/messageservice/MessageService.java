package com.example.messageservice;

import com.example.updateapp.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MessageService extends Service {

	private int messageNotificationID=0;
	private Notification messageNotification=null;
	private NotificationManager messageNotificationManager=null;
	
	private Intent messageIntent=null;
	private PendingIntent messagePendingIntent=null;
	
	private MessageThread messageThread=null;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		messageNotification=new Notification();
		messageNotification.icon=R.drawable.ic_launcher;
		messageNotification.tickerText="新消息";
		messageNotification.defaults=Notification.DEFAULT_SOUND;
		messageNotificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		messageIntent=new Intent(MessageService.this, MessageActivity.class);
		messagePendingIntent=PendingIntent.getActivity(this, 0, messageIntent, 0);
		
		messageThread=new MessageThread();
		messageThread.isRunning=true;
		messageThread.start();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	class MessageThread extends Thread{
		public boolean isRunning=true;
		
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			while (isRunning) {
				try {
					Thread.sleep(5000);
					//获取服务器消息
					String serverMessage=getServerMessage();
					if (serverMessage!=null&&!"".equals(serverMessage)) {
						messageNotification.setLatestEventInfo(MessageService.this, "新消息", "奥巴马宣布"+getServerMessage(), messagePendingIntent);
						messageNotificationManager.notify(messageNotificationID, messageNotification);
						//每次通知完成，ID自增，避免消息覆盖
						messageNotificationID++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String getServerMessage() {
		return "Yes!";
	}
	
	@Override
	public void onDestroy() {
		System.exit(0);
		super.onDestroy();
	}
}
