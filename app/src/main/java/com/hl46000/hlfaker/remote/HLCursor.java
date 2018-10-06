package com.hl46000.hlfaker.remote;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hl46000.hlfaker.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by LONG-iOS Dev on 9/26/2017.
 */

public class HLCursor {
    private final String LOG_TAG = "HLCursor";
    private Context appContext;
    protected WindowManager mWindowsManager;
    protected WindowManager.LayoutParams cursorLParams;
    protected ImageView cursorImageView;
    public int windowsX;
    public int windowsY;

    public HLCursor(Context sharedContext){
        try{
            appContext = sharedContext;
            mWindowsManager = (WindowManager)appContext.getSystemService(Context.WINDOW_SERVICE);
            cursorImageView = new ImageView(appContext);
            cursorImageView.setImageResource(R.drawable.mouse_cursor);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(45, 45);
            cursorImageView.setLayoutParams(params);
            DisplayMetrics deviceDisplay = new DisplayMetrics();
            mWindowsManager.getDefaultDisplay().getMetrics(deviceDisplay);
            windowsX = deviceDisplay.widthPixels;
            windowsY = deviceDisplay.heightPixels;
            //Log.d(LOG_TAG, "WindowsX: " + windowsX + "; WindowsY: " + windowsY);
        }catch (Exception e){
            Log.d(LOG_TAG, "ERROR: " + e.getMessage());
        }
    }

    public boolean enableCursor(boolean enable){
        try{
            AsyncTaskCursor enableCursorTask = new AsyncTaskCursor(this);
            return enableCursorTask.execute(enable).get(5000, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            Log.d(LOG_TAG, "Enable/Disable Cursor ERROR: " + e.getMessage());
            return false;
        }
    }

    public void updateCoordinates(final int x, final int y){
        try{
            AsyncTaskCoor updateCoorTask = new AsyncTaskCoor(this);
            updateCoorTask.execute(x, y).get(5000, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            Log.d(LOG_TAG, "Update Coordinates Cursor ERROR: " + e.getMessage());
            //return false;
        }
    }

    /**
     * AsyncTask Update Cursor Coordinates
     */
    private class AsyncTaskCoor extends AsyncTask<Integer, Integer, Void>{

        private HLCursor myCursor;

        public AsyncTaskCoor(HLCursor _HLCursor){
            myCursor = _HLCursor;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            if(myCursor.cursorLParams != null && params.length >= 2){
                publishProgress(params);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            myCursor.cursorLParams.x = values[0];
            myCursor.cursorLParams.y = values[1];
            try{
                myCursor.mWindowsManager.updateViewLayout(myCursor.cursorImageView, myCursor.cursorLParams);
            }catch (Exception e){
                Log.d(LOG_TAG, "Update Coordinates ERROR: " + e.getMessage());
            }

        }
    }

    /**
     * AsyncTask Enable/Disable Cursor
     */
    private class AsyncTaskCursor extends AsyncTask<Boolean, Boolean, Boolean>{
        private HLCursor myCursor;
        public AsyncTaskCursor(HLCursor _HLCursor){
            myCursor = _HLCursor;
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            try{
                if(params[0]){
                    if(myCursor.cursorLParams == null){
                        myCursor.cursorLParams = new WindowManager.LayoutParams(
                                45,
                                45,
                                WindowManager.LayoutParams.TYPE_PHONE,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                PixelFormat.TRANSLUCENT);
                    }
                    myCursor.cursorLParams.gravity = Gravity.TOP | Gravity.LEFT;
                    myCursor.cursorLParams.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                    myCursor.cursorLParams.x = 300;
                    myCursor.cursorLParams.y = 300;
                    //mWindowsManager.addView(cursorImageView, cursorLParams);
                    publishProgress(true);
                    return true;
                }else {
                    if(myCursor.cursorImageView != null && myCursor.mWindowsManager != null){
                        //mWindowsManager.removeView(cursorImageView);
                        publishProgress(false);
                    }
                    return true;
                }
            }catch (Exception e){
                Log.d(LOG_TAG, "Enable/Disable Cursor ERROR: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            try{
                if(values[0]){
                    myCursor.mWindowsManager.addView(myCursor.cursorImageView, myCursor.cursorLParams);
                }else {
                    myCursor.mWindowsManager.removeView(myCursor.cursorImageView);
                }
            }catch (Exception e){
                Log.d(LOG_TAG, "Enable/Disable Cursor ERROR: " + e.getMessage());
            }
        }

    }
}
