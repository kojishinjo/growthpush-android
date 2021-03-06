package com.growthpush.view;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Shigeru Ogawa on 13/08/12.
 */
public class AlertFragment extends DialogFragment implements DialogInterface.OnClickListener {

	public AlertFragment() {
		super();
	}

	public AlertFragment(String message) {

		Bundle bundle = new Bundle();
		bundle.putString("message", message);
		this.setArguments(bundle);

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

		PackageManager packageManager = getActivity().getPackageManager();
		try {
			ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getActivity().getPackageName(), 0);
			alertBuilder.setIcon(packageManager.getApplicationIcon(applicationInfo));
			alertBuilder.setTitle(packageManager.getApplicationLabel(applicationInfo));
		} catch (NameNotFoundException e) {
		}
		alertBuilder.setMessage(getArguments().getString("message"));
		alertBuilder.setPositiveButton("OK", this);

		Dialog dialog = alertBuilder.create();
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.setCanceledOnTouchOutside(false);

		return dialog;

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		if (shouldLaunchApplication()) {
			PackageManager packageManager = getActivity().getPackageManager();
			Intent intent = packageManager.getLaunchIntentForPackage(getActivity().getPackageName());
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			dialog.dismiss();
			getActivity().finish();
		}

		NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(null, 1);

	}

	private boolean shouldLaunchApplication() {

		try {
			ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Service.ACTIVITY_SERVICE);
			for (ActivityManager.RunningTaskInfo taskInfo : activityManager.getRunningTasks(10)) {
				if (!getActivity().getPackageName().equals(taskInfo.topActivity.getPackageName()))
					continue;
				if (taskInfo.topActivity.getClassName().equals(getActivity().getClass().getName()))
					continue;
				return false;
			}
		} catch (SecurityException e) {
		}

		return true;

	}

}
