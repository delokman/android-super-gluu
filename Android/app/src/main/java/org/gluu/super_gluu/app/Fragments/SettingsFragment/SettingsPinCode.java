package org.gluu.super_gluu.app.fragments.SettingsFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.gluu.super_gluu.app.fragments.PinCodeFragment.PinCodeFragment;

import com.hrules.horizontalnumberpicker.HorizontalNumberPicker;
import com.hrules.horizontalnumberpicker.HorizontalNumberPickerListener;

import org.gluu.super_gluu.app.GluuMainActivity;
import org.gluu.super_gluu.app.NotificationType;
import org.gluu.super_gluu.app.base.ToolbarFragment;
import org.gluu.super_gluu.app.customGluuAlertView.CustomGluuAlert;
import org.gluu.super_gluu.app.settings.Settings;

import SuperGluu.app.R;

/**
 * Created by nazaryavornytskyy on 5/17/17.
 */

public class SettingsPinCode extends ToolbarFragment implements HorizontalNumberPickerListener {

    Button setResetPinButton;
    private Context context;
    private LayoutInflater inflater;
    private PinCodeFragment pinCodeFragment;
    private LinearLayout attemptsLayout;
    private TextView attemptsLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_pincode, container, false);
        context = getContext();
        this.inflater = inflater;

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.nav_drawer_toolbar);
        setupHomeAsUpEnabledToolbar(toolbar, getString(R.string.pin_code));
        setHasOptionsMenu(true);

        setResetPinButton = (Button) view.findViewById(R.id.set_reset_pin_button);
        setResetPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GluuMainActivity.GluuAlertCallback listener = new GluuMainActivity.GluuAlertCallback() {
                    @Override
                    public void onPositiveButton() {
                        Settings.saveIsReset(context);
                        loadPinCodeView(true);
                    }

                    @Override
                    public void onNegativeButton() {
                        //Skip here
                    }
                };
                CustomGluuAlert gluuAlert = new CustomGluuAlert(getActivity());
//                gluuAlert.setMessage(getContext().getString(R.string.change_pin));
                gluuAlert.setSub_title(getContext().getString(R.string.change_pin));
                gluuAlert.setYesTitle(getContext().getString(R.string.yes));
                gluuAlert.setNoTitle(getContext().getString(R.string.no));
                gluuAlert.setmListener(listener);
                gluuAlert.type = NotificationType.RENAME_KEY;
                gluuAlert.show();
            }
        });
        attemptsLayout = (LinearLayout) view.findViewById(R.id.numbers_attempts_view);
        attemptsLabel = (TextView) view.findViewById(R.id.numbers_attempts_label);

        final Switch turOn = (Switch) view.findViewById(R.id.switch_pin_code);
        turOn.setChecked(Settings.getPinCodeEnabled(context));
        setPinCode(turOn.isChecked());
        turOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPinCode(turOn.isChecked());
                if (turOn.isChecked()) {
                    Settings.setFingerprintEnabled(context, !turOn.isChecked());
                }
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

        view.findViewById(R.id.button_ad_free).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("on-ad-free-flow");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });

        //Setup fonts
        TextView settings_textView1 = (TextView) view.findViewById(R.id.settings_textView1);
        TextView settings_textView2 = (TextView) view.findViewById(R.id.settings_textView2);
        TextView textView7 = (TextView) view.findViewById(R.id.textView7);
        TextView numbers_attempts_label = (TextView) view.findViewById(R.id.numbers_attempts_label);


        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "ProximaNova-Regular.otf");
        Typeface faceBold = Typeface.createFromAsset(getActivity().getAssets(), "ProximaNova-Semibold.otf");
        settings_textView1.setTypeface(face);
        settings_textView2.setTypeface(face);
        textView7.setTypeface(face);
        numbers_attempts_label.setTypeface(face);
        attemptsLabel.setTypeface(face);
        setResetPinButton.setTypeface(faceBold);

        return view;
    }

    public void checkPinCode() {
        String pinCode = Settings.getPinCode(context);
        if (!pinCode.equalsIgnoreCase("null")) {
            setResetPinButton.setText(R.string.reset_pin_code);
        } else {
            setResetPinButton.setText(R.string.set_new_pin_code);
        }
    }

    private void setPinCode(Boolean isTurnOn) {
        Settings.setPinCodeEnabled(context, isTurnOn);
        if (isTurnOn) {
            setResetPinButton.setVisibility(View.VISIBLE);
            attemptsLayout.setVisibility(View.VISIBLE);
            attemptsLabel.setVisibility(View.VISIBLE);
        } else {
            setResetPinButton.setVisibility(View.GONE);
            attemptsLayout.setVisibility(View.GONE);
            attemptsLabel.setVisibility(View.GONE);
        }
        checkPinCode();
    }

    private void loadPinCodeView(Boolean isBackStack) {
        PinCodeFragment pinCodeFragment = PinCodeFragment.newInstance(PinCodeFragment.Constant.SET_CODE);

        pinCodeFragment.setIsSettings(true);
        pinCodeFragment.setIsSetNewPinCode(true);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame_layout, pinCodeFragment);
        if (isBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }


    @Override
    public void onHorizontalNumberPickerChanged(HorizontalNumberPicker horizontalNumberPicker, int value) {
        Settings.setPinCodeAttempts(context, String.valueOf(value));
    }
}
