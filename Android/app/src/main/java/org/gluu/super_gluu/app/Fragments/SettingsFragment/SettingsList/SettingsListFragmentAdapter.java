package org.gluu.super_gluu.app.fragments.SettingsFragment.SettingsList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import SuperGluu.app.R;

/**
 * Created by nazaryavornytskyy on 5/17/17.
 */

public class SettingsListFragmentAdapter extends BaseAdapter {

    private List<String> list;
    private LayoutInflater mInflater;
    private Activity activity;
    private SettingsListFragment.SettingsListListener mListener;

    public SettingsListFragmentAdapter(Activity activity, List<String> listContact, SettingsListFragment.SettingsListListener settingsListListener) {
        list = listContact;
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
        mListener = settingsListListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = mInflater;
            view = inflater.inflate(R.layout.fragment_setting_list, null);
        }
        view.setTag(position);

        TextView settingName = (TextView) view.findViewById(R.id.settings_name);

        if (settingName != null) {
            settingName.setText(list.get(position));
        }
        TextView status = (TextView) view.findViewById(R.id.settings_status);
        if (status != null) {
            status.setText("Status: " + list.get(position));
        }
        return view;
    }
}
