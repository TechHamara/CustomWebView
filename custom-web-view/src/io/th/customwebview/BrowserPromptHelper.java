package io.th.customwebview;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.app.role.RoleManager;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;

@DesignerComponent(version = 7, versionName = "1.0", description = "Helper class of CustomWebView extension to add app to browsers list<br> Developed by TechHamara", iconName = "icon.png")
public class BrowserPromptHelper extends AndroidNonvisibleComponent implements OnNewIntentListener {
    public Activity activity;

    public BrowserPromptHelper(ComponentContainer container) {
        super(container.$form());
        activity = container.$context();
        form.registerForOnNewIntent(this);
    }

    public String getUrl(Intent intent) {
        if (intent == null) {
            return "";
        }
        Uri uri = intent.getData();
        if (uri != null) {
            return uri.toString();
        }
        return "";
    }

    @SimpleFunction(description = "Returns the url which started the current activity")
    public String GetStartUrl() {
        return getUrl(activity.getIntent());
    }

    @SimpleEvent(description = "Event raised when app gets resumed and gives the url which started this activity/screen if there is any else empty string")
    public void OnResume(String url) {
        EventDispatcher.dispatchEvent(this, "OnResume", url);
    }

    @SimpleFunction(description = "Returns whether the app is the default browser")
    public boolean IsDefaultBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        ResolveInfo resolveInfo = activity.getPackageManager().resolveActivity(intent, 0);
        if (resolveInfo != null) {
            return resolveInfo.activityInfo.packageName.equals(activity.getPackageName());
        }
        return false;
    }

    @SimpleFunction(description = "Requests the user to set this app as the default browser (Android 10+ only)")
    public void RequestDefaultBrowser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            RoleManager roleManager = (RoleManager) activity.getSystemService(Activity.ROLE_SERVICE);
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_BROWSER)) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_BROWSER)) {
                    Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_BROWSER);
                    activity.startActivityForResult(intent, 0); // Request code 0
                }
            }
        } else {
            // Fallback for older versions: Open settings
            try {
                Intent intent = new Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS");
                activity.startActivity(intent);
            } catch (Exception e) {
                // Fallback to generic settings if specific one fails
                activity.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        OnResume(getUrl(intent));
    }
}