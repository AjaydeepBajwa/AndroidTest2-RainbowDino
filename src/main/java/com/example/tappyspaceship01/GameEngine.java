package com.example.tappyspaceship01;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {

    // Android debug variables
    final static String TAG="DINO-RAINBOWS";

    // screen size
    int screenHeight;
    int screenWidth;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;



    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;
    Player player;
    boolean playerMoveUp;



    // -----------------------------------
    // GAME SPECIFIC VARIABLES
    // -----------------------------------

    // ----------------------------
    // ## SPRITES
    // ----------------------------

    // represent the TOP LEFT CORNER OF THE GRAPHIC

    // ----------------------------
    // ## GAME STATS
    // ----------------------------
    int score = 0;
    int lives = 3;
    int laneRightX;
    int lane1Y;
    int lane2Y;
    int lane3Y;
    int lane4Y;
    int laneDistanceDifference = 200;


    public GameEngine(Context context, int w, int h) {
        super(context);

        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;
        //this.laneRightX = this.screenWidth -200;
//        this.lane1Y = this.screenHeight/6;
//        this.lane2Y = this.screenHeight/6+200;
//        this.lane3Y = this.screenHeight/6+400;
//        this.lane4Y = this.screenHeight/6+500;

        player =  new Player(context,this.screenWidth - 200,(this.screenHeight/6+200) - 150);

        this.printScreenInfo();
    }



    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }

    private void spawnPlayer() {
        //@TODO: Start the player at the left side of screen
    }
    private void spawnEnemyShips() {
        Random random = new Random();

        //@TODO: Place the enemies in a random location

    }

    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------
    @Override
    public void run() {
        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------

    public void updatePositions() {


    }
    public void playerMovement(){
        if ((this.playerMoveUp == true)&&(this.player.getyPosition()> 0)){
            this.player.setyPosition(player.getyPosition() - 200);
        }
        else if ((this.playerMoveUp == false)&&(this.player.getyPosition()< this.screenHeight - 150)){
            this.player.setyPosition(player.getyPosition() + 200);
        }
    }

    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,255,255,255));
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setColor(Color.BLACK);
            paintbrush.setTextSize(60);
            this.canvas.drawRect(20,this.screenHeight/6,this.screenWidth-300,this.screenHeight/6 + 20,paintbrush);
            this.canvas.drawRect(20,this.screenHeight/6+200,this.screenWidth-300,this.screenHeight/6+200 + 20,paintbrush);
            this.canvas.drawRect(20,this.screenHeight/6+400,this.screenWidth-300,this.screenHeight/6+400 + 20,paintbrush);
            this.canvas.drawRect(20,this.screenHeight/6+600,this.screenWidth-300,this.screenHeight/6+600 + 20,paintbrush);
            this.canvas.drawText("SCORE = " +this.score+"",this.screenWidth-300,50,paintbrush);
            this.canvas.drawText("LIVES = " +this.lives+"",this.screenWidth-700,50,paintbrush);
            this.canvas.drawBitmap(this.player.getImage(),this.player.getxPosition(),this.player.getyPosition(),paintbrush);


            // DRAW THE PLAYER HITBOX
            // ------------------------
            // 1. change the paintbrush settings so we can see the hitbox
            paintbrush.setColor(Color.BLUE);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);

            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    public void setFPS() {
        try {
            gameThread.sleep(120);
        }
        catch (Exception e) {

        }
    }

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------


    String fingerAction = "";

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        float fingerYPosition = event.getY();
        //float fingerYPosition = event.getY();
        //@TODO: What should happen when person touches the screen?
        if (userAction == MotionEvent.ACTION_DOWN) {
            if (fingerYPosition < this.screenHeight/2){
                this.playerMoveUp = true;
                this.playerMovement();
            }
            else if(fingerYPosition > this.screenHeight /2){
                this.playerMoveUp = false;
                this.playerMovement();
            }

        }
        else if (userAction == MotionEvent.ACTION_UP) {

        }

        return true;
    }
}
