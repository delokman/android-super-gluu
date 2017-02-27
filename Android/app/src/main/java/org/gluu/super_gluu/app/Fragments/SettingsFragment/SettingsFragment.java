package org.gluu.super_gluu.app.Fragments.SettingsFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.hrules.horizontalnumberpicker.HorizontalNumberPicker;
import com.hrules.horizontalnumberpicker.HorizontalNumberPickerListener;

import org.gluu.super_gluu.app.Activities.GluuApplication;
import org.gluu.super_gluu.app.CustomGluuAlertView.CustomGluuAlert;
import org.gluu.super_gluu.app.Fragments.PinCodeFragment.PinCodeFragment;
import org.gluu.super_gluu.app.GluuMainActivity;
import org.gluu.super_gluu.app.settings.Settings;
import org.gluu.super_gluu.net.CommunicationService;

import SuperGluu.app.R;

/**
 * Created by nazaryavornytskyy on 3/23/16.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener, HorizontalNumberPickerListener {

    Button setResetPinButton;
    private Context context;
    private PinCodeFragment pinCodeFragment;
    private LinearLayout attemptsLayout;
    private TextView attemptsLabelt;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = getContext();
        setResetPinButton = (Button) view.findViewById(R.id.set_reset_pin_button);
        setResetPinButton.setOnClickListener(this);
        attemptsLayout = (LinearLayout) view.findViewById(R.id.numbers_attempts_view);
        attemptsLabelt = (TextView) view.findViewById(R.id.numbers_attempts_label);

        final Switch turOn = (Switch) view.findViewById(R.id.switch_pin_code);
        turOn.setChecked(Settings.getPinCodeEnabled(context));
        setPinCode(turOn.isChecked());
        turOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPinCode(turOn.isChecked());
            }
        });
        if (Settings.getPinCodeEnabled(context)) {
            setResetPinButton.setVisibility(View.VISIBLE);
        } else {
            setResetPinButton.setVisibility(View.GONE);
        }
        HorizontalNumberPicker numberPicker = (HorizontalNumberPicker) view.findViewById(R.id.horizontal_number_picker);
        numberPicker.setMaxValue(10);
        numberPicker.setMinValue(5);
        numberPicker.setValue(Settings.getPinCodeAttempts(context));
        numberPicker.setListener(this);
        checkPinCode();

        final Switch switchSSL = (Switch) view.findViewById(R.id.switch_ssl);
        switchSSL.setChecked(Settings.getSSLEnabled(context));
        switchSSL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchSSL.isChecked()){
                    showWarning(R.string.warning_trust_all_certificate);
                }
                GluuApplication.isTrustAllCertificates =switchSSL.isChecked();
                Settings.setSSLEnabled(context, switchSSL.isChecked());
                Log.v("TAG","SSL Settings enable: "+switchSSL.isChecked());
                // Init network layer
                CommunicationService.init();
            }
        });

        return view;
    }

    private void setPinCode(Boolean isTurnOn){
        Settings.setPinCodeEnabled(context, isTurnOn);
        if (isTurnOn) {
            setResetPinButton.setVisibility(View.VISIBLE);
            attemptsLayout.setVisibility(View.VISIBLE);
            attemptsLabelt.setVisibility(View.VISIBLE);
        } else {
            setResetPinButton.setVisibility(View.GONE);
            attemptsLayout.setVisibility(View.GONE);
            attemptsLabelt.setVisibility(View.GONE);
        }
        checkPinCode();
    }

    public void checkPinCode(){
        String pinCode = Settings.getPinCode(context);
        if (!pinCode.equalsIgnoreCase("null")){
            setResetPinButton.setText(R.string.reset_pin_code);
        } else {
            setResetPinButton.setText(R.string.set_new_pin_code);
        }
    }

    @Override
    public void onClick(View v) {
        GluuMainActivity.GluuAlertCallback listener = new GluuMainActivity.GluuAlertCallback(){
            @Override
            public void onPositiveButton() {
                Settings.saveIsReset(context);
                loadPinCodeView(true);
            }
        };
        CustomGluuAlert gluuAlert = new CustomGluuAlert(getActivity());
        gluuAlert.setMessage(getContext().getString(R.string.change_pin));
        gluuAlert.setYesTitle(getContext().getString(R.string.yes));
        gluuAlert.setNoTitle(getContext().getString(R.string.no));
        gluuAlert.setmListener(listener);
        gluuAlert.show();
    }

    private void loadPinCodeView(Boolean isBackStack){
        pinCodeFragment = new PinCodeFragment();
        pinCodeFragment.setIsSettings(true);
        pinCodeFragment.setIsSetNewPinCode(true);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.root_frame, pinCodeFragment);
        if (isBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onHorizontalNumberPickerChanged(HorizontalNumberPicker horizontalNumberPicker, int value) {

        Settings.setPinCodeAttempts(context, String.valueOf(value));
    }

    private void showWarning(int statusId) {
        String message = context.getString(statusId);
        Intent intent = new Intent("ox_request-precess-event");
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}