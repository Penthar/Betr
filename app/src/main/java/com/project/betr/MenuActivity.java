package com.project.betr;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.project.betr.databinding.ActivityMenuBinding;

import org.w3c.dom.Text;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMenuBinding binding;

    Toolbar toolbar;
    ImageView player_icon_image;
    int imageid;
    double userScore;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_workout, R.id.navigation_Edit, R.id.navigation_import, R.id.navigation_map)
                .build();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_menu);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuth fbAuth = FirebaseAuth.getInstance();

        ImageView player_icon_image = toolbar.findViewById(R.id.player_icon);
        player_icon_image.setOnClickListener(this);
        player_icon_image.setClickable(false);
        Log.d("score", "onResume: " + (fbAuth.getCurrentUser()!=null));
        if(fbAuth.getCurrentUser()!=null){
            new DBHelper().getUserFromUID(fbAuth.getCurrentUser().getUid(), new DBHelper.DataStatus() {
                @Override
                public void DataIsLoaded(User user) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    user.calculateTotalUserScore();
                    imageid = returnPlayerIcon(user.getScore());
                    player_icon_image.setBackgroundResource(imageid);
                    player_icon_image.setClickable(true);
                    userScore = user.getScore();
                    username = user.getName();
                    Log.d("score", "DataIsLoaded: " + userScore);
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

    @Override
    public void onClick(View view) {
            LinearLayout dialog_layout = new LinearLayout(this);
            dialog_layout.setOrientation(LinearLayout.VERTICAL);
            ImageView iconImage = new ImageView(this);
            iconImage.setBackgroundResource(imageid);
            TextView nameText = new TextView(this);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textParams.gravity = Gravity.CENTER;
            textParams.setMargins(0,dpToPx(10),0,dpToPx(20));
            nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            nameText.setText(username);
            dialog_layout.addView(nameText);
            nameText.setLayoutParams(textParams);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imgParams.gravity = Gravity.CENTER;
            imgParams.setMargins(0,0,0,dpToPx(10));
            iconImage.setLayoutParams(imgParams);
            dialog_layout.addView(iconImage);
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout prog_bar_layout = (LinearLayout) inflater.inflate(R.layout.progress_dialog_bar, dialog_layout);
            TextView rightText = prog_bar_layout.findViewById(R.id.rightText);
            ProgressBar progressBar = prog_bar_layout.findViewById(R.id.progBar);
            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            barParams.gravity = Gravity.CENTER_HORIZONTAL;
            barParams.setMargins(0, dpToPx(20), 0, dpToPx(20));
            prog_bar_layout.setLayoutParams(barParams);
            TextView leftText = prog_bar_layout.findViewById(R.id.leftText);
            if(userScore<=2.75) {
                rightText.setText("0");
                leftText.setText("2.75");
                progressBar.setMax(275);
                progressBar.setProgress((int)(userScore*100));
            }
            else if (userScore>2.75 && userScore<=3.5){
                rightText.setText("2.75");
                leftText.setText("3.5");
                progressBar.setMax(75);
                progressBar.setProgress((int)(userScore*100-275));
            }
            else if (userScore>3.5 && userScore<=4){
                rightText.setText("3.5");
                leftText.setText("4");
                progressBar.setMax(50);
                progressBar.setProgress((int)(userScore*100-350));
            }

            else if (userScore < 4.25) {
                rightText.setText("4");
                leftText.setText("4.25");
                progressBar.setMax(25);
                progressBar.setProgress((int) (userScore * 100-425));
            }
            else{
                rightText.setText("MAX");
                leftText.setText("MAX");
                progressBar.setMax(1);
                progressBar.setProgress(1);
            }
            TextView currScore = new TextView(this);
            currScore.setLayoutParams(textParams);
            currScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

// Set the rounding mode if needed (optional)
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);

// Format the double value to a string
        String roundedValue = decimalFormat.format(userScore);
            currScore.setText(roundedValue);
            dialog_layout.addView(currScore);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialog_layout);
            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();


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
}