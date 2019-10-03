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
    Item rainbow;
    Item candy;
    Item garbage;
    int ObjectY = 100;
    boolean playerMoveUp;
    Item object;
    public ArrayList<Item> objects = new ArrayList<Item>();

    Random random;
    Item objectToShow;




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
        //this.ObjectY = (this.screenHeight/6)-150;
        this.spawnObjects();



        player =  new Player(context,this.screenWidth - 200,(this.screenHeight/6+200) - 150);

        this.spawnObjects();
        rainbow = new Item(context,20, this.ObjectY,R.drawable.rainbow64);
        candy = new Item(context,20, this.ObjectY,R.drawable.candy64);
        garbage = new Item(context,20, this.ObjectY,R.drawable.poop64);

        Item[] objectArray = {rainbow, candy, garbage};
        random = new Random();
        int i = random.nextInt(objectArray.length);
        this.object = objectArray[i];
        //this.objects.add(objectArray[i]);


        this.printScreenInfo();
    }



    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }

    private void spawnPlayer() {
        //@TODO: Start the player at the left side of screen
    }
    private void spawnObjects() {
        random = new Random();
        this.lane1Y = (this.screenHeight/6+200)-150;
        this.lane2Y = (this.screenHeight/6)-150;
        this.lane3Y = (this.screenHeight/6+400)-150;
        this.lane4Y = (this.screenHeight/6 + 600-150);
        int[] intArray = {lane1Y, lane2Y, lane3Y,lane4Y};

        int rnd = random.nextInt(intArray.length);
        this.ObjectY = intArray[rnd];
        //String rd = "Value is " + (intArray[this.ObjectY]);

        //System.out.println(rd);

//        rainbow = new Item(context,20,(this.screenHeight/6+200)-150,R.drawable.rainbow64);
//        candy = new Item(context,20,(this.screenHeight/6)-150,R.drawable.candy64);
//        garbage = new Item(context,20,(this.screenHeight/6+400)-150,R.drawable.poop64);

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

        this.object.setxPosition(this.object.getxPosition() + 20);

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
            this.canvas.drawBitmap(this.object.getImage(),this.object.getxPosition(),this.object.getyPosition(),paintbrush);
//            for (int i=0;i<this.objects.size();i++){
//                this.canvas.drawBitmap(this.objects.get(i).getImage(),this.objects.get(i).getxPosition(),this.objects.get(i).getyPosition(),paintbrush);
//            }
            this.canvas.drawBitmap(this.player.getImage(),this.player.getxPosition(),this.player.getyPosition(),paintbrush);
//            this.canvas.drawBitmap(this.rainbow.getImage(),this.rainbow.getxPosition(),this.rainbow.getyPosition(),paintbrush);
//            this.canvas.drawBitmap(this.candy.getImage(),this.candy.getxPosition(),this.candy.getyPosition(),paintbrush);
//            this.canvas.drawBitmap(this.garbage.getImage(),this.garbage.getxPosition(),this.garbage.getyPosition(),paintbrush);

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
