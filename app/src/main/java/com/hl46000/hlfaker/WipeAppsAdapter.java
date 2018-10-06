package com.hl46000.hlfaker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WipeAppsAdapter extends ArrayAdapter<WipeApps> {

    private List<WipeApps> wipeApps;
    private Context myContext;
    public WipeAppsAdapter(Context context, int resourceID, List<WipeApps> _wipeApps) {
        super(context, resourceID, _wipeApps);
        this.wipeApps = new ArrayList<WipeApps>();
        this.wipeApps.addAll(_wipeApps);
        this.myContext = context;
    }

    private class AppInfoView{
        CheckBox appName;
        ImageView appIcon;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        AppInfoView _appInfoView = null;

        if(convertView == null){
            LayoutInflater layoutInf = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInf.inflate(R.layout.wipe_app_info, null);

            _appInfoView = new AppInfoView();
            _appInfoView.appName = (CheckBox) convertView.findViewById(R.id.appNameCheckBox);
            _appInfoView.appIcon = (ImageView) convertView.findViewById(R.id.appIconImageView);
            convertView.setTag(_appInfoView);

            _appInfoView.appName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox _checkBox = (CheckBox) v;
                    WipeApps appInfo = (WipeApps) _checkBox.getTag();
                    appInfo.setIsWipe(_checkBox.isChecked());
                    saveToSharedPref(appInfo);
                    Toast.makeText(myContext.getApplicationContext(), "Package: " + appInfo.getPackage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            _appInfoView = (AppInfoView) convertView.getTag();
        }

        WipeApps _appInfo = wipeApps.get(position);
        _appInfoView.appName.setText(_appInfo.getName());
        _appInfoView.appName.setChecked(_appInfo.getIsWipe());
        _appInfoView.appName.setTag(_appInfo);
        Drawable appIcon = _appInfo.getAppIcon();
        if (appIcon != null){
            _appInfoView.appIcon.setImageDrawable(appIcon);
        }

        return  convertView;

    }

    public void saveToSharedPref(WipeApps app){
        WipeSharedPref wsp = new WipeSharedPref(myContext);
        List<String> listWipeApp = wsp.getValue(Common.WIPE_PREF_KEY);
        if (listWipeApp != null){
            if (app.getIsWipe()){
                if(!listWipeApp.contains(app.getPackage())){
                    listWipeApp.add(app.getPackage());
                }
            }else{
                if(listWipeApp.contains(app.getPackage())){
                    listWipeApp.remove(app.getPackage());
                }

            }
        }else {
            listWipeApp = new ArrayList<String>();
            if (app.getIsWipe()){
                listWipeApp.add(app.getPackage());
            }
        }

        wsp.setValue(Common.WIPE_PREF_KEY, listWipeApp);

    }
}
