package com.sunny.cwvutils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class BrowserActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Intent launch = getLaunchIntent();
            if (launch != null) {
                startActivity(launch);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            finish();
        }
    }

    private Intent getLaunchIntent() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("BrowserPromptHelper", 0);
            String mScreen = sharedPreferences.getString("scrName", "");
            Intent intent = mScreen.isEmpty() ? getPackageManager().getLaunchIntentForPackage(getPackageName())
                    : new Intent();
            if (!mScreen.isEmpty()) {
                intent.setClassName(this, mScreen);
            }
            // Manually quote the string to avoid JsonUtil dependency
            String startValue = sharedPreferences.getString("strtvlu", "");
            intent.putExtra("APP_INVENTOR_START", "\"" + startValue + "\"");
            intent.setPackage(null);
            if (getIntent().getData() != null) {
                intent.setData(getIntent().getData());
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
