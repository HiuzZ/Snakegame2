package com.map.nguyendinhhieu.snakegame2;

import android.content.Intent;
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

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        setContentView(R.layout.activity_main);

        txtScore = findViewById(R.id.txt_score);
        txtBestScore = findViewById(R.id.txt_best_score);
        gameView = findViewById(R.id.gv);
        gameView.init(txtScore, txtBestScore);

        // Listen for game over event from GameView
        gameView.setGameOverListener(() -> {
            // On game over, set an intent flag to switch the play button to replay mode
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("GAME_OVER", true);  // Pass the game-over flag
            startActivity(intent);
            finish();
        });
        // Retrieve TextView and GameView instances after setting content view
        txtScore = findViewById(R.id.txt_score);
        txtBestScore = findViewById(R.id.txt_best_score);
        gameView = findViewById(R.id.gv);

        // Initialize GameView with TextViews
        gameView.init(txtScore, txtBestScore);
    }
}

