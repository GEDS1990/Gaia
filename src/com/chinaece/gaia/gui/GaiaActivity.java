package com.chinaece.gaia.gui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.chinaece.gaia.R;
import com.chinaece.gaia.constant.Gaia;
import com.chinaece.gaia.db.DataStorage;
import com.chinaece.gaia.http.OAHttpApi;
import com.chinaece.gaia.types.UserType;
import com.chinaece.gaia.util.UpdateVersionInfo;

public class GaiaActivity extends Activity {
	private final HashMap<String, String> map  = new HashMap<String, String>();
	private URL formatUrl;
	
    boolean network = false;	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DataStorage.load(this);
		try{
			network = getIntent().getBooleanExtra("network", true);
		}catch (Exception e) {
			Toast.makeText(getApplicationContext(), "请检查网络", Toast.LENGTH_SHORT).show();
		}
		if(network && DataStorage.properties.get("token") != null && DataStorage.properties.get("url") != null){
			Intent intent = new Intent(GaiaActivity.this,MainActivity.class);
			startActivity(intent);	
			this.finish();
		}
		setContentView(R.layout.login);
		final Spinner url = (Spinner) findViewById(R.id.edtOAUrl);
//		if(Gaia.DEBUG){
//			map.put("开发测试", "http://10.4.3.1:8080/obpm/");
//		}
		map.put("华东有色电子政务平台", "http://oa.china-ece.com:18081");
		String[] keys = {};
		keys = map.keySet().toArray(keys);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,keys);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		url.setAdapter(adapter);
		Button loginButton = (Button) findViewById(R.id.btnLogin);
		final EditText user = (EditText) findViewById(R.id.edtUserId);
		final EditText password = (EditText) findViewById(R.id.edtPassWord);
		final EditText domain = (EditText) findViewById(R.id.edtDomain);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					formatUrl = new URL(map.get(url.getSelectedItem()));
					ApiTask task = new ApiTask();
					task.execute(formatUrl.toString(), user.getText()
							.toString().trim(), password.getText().toString().trim(), domain
							.getText().toString().trim());
				} catch (MalformedURLException e) {
				  Toast.makeText(getApplicationContext(), "请输入正确的网址", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		Button Ebutton = (Button) findViewById(R.id.btnExit);
		Ebutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(GaiaActivity.this)
						.setTitle("提示")
						.setMessage("确定要退出OA系统")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										GaiaActivity.this.finish();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										return;
									}
								}).create();
				dialog.show();
			}
		});
		UpdateVersionInfo.CheckVersionTask(GaiaActivity.this);
	}
	
	class ApiTask extends AsyncTask<String, Integer, UserType> {
		private ProgressDialog dialog;
		
		@Override
        protected void onPreExecute() {
			dialog = ProgressDialog.show(GaiaActivity.this, "请稍等...", "正在登录...");
        }
		
		@Override
		protected UserType doInBackground(String... params) {
			OAHttpApi OaApi = new OAHttpApi(params[0]);
			UserType user = OaApi.getToken(params[1], params[2], params[3]);
			return user;
		}
		
		@Override
		protected void onPostExecute(UserType user) {
			dialog.dismiss();
			if (user != null) {
				if(user.getToken().indexOf("null") == -1){
					DataStorage.properties.put("token", user.getToken());
					DataStorage.properties.put("url", formatUrl.toString());
					DataStorage.properties.put("name", user.getName());
					DataStorage.save(GaiaActivity.this);
					Intent intent = new Intent(GaiaActivity.this,MainActivity.class);
					startActivityForResult(intent,11);
					finish();
				}
				else{
					Toast.makeText(getApplicationContext(), "请登陆OA系统生成鉴证码", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "请输入合法的用户名和密码",
						Toast.LENGTH_LONG).show();
			}
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		map.clear();
	}
}