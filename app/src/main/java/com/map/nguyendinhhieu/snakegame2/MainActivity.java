package com.map.nguyendinhhieu.snakegame2;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView txtScore;
    private TextView txtBestScore;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set screen width and height
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        setContentView(R.layout.activity_main);

        // Retrieve TextView and GameView instances after setting content view
        txtScore = findViewById(R.id.txt_score);
        txtBestScore = findViewById(R.id.txt_best_score);
        gameView = findViewById(R.id.gv);

        // Initialize GameView with TextViews
        gameView.init(txtScore, txtBestScore);
    }
}

