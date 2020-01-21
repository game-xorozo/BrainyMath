package com.nileworx.brainymath;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.example.games.basegameutils.BaseGameActivity;

public class MainActivity extends BaseGameActivity {

	SharedPreferences mSharedPreferences;
	Editor e;
	String marketLink;
	SoundClass sou;
	CustomDialog dialog;
	private long mLastClickTime = 0;

	// ========================================================================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dialog = new CustomDialog(MainActivity.this);

		sou = new SoundClass(MainActivity.this);


		marketLink = "https://play.google.com/store/apps/details?id=" + getPackageName();



		mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
		e = mSharedPreferences.edit();
		e.putInt("levelValue",
				1);
		e.putInt("scoreValue",
				0);
		e.commit();

        showRateDlg();
		final ImageButton play = (ImageButton) findViewById(R.id.play);
		play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.play);
				Intent intent = new Intent(MainActivity.this, GameActivity.class);
				startActivity(intent);

			}
		});

		final ImageButton sound = (ImageButton) findViewById(R.id.sound);

		if (mSharedPreferences.getInt("sound", 1) == 1) {
			sound.setBackgroundResource(R.drawable.button_sound_on_main);
		} else {
			sound.setBackgroundResource(R.drawable.button_sound_off_main);
		}

		sound.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mSharedPreferences.getInt("sound", 1) == 1) {
					e.putInt("sound", 0);
					e.commit();
					sound.setBackgroundResource(R.drawable.button_sound_off_main);
				} else {
					e.putInt("sound", 1);
					e.commit();
					sound.setBackgroundResource(R.drawable.button_sound_on_main);

					sou.playSound(R.raw.buttons);
				}
				// e.commit(); // save changes
			}
		});

		final ImageButton share = (ImageButton) findViewById(R.id.share);
		share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				sou.playSound(R.raw.buttons);

				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				String shareBody = "Matches Puzzle on Google Play \n\n" + marketLink;
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Matches Puzzle");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
				startActivity(Intent.createChooser(sharingIntent, "Share via"));

			}
		});

		final ImageButton rate = (ImageButton) findViewById(R.id.rate);
		rate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				sou.playSound(R.raw.buttons);

				Intent intent = new Intent(Intent.ACTION_VIEW);

				intent.setData(Uri.parse("market://details?id=" + getPackageName()));

				if (!MyStartActivity(intent)) {
					// Market (Google play) app seems not installed, let's try
					// to open a webbrowser
					intent.setData(Uri.parse(marketLink));
					if (!MyStartActivity(intent)) {
						// Well if this also fails, we have run out of options,
						// inform the user.
						Toast.makeText(MainActivity.this, "Could not open Android market, please install the market app.", Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		final ImageButton exit = (ImageButton) findViewById(R.id.exit);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				sou.playSound(R.raw.buttons);

				dialog.showDialog(R.layout.purple_dialog, "exitDlg", "Are you sure you want to exit?", null);

			}
		});

		final ImageButton leaderboard = (ImageButton) findViewById(R.id.leaderboard);
		leaderboard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
					return;
				}
				mLastClickTime = SystemClock.elapsedRealtime();
				sou.playSound(R.raw.buttons);
				if (!isSignedIn()) {
					beginUserInitiatedSignIn();
				} else {
					startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), getString(R.string.leaderboard_high_score)), 2);
				}

			}
		});
	}

	// ==============================================================================

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("tag", "onActivityResult(" + requestCode + "," + resultCode + "," + data);

		if (requestCode == 2 && resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
			mHelper.disconnect();
			// update your logic here (show login btn, hide logout btn).
		} else {
			mHelper.onActivityResult(requestCode, resultCode, data);
		}
	}

	// ==============================================================================

	public boolean MyStartActivity(Intent aIntent) {
		try {
			startActivity(aIntent);
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}

    // =========================================================================================

    public void showRateDlg() {

        if (mSharedPreferences.getInt("usingNum", 0) >= Integer.parseInt(getResources().getString(R.string.rateAfterUsingNum))) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    e.putInt("usingNum", 0);
                    e.commit();
                    String msg = getResources().getString(R.string.rateDlg);
                    dialog.showDialog(R.layout.purple_dialog, "rateDlg", msg, marketLink);

                }
            }, 100);

        }

    }

	// ========================================================================================================

	@Override
	public void onSignInSucceeded() {
	}

	// ========================================================================================================

	@Override
	public void onSignInFailed() {
	}	
}
