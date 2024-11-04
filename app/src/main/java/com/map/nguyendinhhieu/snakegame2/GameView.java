package com.map.nguyendinhhieu.snakegame2;

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

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {
    private Bitmap bmGrass1, bmGrass2, bmSnake, bmApple;
    public static int sizeOfMap = 75 * Constants.SCREEN_WIDTH / 1080;
    private int h = 21, w = 12;
    private ArrayList<Grass> arrGrass = new ArrayList<>();
    private Snake snake;
    private boolean move = false;
    private float mx, my;
    private Handler handler;
    private Runnable r;
    private Apple apple;
    private int score = 0;
    private int bestScore = 0;
    private TextView txtScore;
    private TextView txtBestScore;
    private SharedPreferences sharedPreferences;
    private GameOverListener gameOverListener;

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

        // Set up the grass array to form the map background
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

        // Initialize snake and apple
        snake = new Snake(bmSnake, arrGrass.get(126).getX(), arrGrass.get(126).getY(), 4);
        apple = new Apple(bmApple, arrGrass.get(randomApple()[0]).getX(), arrGrass.get(randomApple()[1]).getY());

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
    // Custom init method to set TextViews after MainActivity sets them up
    public void init(TextView txtScore, TextView txtBestScore) {
        this.txtScore = txtScore;
        this.txtBestScore = txtBestScore;
        this.txtBestScore.setText(" x " + bestScore);  // Initialize best score display
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
        canvas.drawColor(0xFF1A6100);  // Draw background color

        // Draw grass tiles
        for (Grass grass : arrGrass) {
            canvas.drawBitmap(grass.getBm(), grass.getX(), grass.getY(), null);
        }

        // Update and draw snake and apple
        snake.update();
        snake.draw(canvas);
        apple.draw(canvas);

        // Check for snake eating the apple
        if (snake.getArrPartSnake().get(0).getrBody().intersect(apple.getR())) {
            score++;
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

            // Generate new apple position
            randomApple();
            apple.reset(arrGrass.get(randomApple()[0]).getX(), arrGrass.get(randomApple()[1]).getY());
            snake.addPart();
        }

        // Check for collision with self
        for (int i = 1; i < snake.getArrPartSnake().size(); i++) {
            if (snake.getArrPartSnake().get(0).getrBody().intersect(snake.getArrPartSnake().get(i).getrBody())) {
                snake.resetGame();
                score = 0;
                txtScore.setText(" x " + score);  // Reset score display

                if (gameOverListener != null) {
                    gameOverListener.onGameOver();  // Trigger game over
                }
                return;
            }
        }

        // Schedule next frame
        handler.postDelayed(r, 100);
    }

    public int[] randomApple() {
        int[] xy = new int[2];
        Random random = new Random();
        xy[0] = random.nextInt(arrGrass.size() - 1);
        xy[1] = random.nextInt(arrGrass.size() - 1);

        Rect rect = new Rect(arrGrass.get(xy[0]).getX(), arrGrass.get(xy[1]).getY(),
                arrGrass.get(xy[0]).getX() + sizeOfMap, arrGrass.get(xy[1]).getY() + sizeOfMap);

        // Ensure apple does not spawn on the snake
        boolean check = true;
        while (check) {
            check = false;
            for (PartSnake part : snake.getArrPartSnake()) {
                if (rect.intersect(part.getrBody())) {
                    check = true;
                    xy[0] = random.nextInt(arrGrass.size() - 1);
                    xy[1] = random.nextInt(arrGrass.size() - 1);
                    rect.set(arrGrass.get(xy[0]).getX(), arrGrass.get(xy[1]).getY(),
                            arrGrass.get(xy[0]).getX() + sizeOfMap, arrGrass.get(xy[1]).getY() + sizeOfMap);
                }
            }
        }
        return xy;
    }



    public interface GameOverListener {
        void onGameOver();
    }
}
