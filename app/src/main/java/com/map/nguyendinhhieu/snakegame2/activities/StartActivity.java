package com.map.nguyendinhhieu.snakegame2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.map.nguyendinhhieu.snakegame2.MainActivity;
import com.map.nguyendinhhieu.snakegame2.R;

public class StartActivity extends AppCompatActivity {
    private ImageButton btnPlay;
    private ImageButton btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnPlay = findViewById(R.id.btn_play);
        btnHome = findViewById(R.id.btn_home);

        // Get the map type from the intent
        final String mapType = getIntent().getStringExtra("MAP_TYPE");
        String currentMapType = (mapType != null) ? mapType : "Jungle"; // Default to Jungle if null

        // Check if the activity is opened after a game over
        if (getIntent().getBooleanExtra("GAME_OVER", false)) {
            setReplayMode(); // Change play button to replay mode
        }

        // Play button click listener
        btnPlay.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.putExtra("MAP_TYPE", currentMapType); // Pass the current map type
            startActivity(intent);
        });

        // Home button click listener
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, LevelActivity.class);
            startActivity(intent);
            finish(); // Close this activity
        });
    }

    // Change play button to replay mode (e.g., after game over)
    private void setReplayMode() {
        btnPlay.setImageResource(R.drawable.ic_replay); // Set replay icon
    }
}
