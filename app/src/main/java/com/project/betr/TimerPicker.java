package com.project.betr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.project.betr.R;

public class TimerPicker extends androidx.appcompat.widget.AppCompatTextView {
    private NumberPicker minutePicker;
    private NumberPicker secondPicker;
    private int selectedMinute = 0;
    private int selectedSecond = 0;
    AlertDialog.Builder builder;

    public TimerPicker(Context context) {
        super(context);
        this.setPadding(dpToPx(10), 0, dpToPx(10), 0);
        this.setBackground(context.getDrawable(R.drawable.table_block_border));
        init();
    }


    private void init() {
        setText("00:00");
        setGravity(Gravity.CENTER);
        minutePicker = new NumberPicker(getContext());
        secondPicker = new NumberPicker(getContext());
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(layout);

        // Create the MinutePicker view

        minutePicker.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(selectedMinute);
        minutePicker.setOnValueChangedListener((picker, oldVal, newVal) -> selectedMinute = newVal);
        layout.addView(minutePicker);
        // Create the SecondPicker view

        secondPicker.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);
        secondPicker.setValue(selectedSecond);
        secondPicker.setOnValueChangedListener((picker, oldVal, newVal) -> selectedSecond = newVal);
        layout.addView(secondPicker);

        builder.setTitle("pick time");

        builder.setCancelable(true);

        // Create the layout for the dialog

        // Create the OK button
        builder.setNeutralButton("OK", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
// User clicked OK button
                        dialog.dismiss();
                    }
                });

        // Show the dialog
        AlertDialog dialog = builder.create();
        // Set the OnClickListener to show the dialog with the pickers
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {//!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // Create the dialog with the pickers
                removeError();


                dialog.show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        updateTextView();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public int getSelectedMinute() {
        return selectedMinute;
    }

    public int getSelectedSecond() {
        return selectedSecond;
    }
    private void updateTextView(){
        String minutes;
        String seconds;
        if(selectedMinute<10)
            minutes = "0" + selectedMinute;
        else
            minutes = selectedMinute +"";
        if(selectedSecond<10)
            seconds = "0" + selectedSecond;
        else
            seconds = selectedSecond +"";

        this.setText(minutes+":"+seconds);

    }
    private int dpToPx(int dps)
    {
// Get the screen's density scale
        final float scale =

                getResources().getDisplayMetrics().density;
// Convert the dps to pixels, based on density scale
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }
    private void removeError(){
        this.setError(null);
    }
    public void setPicker(int totalTime){
        int minutes = totalTime/100;
        int seconds = totalTime%100;

        this.selectedMinute = minutes;
        this.selectedSecond = seconds;
        updateTextView();
        this.minutePicker.setValue(minutes);
        this.secondPicker.setValue(seconds);


    }
}