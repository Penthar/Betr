package com.project.betr.ui;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.project.betr.DBHelper;
import com.project.betr.Exercise;
import com.project.betr.MainActivity;
import com.project.betr.R;
import com.project.betr.Tile;
import com.project.betr.User;
import com.project.betr.Workout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<Boolean> completedList;
    RelativeLayout background_layout;
    User currentuser;
    private DBHelper dbHelper;
    private FirebaseAuth fbAuth;
    TextView result;
    Double score;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    ValueAnimator backgroundAnimator;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fbAuth = FirebaseAuth.getInstance();
        dbHelper = new DBHelper();
        if(fbAuth.getCurrentUser()==null){
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            this.getActivity().finish();
        }
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ScrollView scrollView = (ScrollView) getView().findViewById(R.id.scrollbar);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
        background_layout = (RelativeLayout) getView().findViewById(R.id.main_layout);
        //ImageView tester = (ImageView) getView().findViewById(R.id.tester);
        //AnimationDrawable frameAnimation = spriteSheetToFrames(R.drawable.ken_idle, 10, 1);
        //tester.setBackground(frameAnimation);
        //frameAnimation.start();
        //
        /*Tile new_tile = new Tile(getContext(), false, R.drawable.goku_idle);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(100, 500, 0, 0);
        new_tile.setLayoutParams(params);
        background_layout.addView(new_tile);*/

    }

    private AnimationDrawable spriteSheetToFrames(int drawableId, int
            imagesInRow, int imagesInColumn, int duration) {
        Drawable spriteSheetDrawable =
                ResourcesCompat.getDrawable(getResources(), drawableId, null);
        Bitmap spriteSheetBitmap =
                ((BitmapDrawable) spriteSheetDrawable).getBitmap();
        AnimationDrawable frameAnimation = new AnimationDrawable();
        int width = spriteSheetBitmap.getWidth() / imagesInRow;
        int height = spriteSheetBitmap.getHeight() / imagesInColumn;
        for (int i = 0; i < imagesInColumn; i++) {
            for (int j = 0; j < imagesInRow; j++) {
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

    private int dpToPx(int dps) {
// Get the screen's density scale
        final float scale =

                getResources().getDisplayMetrics().density;
// Convert the dps to pixels, based on density scale
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    private void create_tiles() {
        RelativeLayout.LayoutParams params0 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params0.setMargins(dpToPx(50), dpToPx(930), 0, 0);
        Tile tile0 = new Tile(getContext(), completedList.get(0), R.drawable.goku_idle, 8, 1, 100);
        tile0.setLayoutParams(params0);
        tile0.setOnClickListener(this);
        tile0.setTag(0);
        if(completedList.get(0))
            tile0.setClickable(false);
        background_layout.addView(tile0);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.setMargins(dpToPx(280), dpToPx(750), 0, 0);
        Tile tile1 = new Tile(getContext(), completedList.get(1), R.drawable.ken_idle, 10, 1, 100);
        tile1.setLayoutParams(params1);
        tile1.setOnClickListener(this);
        tile1.setTag(1);
        if(completedList.get(1))
            tile1.setClickable(false);
        background_layout.addView(tile1);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.setMargins(dpToPx(75), dpToPx(620), 0, 0);
        Tile tile2 = new Tile(getContext(), completedList.get(2), R.drawable.ryu_idle, 4, 1, 200);
        tile2.setLayoutParams(params2);
        tile2.setOnClickListener(this);
        tile2.setTag(2);
        if(completedList.get(2))
            tile2.setClickable(false);
        background_layout.addView(tile2);

        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params3.setMargins(dpToPx(300), dpToPx(560), 0, 0);
        Tile tile3 = new Tile(getContext(), completedList.get(3), R.drawable.johnnycage_idle, 5, 1, 100);
        tile3.setLayoutParams(params3);
        tile3.setOnClickListener(this);
        tile3.setTag(3);
        if(completedList.get(3))
            tile3.setClickable(false);
        background_layout.addView(tile3);

        RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params4.setMargins(dpToPx(150), dpToPx(370), 0, 0);
        Tile tile4 = new Tile(getContext(), completedList.get(4), R.drawable.zang_idle, 4, 1, 150);
        tile4.setLayoutParams(params4);
        tile4.setOnClickListener(this);
        tile4.setTag(4);
        if(completedList.get(4))
            tile4.setClickable(false);
        background_layout.addView(tile4);

        RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params5.setMargins(dpToPx(75), dpToPx(195), 0, 0);
        Tile tile5 = new Tile(getContext(), completedList.get(5), R.drawable.liukang_idle, 6, 1, 100);
        tile5.setLayoutParams(params5);
        tile5.setOnClickListener(this);
        tile5.setTag(5);
        if(completedList.get(5))
            tile5.setClickable(false);
        background_layout.addView(tile5);

        RelativeLayout.LayoutParams params6 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params6.setMargins(dpToPx(250), 0, 0, 0);
        Tile tile6 = new Tile(getContext(), completedList.get(6), R.drawable.saiyan_idle, 3, 1, 150);
        tile6.setLayoutParams(params6);
        tile6.setOnClickListener(this);
        tile6.setTag(6);
        if(completedList.get(6))
            tile6.setClickable(false);
        background_layout.addView(tile6);

        }

    @Override
    public void onResume() {
        super.onResume();
        background_layout.removeAllViews();
        RelativeLayout container_layout = (RelativeLayout) getView().findViewById(R.id.container_layout);
        ProgressBar progressBar = new ProgressBar(getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        progressBar.setLayoutParams(layoutParams);
        container_layout.addView(progressBar);
        new DBHelper().getUserFromUID(fbAuth.getCurrentUser().getUid(), new DBHelper.DataStatus() {
            @Override
            public void DataIsLoaded(User user) {
                currentuser = new User(user);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                container_layout.removeView(progressBar);
                completedList = currentuser.getCompletion();
                create_tiles();
                score = user.getScore();
            }

            @Override
            public void DataIsInserted(boolean success) {

            }

            @Override
            public void getWorkout(Workout workout) {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getTag() instanceof Integer){
            if ((int)view.getTag() >-1 && (int)view.getTag()<7){
                battle((int)view.getTag());
            }
        }
    }
    public void battle(int enemyID){
        RelativeLayout battle_layout = new RelativeLayout(getContext());
        Log.d("mapviewscore", "battle: " + score);
        RelativeLayout.LayoutParams BattleParams = new RelativeLayout.LayoutParams(dpToPx(100), dpToPx(1000));
        battle_layout.setLayoutParams(BattleParams);
        ImageView playerView = new ImageView(getContext());
        playerView.setBackgroundResource(returnPlayerIcon(score));
        builder= new AlertDialog.Builder(getContext());
        builder.setView(battle_layout);
        //builder.setCancelable(false);
        builder.setView(battle_layout);
        ImageView enemyView = new ImageView(getContext());
        RelativeLayout.LayoutParams playerImgParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        playerView.setLayoutParams(playerImgParams);
        RelativeLayout.LayoutParams enemyimgParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        enemyView.setLayoutParams(enemyimgParams);
        battle_layout.addView(playerView);
        battle_layout.addView(enemyView);
         result = new TextView(getContext());
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(dpToPx(200), dpToPx(100));
        result.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        result.setLayoutParams(textParams);
        result.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        textParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        AnimationDrawable frameAnimation;
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(backgroundAnimator!=null)
                    backgroundAnimator.cancel();
                onResume();
            }
        });
        switch (enemyID){
            case 0:
                playerView.setScaleX(-1);
                enemyimgParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
                enemyimgParams.setMargins(0, dpToPx(10), 0 ,0);
                playerImgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

                frameAnimation = spriteSheetToFrames(R.drawable.goku_idle, 8, 1, 100);
                enemyView.setBackground(frameAnimation);
                frameAnimation.start();
                if(score<2.5){
                    beam_attack(true, battle_layout);
                    result.setText("DEFEAT");
                    result.setTextColor(Color.parseColor("#FF0000"));
                }
                else {
                    beam_attack(false, battle_layout);
                    result.setText("VICTORY");
                    result.setTextColor(Color.parseColor("#39FF14"));
                    completedList.set(0,true);
                    dbHelper.updateUserCompletionList(currentuser.getUserID(), completedList);
                }
                break;
            case 1:
                enemyimgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                enemyimgParams.setMargins(0, dpToPx(0), 0 ,0);
                playerImgParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
                enemyimgParams.height = dpToPx(100);
                frameAnimation = spriteSheetToFrames(R.drawable.ken_idle, 10, 1, 100);
                enemyView.setBackground(frameAnimation);
                frameAnimation.start();
                if(score<2.75){
                    beam_attack(false, battle_layout);
                    result.setText("DEFEAT");
                    result.setTextColor(Color.parseColor("#FF0000"));
                }
                else {
                    beam_attack(true, battle_layout);
                    result.setText("VICTORY");
                    result.setTextColor(Color.parseColor("#39FF14"));
                    completedList.set(1,true);
                    dbHelper.updateUserCompletionList(currentuser.getUserID(), completedList);
                }
                break;
            case 2:
                playerView.setScaleX(-1);
                enemyimgParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
                enemyimgParams.setMargins(0, dpToPx(10), 0 ,0);
                playerImgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                enemyimgParams.height = dpToPx(100);

                frameAnimation = spriteSheetToFrames(R.drawable.ryu_idle, 4, 1, 200);//
                enemyView.setBackground(frameAnimation);
                frameAnimation.start();
                if(score<3){//
                    beam_attack(true, battle_layout);
                    result.setText("DEFEAT");
                    result.setTextColor(Color.parseColor("#FF0000"));
                }
                else {
                    beam_attack(false, battle_layout);
                    result.setText("VICTORY");
                    result.setTextColor(Color.parseColor("#39FF14"));
                    completedList.set(2,true);//
                    dbHelper.updateUserCompletionList(currentuser.getUserID(), completedList);
                }
                break;
            case 3:
                enemyimgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                enemyimgParams.setMargins(0, dpToPx(-5), dpToPx(10) ,0);
                playerImgParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
                enemyimgParams.height = dpToPx(100);

                frameAnimation = spriteSheetToFrames(R.drawable.johnnycage_idle, 5, 1, 100);
                enemyView.setBackground(frameAnimation);
                frameAnimation.start();
                if(score<3.25){
                    beam_attack(false, battle_layout);
                    result.setText("DEFEAT");
                    result.setTextColor(Color.parseColor("#FF0000"));
                }
                else {
                    beam_attack(true, battle_layout);
                    result.setText("VICTORY");
                    result.setTextColor(Color.parseColor("#39FF14"));
                    completedList.set(3,true);
                    dbHelper.updateUserCompletionList(currentuser.getUserID(), completedList);
                }
                break;
            case 4:
                playerView.setScaleX(-1);
                enemyimgParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
                enemyimgParams.setMargins(0, dpToPx(10), 0 ,0);
                playerImgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                enemyimgParams.height = dpToPx(100);
                enemyimgParams.width = dpToPx(80);
                frameAnimation = spriteSheetToFrames(R.drawable.zang_idle, 4, 1, 150);//
                enemyView.setBackground(frameAnimation);
                frameAnimation.start();
                if(score<3.5){//
                    beam_attack(true, battle_layout);
                    result.setText("DEFEAT");
                    result.setTextColor(Color.parseColor("#FF0000"));
                }
                else {
                    beam_attack(false, battle_layout);
                    result.setText("VICTORY");
                    result.setTextColor(Color.parseColor("#39FF14"));
                    completedList.set(4,true);//
                    dbHelper.updateUserCompletionList(currentuser.getUserID(), completedList);
                }
                break;
            case 5:
                playerView.setScaleX(-1);
                enemyimgParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
                enemyimgParams.setMargins(0, dpToPx(10), 0 ,0);
                playerImgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                enemyimgParams.height = dpToPx(100);
                enemyimgParams.width = dpToPx(70);
                frameAnimation = spriteSheetToFrames(R.drawable.liukang_idle, 6, 1, 100);//
                enemyView.setBackground(frameAnimation);
                frameAnimation.start();
                if(score<3.75){//
                    beam_attack(true, battle_layout);
                    result.setText("DEFEAT");
                    result.setTextColor(Color.parseColor("#FF0000"));
                }
                else {
                    beam_attack(false, battle_layout);
                    result.setText("VICTORY");
                    result.setTextColor(Color.parseColor("#39FF14"));
                    completedList.set(5,true);//
                    dbHelper.updateUserCompletionList(currentuser.getUserID(), completedList);
                }
                break;
            case 6:
                enemyimgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                enemyimgParams.setMargins(0, dpToPx(-5), dpToPx(10) ,0);
                playerImgParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
                enemyimgParams.height = dpToPx(100);

                frameAnimation = spriteSheetToFrames(R.drawable.saiyan_idle, 3, 1, 150);
                enemyView.setBackground(frameAnimation);
                frameAnimation.start();
                if(score<4.1){
                    beam_attack(false, battle_layout);
                    result.setText("DEFEAT");
                    result.setTextColor(Color.parseColor("#FF0000"));
                }
                else {
                    beam_attack(true, battle_layout);
                    result.setText("YOU DID IT!!");
                    result.setTextColor(Color.parseColor("#39FF14"));
                    completedList.set(6,true);
                    dbHelper.updateUserCompletionList(currentuser.getUserID(), completedList);
                }
                break;

        }
         dialog= builder.create();
        dialog.show();
    }

    public int returnPlayerIcon(double playerscore){
        if(playerscore<=2.75)
            return R.drawable.broly1;
        else if (playerscore>2.75 && playerscore<=3.5)
            return R.drawable.broly2;
        else if (playerscore>3.5 && playerscore<=4)
            return R.drawable.broly3;
        else
            return R.drawable.broly4;

    }
    public void beam_attack(boolean fromRight, RelativeLayout battleField){
        ImageView beamImage = new ImageView(getContext());
        battleField.addView(beamImage);
        AnimationDrawable frameAnimation = spriteSheetToFrames(R.drawable.beam2, 2, 3, 50);
        beamImage.setBackground(frameAnimation);
        frameAnimation.start();
        RelativeLayout.LayoutParams beamParams = new RelativeLayout.LayoutParams(dpToPx(180), dpToPx(50));
        beamImage.setLayoutParams(beamParams);
        if(fromRight){
            beamParams.setMarginStart(dpToPx(0));
            ValueAnimator animator = ValueAnimator.ofInt(dpToPx(beamParams.getMarginStart()), dpToPx(beamParams.getMarginStart() + 300));
            animator.setDuration(1000); // Set the duration of the animation (in milliseconds)
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int animatedValue = (int) valueAnimator.getAnimatedValue();
                    beamParams.setMarginStart(animatedValue);
                    beamImage.setLayoutParams(beamParams);
                }
            });
            animator.start();
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    int startColor = Color.WHITE; // Initial color (white)
                    int endColor = Color.BLACK; // Final color (black)

                    backgroundAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
                    backgroundAnimator.setDuration(1000); // Set the duration of the animation (in milliseconds)

                    backgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int animatedValue = (int) valueAnimator.getAnimatedValue();

                            // Set the background color of the view
                            battleField.setBackgroundColor(animatedValue);
                        }
                    });

                    backgroundAnimator.start();
                    backgroundAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(@NonNull Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(@NonNull Animator animator) {
                            battleField.removeAllViews();
                            if(result.getParent()==null)
                                battleField.addView(result);
                            else{
                                ((ViewGroup)result.getParent()).removeView(result);
                                battleField.addView(result);
                            }
                        }

                        @Override
                        public void onAnimationCancel(@NonNull Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(@NonNull Animator animator) {

                        }
                    });
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {

                }
            });
        }
        else{
            beamParams.setMarginStart(dpToPx(-50));
            ValueAnimator animator = ValueAnimator.ofInt(dpToPx(beamParams.getMarginStart() + 300), dpToPx(beamParams.getMarginStart()) + 100);
            animator.setDuration(1000); // Set the duration of the animation (in milliseconds)
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int animatedValue = (int) valueAnimator.getAnimatedValue();
                    beamParams.setMarginStart(animatedValue);
                    beamImage.setLayoutParams(beamParams);
                }
            });
            animator.start();
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    int startColor = Color.WHITE; // Initial color (white)
                    int endColor = Color.BLACK; // Final color (black)

                    backgroundAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
                    backgroundAnimator.setDuration(1000); // Set the duration of the animation (in milliseconds)

                    backgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int animatedValue = (int) valueAnimator.getAnimatedValue();

                            // Set the background color of the view
                            battleField.setBackgroundColor(animatedValue);
                        }
                    });

                    backgroundAnimator.start();
                    backgroundAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(@NonNull Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(@NonNull Animator animator) {
                            battleField.removeAllViews();
                            if(result.getParent()==null)
                            battleField.addView(result);
                            else{
                                ((ViewGroup)result.getParent()).removeView(result);
                                battleField.addView(result);
                            }

                        }

                        @Override
                        public void onAnimationCancel(@NonNull Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(@NonNull Animator animator) {

                        }
                    });

                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {

                }
            });
        }
    }

}

