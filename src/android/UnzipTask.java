package com.foreveross.chameleon.cordovaplugin;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.FileNotFoundException;

/** 
 * Class explain
 * @author Huang Li  
 * @version Create：2014年6月20日  
 */
public class UnzipTask extends AsyncTask<Void, Void, Integer> {
	
	private static final int ERROR_NO_SDCARD=-1;
	private static final int ERROR_UNZIP_FAIL=-2;
	private static final int ERROR_FILE_NOT_FOUND = -3;
	private static final int OK=1;
	private static final String H5_FILE_NAME="web_lib.so";

	private UnzipListener listener;
	private Activity context;
	private String BasePath;
	public UnzipTask(Activity context,UnzipListener listener) {
		this.context=context;
		this.listener=listener; 
		this.BasePath=context.getFilesDir().getAbsolutePath()+"/";
	}
	@Override
	protected Integer doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		if(!FileUtil.isHaveSD()){
			return ERROR_NO_SDCARD;
		}
		try {
				FileUtil.unzip(H5_FILE_NAME, BasePath, context.getApplicationContext(), listener);
		}catch (FileNotFoundException e){
			e.printStackTrace();
			return ERROR_FILE_NOT_FOUND;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ERROR_UNZIP_FAIL;
		}

		return OK;
	}
	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method 
		super.onProgressUpdate(values);
	}
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		switch (result) {
		case ERROR_NO_SDCARD:
			listener.onFailed("没有检测到内存卡,请安装内存卡后再重试");
			break;
		case ERROR_UNZIP_FAIL:
			listener.onFailed("安装出错,请重启手机！");
			break;
		case ERROR_FILE_NOT_FOUND:
			listener.onFailed("缺少必要的文件");
			break;
		case OK:
			listener.onSuccess();
			break;
		default:
			break;
		}
		super.onPostExecute(result);
	}

}
