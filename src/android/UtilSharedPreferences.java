package com.foreveross.chameleon.cordovaplugin;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by HuangLi on 2015/7/13.
 */
public class UtilSharedPreferences {
    private static UtilSharedPreferences mInstance;
    public static UtilSharedPreferences getInstance() {
        if(mInstance==null) mInstance=new UtilSharedPreferences();
        return mInstance;
    }

    public int getOldBuild(Context context){
        SharedPreferences preferences=context.getSharedPreferences(context.getPackageName(), 0);
        return preferences.getInt("oldBuild", 0);
    }
    public void setOldBuild(Context context,int build){
        SharedPreferences preferences=context.getSharedPreferences(context.getPackageName(), 0);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("oldBuild", build);
        editor.commit();
    }
}
