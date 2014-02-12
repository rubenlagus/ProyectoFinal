/**
 *   Copyright 2013 Ruben Bermudez, Santiago Lopez and Isaac Morely
 *  
 *   This file is part of ProyectoFinal.
 *
 *   ProyectoFinal is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ProyectoFinal is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ProyectoFinal.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.npi.proyectofinal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Ruben Bermudez
 * @author Santiago Lopez
 * @author Isaac Morely
 * 
 * @brief Main activity running the game
 *
 */
public class GameActivity extends Activity implements SensorEventListener {
	private static final int CONSTANT_SIZE = 10;
	private static final int NUMBER_OF_GESTURES = 5;						//< Number of gestures detected
	private static final int CONST_TEMP_ANIMATION_GAMEOVER = 5000;			//< Time (in ms) for te Game Over screen to appear
	private static final int CONST_TEMP_ANIMATION_CLEAR_SIMBOL = 1000;		//< Time (in ms) for a symbol to be cleared
	private static int [] CONST_THRESHOLDS_SPEED_UP = {100, 500, 1000};		//< Thresholds at which speed is increased
	private int sizeW = 0, sizeH = 0;
	private int sizeImage = 0;
	private ViewTreeObserver.OnPreDrawListener call;
	private Game game = null;
	private GameLoopThread gameLoopThread = null;
	private ImageView blackSquare = null;
	private GestureLibrary gLib;			//< Lib for gesture managing
	private SensorManager mSensorManager; 	//< Manager of the sensor of the device
	private Sensor mAccelerometer; 			//< Manager of the accelerometer sensor
	private float mAccel;
	private float mAccelCurrent;
	private float mAccelLast;
	private LinkedList<ImageView> colaEraseImage = new LinkedList<ImageView>(); 
	
	/**
	 * 
	 * @brief Gestures detected
	 *
	 */
	private enum Simbol {
	    LEFT, RIGHT, DOWN, UP, SQUARE 
	}
	
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                //Yes button clicked
            	finish();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                //No button clicked
            	resumeGame();
            	((ImageView) findViewById(R.id.imageButtonPausePlay)).setClickable(true);
            	((ImageView) findViewById(R.id.imageButtonExit)).setClickable(true);
                break;
            }
        }
    };
	
    /**
     * 
     * @author Ruben Bermudez
     * @author Santiago Lopez
     * @author Isaac Morely
     *
     * @brief Class representing a gesture through an image and a symbol
     */    
	private class Element{
		Simbol simbol;		//< Symbol detected (LEFT, RIGHT, UP, LEFT, SQUARE)
		ImageView image;	//< Image corresponding to the symbol
		
		/**
		 * @brief Constructor
		 * @param nImage Image of the symbol
		 * @param nSimbol Symbol represented
		 */
		Element(ImageView nImage, Simbol nSimbol){
			image = nImage;
			simbol = nSimbol;
		}
		
		/**
		 * @brief Get the image of the symbol
		 * @return image
		 */
		
		ImageView GetImage(){
			return image;
		}
		/**
		 * brief Get the symbol
		 * @return simbol
		 */
		Simbol GetSimbol(){
			return simbol;
		}
	}
	
	/**
	 * 
	 * @author Ruben Bermudez
	 * @author Santiago Lopez
	 * @author Isaac Morely
	 * 
	 * @brief Represents the logical status of a game
	 *
	 */
	private class Game{
		private static final int NORMAL_LIFES = 3;	//< Number of lives at the beginning of the game
		private static final int NORMAL_SHAKES = 5;	//< NUmber of shakes at the beginning of the game
		private static final double NORMAL_INCREMENTO = 0.05;
		private static final double NORMAL_INCREMENTO_SPEED = 0.025;
		private static final int MIN_POINTS = 1;
		private static final int MAX_POINTS = 10;
		private static final int SHAKE_REWARD = 100;	//< Ratio at which score threshold for getting shakes is increased
		private int [] thresholdsSpeedUp;
		private int actualThresholdIndex;
		private LinkedList<Element> queueElementsGame = new LinkedList<Element>();	//< Queue storing the elements to appear in the screen
		private int score;				//< Current score
		private int scoreRewardCout;	//< Score threshold at which you get another shake
		private int lifes;				//< Number of lives left
		private int shakes;				//< Number of shakes left
		private int sizeOfElements;
		private double speed = NORMAL_INCREMENTO;	//< Falling speed rate
		private int incrementoY;
		private boolean correctSimbol;
		private boolean shakeDone;
		private double scoreCoefA1;
		private double scoreCoefA2;
		private double scoreCoefB1;
		private double scoreCoefB2;
		private int scoreThres1;
		private int scoreThres2;
		private double limitY;			//< Height limit of the screen
		private int initNextY;
		private int finalNextY;
		private int nextY;
		private boolean paused;
		private int coolDownShake;		//< Time to wait between a shake and the next
		private boolean playShakeWrong;
		GameActivity UI;
		
		/**
		 * @brief Constructor
		 * 
		 */
		Game(GameActivity nUI, int [] nThresholdsSpeedUp){
			UI = nUI;
			score = 0;
			scoreRewardCout = SHAKE_REWARD;
			lifes = NORMAL_LIFES;
			shakes = NORMAL_SHAKES;
			correctSimbol = false;
			shakeDone = false;
			incrementoY = 0;
			scoreCoefA1 = 0;
			scoreCoefA2 = 0;
			scoreCoefB1 = 0;
			scoreCoefB2 = 0;
			scoreThres1 = 999999;
			scoreThres2 = 999999;
			limitY = 0;
			sizeOfElements = 0;
			initNextY = 9999999;
			finalNextY = 9999999;
			nextY = 9999999;
			paused = false;
			coolDownShake = 0;
			thresholdsSpeedUp = nThresholdsSpeedUp;
			actualThresholdIndex = 0;
			playShakeWrong = false;
			
			OurSoundPlayer.initSounds(UI);
			nUI.UpdatedScoreText(score);
			nUI.UpdatedLifeText(lifes);
			nUI.UpdatedShakeText(shakes);
		}
		
		/**
		 * @brief Change height limit
		 * @param newLimitY new height of the game screen
		 */
		void setBounds(double newLimitY){
			limitY = newLimitY;
			calculateCoefs();
		}
		
		void setSizeElements(int nSizeOfElements){
			sizeOfElements = nSizeOfElements;
			incrementoY = (int) (speed * sizeOfElements);
			calculateCoefs();
		}
		/**
		 * @brief Change current speed
		 * @param nSpeed New speed
		 */
		void setSpeed(double nSpeed){
			speed = nSpeed;
			incrementoY = (int) (speed * sizeOfElements);
		}
		/**
		 * @brief Change the game from paused to running or the other way around
		 */
		void PausePlay(){
			paused = !paused;
		}
		/**
		 * @brief Check whether the game is paused or not
		 * @return paused Game is paused or not
		 */
		boolean isPaused(){
			return paused;
		}
		
		/**
		 * @brief Check if it is game over
		 * @return GameOver True when number of lives is zero or below
		 */
		boolean isGameOver(){
			return lifes <= 0;
		}
		
		/**
		 * @brief Set threshold to the amount of points obtained from a correct symbol depending on the position on screen
		 * @param nInitNextY Highest threshold
		 * @param nFinalNextY Smallest threshold
		 */
		
		void setAddElementThreshold(int nInitNextY, int nFinalNextY){
			initNextY = nInitNextY;
			finalNextY = nFinalNextY;
		}
		
		/**
		 * @brief Add a new symbol to the game
		 * @param element
		 */
		void addImage(Element element){
			int rangeRandom = finalNextY - initNextY + 1;
			
			nextY = (int) (Math.random() * rangeRandom) + initNextY; 
			queueElementsGame.add(element);
		}
		/**
		 * @brief Clear a detected symbol
		 * @param detectedSimbol Symbol to be compared with the one in the lowest part of the screen
		 */
		void clearSimbol(Simbol detectedSimbol){
			if(detectedSimbol == queueElementsGame.getFirst().GetSimbol())
				if(correctSimbol == false)
					correctSimbol = true;
		}
		
		/**
		 * @brief Check whether you can use shake or not
		 * @return Shake
		 */
		boolean doShake(){
			if(shakeDone == false && shakes > 0 && coolDownShake == 0){
				shakeDone = true;
				
				return true;
			}
			else if(shakes <= 0)
				playShakeWrong = true;
			
			return false;
		}
		
		/**
		 * @brief Calculate coefficients applied to calculate score
		 */
		void calculateCoefs(){
			scoreThres1 = (int) (limitY/2 - sizeOfElements);
			scoreThres2 = (int) (limitY/2 + sizeOfElements);
			scoreCoefA1 = (MAX_POINTS - MIN_POINTS)/(scoreThres1 * 1.0);
			scoreCoefA2 = (MAX_POINTS - MIN_POINTS)/((scoreThres2 - limitY) * 1.0);
			scoreCoefB1 = MIN_POINTS;
			scoreCoefB2 = MIN_POINTS - scoreCoefA2*limitY;
		}
		
		
		/**
		 * @brief Increase the score depending on pos and increase shakes left
		 * @param pos Position on screen of the symbol
		 */
		void IncreaseScore(int pos){
			
			//if the element was in the first 1/6 of the screen
			if(pos < scoreThres1)
				score += scoreCoefA1*pos + scoreCoefB1;
			//if the element was in the last 1/6 of the screen
			else if(pos > scoreThres2)
				score += scoreCoefA2*pos + scoreCoefB2;
			// in other case
			else
				score += MAX_POINTS;
			
			// Increase the number of shakes left
			if(score >= scoreRewardCout){
				++shakes;
				UI.UpdatedShakeText(shakes);
				scoreRewardCout += SHAKE_REWARD;
				
				OurSoundPlayer.playSound(UI, OurSoundPlayer.R2);
			}
			else
				OurSoundPlayer.playSound(UI, OurSoundPlayer.R1);
			
			if(actualThresholdIndex < thresholdsSpeedUp.length)
				if(score >= thresholdsSpeedUp[actualThresholdIndex]){
					setSpeed(speed + NORMAL_INCREMENTO_SPEED);
					++actualThresholdIndex;
					
					OurSoundPlayer.playSound(UI, OurSoundPlayer.CHEER);
				}
		}		
		
		/**
		 * @brief Get the current score
		 * @return score Current score
		 */
		int getScore(){
			return score;
		}
		
		void Run(){
			if(correctSimbol && queueElementsGame.size() > 0){
				RelativeLayout.LayoutParams layoutParams = (LayoutParams) queueElementsGame.get(0)
						.GetImage().getLayoutParams();
				IncreaseScore(layoutParams.topMargin);
				UI.EraseImageView(queueElementsGame.get(0).GetImage());
				UI.UpdatedScoreText(score);
				queueElementsGame.remove(0);
				correctSimbol = false;
			}
			else if(queueElementsGame.size() == 0)
				correctSimbol = false;
			
			if(shakeDone && queueElementsGame.size() > 0){
				--shakes;
				UI.EraseImageView(queueElementsGame.get(0).GetImage());
				UI.UpdatedShakeText(shakes);
				OurSoundPlayer.playSound(UI, OurSoundPlayer.SHAKE);
				queueElementsGame.remove(0);
				shakeDone = false;
				coolDownShake = 50;
			}
			else if(queueElementsGame.size() == 0){
				shakeDone = false;
				coolDownShake = 50;
			}
			else {
				if(playShakeWrong){
					playShakeWrong = false;
					
					OurSoundPlayer.playSound(UI, OurSoundPlayer.W2);
				}
					
				
				if(coolDownShake > 0)
					--coolDownShake;
			}
			
			ListIterator<Element> it = queueElementsGame.listIterator();
			
			while(it.hasNext()){
				ImageView temp = it.next().GetImage();
				RelativeLayout.LayoutParams layoutParams = (LayoutParams) temp.getLayoutParams();
			    layoutParams.topMargin += incrementoY;
				
			    if(layoutParams.topMargin < limitY)
			    	temp.setLayoutParams(layoutParams);
			    else{
			    	((RelativeLayout) UI.findViewById(R.id.fatherLayout)).removeView(temp);
			    	it.remove();
			    	
			    	if(lifes > 0){
			    		--lifes;
			    		
			    		OurSoundPlayer.playSound(UI, OurSoundPlayer.W1);
			    	}
			    }
			}
			
			UI.UpdatedLifeText(lifes);
			
			if(lifes > 0){
				if(queueElementsGame.size() == 0)
					UI.addImage();
				else{ 
					ImageView temp = queueElementsGame.getLast().GetImage();
					RelativeLayout.LayoutParams layoutParams = (LayoutParams) temp.getLayoutParams();
					 
					if(layoutParams.topMargin >= nextY)
						UI.addImage();
				}
			}
			else
				gameLoopThread.setRunning(false);
		}
	}
	
	/**
	 * 
	 * @author Ruben Bermudez
	 * @author Santiago Lopez
	 * @author Isaac Morely
	 * 
	 * @brief Thread executing the game
	 *
	 */
	private class GameLoopThread extends Thread {
		private boolean running = false;
	    
		GameLoopThread(){
			
		}
		
		void setRunning(boolean run) {
			running = run;
		}
		
		@Override
		public void run() {
			while (running) {
				runOnUiThread(new Runnable() {
				     @Override
				     public void run() {
				    	 game.Run();
				    }
				});
				
				try {
					sleep(20);
				} catch (InterruptedException e) {
					Log.e("Error: GameThread: ", e.toString());
				}
			}
			
			if(game.isGameOver()){
				runOnUiThread(new Runnable() {
				     @Override
				     public void run() {
				    	 GameOver();
				    }
				});
			}
		}
	} 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		game = new Game(this, CONST_THRESHOLDS_SPEED_UP);
		
		gLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
		 if (!gLib.load()) {
			 Toast.makeText(GameActivity.this, "Error loading gLib", Toast.LENGTH_SHORT).show();
			 Log.w("Gesture", "could not load gesture library");
			 finish();
		 }
		 GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
		 gestures.addOnGesturePerformedListener(handleGestureListener);
		
		 mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		 if (mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() == 0) {
			 AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
			 builder.setTitle("Accelerometer error");
			 builder.setMessage("There is no accelerometer available. The application will be closed");
			 builder.setCancelable(false);
			 builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                finish();
		           }
		       });
			 AlertDialog alertDialog = builder.create();
			 alertDialog.show();
		 }
		 else {
			 mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			 mAccel = 0.00f;
			 mAccelCurrent = SensorManager.GRAVITY_EARTH;
			 mAccelLast = SensorManager.GRAVITY_EARTH;
		 }
		 
		final RelativeLayout rlV = (RelativeLayout) findViewById(R.id.fatherLayout);
		ViewTreeObserver vto = rlV.getViewTreeObserver();
		call = new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
		    	sizeH = rlV.getHeight();	//height of imageView
		    	sizeW = rlV.getWidth();		//width of imageView
		    	sizeImage = sizeH/CONSTANT_SIZE;
		    	game.setBounds(sizeH);
		    	game.setSizeElements(sizeImage);
		    	game.setAddElementThreshold(sizeH/2 - sizeH/3, sizeH/2 + sizeH/3);
		    	addImage();
		    	gameLoopThread = new GameLoopThread();
				gameLoopThread.setRunning(true);
				gameLoopThread.start();
		    	ViewTreeObserver vto = rlV.getViewTreeObserver();
		    	vto.removeOnPreDrawListener(call);
				return true;
			}
		};
    	
    	vto.addOnPreDrawListener(call);
	}
	
	private OnGesturePerformedListener handleGestureListener = new OnGesturePerformedListener() {
		/**
		 * @brief Predict the symbol drawn by the player and compare it with the symbol closer to the end, clearing it if they were the same
		 */
		@Override
		public void onGesturePerformed(GestureOverlayView gestureView, Gesture gesture) {
			ArrayList<Prediction> predictions = gLib.recognize(gesture);
			
			if (predictions.size() > 0) {
				Prediction prediction = predictions.get(0);
				if (prediction.score > 1.0) {
					//Toast.makeText(GameActivity.this, prediction.name, Toast.LENGTH_SHORT).show();
					if (prediction.name.compareToIgnoreCase("biggerthan") == 0 || prediction.name.compareToIgnoreCase("biggerthan2") == 0) {
						game.clearSimbol(Simbol.RIGHT);
					}
					else {
						if (prediction.name.compareToIgnoreCase("lessthan") == 0 || prediction.name.compareToIgnoreCase("lessthan2") == 0) {
							game.clearSimbol(Simbol.LEFT);
						}
						else {
							if (prediction.name.compareToIgnoreCase("up") == 0 || prediction.name.compareToIgnoreCase("up2") == 0)
								game.clearSimbol(Simbol.UP);
							else {
								if (prediction.name.compareToIgnoreCase("down") == 0 || prediction.name.compareToIgnoreCase("down2") == 0)
									game.clearSimbol(Simbol.DOWN);
								else {
									if (prediction.name.compareToIgnoreCase("square") == 0 || prediction.name.compareToIgnoreCase("square2") == 0)
										game.clearSimbol(Simbol.SQUARE);
									else {
									}
								}
							}
						}
					}					
				}
			}
		}
	 };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	/**
	 * @name scaleImage
	 * @brief Scale an image
	 * 
	 */
	private void scaleImage(ImageView view, int boundBoxInDp1, int boundBoxInDp2){
	    // Get the ImageView and its bitmap
	    Drawable drawing = view.getDrawable();
	    Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();

	    // Get current dimensions
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();

	    // Determine how much to scale: the dimension requiring less scaling is
	    // closer to the its side. This way the image always stays inside your
	    // bounding box AND either x/y axis touches it.
	    float xScale = ((float) boundBoxInDp1) / width;
	    float yScale = ((float) boundBoxInDp2) / height;
	    float scale = xScale <= yScale ? xScale : yScale;

	    // Create a matrix for the scaling and add the scaling data
	    Matrix matrix = new Matrix();
	    matrix.postScale(scale, scale);

	    // Create a new bitmap and convert it to a format understood by the ImageView
	    //RelativeLayout.LayoutParams paramst = (LayoutParams) tv.getLayoutParams();
    	//paramst.setMargins((int) (width*scale*(335.0/tamX) + (boundBoxInDp1 - width*scale)/2), 
    		//	(int)(height*scale*620.0/tamY + (boundBoxInDp2 - height*scale)/2), 0, 0);
    	
    	Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	    @SuppressWarnings("deprecation")
		BitmapDrawable result = new BitmapDrawable(scaledBitmap);
	    width = scaledBitmap.getWidth();
	    height = scaledBitmap.getHeight();

	    // Apply the scaled bitmap
	    view.setImageDrawable(result);

	    // Now change ImageView's dimensions to match the scaled image
	    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
	    params.width = width;
	    params.height = height;
	    view.setLayoutParams(params);
	}
	
	/**
	 * @name createElement
	 * @brief Create a new element (symbol and image)
	 * @return element Resulting element
	 */
	private Element createElement(){
		ImageView imageView = new ImageView(this);
		Simbol simbol;
		int randomSelect = (int) (NUMBER_OF_GESTURES * Math.random());
		
		switch (randomSelect) {
        case 0:
        	simbol = Simbol.LEFT;
        	imageView.setImageResource(R.drawable.ic_less);
            break;
                
        case 1:
        	simbol = Simbol.RIGHT;
        	imageView.setImageResource(R.drawable.ic_more);
            break;
                     
        case 2: 
        	simbol = Simbol.DOWN;
        	imageView.setImageResource(R.drawable.ic_down);
        	break;
        case 3:
        	simbol = Simbol.UP;
        	imageView.setImageResource(R.drawable.ic_up);
            break;
        case 4:
        	simbol = Simbol.SQUARE;
        	imageView.setImageResource(R.drawable.ic_square);
            break;
                    
        default:
        	simbol = Simbol.SQUARE;
        	imageView.setImageResource(R.drawable.ic_square);
            break;
		}
		
		return new Element(imageView, simbol);
	}
	
	/**
	 * @name addImage
	 * @brief Add a new image (gesture) to the game
	 */
	void addImage(){
		int rangeRandom = sizeW - 2*sizeImage + 1;
		Element element = createElement();
		RelativeLayout.LayoutParams vp = 
		    new RelativeLayout.LayoutParams(sizeImage, sizeImage);
		
		vp.setMargins((int) (rangeRandom * Math.random() + sizeImage/2), -sizeImage, 0, 0);
		element.GetImage().setLayoutParams(vp);
		element.GetImage().setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		scaleImage(element.GetImage(), sizeImage, sizeImage);
		((RelativeLayout) findViewById(R.id.fatherLayout)).addView(element.GetImage());
		game.addImage(element);
	}
	
	/**
	 * @name EraseImageView
	 * @brief Erase previous view to change to Game Over
	 * @param view
	 */
	void EraseImageView(ImageView view){
		Animation gameOverAnimation = new AlphaAnimation(1.00f, 0.00f);

		colaEraseImage.addLast(view);
		gameOverAnimation.setDuration(CONST_TEMP_ANIMATION_CLEAR_SIMBOL);
		gameOverAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
		    public void onAnimationEnd(Animation animation) {
		    	((RelativeLayout) findViewById(R.id.fatherLayout)).removeView(colaEraseImage.getFirst());
		    	colaEraseImage.removeFirst();
		    }
		});

		colaEraseImage.getLast().startAnimation(gameOverAnimation);
	}
	
	/**
	 * @name UpdatedScoreText
	 * @brief Updates the score field in the game
	 * @param nScore New score
	 */
	void UpdatedScoreText(int nScore){
		((TextView) findViewById(R.id.scoreNumberText)).setText(Integer.toString(nScore));
	}
	
	/**
	 * @name UpdatedLifeText
	 * @brief Updates the lives field in the game
	 * @param nLife New number of lives
	 */
	void UpdatedLifeText(int nLife){
		if(nLife < 10)
			((TextView) findViewById(R.id.LifeNumberText)).setText(Integer.toString(nLife));
		else
			((TextView) findViewById(R.id.LifeNumberText)).setText("9");
	}
	
	/**
	 * @name UpdatedShakeText
	 * @brief Updates the number of shakes left in the game
	 * @param nShake New number of shakes
	 */
	void UpdatedShakeText(int nShake){
		if(nShake < 10)
			((TextView) findViewById(R.id.shakeNumberText)).setText("0" + Integer.toString(nShake));
		else if(nShake >= 10 && nShake < 100)
			((TextView) findViewById(R.id.shakeNumberText)).setText(Integer.toString(nShake));
		else
			((TextView) findViewById(R.id.shakeNumberText)).setText("99");
	}
	
	/**
	 * @name stopGame
	 * @brief End the execution of the game
	 */
	@SuppressWarnings("deprecation")
	private void stopGame(){
		if (gameLoopThread != null) {
        	gameLoopThread.setRunning(false);
        	//gameLoopThread.interrupt(); // request to terminate thread in regular way
        	
        	try {
				gameLoopThread.join(500); // wait until thread ends or timeout after 0.5 second
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
            
        	if (gameLoopThread.isAlive()) {
                // this is needed only when something is wrong with thread, for example hangs in ininitive loop or waits to long for lock to be released by other thread.
                Log.e("Error: GameThread: ", "Serious problem with thread!");
                gameLoopThread.stop();
            }
        }
	}
	
	/**
	 * @name startGame
	 * @brief Start a new game
	 */
	private void startGame(){
		gameLoopThread = new GameLoopThread();
		gameLoopThread.setRunning(true);
		gameLoopThread.start();
	}
	
	/**
	 * @name onResume
	 * @brief  This method is called always before show the view, so it is the right place to start listening
	 * 		the sensor.
	 * @note Starting here, we can save battery just using onPause to stop the listening. 
	 * 
	 * @see android.app.Activity#onRsume()
	 */
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	/**
	 * @name  onPause
	 * @brief This method is called just when the view is not visible for the user (it doesn't mean that
	 * 		the activity is going to be destroyed.
	 * @note The sensor listener is stopped to save battery
	 * 
	 * @see android.app.Activity#onPause()
	 */
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
	
	public void onRestart(){
		super.onRestart();
		
		if(!game.isPaused()){
			startGame();
		}
	}
	
	/**
	 * @name onStop
	 * @brief Stop the execution of the game
	 */
	@Override
	public void onStop(){
		stopGame();
		
        super.onStop();
	}
	/**
	 * @name pauseGame
	 * @brief Pause the current game
	 */
	private void pauseGame(){
		game.PausePlay();
		stopGame();
	}
	/**
	 * @name resumeGame
	 * @brief Continue the current game
	 */
	private void resumeGame(){
		startGame();
		game.PausePlay();
	}
	
	/**
	 * @name pausePlayButton
	 * @brief Pause and continue the game pressing the pause or play button on screen
	 * @param view Current view
	 */	
	public void pausePlayButton(View view){
		if(!game.isPaused()){
			pauseGame();
			RelativeLayout.LayoutParams vp = 
	    		    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 
	    		    		RelativeLayout.LayoutParams.MATCH_PARENT);
	    	((ImageView) findViewById(R.id.imageButtonPausePlay)).setImageResource(R.drawable.ic_play);
	    	blackSquare = new ImageView(this);
	    	blackSquare.setLayoutParams(vp);
	    	blackSquare.setScaleType(ImageView.ScaleType.FIT_XY);
	    	blackSquare.setImageResource(R.drawable.black);
	    	blackSquare.setAlpha((float) 0.5);
	    	((RelativeLayout) findViewById(R.id.screenLayout)).addView(blackSquare);
			((ImageView) findViewById(R.id.imageButtonExit)).setClickable(false);
		}
		else{
			resumeGame();
			((ImageView) findViewById(R.id.imageButtonPausePlay)).setImageResource(R.drawable.ic_pause);
			((RelativeLayout) findViewById(R.id.screenLayout)).removeView(blackSquare);
			blackSquare = null;
			((ImageView) findViewById(R.id.imageButtonExit)).setClickable(true);
		}
	}
	/**
	 * @name GameOver
	 * @brief Show Game Over animation
	 */
	private void GameOver(){
		if(game.isPaused()){
			((ImageView) findViewById(R.id.imageButtonPausePlay)).setImageResource(R.drawable.ic_pause);
			((RelativeLayout) findViewById(R.id.screenLayout)).removeView(blackSquare);
			blackSquare = null;
		}
		else
			game.PausePlay();
		
		((ImageView) findViewById(R.id.imageButtonPausePlay)).setClickable(false);
		((ImageView) findViewById(R.id.imageButtonExit)).setClickable(false);
		
		ImageView GameOver = new ImageView(this);
		GameOver = new ImageView(this);
		RelativeLayout.LayoutParams vp = 
    		    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 
    		    		RelativeLayout.LayoutParams.MATCH_PARENT);
		
		GameOver.setLayoutParams(vp);
		GameOver.setScaleType(ImageView.ScaleType.FIT_XY);
		GameOver.setImageResource(R.drawable.game_over_screen);
		GameOver.setAlpha((float) 1.0);
    	((RelativeLayout) findViewById(R.id.screenLayout)).addView(GameOver);
		Animation gameOverAnimation = new AlphaAnimation(0.00f, 1.00f);

		gameOverAnimation.setDuration(CONST_TEMP_ANIMATION_GAMEOVER);
		gameOverAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
		    public void onAnimationEnd(Animation animation) {
		    	int timeSleep = OurSoundPlayer.DEFEAT_MUSIC_DURATION - CONST_TEMP_ANIMATION_GAMEOVER;
		    	
		    	if(timeSleep > 0){
		    		Handler handler = new Handler(); 
		    	    handler.postDelayed(new Runnable() { 
		    	         public void run() { 
		    	        	 goRegisScore();
		    	         } 
		    	    }, timeSleep); 
		    	}
		    	else
		    		goRegisScore();
		    }
		});
		
		OurSoundPlayer.playMusic(this, OurSoundPlayer.DEFEAT_MUSIC);
		GameOver.startAnimation(gameOverAnimation);
	}

	/**
	 * @name exitButton
	 * @brief Exit button on screen to quit the game
	 * @param view Current view
	 */
	public void exitButton(View view){
		((ImageView) findViewById(R.id.imageButtonPausePlay)).setClickable(false);
		((ImageView) findViewById(R.id.imageButtonExit)).setClickable(false);
		pauseGame();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setMessage("¿Quieres salir?")
	        .setPositiveButton("Sí", dialogClickListener)
	            .setNegativeButton("No", dialogClickListener).show();
	}
	
	private void goRegisScore(){
		Intent intent = new Intent(this, ScoreRegistrer.class);
		
	    intent.putExtra(getString(R.string.score), game.getScore());
	    startActivity(intent);
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if ((keyCode == KeyEvent.KEYCODE_BACK)){
	    	if(!game.isPaused())
	    		exitButton(null);
	    	
	    	return true;
	    }
	    else
	    	return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}
	
	/**
	 * @name onSensorChanged
	 * @brief Check changes in the accelerometer to know if there is a "shake movement"
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] mGravity = event.values.clone();
		// Shake detection
		float x = mGravity[0];
		float y = mGravity[1];
		float z = mGravity[2];
		mAccelLast = mAccelCurrent;
		mAccelCurrent = (float) Math.sqrt(x*x + y*y + z*z);
		float delta = mAccelCurrent - mAccelLast;
		mAccel = mAccel * 0.9f + delta;
		if(mAccel > 7){ 
			//Toast.makeText(GameActivity.this, "acelerando!!!!", Toast.LENGTH_SHORT).show();
			game.doShake();
		}
	}
}
