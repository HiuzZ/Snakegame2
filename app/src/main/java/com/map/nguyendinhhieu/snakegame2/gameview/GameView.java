package com.map.nguyendinhhieu.snakegame2.gameview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.map.nguyendinhhieu.snakegame2.map.Grass;
import com.map.nguyendinhhieu.snakegame2.R;
import com.map.nguyendinhhieu.snakegame2.contants.Constants;
import com.map.nguyendinhhieu.snakegame2.foods.Apple;
import com.map.nguyendinhhieu.snakegame2.foods.Pineapple;
import com.map.nguyendinhhieu.snakegame2.snake.PartSnake;
import com.map.nguyendinhhieu.snakegame2.snake.Snake;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {
    private Bitmap bmGrass1, bmGrass2, bmSnake, bmApple, bmPineapple, bmCactus1x1, bmCactus1x2;
    public static int sizeOfMap = 75 * Constants.SCREEN_WIDTH / 1080;
    private int h = 21, w = 12;
    private ArrayList<Grass> arrGrass = new ArrayList<>();
    private ArrayList<Rect> cacti = new ArrayList<>();  // List of cacti positions
    private Snake snake;
    private boolean move = false;
    private float mx, my;
    private Handler handler;
    private Runnable r;
    private Apple apple;
    private Pineapple pineapple;
    private int score = 0;
    private int bestScore = 0;
    private TextView txtScore;
    private TextView txtBestScore;
    private SharedPreferences sharedPreferences;
    private GameOverListener gameOverListener;
    private int appleEaten;
    private String currentMap = "Jungle";

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // Load shared preferences for the best score
        sharedPreferences = context.getSharedPreferences("snake_game_prefs", Context.MODE_PRIVATE);
        bestScore = sharedPreferences.getInt("best_score", 0);

        // Load bitmaps and scale them to fit the map
        bmGrass1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.grass);
        bmGrass1 = Bitmap.createScaledBitmap(bmGrass1, sizeOfMap, sizeOfMap, true);
        bmGrass2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.grass03);
        bmGrass2 = Bitmap.createScaledBitmap(bmGrass2, sizeOfMap, sizeOfMap, true);
        bmSnake = BitmapFactory.decodeResource(this.getResources(), R.drawable.snake1);
        bmSnake = Bitmap.createScaledBitmap(bmSnake, 14 * sizeOfMap, sizeOfMap, true);
        bmApple = BitmapFactory.decodeResource(this.getResources(), R.drawable.apple);
        bmApple = Bitmap.createScaledBitmap(bmApple, sizeOfMap, sizeOfMap, true);
        bmPineapple = BitmapFactory.decodeResource(this.getResources(), R.drawable.pineapple);
        bmPineapple = Bitmap.createScaledBitmap(bmPineapple, sizeOfMap, sizeOfMap, true);

        // Set up handler for drawing updates
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
    }


    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    // Set the current map and load corresponding resources
    public void setMap(String mapType) {
        this.currentMap = mapType;

        if (mapType.equals("Jungle")) {
            bmGrass1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.grass);
            bmGrass2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.grass03);
        } else if (mapType.equals("Desert")) {
            bmGrass1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.sand);
            bmGrass2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.sand);

            // Load cactus bitmaps for Desert
            bmCactus1x1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.cactus1x1);
            bmCactus1x1 = Bitmap.createScaledBitmap(bmCactus1x1, sizeOfMap, sizeOfMap, true);

            bmCactus1x2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.cactus1x2);
            bmCactus1x2 = Bitmap.createScaledBitmap(bmCactus1x2, sizeOfMap, 2 * sizeOfMap, true);
        } else if (mapType.equals("Arctic")) {
            bmGrass1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.ice);
            bmGrass2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.ice);
        }

        bmGrass1 = Bitmap.createScaledBitmap(bmGrass1, sizeOfMap, sizeOfMap, true);
        bmGrass2 = Bitmap.createScaledBitmap(bmGrass2, sizeOfMap, sizeOfMap, true);

        bmSnake = BitmapFactory.decodeResource(this.getResources(), R.drawable.snake1);
        bmSnake = Bitmap.createScaledBitmap(bmSnake, 14 * sizeOfMap, sizeOfMap, true);

        bmApple = BitmapFactory.decodeResource(this.getResources(), R.drawable.apple);
        bmApple = Bitmap.createScaledBitmap(bmApple, sizeOfMap, sizeOfMap, true);

        bmPineapple = BitmapFactory.decodeResource(this.getResources(), R.drawable.pineapple);
        bmPineapple = Bitmap.createScaledBitmap(bmPineapple, sizeOfMap, sizeOfMap, true);

        // Set up the grass array for the selected map
        arrGrass.clear();
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if ((i + j) % 2 == 0) {
                    arrGrass.add(new Grass(bmGrass1, j * sizeOfMap + Constants.SCREEN_WIDTH / 2 - (w / 2) * sizeOfMap,
                            i * sizeOfMap + 100 * Constants.SCREEN_HEIGHT / 1920, sizeOfMap, sizeOfMap));
                } else {
                    arrGrass.add(new Grass(bmGrass2, j * sizeOfMap + Constants.SCREEN_WIDTH / 2 - (w / 2) * sizeOfMap,
                            i * sizeOfMap + 100 * Constants.SCREEN_HEIGHT / 1920, sizeOfMap, sizeOfMap));
                }
            }
        }

        // Initialize snake and apple for the new map
        snake = new Snake(bmSnake, arrGrass.get(126).getX(), arrGrass.get(126).getY(), 4);
        apple = new Apple(bmApple, arrGrass.get(randomApple()[0]).getX(), arrGrass.get(randomApple()[1]).getY());

        // Initialize cacti for Desert map
        if (mapType.equals("Desert")) {
            generateCacti();
        } else {
            cacti.clear();  // Clear cacti for non-Desert maps
        }
    }

    // Custom init method to set TextViews after MainActivity sets them up
    public void init(TextView txtScore, TextView txtBestScore) {
        this.txtScore = txtScore;
        this.txtBestScore = txtBestScore;
        this.txtBestScore.setText(" x " + bestScore);  // Initialize best score display
    }

    // generate cacti
    private void generateCacti() {
        cacti.clear();  // Clear existing cacti
        Random random = new Random();
        int snakeStartX = snake.getArrPartSnake().get(0).getX();
        int snakeStartY = snake.getArrPartSnake().get(0).getY();

        for (int i = 0; i < 7; i++) {  // Add 7 random cacti
            boolean isValid = false;
            Rect cactusRect = null;

            while (!isValid) {
                int index = random.nextInt(arrGrass.size() - 1);

                if (random.nextBoolean()) {
                    // Cactus 1x1
                    cactusRect = new Rect(arrGrass.get(index).getX(), arrGrass.get(index).getY(),
                            arrGrass.get(index).getX() + sizeOfMap, arrGrass.get(index).getY() + sizeOfMap);
                } else {
                    // Cactus 1x2
                    cactusRect = new Rect(arrGrass.get(index).getX(), arrGrass.get(index).getY(),
                            arrGrass.get(index).getX() + sizeOfMap, arrGrass.get(index).getY() + 2 * sizeOfMap);
                }

                // Ensure no cactus at snake's starting position
                if (!cactusRect.contains(snakeStartX, snakeStartY)) {
                    isValid = true;
                }
            }
            cacti.add(cactusRect);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (!move) {
                    mx = event.getX();
                    my = event.getY();
                    move = true;
                } else {
                    // Check for movement direction
                    if (mx - event.getX() > 100 * Constants.SCREEN_WIDTH / 1080 && !snake.isMove_right()) {
                        mx = event.getX();
                        my = event.getY();
                        snake.setMove_left(true);
                    } else if (event.getX() - mx > 100 * Constants.SCREEN_WIDTH / 1080 && !snake.isMove_left()) {
                        mx = event.getX();
                        my = event.getY();
                        snake.setMove_right(true);
                    } else if (my - event.getY() > 100 * Constants.SCREEN_WIDTH / 1080 && !snake.isMove_bottom()) {
                        mx = event.getX();
                        my = event.getY();
                        snake.setMove_top(true);
                    } else if (event.getY() - my > 100 * Constants.SCREEN_WIDTH / 1080 && !snake.isMove_top()) {
                        mx = event.getX();
                        my = event.getY();
                        snake.setMove_bottom(true);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                mx = 0;
                my = 0;
                move = false;
                break;
        }
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (currentMap.equals("Jungle")) {
            canvas.drawColor(0xFF1A6100);  // Jungle background
        } else if (currentMap.equals("Desert")) {
            canvas.drawColor(0xFFFFD27F);  // Desert background
        } else if (currentMap.equals("Arctic")) {
            canvas.drawColor(0xFFB3E5FC);  // Arctic background
        }

        // Draw grass tiles
        for (Grass grass : arrGrass) {
            canvas.drawBitmap(grass.getBm(), grass.getX(), grass.getY(), null);
        }

        // Draw cacti for Desert map
        if (currentMap.equals("Desert")) {
            for (Rect cactus : cacti) {
                if (cactus.height() == sizeOfMap) {
                    canvas.drawBitmap(bmCactus1x1, cactus.left, cactus.top, null);
                } else {
                    canvas.drawBitmap(bmCactus1x2, cactus.left, cactus.top, null);
                }
            }
        }

        // Update and draw snake
        snake.update();
        snake.draw(canvas);

        // Check for collision with cacti
        for (Rect cactus : cacti) {
            if (snake.getArrPartSnake().get(0).getrBody().intersect(cactus)) {
                // Trigger game over
                snake.resetGame();
                if (gameOverListener != null) {
                    gameOverListener.onGameOver();
                }
                return;
            }
        }

        // Handle pineapple spawning logic
        if (appleEaten >= 5) {
            if (pineapple == null) {  // Spawn pineapple if it hasn't appeared yet
                int[] pineapplePosition = randomPineapple();
                pineapple = new Pineapple(bmPineapple, arrGrass.get(pineapplePosition[0]).getX(),
                        arrGrass.get(pineapplePosition[1]).getY());
            }
        }

        // Draw pineapple if it exists
        if (pineapple != null) {
            pineapple.draw(canvas);

            // Check if snake eats the pineapple
            if (snake.getArrPartSnake().get(0).getrBody().intersect(pineapple.getR())) {
                score += 2;  // Gain extra points for eating the pineapple
                txtScore.setText(" x " + score);
                pineapple = null;  // Remove pineapple after it's eaten
                appleEaten = 0;  // Reset appleEaten counter
            }
        } else {  // Only draw and handle apple if no pineapple exists
            apple.draw(canvas);

            // Check for snake eating the apple
            if (snake.getArrPartSnake().get(0).getrBody().intersect(apple.getR())) {
                score++;
                appleEaten++;  // Increment apple counter
                txtScore.setText(" x " + score);  // Update score display

                // Update best score if necessary
                if (score > bestScore) {
                    bestScore = score;
                    txtBestScore.setText(" x " + bestScore);

                    // Save new best score to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("best_score", bestScore);
                    editor.apply();
                }

                // Only reset apple position if fewer than 5 apples have been eaten
                if (appleEaten < 5) {
                    randomApple();
                    apple.reset(arrGrass.get(randomApple()[0]).getX(), arrGrass.get(randomApple()[1]).getY());
                }
                snake.addPart();
            }
        }

        // Check for collision with self
        for (int i = 1; i < snake.getArrPartSnake().size(); i++) {
            if (snake.getArrPartSnake().get(0).getrBody().intersect(snake.getArrPartSnake().get(i).getrBody())) {
                snake.resetGame();
                score = 0;
                txtScore.setText(" x " + score);  // Reset score display
                appleEaten = 0;  // Reset appleEaten counter
                pineapple = null;  // Remove pineapple

                if (gameOverListener != null) {
                    gameOverListener.onGameOver();  // Trigger game over
                }
                return;
            }
        }

        // Check for snake going out of bounds
        if ((snake.getArrPartSnake().get(0).getX() - Constants.SCREEN_WIDTH / 2 + (w / 2) * sizeOfMap) / sizeOfMap < 0
                || (snake.getArrPartSnake().get(0).getX() - Constants.SCREEN_WIDTH / 2 + (w / 2) * sizeOfMap) / sizeOfMap >= w
                || (snake.getArrPartSnake().get(0).getY() - 100 * Constants.SCREEN_HEIGHT / 1920) / sizeOfMap < 0
                || (snake.getArrPartSnake().get(0).getY() - 100 * Constants.SCREEN_HEIGHT / 1920) / sizeOfMap >= h) {
            snake.resetGame();
            score = 0;
            txtScore.setText(" x " + score);
            appleEaten = 0;  // Reset appleEaten counter
            pineapple = null;  // Remove pineapple

            if (gameOverListener != null) {
                gameOverListener.onGameOver();
            }
            return;
        }

        // Schedule next frame
        if(currentMap.equals("Arctic")) {
            int delay = Math.max(50, 100 - score * 5);
            handler.postDelayed(r, delay);
        } else {
            int delay = Math.max(50, 200 - score * 5);
            handler.postDelayed(r, delay);
        }
    }

    public int[] randomApple() {
        int[] xy = new int[2];
        Random random = new Random();
        boolean isValid = false;

        while (!isValid) {
            xy[0] = random.nextInt(arrGrass.size() - 1);
            xy[1] = random.nextInt(arrGrass.size() - 1);

            Rect rect = new Rect(arrGrass.get(xy[0]).getX(), arrGrass.get(xy[1]).getY(),
                    arrGrass.get(xy[0]).getX() + sizeOfMap, arrGrass.get(xy[1]).getY() + sizeOfMap);

            // Check for collisions with snake
            isValid = true;
            for (PartSnake part : snake.getArrPartSnake()) {
                if (rect.intersect(part.getrBody())) {
                    isValid = false;
                    break;
                }
            }

            // Additional check for collisions with cacti on Desert map
            if (currentMap.equals("Desert")) {
                for (Rect cactus : cacti) {
                    if (rect.intersect(cactus)) {
                        isValid = false;
                        break;
                    }
                }
            }
        }

        return xy;
    }


    public int[] randomPineapple() {
        int[] xy = new int[2];
        Random random = new Random();
        boolean isValid = false;

        while (!isValid) {
            xy[0] = random.nextInt(arrGrass.size() - 1);
            xy[1] = random.nextInt(arrGrass.size() - 1);

            Rect rect = new Rect(arrGrass.get(xy[0]).getX(), arrGrass.get(xy[1]).getY(),
                    arrGrass.get(xy[0]).getX() + sizeOfMap, arrGrass.get(xy[1]).getY() + sizeOfMap);

            // Check for collisions with snake and cacti
            isValid = true;
            for (PartSnake part : snake.getArrPartSnake()) {
                if (rect.intersect(part.getrBody())) {
                    isValid = false;
                    break;
                }
            }

            if (currentMap.equals("Desert")) {
                for (Rect cactus : cacti) {
                    if (rect.intersect(cactus)) {
                        isValid = false;
                        break;
                    }
                }
            }
        }

        return xy;
    }

    public interface GameOverListener {
        void onGameOver();
    }
}
