package com.sterbsociety.orarisapienza.utils;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.activities.SettingsActivity;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

public class AppUtils {

    private static final int PICK_FROM_GALLERY = 1;
    public static final String APP_VERSION = "1.0";

    /**
     * This method closes the keyboard inside an Activity and from a specific view.
     */
    public static void hideKeyboard(Activity activity, View view) {

        if (view != null) {
            InputMethodManager imm;
            if ((imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE)) != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * From JavaDOC (https://developer.android.com/training/permissions/requesting):
     * This methods checks at runtime if the app has the permission to read from external storage,
     * and if it has not then the app has to explicitly ask the user for permission.
     */
    public static void pickImage(Activity activity) {

        try {
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity.startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is responsible for the appearance and behaviour of the spinners.
     */
    public static void customizeSpinner(Activity activity, Spinner mSpinner, String[] itemList) {

        final List<String> firstList = new ArrayList<>(Arrays.asList(itemList));
        final ArrayAdapter<String> firstSpinnerAdapter = new ArrayAdapter<String>(activity, R.layout.spinner_item, firstList) {

            @Override
            public boolean isEnabled(int position) {
                // Disable the first item from Spinner, the first item will be use for hint.
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        firstSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSpinner.setAdapter(firstSpinnerAdapter);
    }

    public static String hash(String mString) {

        String result;
        if ((result = getHash(mString, "MD5")) != null)
            return result;

        if ((result = getHash(mString, "SHA256")) != null)
            return result;

        if ((result = getHash(mString, "SHA1")) != null)
            return result;
        return mString;
    }


    private static String getHash(String mString, String hashType) {

        try {

            byte[] bytesOfMessage = mString.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance(hashType);
            byte[] resultByte = md.digest(bytesOfMessage);
            StringBuilder sb = new StringBuilder();
            for (byte aResultByte : resultByte) {
                sb.append(Integer.toHexString((aResultByte & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Method from Warpzit, DOCs here: https://stackoverflow.com/questions/9248930/android-animate-drop-down-up-view-proper/9290723#9290723
     * This method can be used to calculate the height and set it for views with wrap_content as height.
     * This should be done before ExpandCollapseAnimation is created.
     * @param activity
     * @param view
     */
    public static void setHeightForWrapContent(Activity activity, View view) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int screenWidth = metrics.widthPixels;

        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY);

        view.measure(widthMeasureSpec, heightMeasureSpec);
        int height = view.getMeasuredHeight();
        view.getLayoutParams().height = height;
    }

    private static Boolean animationsAllowed, updatesAllowed, secureExitAllowed, notificationAllowed, vibrationAllowed, currentTheme;
    private static String sCurrentRingtone, currentLanguage;

    public static void loadSettings(Activity activity) {

        // The code below ensures that the settings are properly initialized with their default values.
        PreferenceManager.setDefaultValues(activity, R.xml.preferences, false);

        // The code below retrieves an object representation (SharedPreferences) of settings, then retrieves the value associated to the key.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        animationsAllowed = sharedPref.getBoolean(SettingsActivity.KEY_PREF_ANIMATION_SWITCH, false);
        updatesAllowed = sharedPref.getBoolean(SettingsActivity.KEY_PREF_UPDATE_SWITCH, false);
        secureExitAllowed = sharedPref.getBoolean(SettingsActivity.KEY_PREF_EXIT_SWITCH, false);
        notificationAllowed = sharedPref.getBoolean(SettingsActivity.KEY_PREF_NOTIFICATION_SWITCH, false);
        vibrationAllowed = sharedPref.getBoolean(SettingsActivity.KEY_PREF_VIBRATION_SWITCH, false);
        currentTheme = sharedPref.getBoolean(SettingsActivity.KEY_PREF_THEME, false);
        sCurrentRingtone = sharedPref.getString(SettingsActivity.KEY_PREF_RINGTONE, "");
        currentLanguage = sharedPref.getString(SettingsActivity.KEY_PREF_LANGUAGE, "");

        if (currentLanguage.equals("")) {
            if (isUserLanguageSupported(activity))
                currentLanguage = Locale.getDefault().getLanguage();
            else
                currentLanguage = "en";
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SettingsActivity.KEY_PREF_LANGUAGE, currentLanguage);
            editor.apply();
        }
    }

    public static boolean areAnimationsAllowed() {
        return animationsAllowed;
    }

    public static boolean areUpdatesAllowed() {
        return updatesAllowed;
    }

    public static boolean isSecureExitAllowed() {
        return secureExitAllowed;
    }

    public static boolean areNotificationAllowed() {
        return notificationAllowed;
    }

    public static boolean isVibrationAllowed() {
        return vibrationAllowed;
    }

    private static boolean rebootScheduled = false;

    public static void scheduleReboot() {
        rebootScheduled = true;
    }

    public static boolean isRebootScheduled() {
        return rebootScheduled;
    }

    public static void reboot(Activity activity, Intent intent) {
        rebootScheduled = false;
        activity.finish();
        activity.startActivity(intent);
    }

    public static boolean isDarkTheme() {
        return currentTheme;
    }

    public static Ringtone getDefaultRingtone(Activity activity) {
        return RingtoneManager.getRingtone(activity, Settings.System.DEFAULT_RINGTONE_URI);
    }

    public static Ringtone getCurrentRingtone(Activity activity) {
        if (sCurrentRingtone.equals("")) {
            return getDefaultRingtone(activity);
        }
        return RingtoneManager.getRingtone(activity, Uri.parse(sCurrentRingtone));
    }

    public static String getCurrentRingtoneTitle(Activity activity) {
        return getCurrentRingtone(activity).getTitle(activity);
    }

    public static String getTitleOf(Uri ringtone, Activity activity) {
        return RingtoneManager.getRingtone(activity, ringtone).getTitle(activity);
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    private static boolean isUserLanguageSupported(Activity activity) {
        String[] supportedLanguages = activity.getResources().getStringArray(R.array.languagesValues);
        String usrLang = Locale.getDefault().getLanguage();
        for (String lang: supportedLanguages) {
            if (usrLang.equals(lang))
                return true;
        }
        return false;
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static String getStringByLocal(Activity context, int id, String locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(locale));
        return context.createConfigurationContext(configuration).getResources().getString(id);
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static String getStringByLocal(Activity context, int id) {
        return getStringByLocal(context, id, currentLanguage);
    }

    public static void setLocale(Activity activity) {
        Locale myLocale = new Locale(AppUtils.getCurrentLanguage());
        Resources res = activity.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public static void changeTheme() {
        currentTheme = !currentTheme;
    }

    public static void applyTheme(Activity activity) {
        if (isDarkTheme()) {
            activity.setTheme(R.style.AppTheme_Dark);
        }
    }

    public static void applyThemeNoActionBar(Activity activity) {
        if (AppUtils.isDarkTheme())
            activity.setTheme(R.style.AppTheme_Dark_NoActionBar);
    }

    public static int convertDpToPixel(int dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static void hideSystemUI(View decorView) {

        final View mDecorView = decorView;

        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                // Note that system bars will only be "visible" if none of the
                // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    // The system bars are visible.
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideSystemUI(mDecorView);
                        }
                    }, 2000);
                }
            }
        });
    }

    private static final int EARTH_RADIUS = 6371; // Approx Earth radius in KM

    public static double distance(double startLat, double startLong,
                                  double endLat, double endLong) {

        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // <-- d
    }

    private static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}