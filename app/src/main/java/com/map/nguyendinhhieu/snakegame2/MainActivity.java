package com.map.nguyendinhhieu.snakegame2;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.map.nguyendinhhieu.snakegame2.activities.StartActivity;
import com.map.nguyendinhhieu.snakegame2.contants.Constants;
import com.map.nguyendinhhieu.snakegame2.gameview.GameView;

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

        // Get the selected map type from the intent
        final String mapType = getIntent().getStringExtra("MAP_TYPE");
        String currentMapType = (mapType != null) ? mapType : "Jungle"; // Default to Jungle if null

        setContentView(R.layout.activity_main);

        txtScore = findViewById(R.id.txt_score);
        txtBestScore = findViewById(R.id.txt_best_score);
        gameView = findViewById(R.id.gv);
        gameView.init(txtScore, txtBestScore);

        // Set up the map type in GameView
        gameView.setMap(currentMapType);

        // Listen for game over event from GameView
        gameView.setGameOverListener(() -> {
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            // Include the current MAP_TYPE in the intent
            intent.putExtra("MAP_TYPE", currentMapType);
            intent.putExtra("GAME_OVER", true); // Indicate game-over state

            startActivity(intent);
            finish();
        });
    }
}
