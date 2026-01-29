package com.sunny.cwvutils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.UsesActivities;
import com.google.appinventor.components.annotations.androidmanifest.ActionElement;
import com.google.appinventor.components.annotations.androidmanifest.ActivityElement;
import com.google.appinventor.components.annotations.androidmanifest.CategoryElement;
import com.google.appinventor.components.annotations.androidmanifest.DataElement;
import com.google.appinventor.components.annotations.androidmanifest.IntentFilterElement;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.OnNewIntentListener;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Settings;

@DesignerComponent(version = 14, versionName = "14", description = "Helper class of CustomWebView extension to add app to browsers list<br> Developed by Sunny Gupta and Customized by TechHamara", category = ComponentCategory.EXTENSION, nonVisible = true, iconName = "https://i.ibb.co/4wLNN1Hs/ktvu4bapylsvnykoyhdm-c-fill-w-20-h-20.png", helpUrl = "https://github.com/vknow360/CustomWebView", androidMinSdk = 21)
@UsesActivities(activities = { @ActivityElement(intentFilters = {
                @IntentFilterElement(actionElements = {
                                @ActionElement(name = "android.intent.action.VIEW") }, categoryElements = {
                                                @CategoryElement(name = "android.intent.category.DEFAULT"),
                                                @CategoryElement(name = "android.intent.category.BROWSABLE")
                                }, dataElements = {
                                                @DataElement(scheme = "http"),
                                                @DataElement(scheme = "https")
                                }),
                @IntentFilterElement(actionElements = {
                                @ActionElement(name = "android.intent.action.VIEW") }, categoryElements = {
                                                @CategoryElement(name = "android.intent.category.DEFAULT"),
                                                @CategoryElement(name = "android.intent.category.BROWSABLE")
                                }, dataElements = {
                                                @DataElement(scheme = "http"),
                                                @DataElement(scheme = "https"),
                                                @DataElement(mimeType = "text/html"),
                                                @DataElement(mimeType = "text/plain"),
                                                @DataElement(mimeType = "application/xhtml+xml")
                                }) }, name = "com.sunny.CustomWebView.BrowserActivity", exported = "true", launchMode = "singleTask")
})
@SimpleObject(external = true)
public class BrowserPromptHelper extends AndroidNonvisibleComponent implements OnNewIntentListener {
        private static final String TAG = "BrowserPromptHelper";

        public Activity activity;

        public BrowserPromptHelper(ComponentContainer container) {
                super(container.$form());
                activity = container.$context();
                form.registerForOnNewIntent(this);
        }

        private String getUrl(Intent intent) {
                Uri uri = intent.getData();
                if (uri != null) {
                        return uri.toString();
                }
                return "";
        }

        @SimpleFunction(description = "Registers the screen to be opened when the app is launched from browser")
        public void RegisterScreen(String screenName, String startValue) {
                SharedPreferences.Editor edit = form.getSharedPreferences(TAG, 0).edit();
                String replaceAll = this.form.getClass().getName().replace(this.form.getClass().getSimpleName(),
                                screenName);
                edit.putString("scrName", replaceAll);
                edit.putString("strtvlu", startValue);
                edit.apply();
        }

        @SimpleFunction(description = "Returns the url which started the current activity")
        public String GetStartUrl() {
                return getUrl(activity.getIntent());
        }

        @SimpleFunction(description = "Checks if the app is the default browser")
        public boolean IsDefaultBrowser() {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                PackageManager pm = activity.getPackageManager();
                ResolveInfo resolveInfo = pm.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);
                if (resolveInfo != null) {
                        return activity.getPackageName().equals(resolveInfo.activityInfo.packageName);
                }
                return false;
        }

        @SimpleFunction(description = "Requests the user to set this app as the default browser")
        public void RequestSetDefaultBrowser() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        RoleManager roleManager = (RoleManager) activity.getSystemService(Context.ROLE_SERVICE);
                        if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_BROWSER)) {
                                if (!roleManager.isRoleHeld(RoleManager.ROLE_BROWSER)) {
                                        Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_BROWSER);
                                        form.startActivityForResult(intent, 0);
                                }
                        }
                } else {
                        OpenDefaultAppsSettings();
                }
        }

        @SimpleFunction(description = "Opens the default apps settings screen")
        public void OpenDefaultAppsSettings() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
                        form.startActivity(intent);
                } else {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        form.startActivity(intent);
                }
        }

        @SimpleEvent(description = "Event raised when app gets resumed and gives the url which started this activity/screen if there is any else empty string")
        public void OnResume(String url) {
                EventDispatcher.dispatchEvent(this, "OnResume", url);
        }

        @Override
        public void onNewIntent(Intent intent) {
                OnResume(getUrl(intent));
        }
}