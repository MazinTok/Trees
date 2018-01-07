package com.mazinaltokhais.trees;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;

import com.eggheadgames.aboutbox.AboutConfig;
import com.eggheadgames.aboutbox.IAnalytic;
import com.eggheadgames.aboutbox.IDialog;
//import com.danielstone.materialaboutlibrary.MaterialAboutActivity;

/**
 * Created by mazoo_000 on 01/08/2017.
 */

public class MyApplication extends Application {
    private static MyApplication mAppInstance = null;
    private Location mLastKnownLocation;
    private static Context mContext;
    //-----------
    AboutConfig aboutConfig;// = AboutConfig.getInstance();

    //----------
    public Location getmLastKnownLocation() {
        return mLastKnownLocation;
    }

    public void setmLastKnownLocation(Location mLastKnownLocation) {
        this.mLastKnownLocation = mLastKnownLocation;
    }

    public static MyApplication getInstance() {
        return mAppInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //  instance = this;
//        mContext = getApplicationContext();
//        mAppInstance = this;
//        aboutConfig = AboutConfig.getInstance();
//
//        String temp ="dd";
//        aboutConfig.appName = getString(R.string.app_name);
//        aboutConfig.appIcon = R.mipmap.ic_launcher;
//        aboutConfig.aboutLabelTitle = getString(R.string.About_App);
//
//        String version= "1.0.0";
//        try {
//            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
//             version = pInfo.versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        aboutConfig.version = version;
//        // app publisher for "Try Other Apps" item
//        aboutConfig.appPublisher = "market://dev?id==Mazin+Altokhais";
////        aboutConfig.aboutLabelTitle = getString(R.string.About_App);
//
//        aboutConfig.packageName = getApplicationContext().getPackageName();
////        aboutConfig.buildType = google ? AboutConfig.BuildType.GOOGLE : AboutConfig.BuildType.AMAZON;
////     aboutConfig.
////        aboutConfig.twitterUserName = getString(R.string.twitterUserName);
////        aboutConfig.webHomePage = getString(R.string.webHomePage);
////        aboutConfig.appPublisher
////        aboutConfig.
//
//
//        // if pages are stored locally, then you need to override aboutConfig.dialog to be able use custom WebView
////        aboutConfig.companyHtmlPath = temp;
////        aboutConfig.privacyHtmlPath = temp;
////        aboutConfig.acknowledgmentHtmlPath = temp;
//
//        aboutConfig.dialog = new IDialog() {
//            @Override
//            public void open(AppCompatActivity appCompatActivity, String url, String tag) {
//                // handle custom implementations of WebView. It will be called when user click to web items. (Example: "Privacy", "Acknowledgments" and "About")
//            }
//        };
//
//        aboutConfig.analytics = new IAnalytic() {
//            @Override
//            public void logUiEvent(String s, String s1) {
//                // handle log events.
//            }
//
//            @Override
//            public void logException(Exception e, boolean b) {
//                // handle exception events.
//            }
//        };
//        // set it only if aboutConfig.analytics is defined.
//        aboutConfig.logUiEventName = "Log";
//
//        // Contact Support email details
//        aboutConfig.emailAddress = getString(R.string.emailAddress);
//        aboutConfig.emailSubject = getString(R.string.emailSubject);
//        aboutConfig.emailBody = "";
//
//
//        aboutConfig.author = "Tolstoy";
//        // if pages are stored locally, then you need to override aboutConfig.dialog to be able use custom WebView
////        aboutConfig.companyHtmlPath = COMPANY_HTML_PATH;
//        aboutConfig.privacyHtmlPath = "";
//        aboutConfig.acknowledgmentHtmlPath = "";
//
//        aboutConfig.shareMessage = getString(R.string.share_message) + "\n"+ getString(R.string.app_url);
//        aboutConfig.sharingTitle = getString(R.string.sharing_title);
//
//
//    }


        //-------------------------------
        AboutConfig aboutConfig = AboutConfig.getInstance();
        aboutConfig.appName = getString(R.string.app_name);
        aboutConfig.appIcon = R.mipmap.ic_launcher;
        //aboutConfig.version = "1.0.0";
        String version= "1.0.0";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
             version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        aboutConfig.version = version;
//        aboutConfig.author = "Tolstoy";
        aboutConfig.aboutLabelTitle = getString(R.string.About_App);//"About App";
        aboutConfig.packageName = getApplicationContext().getPackageName();
        aboutConfig.buildType =  AboutConfig.BuildType.GOOGLE ;

//        aboutConfig.facebookUserName = "FACEBOOK_USER_NAME";
//        aboutConfig.twitterUserName = "FACEBOOK_USER_NAME";
//        aboutConfig.webHomePage = "FACEBOOK_USER_NAME";

        aboutConfig.twitterUserName = getString(R.string.twitterUserName);
        aboutConfig.webHomePage = getString(R.string.webHomePage);

        // app publisher for "Try Other Apps" item
        aboutConfig.appPublisher = "Mazin Altokhais";

        // if pages are stored locally, then you need to override aboutConfig.dialog to be able use custom WebView
        aboutConfig.companyHtmlPath = "FACEBOOK_USER_NAME";
        aboutConfig.privacyHtmlPath = "FACEBOOK_USER_NAME";
        aboutConfig.acknowledgmentHtmlPath = "FACEBOOK_USER_NAME";

        aboutConfig.dialog = new IDialog() {
            @Override
            public void open(AppCompatActivity appCompatActivity, String url, String tag) {
                // handle custom implementations of WebView. It will be called when user click to web items. (Example: "Privacy", "Acknowledgments" and "About")
            }
        };

        aboutConfig.analytics = new IAnalytic() {
            @Override
            public void logUiEvent(String s, String s1) {
                // handle log events.
            }

            @Override
            public void logException(Exception e, boolean b) {
                // handle exception events.
            }
        };
        // set it only if aboutConfig.analytics is defined.
        aboutConfig.logUiEventName = "Log";

        // Contact Support email details
        aboutConfig.emailAddress = getString(R.string.emailAddress);
        aboutConfig.emailSubject = getString(R.string.emailSubject);
        aboutConfig.emailBody = "";
        aboutConfig.sharingTitle = getString(R.string.sharing_title);
        aboutConfig.shareMessage = getString(R.string.share_message) + "\n"+ getString(R.string.app_url);


    }
}
