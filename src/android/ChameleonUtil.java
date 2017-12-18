package com.foreveross.chameleon.cordovaplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;


/** 
 * Class explain
 * @author Huang Li  
 * @version Create：2015年7月10日
 */
public class ChameleonUtil extends CordovaPlugin {
	public static  final String WEB_FOLDER_NAME="www";

	@Override
		public boolean execute(String action, JSONArray args,
				CallbackContext callbackContext) throws JSONException {
            if (action.equals("removeWebviewCache")) {
                boolean isQuit=false;
                if(args.length()!=0) isQuit=args.getBoolean(0);
                final boolean _q=isQuit;
                this.cordova.getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        webView.clearCache(true);
                        if(_q)System.exit(0);
                    }
                }) ;
                return true;
            }
            return false;
		}
}