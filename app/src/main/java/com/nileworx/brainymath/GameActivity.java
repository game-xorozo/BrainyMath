package com.nileworx.brainymath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

public class GameActivity extends BaseGameActivity {


    public static final boolean Debugging = false;


    private int mInterval = 1000;
    private Handler mHandler;
    int prog = 100;
    ProgressBar progressBar;
    CountDownTimer mCountDownTimer;
    CountDownTimer timer;
    int i = 5;

    SoundClass sou;
    CustomDialog dialog;
    boolean isRunning = false;
    int curProg = 100;
    TextView scoreValue, secondsValue;
    TextView xNumView, operatorView, yNumView, equalSignView, zNumView;
    TextView choice1View, choice2View, choice3View;

    int scoreValueNum = 0;

    SharedPreferences mSharedPreferences;
    Editor editor;
    ArrayList<Integer> choicesArray = new ArrayList<Integer>();

    private InterstitialAd interstitial;

    Integer x, y, z;
    int eqNum = 1;
    int n1, n2;
    int choice1, choice2, choice3;
    int operRand = 1;
    int hideNum, correctChoiceNum;
    int seconds;
    int answer;
    String operator;
    MyCountDownTimer myCountDownTimer;
    Long curTime;
    int gameEquationBgSwitch = 1;

    private long mLastClickTime = 0;

    Typeface hoboSTD;
    boolean isStopped;
    RelativeLayout gameEquationBG;
    RelativeLayout gameOverScreen;
    TextView gameOverText, yourScore, yourScoreValue, highScore, highScoreValue;
    String marketLink;
    Button mainBtn, playAgainBtn;
    // Methods

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper.setAutoSign(false);
        int seconds = Integer.parseInt(getResources().getString(R.string.secs4));
        dialog = new CustomDialog(GameActivity.this);
        sou = new SoundClass(GameActivity.this);


        marketLink = "https://play.google.com/store/apps/details?id=" + getPackageName();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int sWidth = displaymetrics.widthPixels;
        int sHeight = displaymetrics.heightPixels;

        int dens = displaymetrics.densityDpi;
        double wi = (double) sWidth / (double) dens;
        double hi = (double) sHeight / (double) dens;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);

        if (sWidth == 320 && sHeight == 480 && screenInches >= 3) {
            setContentView(R.layout.activity_game_3_2);
            Log.e("aaqqee", "1");
        } else if (sWidth > 480 && screenInches >= 4 && screenInches <= 5) {
            setContentView(R.layout.activity_game_4x);
            Log.e("aaqqee", "2");
        } else if (screenInches >= 5 && screenInches <= 6.5) {
            setContentView(R.layout.activity_game_5x);
            Log.e("aaqqee", "3");
        } else if (screenInches > 6.5 && screenInches < 9) {
//			Log.e("if", "3");
            setContentView(R.layout.activity_game_7x);
        } else {
            setContentView(R.layout.activity_game);
            Log.e("aaqqee", "4");
        }

        AdView ad = (AdView) findViewById(R.id.adView);
        ad.loadAd(new AdRequest.Builder().build());

        // Create the interstitial.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getResources().getString(
                R.string.adInterstitialUnitId));

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Begin loading your interstitial.
        interstitial.loadAd(adRequest);



        gameEquationBG = (RelativeLayout) findViewById(R.id.game_equation_initial);

        mSharedPreferences = getApplicationContext().getSharedPreferences(
                "MyPref", 0);
        editor = mSharedPreferences.edit();

        if (mSharedPreferences.getInt("usingNum", 0) != 100) {
            countUsingNumForRating();
        }



        scoreValue = (TextView) findViewById(R.id.scoreValue);
        secondsValue = (TextView) findViewById(R.id.secondsValue);


        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        xNumView = (TextView) findViewById(R.id.xNum);
        operatorView = (TextView) findViewById(R.id.operator);
        yNumView = (TextView) findViewById(R.id.yNum);
        equalSignView = (TextView) findViewById(R.id.equalSign);
        zNumView = (TextView) findViewById(R.id.zNum);

        choice1View = (TextView) findViewById(R.id.choice1);
        choice2View = (TextView) findViewById(R.id.choice2);
        choice3View = (TextView) findViewById(R.id.choice3);

        choice1View.setOnClickListener(choiceClickHandler);
        choice2View.setOnClickListener(choiceClickHandler);
        choice3View.setOnClickListener(choiceClickHandler);

        hoboSTD = Typeface.createFromAsset(getAssets(), "fonts/"
                + getResources().getString(R.string.main_font));


        scoreValue.setTypeface(hoboSTD);
        secondsValue.setTypeface(hoboSTD);

        gameOverScreen = (RelativeLayout) findViewById(R.id.gameOverScreen);

        gameOverText = (TextView) findViewById(R.id.gameOverText);
        yourScore = (TextView) findViewById(R.id.yourScore);
        yourScoreValue = (TextView) findViewById(R.id.yourScoreValue);
        highScore = (TextView) findViewById(R.id.highScore);
        highScoreValue = (TextView) findViewById(R.id.highScoreValue);

        mainBtn = (Button) findViewById(R.id.mainBtn);
        playAgainBtn = (Button) findViewById(R.id.playAgainBtn);

        gameOverText.setTypeface(hoboSTD);
        yourScore.setTypeface(hoboSTD);
        yourScoreValue.setTypeface(hoboSTD);
        highScore.setTypeface(hoboSTD);
        highScoreValue.setTypeface(hoboSTD);

        scoreValue.setText(String.valueOf(scoreValueNum));
//
//        eqNum = mSharedPreferences.getInt("scoreValue", 0) + 1;
        genNewEquation();


        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sou.playSound(R.raw.buttons);
                Intent intent = new Intent(GameActivity.this, MainActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
            }
        });


        playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sou.playSound(R.raw.buttons);

                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
            }
        });
    }

    // =========================================================================================

    public void countUsingNumForRating() {

        editor.putInt("usingNum", mSharedPreferences.getInt("usingNum", 0) + 1);
        editor.commit();

    }

    // =========================================================================================


    public void showInterstitialAd() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (interstitial.isLoaded()) {
                    interstitial.show();
                }

            }
        }, 1000);
    }

    // =========================================================================================

    private void genNewEquation() {

        curProg = 100;
        switchEquationBG();
        progressBar.setProgress(100);


        xNumView.setTextColor(Color.WHITE);
        yNumView.setTextColor(Color.WHITE);
        zNumView.setTextColor(Color.WHITE);
        Random r = new Random();

        operRand = r.nextInt(3 - 1) + 1;


        choice1View.setClickable(true);
        choice2View.setClickable(true);
        choice3View.setClickable(true);

        if (eqNum >= 1 && eqNum <= 10) {
            seconds = Integer.parseInt(getResources().getString(R.string.secs4));
            Log.e("eqnum", "11");
            n1 = r.nextInt(10 - 1) + 1;
            n2 = r.nextInt(10 - 1) + 1;
        } else if (eqNum >= 11 && eqNum <= 50) {
            n1 = r.nextInt(21 - 1) + 1;
            n2 = r.nextInt(10 - 1) + 1;
        } else if (eqNum >= 51 && eqNum <= 70) {
            n1 = r.nextInt(21 - 1) + 1;
            n2 = r.nextInt(21 - 1) + 1;
        } else if (eqNum >= 71 && eqNum <= 100) {
            n1 = r.nextInt(51 - 1) + 1;
            n2 = r.nextInt(51 - 1) + 1;
        } else if (eqNum >= 101 && eqNum <= 150) {
            n1 = r.nextInt(100 - 1) + 1;
            n2 = r.nextInt(100 - 1) + 1;
        } else if (eqNum >= 151 && eqNum <= 200) {
            n1 = r.nextInt(201 - 1) + 1;
            n2 = r.nextInt(201 - 1) + 1;
        } else if (eqNum > 200) {
            n1 = r.nextInt(1000 - 1) + 1;
            n2 = r.nextInt(1000 - 1) + 1;
        }


        if (operRand == 1) {
            operator = "+";
            x = n1;
            y = n2;
            z = x + y;
        } else {
            operator = "-";
            if (n1 >= n2) {
                x = n1;
                y = n2;
            } else {
                x = n2;
                y = n1;
            }
            z = x - y;
        }
        if (eqNum > 10) {
            if (x < 10 && y < 10) {
                seconds = Integer.parseInt(getResources().getString(R.string.secs3));
            } else if ((x >= 10 && x < 100) && (y >= 10 && y < 100)) {
                seconds = Integer.parseInt(getResources().getString(R.string.secs6));
            } else if (x >= 100 && y >= 100) {
                seconds = Integer.parseInt(getResources().getString(R.string.secs10));
            } else if ((x >= 10 && y < 10) || (y >= 10 && x < 10)) {
                seconds = Integer.parseInt(getResources().getString(R.string.secs3));
            } else if (((x >= 10 && x < 100) && y >= 100) || ((y >= 10 && y < 100) && x >= 100)) {
                seconds = Integer.parseInt(getResources().getString(R.string.secs8));
            } else {
                seconds = Integer.parseInt(getResources().getString(R.string.secs4));
            }
        }

        xNumView.setText(String.valueOf(x));
        operatorView.setText(operator);
        yNumView.setText(String.valueOf(y));
        zNumView.setText(String.valueOf(z));

        hideNum = r.nextInt(4 - 1) + 1;

        if (hideNum == 1) {
            xNumView.setText("?");
            xNumView.setTextColor(Color.YELLOW);
            answer = x;
        } else if (hideNum == 2) {
            yNumView.setText("?");
            yNumView.setTextColor(Color.YELLOW);
            answer = y;
        } else if (hideNum == 3) {
            zNumView.setText("?");
            zNumView.setTextColor(Color.YELLOW);
            answer = z;
        }

        genRandomChoices(answer);
        correctChoiceNum = r.nextInt(4 - 1) + 1;

        if (correctChoiceNum == 1) {
            choice1 = answer;
            choice2 = choicesArray.get(0);
            choice3 = choicesArray.get(1);
        } else if (correctChoiceNum == 2) {
            choice2 = answer;
            choice1 = choicesArray.get(0);
            choice3 = choicesArray.get(1);
        } else if (correctChoiceNum == 3) {
            choice3 = answer;
            choice1 = choicesArray.get(0);
            choice2 = choicesArray.get(1);
        }

        if (eqNum > 1) {
            secondsValue.setVisibility(View.VISIBLE);
            secondsValue.setText(String.valueOf(seconds));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    myCountDownTimer = new MyCountDownTimer(((seconds+1)*1000)+100, 1000);
                    myCountDownTimer.start();
                }
            }, 0);

        } else {
            secondsValue.setVisibility(View.GONE);
        }



        if (z >= 1000 && hideNum != 3) {
            float numSizeSP = conPxToSp(xNumView.getTextSize());
            float newNumSizeSP = numSizeSP - ((numSizeSP * 20) / 100);
            xNumView.setTextSize(TypedValue.COMPLEX_UNIT_SP, newNumSizeSP);
            operatorView.setTextSize(TypedValue.COMPLEX_UNIT_SP, newNumSizeSP);
            yNumView.setTextSize(TypedValue.COMPLEX_UNIT_SP, newNumSizeSP);
            equalSignView.setTextSize(TypedValue.COMPLEX_UNIT_SP, newNumSizeSP);
            zNumView.setTextSize(TypedValue.COMPLEX_UNIT_SP, newNumSizeSP);

        }



        float choiceSizeSP = conPxToSp(choice1View.getTextSize());
        float newChoiceSizeSP = choiceSizeSP - ((choiceSizeSP * 20) / 100);
        if (choice1 >= 1000) {
            choice1View.setTextSize(TypedValue.COMPLEX_UNIT_SP, newChoiceSizeSP);
        }

        if (choice2 >= 1000) {
            choice2View.setTextSize(TypedValue.COMPLEX_UNIT_SP, newChoiceSizeSP);
        }

        if (choice3 >= 1000) {
            choice3View.setTextSize(TypedValue.COMPLEX_UNIT_SP, newChoiceSizeSP);
        }

        choice1View.setText(String.valueOf(choice1));
        choice2View.setText(String.valueOf(choice2));
        choice3View.setText(String.valueOf(choice3));

        xNumView.setTypeface(hoboSTD);
        operatorView.setTypeface(hoboSTD);
        yNumView.setTypeface(hoboSTD);
        equalSignView.setTypeface(hoboSTD);
        zNumView.setTypeface(hoboSTD);

        choice1View.setTypeface(hoboSTD);
        choice2View.setTypeface(hoboSTD);
        choice3View.setTypeface(hoboSTD);





    }

    private float conPxToSp(float px) {
        float sp = px / getResources().getDisplayMetrics().scaledDensity;

        return sp;
    }

    // =========================================================================================

    public void genRandomChoices(int answer) {

        int randChoice;
        do {

            Random r = new Random();

            int signRand = r.nextInt(3 - 1) + 1;

            if (signRand == 1) {
                randChoice = answer + (r.nextInt(10 - 1) + 1);
            } else {
                randChoice = answer - (r.nextInt(10 - 1) + 1);
            }


            if (!choicesArray.contains(Math.abs(randChoice)) && Math.abs(randChoice) != answer) {
                choicesArray.add(Math.abs(randChoice));
            }

        } while (choicesArray.size() < 2);

    }

    // ==============================================================================

    private View.OnClickListener choiceClickHandler = new View.OnClickListener() {
        public void onClick(View v) {
            choice1View.setClickable(false);
            choice2View.setClickable(false);
            choice3View.setClickable(false);

            mLastClickTime = SystemClock.elapsedRealtime();
            sou.playSound(R.raw.buttons);
            checkAnswer(Integer.parseInt(String.valueOf(v.getTag())));
        }
    };


    // =========================================================================================

    public void checkAnswer(int tag) {

        if (eqNum > 1) {
            myCountDownTimer.cancel();
            myCountDownTimer = null;
        }

        choicesArray.clear();
        isStopped = true;

        if (tag == correctChoiceNum) {
            correctAnswer();
        } else {

            gameOver();
        }


    }


    // =========================================================================================

    public void correctAnswer() {
        scoreValueNum = scoreValueNum + 1;
        scoreValue.setText(String.valueOf(scoreValueNum));
        eqNum = scoreValueNum + 1;

        genNewEquation();

    }

    // =========================================================================================

    public void gameOver() {

        choice1View.setClickable(false);
        choice2View.setClickable(false);
        choice3View.setClickable(false);
        gameOverScreen.setVisibility(View.VISIBLE);
        yourScoreValue.setText(String.valueOf(scoreValueNum));
        if (scoreValueNum > mSharedPreferences.getInt("highScore", 0)) {
            editor.putInt("highScore",
                    scoreValueNum);
            editor.commit();

            highScoreValue.setText(String.valueOf(scoreValueNum));
            if (getApiClient().isConnected()) {

                Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_high_score), scoreValueNum);
            }
        } else {
            highScoreValue.setText(String.valueOf(mSharedPreferences.getInt("highScore", 0)));
        }

        countPlayingNumForAds();

    }

    // =========================================================================================

    public void countPlayingNumForAds() {

        editor.putInt("playingNum",
                mSharedPreferences.getInt("playingNum", 0) + 1);
        editor.commit();

        if (mSharedPreferences.getInt("playingNum", 0) >= Integer.parseInt(getResources().getString(R.string.showAdAfterUsingNum))) {
            showInterstitialAd();
            editor.putInt("playingNum", 0);
            editor.commit();
        }
    }

    // =========================================================================================

    private void switchEquationBG() {

        if (gameEquationBgSwitch == 1) {
            gameEquationBG.setBackgroundResource(R.drawable.game_equation_bg);
            gameEquationBgSwitch = 2;
        } else {
            gameEquationBG.setBackgroundResource(R.drawable.game_equation_bg2);
            gameEquationBgSwitch = 1;
        }
    }

    // ==============================================================================

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    // ==============================================================================

    @Override
    protected void onPause() {
        super.onPause();

        if (myCountDownTimer != null) {
            myCountDownTimer.cancel();
            myCountDownTimer = null;
        }
    }

    // ==============================================================================

    @Override
    public void onSignInFailed() {
        // TODO Auto-generated method stub

    }

    // ==============================================================================

    @Override
    public void onSignInSucceeded() {
        // TODO Auto-generated method stub

    }

    public class MyCountDownTimer extends CountDownTimer {

        int x = seconds;
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {


            if (x < seconds) {
                curProg = (int) (curProg - Math.ceil((double) 100 / seconds));
                progressBar.setProgress(curProg);
            }


            if (x <= 0) {
                secondsValue.setText(String.valueOf(0));
            } else {
                secondsValue.setText(String.valueOf(x));
            }

            if (x >= 0 && x < seconds) {
                sou.playSound(R.raw.clock_tick);
            }

            if (progressBar.getProgress() <= 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        choice1View.setClickable(false);
                        choice2View.setClickable(false);
                        choice3View.setClickable(false);
                        gameOver();
                    }
                }, 10);
            }
            x = x-1;
        }

        @Override
        public void onFinish() {


        }

    }


} // end class
