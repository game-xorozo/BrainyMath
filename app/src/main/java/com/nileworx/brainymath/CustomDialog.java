package com.nileworx.brainymath;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomDialog {

	public Context context;
	MediaPlayer sound;

	SharedPreferences mSharedPreferences;
	Editor editor;


	SoundClass sou;

	// ==============================================================================

	public CustomDialog(Context context) {
		this.context = context;


		sou = new SoundClass(context);

        mSharedPreferences = context.getSharedPreferences("MyPref", 0);
        editor = mSharedPreferences.edit();
	}

	// ==============================================================================

	public void showDialog(int layout, String dialogName, String msg, String data) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.setContentView(layout);

		Typeface hoboSTD = Typeface.createFromAsset(context.getAssets(), "fonts/" + context.getResources().getString(R.string.main_font));

		// set the custom dialog components - text, image and button
		TextView message = (TextView) dialog.findViewById(R.id.message);
		message.setText(msg.trim());
		message.setTypeface(hoboSTD);

		LinearLayout confirmDlg = (LinearLayout) dialog.findViewById(R.id.confirmDlg);
		LinearLayout askRateDlg = (LinearLayout) dialog.findViewById(R.id.askRateDlg);

		if (dialogName.equals("exitDlg")) {
			confirmDlg.setVisibility(View.VISIBLE);
			exitDlg(dialog);
		} else if (dialogName.equals("rateDlg")) {
			askRateDlg.setVisibility(View.VISIBLE);
			rateDlg(dialog, data);
		}

		dialog.setCanceledOnTouchOutside(false);

		dialog.show();
	}

	// ==============================================================================

	private void exitDlg(final Dialog dialog) {

		Button noBtn = (Button) dialog.findViewById(R.id.noBtn);
		// if button is clicked, close the custom dialog
		noBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				dialog.dismiss();
			}
		});

		Button yesBtn = (Button) dialog.findViewById(R.id.yesBtn);
		// if button is clicked, close the custom dialog
		yesBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				((Activity) context).finish();
				System.exit(0);
			}
		});
	}

	// ==============================================================================

	private void rateDlg(final Dialog dialog, final String marketLink) {

		Button rateBtn = (Button) dialog.findViewById(R.id.rateBtn);
		// if button is clicked, close the custom dialog
		rateBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				dialog.dismiss();
				MainActivity mainAct = (MainActivity) context;
				Intent intent = new Intent(Intent.ACTION_VIEW);

				intent.setData(Uri.parse("market://details?id=" + context.getPackageName()));

				if (!mainAct.MyStartActivity(intent)) {
					// Market (Google play) app seems not
					// installed, let's try
					// to open a webbrowser
					intent.setData(Uri.parse(marketLink));
					if (!mainAct.MyStartActivity(intent)) {
						// Well if this also fails, we have run
						// out of options,
						// inform the user.
						Toast.makeText(context, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
					} else {
						editor.putInt("usingNum", 100);
						editor.commit();
					}
				} else {
					editor.putInt("usingNum", 100);
					editor.commit();
				}
			}
		});

		Button laterBtn = (Button) dialog.findViewById(R.id.laterBtn);
		// if button is clicked, close the custom dialog
		laterBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				editor.putInt("usingNum", 0);
				editor.commit();
				dialog.dismiss();
			}
		});
	}

}