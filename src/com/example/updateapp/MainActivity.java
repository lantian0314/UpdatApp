package com.example.updateapp;

import com.example.config.Global;
import com.example.update.UpdateService;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		checkVersion();
	}

	private void checkVersion() {
		if (Global.localVersion < Global.serverVersion) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("�������")
					.setMessage("�����°汾,������������ʹ��.")
					.setPositiveButton("����",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent updateIntent=new Intent(getApplicationContext(), UpdateService.class);
									updateIntent.putExtra("titleId",R.string.app_name);
									startService(updateIntent);
								}
							}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
			alert.create().show();
		}

	}
}
