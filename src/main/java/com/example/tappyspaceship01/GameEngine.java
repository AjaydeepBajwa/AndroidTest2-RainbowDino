package com.example.tappyspaceship01;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {

    // Android debug variables
    final static String TAG = "DINO-RAINBOWS";

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


    //Declaring and initialising the Y coordinate of Object
    int ObjectY = 50;

    boolean playerMoveUp;

    //Declaring ArrayList Variable of type Item.
    public ArrayList<Item> objects = new ArrayList<Item>();

    //Random object Variable
    Random random;


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

    //Sprite/Object variables
    Player player;
    Item rainbow;
    Item candy;
    Item garbage;

    //Game Stats
    int score = 0;
    int lives = 3;
    int lane1Y;
    int lane2Y;
    int lane3Y;
    int lane4Y;

    public GameEngine(Context context, int w, int h) {
        super(context);

        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;

        // Y Coordinates of Lanes
        this.lane1Y = (this.screenHeight / 6 + 200) - 150;
        this.lane2Y = (this.screenHeight / 6) - 150;
        this.lane3Y = (this.screenHeight / 6 + 400) - 150;
        this.lane4Y = (this.screenHeight / 6 + 600 - 150);

        //Setup Player object
        player = new Player(context, this.screenWidth - 200, (this.screenHeight / 6 + 200) - 150);

        //Setup/initialize Objects
        rainbow = new Item(context, 20, this.ObjectY, R.drawable.rainbow64);
        garbage = new Item(context, 20, this.ObjectY, R.drawable.poop64);
        candy = new Item(context, 20, this.ObjectY, R.drawable.candy64);

        this.printScreenInfo();
    }


    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }

    private void spawnObjects() {

        if (this.objects.size() == 0) {

            this.random = new Random();

            //creating an array of Y coordinates of lanes where the objects would be randomly positioned/located.
            int[] intArray = {this.lane1Y, this.lane2Y, this.lane3Y, this.lane4Y};

            //getting a random number from 0 to 4
            int rnd = random.nextInt(intArray.length);

            //creating a array of Item objects from which a random object would be selected
            Item[] objectArray = {this.rainbow, this.candy, this.garbage};

            //getting a random number from 0 to 3
            int i = this.random.nextInt(objectArray.length);

            // adding a random object to Array List of Objects
            this.objects.add(objectArray[i]);

            // setting X and Y Coordinates of random object.
            this.objects.get(0).setxPosition(20);
            this.objects.get(0).setyPosition(intArray[rnd]);

            //@TODO: Place the enemies in a random location
        }
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

        //updating sprite positions
        this.spawnObjects();
        this.objectMovement();
        this.objectPlayerCollision();

    }

    public void objectPlayerCollision(){
        //Getting Array List Objects using for loop.
        for (int i = 0; i < this.objects.size(); i++) {
            //If Player Hitbox and Object Hitbox collide
            if (((this.objects.get(i).getHitbox().intersect(this.player.getHitbox())) == true)) {
                //If the object is Candy or Rainbow, increase the score by 1
                if (((this.objects.get(i).getImagePath() == R.drawable.candy64)) || (this.objects.get(i).getImagePath() == R.drawable.rainbow64)) {
//                    int x = this.objects.get(i).getHitbox().centerX();
//                    System.out.println("Center X coordinate of object hitbox : " + x);

                    //Removing object after it hits player
                    this.objects.remove(i);
                    this.score = this.score + 1;


                    Log.d(TAG, "LIVES ARE :" + this.lives);
                }
                //If the Object is Garbage(Poop) decrease lives by 1
                else if ((this.objects.get(i).getImagePath()) == R.drawable.poop64){
                    System.out.println("POOOOOOOOOOOOOOOOOOOOOP");
                    this.objects.remove(i);
                    this.lives = this.lives - 1;
                }
            }

            //If the object hits screen edge remove the object
            else if (this.objects.get(i).getxPosition() >= this.screenWidth - this.player.getImage().getWidth()+2){
                this.objects.remove(i);
            }
        }
    }


    public void objectMovement() {

        for (int i = 0; i < this.objects.size(); i++) {
            //get x coordinate of object
            int objectX = this.objects.get(i).getxPosition();
            //Move object to right
            if (objectX < this.screenWidth - this.player.getImage().getWidth()) {
                //updating object Hitbox with new coordinates
                this.objects.get(i).updateHitBox();
                this.objects.get(i).setxPosition(this.objects.get(i).getxPosition() + 20);
            }
        }

    }

    public void playerMovement() {

        if ((this.playerMoveUp == true) && (this.player.getyPosition() > this.player.getImage().getHeight())) {
            //Moving player UP
            this.player.setyPosition(player.getyPosition() - 200);
        } else if ((this.playerMoveUp == false) && (this.player.getyPosition() < this.screenHeight - (this.player.getImage().getHeight())*3)) {
            //Moving player DOWN
            this.player.setyPosition(player.getyPosition() + 200);
        }
        //updating player hitbox
        this.player.updateHitBox();
    }

    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255, 255, 255, 255));
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setColor(Color.BLACK);
            paintbrush.setTextSize(60);

            //Drawing the Lanes at equal distances from each other
            this.canvas.drawRect(20, this.screenHeight / 6, this.screenWidth - 300, this.screenHeight / 6 + 20, paintbrush);
            this.canvas.drawRect(20, this.screenHeight / 6 + 200, this.screenWidth - 300, this.screenHeight / 6 + 200 + 20, paintbrush);
            this.canvas.drawRect(20, this.screenHeight / 6 + 400, this.screenWidth - 300, this.screenHeight / 6 + 400 + 20, paintbrush);
            this.canvas.drawRect(20, this.screenHeight / 6 + 600, this.screenWidth - 300, this.screenHeight / 6 + 600 + 20, paintbrush);
            //Draw score and lives
            this.canvas.drawText("SCORE = " + this.score + "", this.screenWidth - 300, 50, paintbrush);
            this.canvas.drawText("LIVES = " + this.lives + "", this.screenWidth - 700, 50, paintbrush);

            //Draw Player Image
            this.canvas.drawBitmap(this.player.getImage(), this.player.getxPosition(), this.player.getyPosition(), paintbrush);
            //Draw Object Images
            for (int i = 0; i < this.objects.size(); i++) {
                this.canvas.drawBitmap(this.objects.get(i).getImage(), this.objects.get(i).getxPosition(), this.objects.get(i).getyPosition(), paintbrush);
            }

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
            gameThread.sleep(60);
        } catch (Exception e) {

        }
    }

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        float fingerYPosition = event.getY();
        //float fingerYPosition = event.getY();
        //@TODO: What should happen when person touches the screen?
        if (userAction == MotionEvent.ACTION_DOWN) {
            if (fingerYPosition < this.screenHeight / 2) {
                this.playerMoveUp = true;
                this.playerMovement();
            } else if (fingerYPosition > this.screenHeight / 2) {
                this.playerMoveUp = false;
                this.playerMovement();
            }

        } else if (userAction == MotionEvent.ACTION_UP) {

        }

        return true;
    }
}
