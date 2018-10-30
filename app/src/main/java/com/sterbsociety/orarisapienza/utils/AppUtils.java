package com.sterbsociety.orarisapienza.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.sterbsociety.orarisapienza.MailTask;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.activities.SettingsActivity;
import com.sterbsociety.orarisapienza.activities.StudyPlanActivity;
import com.sterbsociety.orarisapienza.adapters.SearchViewAdapter;
import com.sterbsociety.orarisapienza.models.Building;
import com.sterbsociety.orarisapienza.models.Classroom;
import com.sterbsociety.orarisapienza.models.Course;
import com.sterbsociety.orarisapienza.models.POJO;
import com.sterbsociety.orarisapienza.models.Root;
import com.sterbsociety.orarisapienza.models.StudyPlan;
import com.sterbsociety.orarisapienza.models.TimeLineModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

public class AppUtils {

    private static final int PICK_FROM_GALLERY = 1;
    public static final String APP_VERSION = "1.0";
    public static final String DEFAULT_KEY = "KEY";
    private static final String DATABASE_NAME = "courses.db";
    public static final int WEEK_LENGTH = 342;
    public static final int DAY_LENGTH = 57;
    private static final int TIME_INTERVAL = 15;

    /**
     * This method closes the keyboard inside an Activity and from a specific view.
     */
    public static void hideSoftKeyboard(Activity activity, View view) {

        if (view != null) {
            InputMethodManager imm;
            if ((imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE)) != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Resource and explanations found at:
     * https://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
     */
    @SuppressLint("ClickableViewAccessibility")
    public static void setupUIElements(Activity activity, View view) {

        final Activity mActivity = activity;
        final View mView = view;

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                AppUtils.hideSoftKeyboard(mActivity, mView);
                return false;
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUIElements(activity, innerView);
            }
        }
    }

    public static final String INTRO_SLIDER_APP = "IntroSliderApp";
    public static final String FIRST_TIME_FLAG = "FirstTimeStartFlag";


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
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {

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
                sb.append(Integer.toHexString((aResultByte & 0xFF) | 0x100), 1, 3);
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
     */
    public static void setHeightForWrapContent(Activity activity, View view) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int screenWidth = metrics.widthPixels;

        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY);

        view.measure(widthMeasureSpec, heightMeasureSpec);
        view.getLayoutParams().height = view.getMeasuredHeight() + 50;
    }

    private static final String KEY_PREF_DONATION_ACTIVE = "donation_pref";
    private static final String KEY_PREF_PRO_VERSION = "pro_ver";
    private static final String KEY_PREF_SHOWCASE_FILTER = "filter_showcase";

    public static boolean firstShowcaseFilter;

    private static boolean animationsAllowed, updatesAllowed, secureExitAllowed, notificationAllowed,
            vibrationAllowed, currentTheme, firstTimeStartUp, hasPurchasedNoAdsVersion;
    private static String sCurrentRingtone, currentLanguage, currentStudyPlan, currentDBVersion;
    private static Set<String> mFavouriteClassSet, mFavouriteCourseSet, mFavouriteBuildingSetCodes;

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
        hasPurchasedNoAdsVersion = sharedPref.getBoolean(KEY_PREF_PRO_VERSION, false);
        firstShowcaseFilter = sharedPref.getBoolean(KEY_PREF_SHOWCASE_FILTER, true);
        currentLanguage = sharedPref.getString(SettingsActivity.KEY_PREF_LANGUAGE, "");
        if (currentLanguage.equals("")) {
            if (isUserLanguageSupported(activity)) {
                currentLanguage = Locale.getDefault().getLanguage();
            } else {
                currentLanguage = "en";
            }
            sharedPref.edit().putString(SettingsActivity.KEY_PREF_LANGUAGE, currentLanguage).apply();
        }
        readGeneralPreferences(activity);
    }

    private static ArrayList<Classroom> classroomList, favouriteClassroomList;

    public static void setShowcaseFilter(Activity activity) {
        firstShowcaseFilter = false;
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(KEY_PREF_SHOWCASE_FILTER, false).apply();
    }

    public static ArrayList<Classroom> getFavouriteClassroomList() {
        return favouriteClassroomList;
    }

    public static void commuteExitPreference() {
        secureExitAllowed = !secureExitAllowed;
    }

    public static void commuteUpdatePreference() {
        updatesAllowed = !updatesAllowed;
    }

    public static void commuteAnimationPreference() {
        animationsAllowed = !animationsAllowed;
    }

    public static void commuteNotificationPreference() {
        notificationAllowed = !notificationAllowed;
    }

    public static void commuteVibrationPreference() {
        vibrationAllowed = !vibrationAllowed;
    }

    public static ArrayList<Classroom> getClassroomList() {
        return classroomList;
    }

    private static final String GENERAL_PREF = "com.sterbsociety.orarisapienza.general";
    public static boolean isDonationActive = false;


    /**
     * @param activity reference to the activity
     *                 Details about the weird behaviour of SharedPreferences with StringSet can be find here:
     *                 https://stackoverflow.com/questions/14034803/misbehavior-when-trying-to-store-a-string-set-using-sharedpreferences
     *                 and here
     *                 https://developer.android.com/reference/android/content/SharedPreferences
     */
    private static void readGeneralPreferences(Activity activity) {

        // Here we create different HashSets from the those which are stored in SharedPreferences.
        SharedPreferences sharedPreferences = activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE);
        mFavouriteClassSet = new HashSet<>(sharedPreferences.getStringSet(KEY_PREF_CLASS_FAVOURITES, new HashSet<>()));
        firstTimeStartUp = sharedPreferences.getBoolean(FIRST_TIME_FLAG, true);
        currentDBVersion = sharedPreferences.getString(DB_KEY, null);
        isDonationActive = sharedPreferences.getBoolean(KEY_PREF_DONATION_ACTIVE, false);

        mFavouriteBuildingSetCodes = new HashSet<>(sharedPreferences.getStringSet(KEY_PREF_BUILDING_FAVOURITES, new HashSet<>()));
        mFavouriteCourseSet = new HashSet<>(sharedPreferences.getStringSet(KEY_PREF_COURSE_FAVOURITES, new HashSet<>()));

        currentStudyPlan = sharedPreferences.getString(KEY_PREF_STUDY_PLAN, null);
        checkIfIsCurrentStudyPlanExpired(activity);
    }

    private static void checkStudyPlanIntegrity(Activity activity) {

        final StudyPlan studyPlan = getStudyPlan();

        if (studyPlan == null) {
            return;
        }
        try {
            for (TimeLineModel model : studyPlan.getDataList()) {
                int buildingIndex = model.getClassroom().getBuildingIndex();
                if (!buildingList.get(buildingIndex).code.equals(model.getClassroom().getBuildingCode())) {
                    clearCachedStudyPlan(activity);
                    StyleableToast.makeText(activity, getStringByLocal(activity, R.string.study_plan_corrupt),
                            Toast.LENGTH_LONG, R.style.errorToast).show();
                    return;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            sendSilentReport(activity, 333, ex, AppUtils.class.getSimpleName());
            clearCachedStudyPlan(activity);
        }
    }

    private static final String KEY_PREF_CLASS_FAVOURITES = "fav_class";
    private static final String KEY_PREF_BUILDING_FAVOURITES = "fav_building";

    public static void addBuildingToFavourites(Activity activity, Building building, int index) {

        final String buildingCode = building.code;

        // todo may be useful to limit user history max to 10 or less
        if (!isFavouriteBuilding(buildingCode)) {
            // If the Building is not yet in favourites, then it will be added. Since favourites are stored in a set
            // we can't keep track of order in there, so the code is formed by POSITION + BuildingCode.
            mFavouriteBuildingSetCodes.add(mFavouriteBuildingSetCodes.size() + FAV_SEPARATOR + buildingCode);
            mFavouriteBuildingList.add(0, building);
            activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().putStringSet(KEY_PREF_BUILDING_FAVOURITES, mFavouriteBuildingSetCodes).apply();
            buildingList.remove(building);
            buildingList.add(0, building);
        } else {
            // Just the history position needs to be fixed, simple thing -> hard to write.
            int buildingIndex = -1;
            // We search through all the favourites the index of the building to 'reorder'
            for (String currentBuildingCode : mFavouriteBuildingSetCodes) {
                String[] parts = currentBuildingCode.split(FAV_SEPARATOR);
                int tmpIndex = Integer.parseInt(parts[0]);
                if (parts[1].equals(buildingCode)) {
                    buildingIndex = tmpIndex;
                }
            }

            if (buildingIndex != -1) {
                // If the index is != -1 then the hashMap is reordered with the previous logic of POSITION in name
                // Items before the selected one keep their POSITION, those after scale 1 position up, and the current
                // item is set as the last (first) item in the order.
                Set<String> tmpHashSet = new HashSet<>(mFavouriteBuildingSetCodes);
                mFavouriteBuildingSetCodes.clear();
                for (String currentBuildingCode : tmpHashSet) {
                    String[] parts = currentBuildingCode.split(FAV_SEPARATOR);
                    final int currentIndex = Integer.parseInt(parts[0]);
                    if (currentIndex < buildingIndex) {
                        mFavouriteBuildingSetCodes.add(currentBuildingCode);
                    } else if (currentIndex > buildingIndex) {
                        mFavouriteBuildingSetCodes.add(currentIndex - 1 + FAV_SEPARATOR + parts[1]);
                    } else {
                        mFavouriteBuildingSetCodes.add(tmpHashSet.size() - 1 + FAV_SEPARATOR + parts[1]);
                    }
                }
                activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().putStringSet(KEY_PREF_BUILDING_FAVOURITES, mFavouriteBuildingSetCodes).apply();
            }

            mFavouriteBuildingList.remove(building);
            mFavouriteBuildingList.add(0, building);

            buildingList.remove(index);
            buildingList.add(0, building);
        }
    }

    public static void addClassroomToFavourites(Activity activity, Classroom classroom) {

        mFavouriteClassSet.add(classroom.getBuildingCode() + "-" + classroom.getCode());
        favouriteClassroomList.add(classroom);
        activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().putStringSet(KEY_PREF_CLASS_FAVOURITES, mFavouriteClassSet).apply();
    }

    public static void removeClassFromFavourites(Activity activity, Classroom classroom) {

        mFavouriteClassSet.remove(classroom.getBuildingCode() + "-" + classroom.getCode());
        favouriteClassroomList.remove(classroom);
        activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().putStringSet(KEY_PREF_CLASS_FAVOURITES, mFavouriteClassSet).apply();
    }

    public static Set<String> getFavouriteClassSet() {
        return mFavouriteClassSet;
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

    private static Ringtone getDefaultRingtone(Activity activity) {
        return RingtoneManager.getRingtone(activity, Settings.System.DEFAULT_RINGTONE_URI);
    }

    private static Ringtone getCurrentRingtone(Activity activity) {
        if (sCurrentRingtone.equals("")) {
            return getDefaultRingtone(activity);
        }
        return RingtoneManager.getRingtone(activity, Uri.parse(sCurrentRingtone));
    }

    public static void setCurrentRingtone(String ringtone) {
        sCurrentRingtone = ringtone;
    }

    public static String getCurrentRingtoneTitle(Activity activity) {
        return getCurrentRingtone(activity).getTitle(activity);
    }

    public static String getTitleOf(Uri ringtone, Activity activity) {
        return RingtoneManager.getRingtone(activity, ringtone).getTitle(activity);
    }

    public static void updateCurrentLanguage(String language) {
        currentLanguage = language;
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    private static boolean isUserLanguageSupported(Activity activity) {
        String[] supportedLanguages = activity.getResources().getStringArray(R.array.languagesValues);
        String usrLang = Locale.getDefault().getLanguage();
        for (String lang : supportedLanguages) {
            if (usrLang.equals(lang))
                return true;
        }
        return false;
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static String getStringByLocal(Context context, int id, String locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(locale));
        return context.createConfigurationContext(configuration).getResources().getString(id);
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static String getStringByLocal(Context context, int id) {
        return getStringByLocal(context, id, currentLanguage);
    }

    public static void setLocale(Activity activity) {
        Locale myLocale = new Locale(getCurrentLanguage());
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
        if (isDarkTheme())
            activity.setTheme(R.style.AppTheme_Dark_NoActionBar);
    }

    public static int convertDpToPixel(int dp, Context context) {
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

        mDecorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                // The system bars are visible.
                new Handler().postDelayed(() -> hideSystemUI(mDecorView), 2000);
            }
        });
    }

    private static final int EARTH_RADIUS = 6371; // Approx Earth radius in KM

    public static final int GPS_ACCESS = 42;

    public static void askForGPSPermission(Activity activity) {

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION
                    , android.Manifest.permission.ACCESS_FINE_LOCATION}, GPS_ACCESS);
        }
    }

    public static final int GPS_RESULT = 77;

    public static boolean isGPSEnabled(Activity activity) {
        return isGPSEnabled(activity, null);
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isGPSEnabled(Activity activity, LocationManager locationManager) {
        try {
            if (locationManager == null)
                return ((LocationManager) activity.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
            else
                return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            return false;
        }
    }

    private static final String CRASH_REPORT_TITLE = "Crash Report";
    private static final String CRASH_REPORT_BODY_HEADER = "This is an automatic message. Our App crashed due to some reason.\nPlease look at line: ";
    private static final String CRASH_REPORT_BODY_WHERE = " in ";
    private static final String CRASH_REPORT_ERROR_STACKTRACE = ".\n\nError stacktrace:\n";

    private static String getStackTraceAsString(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    public static void sendSilentReport(Activity activity, int errorLine, String error, String className) {
        new MailTask(activity, null, null, null, null).
                execute(CRASH_REPORT_TITLE                          // Crash report title
                        , CRASH_REPORT_BODY_HEADER                  // The header
                                + errorLine                                 // Line of the begin of try-catch block
                                + CRASH_REPORT_BODY_WHERE                   // Where the error is occurred
                                + CRASH_REPORT_ERROR_STACKTRACE             // Default stacktrace message
                                + error
                                + className                                 // Where the error occurred.
                        , null);                                    // Not required param.
    }

    public static void sendSilentReport(Activity activity, int errorLine, Exception ex, String className) {
        sendSilentReport(activity, errorLine, getStackTraceAsString(ex), className);
    }

    private static int SELECTED_CLASS_BTN_INDEX = 2;

    public static int getSelectedClassBtnIndex() {
        return SELECTED_CLASS_BTN_INDEX;
    }

    public static boolean[] SELECTED_DAY_BTN_INDEX;
    private static int CURRENT_DAY = -1;

    private static void initDate() {

        SELECTED_DAY_BTN_INDEX = new boolean[6];
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        int index;

        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY || day == Calendar.MONDAY)
            index = 0;
        else
            index = day - 2;
        SELECTED_DAY_BTN_INDEX[index] = true;
        CURRENT_DAY = index;
    }

    public static int getCurrentWeekDayIndex() {
        if (CURRENT_DAY == -1) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            if (day == Calendar.SUNDAY || day == Calendar.MONDAY)
                CURRENT_DAY = 0;
            else
                CURRENT_DAY = day - 2;
        }
        return CURRENT_DAY;
    }

    private static int getCurrentDayIndex() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            case Calendar.SATURDAY:
                return 5;
            default:
                return 6;
        }
    }

    public static boolean[] getSelectedDayBtnIndex() {
        if (SELECTED_DAY_BTN_INDEX == null)
            initDate();
        return SELECTED_DAY_BTN_INDEX;
    }

    private final static int MIN_HOUR = 8;
    private final static int MAX_HOUR = 22;

    public static int CACHED_MIN_HOUR = 10;
    public static int CACHED_MAX_HOUR = 14;

    // This value is just a custom bizarre value that means 'no distance limit' (half of 42 xD).
    private static int DISTANCE_FROM_CURRENT_POSITION = 21;

    public static int getDistanceFromCurrentPosition() {
        return DISTANCE_FROM_CURRENT_POSITION;
    }

    public static final String KEY_FILTER_DAY = "key_day";
    public static final String KEY_FILTER_AVAILABILITY = "key_availability";
    public static final String KEY_FILTER_MIN_HOUR = "key_min_hour";
    public static final String KEY_FILTER_MAX_HOUR = "key_max_hour";
    public static final String KEY_FILTER_DISTANCE = "key_distance";

    public final static int FILTER_ACTIVITY = 111;

    public static void initClassroomFilters() {
        SELECTED_DAY_BTN_INDEX = new boolean[6];
        SELECTED_DAY_BTN_INDEX[getCurrentWeekDayIndex()] = true;
        SELECTED_CLASS_BTN_INDEX = 2;
        CACHED_MIN_HOUR = 10;
        CACHED_MAX_HOUR = 14;
        DISTANCE_FROM_CURRENT_POSITION = 21;
    }

    public static void updateCachedFilters(Intent data) {

        SELECTED_DAY_BTN_INDEX = data.getBooleanArrayExtra(KEY_FILTER_DAY);
        SELECTED_CLASS_BTN_INDEX = data.getIntExtra(KEY_FILTER_AVAILABILITY, SELECTED_CLASS_BTN_INDEX);
        CACHED_MIN_HOUR = data.getIntExtra(KEY_FILTER_MIN_HOUR, MIN_HOUR);
        CACHED_MAX_HOUR = data.getIntExtra(KEY_FILTER_MAX_HOUR, MAX_HOUR);
        DISTANCE_FROM_CURRENT_POSITION = data.getIntExtra(KEY_FILTER_DISTANCE, DISTANCE_FROM_CURRENT_POSITION);
    }

    private static ArrayList<Course> courseList, mFavouriteCourseList = new ArrayList<>();

    public static List<Course> getCoursesList() {
        return courseList;
    }

    public static List<Course> getFavouriteCourses() {
        return new ArrayList<>(mFavouriteCourseList);
    }

    public static boolean hasAlreadyBeenSearchedByUser(String courseCode) {
        for (String favouriteCourseId : mFavouriteCourseSet) {
            if (favouriteCourseId.split(FAV_SEPARATOR)[1].equals(courseCode))
                return true;
        }
        return false;
    }

    /**
     * It checks if a building is already put in favourites,
     * in accordance with the logic described at 282.
     */
    public static boolean isFavouriteBuilding(String buildingCode) {
        for (String favouriteBuildingCode : mFavouriteBuildingSetCodes) {
            if (favouriteBuildingCode.split(FAV_SEPARATOR)[1].equals(buildingCode))
                return true;
        }
        return false;
    }

    private static final String KEY_PREF_COURSE_FAVOURITES = "fav_course";

    private static final String KEY_PREF_STUDY_PLAN = "study_plan";

    public static void addCourseToFavourites(Activity activity, Course course, SearchViewAdapter searchViewAdapter, int index) {

        final String courseCode = course.getFullName();

        // todo may be useful to limit user history max to 10 or less
        if (!hasAlreadyBeenSearchedByUser(courseCode)) {
            // If the Course has never been searched before, then it will be added. Since favourites are stored in a set
            // we can't keep track of order in there, so the code is formed by POSITION + CourseID.
            mFavouriteCourseSet.add(mFavouriteCourseSet.size() + FAV_SEPARATOR + courseCode);
            mFavouriteCourseList.add(0, course);
            activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().putStringSet(KEY_PREF_COURSE_FAVOURITES, mFavouriteCourseSet).apply();
            courseList.remove(course);
            courseList.add(0, course);
            searchViewAdapter.updateSuggestions(courseList);
        } else {
            // Just the history position needs to be fixed, simple thing -> hard to write.
            int courseIndex = -1;
            // We search through all the favourites the index of the course to 'reorder'
            for (String currentCourseCode : mFavouriteCourseSet) {
                String[] parts = currentCourseCode.split(FAV_SEPARATOR);
                int tmpIndex = Integer.parseInt(parts[0]);
                if (parts[1].equals(courseCode)) {
                    courseIndex = tmpIndex;
                }
            }

            if (courseIndex != -1) {
                // If the index is != -1 then the hashMap is reordered with the previous logic of POSITION in name
                // Items before the selected one keep their POSITION, those after scale 1 position up, and the current
                // item is set as the last (first) item in the order.
                Set<String> tmpHashSet = new HashSet<>(mFavouriteCourseSet);
                mFavouriteCourseSet.clear();
                for (String currentCourseCode : tmpHashSet) {
                    String[] parts = currentCourseCode.split(FAV_SEPARATOR);
                    final int currentIndex = Integer.parseInt(parts[0]);
                    if (currentIndex < courseIndex) {
                        mFavouriteCourseSet.add(currentCourseCode);
                    } else if (currentIndex > courseIndex) {
                        mFavouriteCourseSet.add(currentIndex - 1 + FAV_SEPARATOR + parts[1]);
                    } else {
                        mFavouriteCourseSet.add(tmpHashSet.size() - 1 + FAV_SEPARATOR + parts[1]);
                    }
                }
                activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().putStringSet(KEY_PREF_COURSE_FAVOURITES, mFavouriteCourseSet).apply();
            }
            mFavouriteCourseList.remove(course);
            mFavouriteCourseList.add(0, course);

            courseList.remove(index);
            courseList.add(0, course);
            searchViewAdapter.updateSuggestions(courseList);
        }
    }

    private static int TOOLBAR_COLOR = -1;

    public static int getToolbarColor() {
        return TOOLBAR_COLOR;
    }

    public static void setToolbarColor(Drawable background) {
        TOOLBAR_COLOR = ((ColorDrawable) background).getColor();
    }

    /**
     * @param activity    is the current activity where the Ad will be displayed
     * @param adContainer is the wrapper where to put the Ad
     * @param unitId      is the AdUnitId to earn money from advertisement
     */
    public static void setAdLayout(Activity activity, LinearLayout adContainer, String unitId) {

        if (!NetworkStatus.getInstance().isOnline(activity) || hasPurchasedNoAdsVersion) {
            // Here we could put a message to promote our app.
            // The line below removes the 'inactive' ad.
            ((ViewGroup) adContainer.getParent()).removeView(adContainer);
        } else {
            // AdMob App ID: ca-app-pub-9817701892167034~2496155654
            AdView mAdView = new AdView(activity.getApplicationContext());
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(unitId);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            adContainer.addView(mAdView, params);
        }
    }

    public static Boolean isFirstTimeStartApp() {
        return firstTimeStartUp;
    }

    public static void setFirstTimeStartApp(Activity activity) {
        activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().putBoolean(FIRST_TIME_FLAG, false).apply();
    }

    private static ArrayList<Building> buildingList, mFavouriteBuildingList;

    public static ArrayList<Building> getBuildingList() {
        return buildingList;
    }

    public static ArrayList<Building> getFavouriteBuildingList() {
        return new ArrayList<>(mFavouriteBuildingList);
    }

    /**
     * @param classroom is the classroom located in the building
     * @return the building that houses the classroom
     * This is a safe method since the classroom.getBuildingIndex is created at runtime,
     * so we use it to reference to its main Building object.
     */
    public static Building getRealBuilding(Classroom classroom) {
        return buildingList.get(classroom.getBuildingIndex());
    }

    public static void saveStudyPlan(StudyPlanActivity activity, @NonNull String studyPlan) {
        checkIfIsCurrentStudyPlanExpired(activity);
        if (currentStudyPlan == null) {
            activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().putString(KEY_PREF_STUDY_PLAN, studyPlan).apply();
            currentStudyPlan = studyPlan;
            activity.goBackToMainActivity(true);
        } else {

            new AlertDialog.Builder(activity)
                    .setMessage(AppUtils.getStringByLocal(activity, R.string.confirm_plan_overriding))
                    .setCancelable(false)
                    .setPositiveButton(AppUtils.getStringByLocal(activity, R.string.ok), (dialog, id) -> {
                        activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().putString(KEY_PREF_STUDY_PLAN, studyPlan).apply();
                        currentStudyPlan = studyPlan;
                        activity.goBackToMainActivity(true);
                    })
                    .setNegativeButton(AppUtils.getStringByLocal(activity, R.string._no), null)
                    .show();
            activity.goBackToMainActivity(false);
        }
    }

    private static void checkIfIsCurrentStudyPlanExpired(Activity activity) {
        if (currentStudyPlan == null) {
            return;
        }
        try {
            final Date expirationDate = fullDateFormat.parse(new Gson().fromJson(currentStudyPlan, StudyPlan.class).getEndRequestDate());
            if (expirationDate.before(new Date())) {
                clearCachedStudyPlan(activity);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void clearCachedStudyPlan(Activity activity) {
        activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().remove(KEY_PREF_STUDY_PLAN).apply();
        currentStudyPlan = null;
    }

    public static boolean isThereAnActiveStudyPlan() {
        return currentStudyPlan != null;
    }

    public static StudyPlan getStudyPlan() {
        return new Gson().fromJson(currentStudyPlan, StudyPlan.class);
    }

    public static final int STUDY_PLAN = 79;

    private static final SimpleDateFormat fullDateFormat = new SimpleDateFormat("E, d MMM, yyyy HH:mm", Locale.ENGLISH);

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    private static final SimpleDateFormat mediumDateFormat = new SimpleDateFormat("E HH:mm", Locale.ENGLISH);

    public static SimpleDateFormat getFullDateFormatter() {
        return fullDateFormat;
    }

    public static SimpleDateFormat getMediumDateFormat() {
        return mediumDateFormat;
    }

    public static String getSimpleDate(@NonNull String fullDate) {
        try {
            return simpleDateFormat.format(fullDateFormat.parse(fullDate));
        } catch (ParseException e) {
            return fullDate.substring(fullDate.length() - 5, fullDate.length());
        }
    }

    public static boolean isCurrentDatabaseOutDated(DataSnapshot dataSnapshot) {
        try {
            int onlineVersion = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue(String.class)).replaceAll("\\.", ""));
            return Integer.parseInt(currentDBVersion.replaceAll("\\.", "")) < onlineVersion;
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
    }

    @Nullable
    private static Root rawDB = null;

    public static void parseRawDB(Activity activity) {
        try {
            AssetManager assetManager = activity.getAssets();
            // We assume this array to have at least one element.
            InputStream inputStream = assetManager.open("root.json");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            rawDB = new Gson().fromJson(sb.toString(), Root.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isOfflineDBOutdated(Activity activity, DataSnapshot dataSnapshot) {
        if (rawDB == null) {
            return true;
        }
        int onlineVersion = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue(String.class)).replaceAll("\\.", ""));
        return Integer.parseInt(rawDB.version.replaceAll("\\.", "")) < onlineVersion;
    }

    private static boolean isDBAvailable;

    public static boolean isDBAvailable(Activity activity) {
        if (isDBAvailable) {
            return true;
        }
        if (currentDBVersion != null) {
            return true;
        }
        return (currentDBVersion = activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).getString(DB_KEY, null)) != null;
    }

    @Nullable
    public static String getCurrentDBVersion() {
        return currentDBVersion;
    }

    private static final String DB_KEY = "finalDB";

    public static void saveDatabase(Activity activity, DataSnapshot dataSnapshot) {
        try {
            final POJO pojo = dataSnapshot.child(DB_KEY).getValue(POJO.class);
            FileOutputStream outputStream;
            outputStream = activity.openFileOutput(DATABASE_NAME, Context.MODE_PRIVATE);
            outputStream.write(new Gson().toJson(pojo).getBytes());
            outputStream.close();
            activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().putString(DB_KEY, dataSnapshot.child(KEY_VERSION).getValue(String.class)).apply();
            isDBAvailable = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void moveDatabaseFromRawToInternalStorage(Activity activity) {
        try {
            final FileOutputStream outputStream = activity.openFileOutput(DATABASE_NAME, Context.MODE_PRIVATE);
            outputStream.write(new Gson().toJson(Objects.requireNonNull(rawDB).finalDB).getBytes());
            outputStream.close();
            activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().putString(DB_KEY, rawDB.version).apply();
            isDBAvailable = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static final String KEY_VERSION = "version";
    public static HashMap<String, HashMap<String, String>> SPECIAL_COURSES;
    public static volatile boolean isDBFullyLoaded = false;

    public static void parseDatabase(Activity activity) {
        try {
            File f = new File(activity.getFilesDir().getAbsolutePath() + "/" + DATABASE_NAME);
            FileInputStream fis = activity.openFileInput(DATABASE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            final POJO database = new Gson().fromJson(sb.toString(), POJO.class);
            SPECIAL_COURSES = new HashMap<>(database.specialCourses);
            parseData(activity, database);
            // We create another HashMap to allow JVM to garbageCollect the database instance
            MATRIX = new HashMap<>(database.matrix);
            isDBFullyLoaded = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            isDBFullyLoaded = false;
        }
    }

    // This is the mega matrix which holds info inside each cell
    public static HashMap<String, List<Integer>> MATRIX = new HashMap<>();

    public static HashMap<String, HashMap<String, List<Integer>>> TIMETABLES = new HashMap<>();

    public static List<String> LESSON_LIST = new ArrayList<>();
    private static final String FAV_SEPARATOR = "%";

    /**
     * @param arrayList is the list of buildings from DB
     */
    private static void parseBuildingList(ArrayList<Building> arrayList) {

        final ArrayDeque<Building> resultList = new ArrayDeque<>(arrayList);

        // We pass from HashSet to an ordered ArrayList by reversing the list, look at the Comparator.
        ArrayList<String> dirtySortedFavouriteCodes = new ArrayList<>(mFavouriteBuildingSetCodes);
        Collections.sort(dirtySortedFavouriteCodes, (c1, c2) -> Integer.parseInt(c2.split(FAV_SEPARATOR)[0]) - Integer.parseInt(c1.split(FAV_SEPARATOR)[0]));

        // This is responsible for cleaning buildingCodes from their dirty indexes.
        ArrayList<String> cleanSortedFavouriteCodes = new ArrayList<>();
        for (String dirtyCode : dirtySortedFavouriteCodes) {
            cleanSortedFavouriteCodes.add(dirtyCode.split(FAV_SEPARATOR)[1]);
        }

        // Inside here we put each building in accordance to their code's position.
        Building[] tmpFavourites = new Building[mFavouriteBuildingSetCodes.size()];

        // Note that Iterator.remove() is the only safe way to modify a collection during iteration
        // DOCS at: http://docs.oracle.com/javase/tutorial/collections/interfaces/collection.html
        final Iterator<Building> iterator = resultList.iterator();
        while (iterator.hasNext()) {
            Building building = iterator.next();
            if (isFavouriteBuilding(building.code)) {
                tmpFavourites[cleanSortedFavouriteCodes.indexOf(building.code)] = building;
                iterator.remove();
            }
        }

        mFavouriteBuildingList = new ArrayList<>(Arrays.asList(tmpFavourites));
        buildingList = new ArrayList<>(mFavouriteBuildingList);
        buildingList.addAll(resultList);
    }

    private static ArrayList<Classroom> realClassroomList;

    public static ArrayList<Classroom> getRealClassroomList() {
        if (realClassroomList == null) {
            realClassroomList = new ArrayList<>();
            for (final Classroom classroom : classroomList) {
                if (MATRIX.containsKey(classroom.getFullCode())) {
                    realClassroomList.add(classroom);
                }
            }
        }
        return realClassroomList;
    }

    /**
     * This method re-elaborates classrooms in order to provide each one of them each info
     * and a light-reference to their relative building.
     */
    private static void parseClassroomList() {
        classroomList = new ArrayList<>();
        for (int i = 0; i < buildingList.size(); i++) {
            final Building building = buildingList.get(i);
            for (int j = 0; j < building.aule.size(); j++) {
                final Classroom POJOClassroom = building.aule.get(j);
                classroomList.add(new Classroom(POJOClassroom.getName(), POJOClassroom.getCode(), POJOClassroom.getSits(), building, i));
            }
        }
        // Down here we parse class favourites
        favouriteClassroomList = new ArrayList<>();
        for (Classroom classroom : classroomList) {
            if (mFavouriteClassSet.contains(classroom.getBuildingCode() + "-" + classroom.getCode())) {
                favouriteClassroomList.add(classroom);
            }
        }
    }

    /**
     * @param map is the map of courses ("courseName":[lesson0, lesson1, lesson2, lesson3, ...])
     */
    private static void parseCourseList(HashMap<String, HashMap<String, List<Integer>>> map) {

        final ArrayDeque<Course> resultList = new ArrayDeque<>();
        for (String courseKey : map.keySet()) {
            final String[] parts = courseKey.split("_");
            resultList.add(new Course(parts[0], parts[1], courseKey));
        }
        for (String courseKey : SPECIAL_COURSES.keySet()) {
            final String[] parts = courseKey.split("_");
            resultList.add(new Course(parts[0], parts[1], courseKey));
        }

        // We pass from HashSet to an ordered ArrayList by reversing the list, look at the Comparator.
        ArrayList<String> dirtySortedFavouriteCourses = new ArrayList<>(mFavouriteCourseSet);
        Collections.sort(dirtySortedFavouriteCourses, (c1, c2) -> Integer.parseInt(c2.split(FAV_SEPARATOR)[0]) - Integer.parseInt(c1.split(FAV_SEPARATOR)[0]));

        // This is responsible for cleaning buildingCodes from their dirty indexes.
        ArrayList<String> cleanSortedFavouriteCodes = new ArrayList<>();
        for (String dirtyCode : dirtySortedFavouriteCourses) {
            cleanSortedFavouriteCodes.add(dirtyCode.split(FAV_SEPARATOR)[1]);
        }

        // Inside here we put each building in accordance to their code's position.
        Course[] tmpFavourites = new Course[mFavouriteCourseSet.size()];

        // Note that Iterator.remove() is the only safe way to modify a collection during iteration
        // DOCS at: http://docs.oracle.com/javase/tutorial/collections/interfaces/collection.html
        final Iterator<Course> iterator = resultList.iterator();
        while (iterator.hasNext()) {
            final Course course = iterator.next();
            if (hasAlreadyBeenSearchedByUser(course.getFullName())) {
                tmpFavourites[cleanSortedFavouriteCodes.indexOf(course.getFullName())] = course;
                iterator.remove();
            }
        }

        mFavouriteCourseList = new ArrayList<>(Arrays.asList(tmpFavourites));
        courseList = new ArrayList<>(mFavouriteCourseList);
        courseList.addAll(resultList);
        TIMETABLES = new HashMap<>(map);
    }

    private static void parseData(Activity activity, POJO database) {

        parseBuildingList(database.smap.getBuildings());

        // After parsing buildings, we parse classrooms
        parseClassroomList();
        parseCourseList(database.timeTables);

        // After we have parsed everything we check if the Study Plan is still valid.
        checkStudyPlanIntegrity(activity);

        LESSON_LIST = new ArrayList<>(database.alist);
    }

    public static String getDayByIndex(int index) {
        int i = index / DAY_LENGTH;
        switch (i) {
            case 1:
                return "tue";
            case 2:
                return "wed";
            case 3:
                return "thu";
            case 4:
                return "fri";
            case 5:
                return "sat";
            default:
                return "mon";
        }
    }

    public static String getHourByIndex(int index) {
        // 465 is second offset
        return String.format(Locale.getDefault(), "%02d", (465 + index % DAY_LENGTH * TIME_INTERVAL) / 60)
                + ":" +
                String.format(Locale.getDefault(), "%02d", (465 + index % DAY_LENGTH * TIME_INTERVAL) % 60);
    }


    public static boolean isTableVisible;

    public static String getLiteralYearByNumber(Activity activity, String courseYear) {
        final int yearIndex = Character.getNumericValue(courseYear.charAt(0));
        final String courseType = courseYear.substring(1);
        int yearResource, courseResource;
        switch (yearIndex) {
            case 2:
                yearResource = R.string.second;
                break;
            case 3:
                yearResource = R.string.third;
                break;
            case 4:
                yearResource = R.string.fourth;
                break;
            case 5:
                yearResource = R.string.fifth;
                break;
            case 6:
                yearResource = R.string.sixth;
                break;
            case 7:
                yearResource = R.string.seventh;
                break;
            case 8:
                yearResource = R.string.eighth;
                break;
            case 9:
                yearResource = R.string.ninth;
                break;
            default:
                yearResource = R.string.first;
        }

        if (courseType.toLowerCase().equals("m")) {
            courseResource = R.string.master_degree;
        } else {
            // Else it's a three-year degree
            courseResource = R.string.first_level_degree;
        }
        return getStringByLocal(activity, yearResource) + getStringByLocal(activity, R.string.year)
                + getStringByLocal(activity, R.string.of) + getStringByLocal(activity, courseResource) + ". ";
    }

    public static String getLiteralNumber(Activity activity, String number) {
        switch (Integer.parseInt(number)) {
            case 2:
                return activity.getString(R.string.second);
            case 3:
                return activity.getString(R.string.third);
            case 4:
                return activity.getString(R.string.fourth);
            case 5:
                return activity.getString(R.string.fifth);
            case 6:
                return activity.getString(R.string.sixth);
            case 7:
                return activity.getString(R.string.seventh);
            case 8:
                return activity.getString(R.string.eighth);
            case 9:
                return activity.getString(R.string.ninth);
            default:
                return activity.getString(R.string.first);
        }
    }

    public static String hourToString(int hour) {
        if (String.valueOf(hour).length() == 1) {
            return "0" + hour + ":00";
        }
        return hour + ":00";
    }

    public static int getCurrentTimeToInt() {
        String h = simpleDateFormat.format(new Date());
        int day = getCurrentDayIndex();
        return timeToInt(h, day);
    }

    public static int timeToInt(String h, int day) {
        int dayVar = day * DAY_LENGTH;//day index
        int var = Math.min(DAY_LENGTH, Integer.parseInt(h.split(":")[0]) - 8) * 4 + (Integer.parseInt(h.split(":")[1]) / TIME_INTERVAL);    //hour parsing
        if (var < 0) {
            // 286 is 08:00 of Saturday
            return Math.min(dayVar, 286) % 286;
            //if hour is 00<h<07 than return minimum  between dayVar and 628 (7:05 of friday,the maximum that day can assume in our week) %628 to get 0 if dayVar is superior(saturday,sunday)
        }
        if (dayVar + var < WEEK_LENGTH) {
            return dayVar + var;//a right value, day+hour index in our array
        }
        return 0;   //index out of bound
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6372.8;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c * 1000;
    }

    private static Date minHour, maxHour;

    @NonNull
    public static Date getMinHour() {
        if (minHour == null) {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, MIN_HOUR);
            calendar.set(Calendar.MINUTE, 0);
            minHour = calendar.getTime();
        }
        return minHour;
    }

    @NonNull
    public static Date getMaxHour() {
        if (maxHour == null) {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, MAX_HOUR);
            calendar.set(Calendar.MINUTE, 0);
            maxHour = calendar.getTime();
        }
        return maxHour;
    }

    public static int dayToInt(String day) {
        switch (day.toLowerCase()) {
            case ("mon"):
                return 0;
            case ("tue"):
                return 1;
            case ("wed"):
                return 2;
            case ("thu"):
                return 3;
            case ("fri"):
                return 4;
            case ("sat"):
                return 5;
            case ("sun"):
                return 6;
        }
        // Return 0 and not -1 to avoid other errors, anyway we should be safe.
        return 0;
    }

    public static Classroom getClassroom(String code) {
        for (Classroom classroom : classroomList) {
            if (classroom.getFullCode().equals(code)) {
                return classroom;
            }
        }
        return null;
    }

    @NonNull
    public static Building getNearestBuilding(double latitude, double longitude) {
        double distance, minimum = 10000.0;
        Building nearestBuilding = null;
        for (Building building : buildingList) {
            distance = haversine(latitude, longitude, building.getLat(), building.getLong());
            if (distance < minimum | nearestBuilding == null) {
                minimum = distance;
                nearestBuilding = building;
            }
        }
        // getNearestBuilding may be null only if our database is empty <---> buildingList is empty.
        return nearestBuilding;
    }

    public static Date[] getBestDates() {
        final Calendar calendar = Calendar.getInstance();
        final Date now = new Date();
        final Date maxHour = getMaxHour();
        final Date minHour = getMinHour();
        final int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (now.before(minHour) || now.after(maxHour) || day == Calendar.SUNDAY) {
            calendar.set(Calendar.HOUR_OF_DAY, MIN_HOUR);
            calendar.set(Calendar.MINUTE, 0);
            if (now.after(maxHour) || day == Calendar.SUNDAY) {
                calendar.add(Calendar.DAY_OF_WEEK, 1);
            }
        }
        final Date startDate = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 4);
        return new Date[]{startDate, calendar.getTime()};
    }


    public static boolean doesPDFTableExist(Activity activity, String courseCode) {
        return new File(activity.getFilesDir().getAbsolutePath(), courseCode + ".pdf").exists();
    }

    public static Building getRandBuilding() {
        final List<Building> CU = new ArrayList<>();
        for (Building building : buildingList) {
            if (building.name.startsWith("CU")) {
                CU.add(building);
            }
        }
        return CU.get(new Random().nextInt(CU.size()));
    }
}