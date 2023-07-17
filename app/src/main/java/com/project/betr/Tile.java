package com.project.betr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.core.content.res.ResourcesCompat;

public class Tile extends RelativeLayout{
    boolean completed;
    int icon_code;
    int imageInRow;
    int imageInCol;
    int animDuration;
    public Tile(Context context, boolean is_completed, int icon_code, int imageInRow, int imageInCol, int animDuration) {
        super(context);
        this.completed = is_completed;
        this.icon_code = icon_code;
        this.imageInCol = imageInCol;
        this.imageInRow = imageInRow;
        this.animDuration = animDuration;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tile_layout, this);
        LinearLayout layout = view.findViewById(R.id.tile_circle);
        ImageView image = view.findViewById(R.id.image_holder);
        if(is_completed){
            this.setClickable(false);
            layout.setBackgroundResource(R.drawable.tile_background_completed);
        }
        else{
            layout.setBackgroundResource(R.drawable.tile_background);
        }
        AnimationDrawable frameAnimation = spriteSheetToFrames(icon_code, imageInRow, imageInCol, animDuration);
        image.setBackground(frameAnimation);
        frameAnimation.start();



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
    private AnimationDrawable spriteSheetToFrames(int drawableId, int
            imagesInRow, int imagesInColumn, int duration)
    {
        Drawable spriteSheetDrawable =
                ResourcesCompat.getDrawable(getResources(), drawableId, null);
        Bitmap spriteSheetBitmap =
                ((BitmapDrawable)spriteSheetDrawable).getBitmap();
        AnimationDrawable frameAnimation = new AnimationDrawable();
        int width = spriteSheetBitmap.getWidth() / imagesInRow;
        int height = spriteSheetBitmap.getHeight() / imagesInColumn;
        for (int i = 0; i < imagesInColumn; i++)
        {
            for (int j = 0; j < imagesInRow; j++)
            {
                int fromX = j * width;
                int fromY = i * height;
                Log.d("DrawableAnimation", fromX + " " + fromY);
                Bitmap currentFrame = Bitmap.createBitmap(spriteSheetBitmap,
                        fromX, fromY, width, height);
                Drawable drawable = new BitmapDrawable(getResources(),
                        currentFrame);
                frameAnimation.addFrame(drawable, duration);
            }
        }
        return frameAnimation;
    }
}
