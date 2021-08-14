package com.example.androidlabs;

import android.app.Activity;
import android.widget.CompoundButton;

import com.google.android.material.snackbar.Snackbar;

class CheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
    private final Activity act;

    public CheckedChangeListener(Activity act) {
        this.act = act;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Snackbar.make(buttonView, act.getResources().getString((isChecked) ? R.string.snackbar_on : R.string.snackbar_off), Snackbar.LENGTH_LONG)
                .setAction(act.getResources().getString(R.string.snackbar_undo), click -> buttonView.setChecked(!isChecked))
                .show();
    }
}
