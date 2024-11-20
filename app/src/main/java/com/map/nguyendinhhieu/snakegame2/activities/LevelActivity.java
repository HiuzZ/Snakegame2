package com.map.nguyendinhhieu.snakegame2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.map.nguyendinhhieu.snakegame2.R;

public class LevelActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        Button jungleButton = findViewById(R.id.btn_jungle);
        Button desertButton = findViewById(R.id.btn_desert);
        Button arcticButton = findViewById(R.id.btn_arctic);

        jungleButton.setOnClickListener(v -> {
            Intent intent = new Intent(LevelActivity.this, StartActivity.class);
            intent.putExtra("MAP_TYPE", "Jungle");
            startActivity(intent);
            finish(); // Close LevelActivity
        });

        desertButton.setOnClickListener(v -> {
            Intent intent = new Intent(LevelActivity.this, StartActivity.class);
            intent.putExtra("MAP_TYPE", "Desert");
            startActivity(intent);
            finish(); // Close LevelActivity
        });

        arcticButton.setOnClickListener(v -> {
            Intent intent = new Intent(LevelActivity.this, StartActivity.class);
            intent.putExtra("MAP_TYPE", "Arctic");
            startActivity(intent);
            finish(); // Close LevelActivity
        });
    }
}
