package com.foreveross.chameleon.cordovaplugin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaPreferences;

/** 用来初始化,主要为了在拷贝H5代码到应用目录时,显示一个启动界面而不是黑屏
 * 这个启动界面必须在config.xml用Splashscreen配置才能生效
 * Created by HuangLi on 2015/7/13.
 */
public class InitApp implements UnzipListener {
    private static InitApp mInstance;
    private UnzipTask unzipTask;
    private CordovaActivity mAct;
    private String h5Path;
    private String filePath;
    private Dialog initDialog;

    public static InitApp getInstace(){
        if(mInstance==null) mInstance=new InitApp();
        return mInstance;
    }
    private InitApp(){

    }
    public void showInitPage(Activity context,CordovaPreferences preferences) {
        initDialog = new Dialog(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        initDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String splash = preferences.getString("SplashScreen", null);
         if(splash==null||splash.length()==0) return;
        int splashscreen = context.getResources().getIdentifier(splash, "drawable", context.getPackageName());
        if(splashscreen==0)return;
        RelativeLayout relativeLayout = new RelativeLayout(context);
        ImageView imageView=new ImageView(context);
        imageView.setImageResource(splashscreen);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        relativeLayout.addView(imageView,new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ProgressBar progressView=new ProgressBar(context);
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,1);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
        relativeLayout.setBackgroundColor(Color.WHITE);
        layoutParams.setMargins(0, 0, 0, 30);
        relativeLayout.addView(progressView, layoutParams);

        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        View view = context.getWindow().getDecorView();//decorView是window中的最顶层view，可以从window中获取到decorView
        view.getWindowVisibleDisplayFrame(rect);
        initDialog.setContentView(relativeLayout);
        initDialog.getWindow().getAttributes().height=dm.heightPixels - rect.top;
        initDialog.setCancelable(false);
        initDialog.show();
    }

    public void initH5(CordovaActivity mAct ,  CordovaPreferences preferences,  String launchUrl){

            this.mAct=mAct;
            h5Path=mAct.getFilesDir().getAbsolutePath()+"/"+ChameleonUtil.WEB_FOLDER_NAME;
            PackageManager pManager = mAct.getPackageManager();
            int code = -1;
            try {
                code = pManager.getPackageInfo(mAct.getPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            filePath= launchUrl.substring(26);
            int preferenceCode=UtilSharedPreferences.getInstance().getOldBuild(mAct.getApplicationContext());
            if (code <= preferenceCode&& FileUtil.isExist(h5Path+"/"+filePath)) {
                go();
            } else {
                showInitPage(mAct, preferences);
//                FileUtil.deleteDirectory(h5Path);
                startUnzipTask();
            }

            UtilSharedPreferences.getInstance().setOldBuild(mAct.getApplicationContext(),
                    code);
        }

    private void startUnzipTask(){
        if(unzipTask==null) unzipTask=new UnzipTask(mAct,this);
        if(unzipTask.getStatus()== AsyncTask.Status.PENDING){
            unzipTask.execute();
        }
    }
    private void go(){
        mAct.loadUrl("file://" + h5Path + "/" + filePath);
       if(initDialog!=null) initDialog.hide();
    }
    private void showAlert(String msg){
        AlertDialog.Builder builder=new AlertDialog.Builder(mAct);
        builder.setTitle("警告");
        builder.setMessage(msg);
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                System.exit(0);
            }
        });
        builder.create().show();
    }

    @Override
    public void onStart(int valus) {

    }

    @Override
    public void onSuccess() {
        go();
    }

    @Override
    public void onFailed(String msg) {
        showAlert(msg);
    }

    @Override
    public void onProgress(int progress) {

    }
}