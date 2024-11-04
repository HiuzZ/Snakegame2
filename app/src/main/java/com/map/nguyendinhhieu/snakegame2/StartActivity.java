package com.map.nguyendinhhieu.snakegame2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    private ImageButton btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnPlay = findViewById(R.id.btn_play);

        // Check if GAME_OVER flag is set and update the button icon
        if (getIntent().getBooleanExtra("GAME_OVER", false)) {
            setReplayMode();  // Change icon to replay
        }

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start game activity
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method to switch button to replay mode when game ends
    private void setReplayMode() {
        btnPlay.setImageResource(R.drawable.ic_replay);  // Set to replay icon
    }
}
