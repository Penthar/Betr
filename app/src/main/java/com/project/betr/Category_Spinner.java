package com.project.betr;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Category_Spinner extends androidx.appcompat.widget.AppCompatSpinner {



    public Category_Spinner(Context context) {
        super(context);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.exercise_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.setAdapter(adapter);
        this.setPadding(dpToPx(10), 0, dpToPx(10), 0);
        this.setBackground(context.getDrawable(R.drawable.table_block_border));
    }

    private int dpToPx(int dps) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    public String getCategory() {
        return this.getSelectedItem().toString();
    }

    public void setCategory(String category) {
        int index = ((ArrayAdapter<String>) this.getAdapter()).getPosition(category);
        if (index >= 0) {
            this.setSelection(index);
        }
    }


}
