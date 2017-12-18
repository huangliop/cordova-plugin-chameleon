package com.foreveross.chameleon.cordovaplugin;

/**
 * Class explain
 * @author Huang Li   
 */
public interface UnzipListener {
	/**
	 * @param valus Upzip file's size
	 */
	 void onStart(int valus);
     void onSuccess();
     void onFailed(String msg);
     void onProgress(int progress);
}
