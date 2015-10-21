package com.example.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.example.config.Global;
import com.example.updateapp.MainActivity;
import com.example.updateapp.R;

import android.R.integer;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class UpdateService extends Service {

	private int titleid = 0;

	// �ļ��洢
	private File updateDir = null;
	private File updateFile = null;

	// ֪ͨ������
	private NotificationManager updateManager = null;
	private Notification updateNotification = null;

	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		titleid = intent.getIntExtra("titleId", 0);

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			updateDir = new File(Environment.getExternalStorageDirectory(),
					Global.downloadDir);
			updateFile = new File(updateDir.getPath(), getResources()
					.getString(titleid) + ".apk");
			if (updateFile.exists()) {
				updateFile.delete();
			}
		}else {
			//�ڲ��洢 /data/data/xxxxxappxxxx/filesĿ¼
			updateDir=getFilesDir();
		}

		updateManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		updateNotification = new Notification();

		// �������ع����е��֪ͨ������Ӧ
		updateIntent = new Intent(getApplicationContext(), MainActivity.class);
		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent,
				0);

		// ����֪ͨ����ʾ������
		updateNotification.icon = R.drawable.ic_launcher;
		updateNotification.tickerText = "��ʼ����";
		updateNotification.setLatestEventInfo(this, "�Ϻ�����", "0%",
				updatePendingIntent);

		// ����֪ͨ
		updateManager.notify(0, updateNotification);

		// ����һ���µ��߳����أ����ʹ��Serviceͬ�����أ��ᵼ��ANR���⣬Service����Ҳ������
		new Thread(new updaterunnable()).start();

		return super.onStartCommand(intent, flags, startId);
	}

	private static final int DOWNLOAD_FINISH = 1;
	private static final int DOWNLOAD_FAIL = 2;

	private Handler updaHandler = new Handler() {
		@SuppressWarnings("deprecation")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DOWNLOAD_FINISH:
				Uri uri = Uri.fromFile(updateFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setDataAndType(uri,
						"application/vnd.android.package-archive");
				updatePendingIntent = PendingIntent.getActivity(
						UpdateService.this, 0, installIntent, 0);

				updateNotification.defaults = Notification.DEFAULT_SOUND;// ��������
				updateNotification.setLatestEventInfo(UpdateService.this,
						"�Ϻ�����", "�������,�����װ��", updatePendingIntent);
				updateManager.notify(0, updateNotification);
				
				//ֹͣ����
				stopSelf();
				break;

			case DOWNLOAD_FAIL:
				//����ʧ��
                updateNotification.setLatestEventInfo(UpdateService.this, "�Ϻ�����", "����ʧ�ܡ�", updatePendingIntent);
                updateManager.notify(0, updateNotification);
				break;
			default:
				stopSelf();
				break;
			}
		};
	};

	class updaterunnable implements Runnable {
		Message message = updaHandler.obtainMessage();

		@Override
		public void run() {
			message.what = DOWNLOAD_FINISH;
			try {
				if (!updateDir.exists()) {
					updateDir.mkdirs();
				}

				if (!updateFile.exists()) {
					updateFile.createNewFile();
				}

				// ������QQΪ����
				long downloadSize = downloadUpdateFile(
						"http://softfile.3g.qq.com:8080/msoft/179/1105/10753/MobileQQ1.0(Android)_Build0198.apk",
						updateFile);
				if (downloadSize > 0) {
					updaHandler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				updaHandler.sendMessage(message);
			}
		}

	}

	@SuppressWarnings("deprecation")
	private long downloadUpdateFile(String downloadUrl, File saveFile) {
		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;

		HttpURLConnection connection = null;
		InputStream isInputStream = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				connection.setRequestProperty("RANGE", "bytes=" + currentSize
						+ "-");
			}
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(20000);
			updateTotalSize = connection.getContentLength();

			if (connection.getResponseCode() == 404) {
				throw new Exception("Fail");
			}

			isInputStream = connection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte[] buffer = new byte[1024 * 4];
			int readSize = 0;
			while ((readSize = isInputStream.read(buffer)) > 0) {
				fos.write(buffer, 0, readSize);
				totalSize += readSize;
				// Ϊ��ֹƵ�����أ��ٷֱ����Ӱٷ�֮ʮ��֪ͨһ��
				if (downloadCount == 0
						|| (int) (totalSize * 100 / updateTotalSize) - 1 > downloadCount) {
					downloadCount += 1;
					updateNotification.setLatestEventInfo(UpdateService.this,
							"��������", (int) (totalSize * 100 / updateTotalSize)
									+ "%", updatePendingIntent);
					updateManager.notify(0, updateNotification);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (connection != null) {
					connection.disconnect();
				}
				if (isInputStream != null) {
					isInputStream.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e2) {
			}

		}
		return totalSize;
	}
}
