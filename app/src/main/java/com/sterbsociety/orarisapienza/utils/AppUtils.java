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
import com.sterbsociety.orarisapienza.models.StudyPlan;
import com.sterbsociety.orarisapienza.models.StudyPlanPresenter;
import com.sterbsociety.orarisapienza.models.TimeLineModel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

public class AppUtils {

    private static final int PICK_FROM_GALLERY = 1;
    public static final String APP_VERSION = "1.0";
    public static final String DEFAULT_KEY = "KEY";
    public static final String DATABASE_NAME = "courses.db";

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
        view.getLayoutParams().height = view.getMeasuredHeight();
    }

    private static Boolean animationsAllowed, updatesAllowed, secureExitAllowed, notificationAllowed,
            vibrationAllowed, currentTheme, firstTimeStartUp;
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

        readGeneralPreferences(activity);
    }

    private static ArrayList<Classroom> classroomList, favouriteClassroomList;

    public static ArrayList<Classroom> getFavouriteClassroomList() {
        return favouriteClassroomList;
    }

    public static ArrayList<Classroom> getClassroomList() {
        return classroomList;
    }

    private static final String GENERAL_PREF = "com.sterbsociety.orarisapienza.general";


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
                if (!buildingList.get(buildingIndex).getCode().equals(model.getClassroom().getBuildingCode())) {
                    clearCachedStudyPlan(activity);
                    StyleableToast.makeText(activity, getStringByLocal(activity, R.string.study_plan_corrupt),
                            Toast.LENGTH_LONG, R.style.errorToast).show();
                    return;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            sendSilentReport(activity, 333, ex, AppUtils.class.toString());
            clearCachedStudyPlan(activity);
        }
    }

    private static final String KEY_PREF_CLASS_FAVOURITES = "fav_class";
    private static final String KEY_PREF_BUILDING_FAVOURITES = "fav_building";

    public static void addBuildingToFavourites(Activity activity, Building building, int index) {

        final String buildingCode = building.getCode();

        // todo may be useful to limit user history max to 10 or less
        if (!isFavouriteBuilding(buildingCode)) {
            // If the Building is not yet in favourites, then it will be added. Since favourites are stored in a set
            // we can't keep track of order in there, so the code is formed by POSITION + BuildingCode.
            mFavouriteBuildingSetCodes.add(mFavouriteBuildingSetCodes.size() + "/" + buildingCode);
            mFavouriteBuildingList.add(0, building);
            activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().putStringSet(KEY_PREF_BUILDING_FAVOURITES, mFavouriteBuildingSetCodes).apply();
            buildingList.remove(index);
            buildingList.add(0, building);
        } else {
            // Just the history position needs to be fixed, simple thing -> hard to write.
            int buildingIndex = -1;
            // We search through all the favourites the index of the building to 'reorder'
            for (String currentBuildingCode : mFavouriteBuildingSetCodes) {
                String[] parts = currentBuildingCode.split("/");
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
                    String[] parts = currentBuildingCode.split("/");
                    final int currentIndex = Integer.parseInt(parts[0]);
                    if (currentIndex < buildingIndex) {
                        mFavouriteBuildingSetCodes.add(currentBuildingCode);
                    } else if (currentIndex > buildingIndex) {
                        mFavouriteBuildingSetCodes.add(currentIndex - 1 + "/" + parts[1]);
                    } else {
                        mFavouriteBuildingSetCodes.add(tmpHashSet.size() - 1 + "/" + parts[1]);
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

    public static double distance(double startLat, double startLong,
                                  double endLat, double endLong) {

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // <-- d
    }

    private static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

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

    public static void sendSilentReport(Activity activity, int errorLine, Exception ex, String className) {
        new MailTask(activity, null, null, null, null).
                execute(CRASH_REPORT_TITLE                          // Crash report title
                        , CRASH_REPORT_BODY_HEADER                  // The header
                                + errorLine                                 // Line of the begin of try-catch block
                                + CRASH_REPORT_BODY_WHERE                   // Where the error is occurred
                                + CRASH_REPORT_ERROR_STACKTRACE             // Default stacktrace message
                                + getStackTraceAsString(ex)                 // Utility method
                                + className                                 // Where the error occurred.
                        , null);                                    // Not required param.
    }

    private static int SELECTED_CLASS_BTN_INDEX = 0;

    public static int getSelectedClassBtnIndex() {
        return SELECTED_CLASS_BTN_INDEX;
    }

    private static boolean[] SELECTED_DAY_BTN_INDEX;
    private static int CURRENT_DAY = -1;

    private static void initDate() {

        SELECTED_DAY_BTN_INDEX = new boolean[5];
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
            if (day == Calendar.SATURDAY || day == Calendar.SUNDAY || day == Calendar.MONDAY)
                CURRENT_DAY = 0;
            else
                CURRENT_DAY = day - 2;
        }
        return CURRENT_DAY;
    }

    public static int getCurrentDayIndex() {
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

    private static int MIN_HOUR = 7;
    private static int MAX_HOUR = 22;

    public static int getMinHour() {
        return MIN_HOUR;
    }

    public static int getMaxHour() {
        return MAX_HOUR;
    }

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

    public static void updateCachedFilters(Intent data) {

        SELECTED_DAY_BTN_INDEX = data.getBooleanArrayExtra(KEY_FILTER_DAY);
        SELECTED_CLASS_BTN_INDEX = data.getIntExtra(KEY_FILTER_AVAILABILITY, SELECTED_CLASS_BTN_INDEX);
        MIN_HOUR = data.getIntExtra(KEY_FILTER_MIN_HOUR, MIN_HOUR);
        MAX_HOUR = data.getIntExtra(KEY_FILTER_MAX_HOUR, MAX_HOUR);
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
            if (favouriteCourseId.split("/")[1].equals(courseCode))
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
            if (favouriteBuildingCode.split("/")[1].equals(buildingCode))
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
            mFavouriteCourseSet.add(mFavouriteCourseSet.size() + "/" + courseCode);
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
                String[] parts = currentCourseCode.split("/");
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
                    String[] parts = currentCourseCode.split("/");
                    final int currentIndex = Integer.parseInt(parts[0]);
                    if (currentIndex < courseIndex) {
                        mFavouriteCourseSet.add(currentCourseCode);
                    } else if (currentIndex > courseIndex) {
                        mFavouriteCourseSet.add(currentIndex - 1 + "/" + parts[1]);
                    } else {
                        mFavouriteCourseSet.add(tmpHashSet.size() - 1 + "/" + parts[1]);
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

        if (!NetworkStatus.getInstance().isOnline(activity)) {
            // Here we could put a message to promote our app.
            // The line below removes the 'inactive' ad.
            ((ViewGroup) adContainer.getParent()).removeView(adContainer);

        } else {
            // AdMob App ID: ca-app-pub-9817701892167034~2496155654
            String androidId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
            String deviceId = AppUtils.hash(androidId).toUpperCase();

            AdView mAdView = new AdView(activity.getApplicationContext());
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(unitId);
            AdRequest mAdRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(deviceId)
                    .build();
            mAdView.loadAd(mAdRequest);
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

    private static StudyPlanPresenter mStudyPlanPresenter;

    public static boolean isSameStudyPlanRequestAsBefore(@NonNull StudyPlanPresenter studyPlanPresenter) {
        boolean outcome = false;
        if (mStudyPlanPresenter != null) {
            outcome = mStudyPlanPresenter.getStartDate().equals(studyPlanPresenter.getStartDate())
                    && mStudyPlanPresenter.getEndDate().equals(studyPlanPresenter.getEndDate())
                    && mStudyPlanPresenter.getLatitude() == studyPlanPresenter.getLatitude()
                    && mStudyPlanPresenter.getLongitude() == studyPlanPresenter.getLongitude();
        }
        mStudyPlanPresenter = new StudyPlanPresenter(studyPlanPresenter.getStartDate(), studyPlanPresenter.getEndDate(),
                studyPlanPresenter.getLatitude(), studyPlanPresenter.getLongitude(), studyPlanPresenter.getBuilding());
        return outcome;
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
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, d MMM, yyyy HH:mm", Locale.ENGLISH);
            final Date expirationDate = simpleDateFormat.parse(new Gson().fromJson(currentStudyPlan, StudyPlan.class).getEndRequestDate());
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

    public static SimpleDateFormat getFullDateFormatter() {
        return fullDateFormat;
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    public static String getSimpleDate(@NonNull String fullDate) {
        try {
            return simpleDateFormat.format(fullDateFormat.parse(fullDate));
        } catch (ParseException e) {
            e.printStackTrace();
            return fullDate.substring(fullDate.length() - 5, fullDate.length());
        }
    }

    public static String getCurrentDBVersion() {
        return currentDBVersion;
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

    private static boolean isDBAvailable;

    public static boolean isDBAvailable(Activity activity) {
        if (isDBAvailable)
            return true;
        if (currentDBVersion != null)
            return true;
        return (currentDBVersion = activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).getString(DB_KEY, null)) != null;
    }

    private static final String DB_KEY = "finalDB";

    public static void saveDatabase(Activity activity, DataSnapshot dataSnapshot) {
        try {
            POJO pojo = dataSnapshot.child(DB_KEY).getValue(POJO.class);
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

    public static final String KEY_VERSION = "version";

    public static void parseDatabase(Activity activity) {
        try {
            FileInputStream fis = activity.openFileInput(DATABASE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            final POJO database = new Gson().fromJson(sb.toString(), POJO.class);
            parseData(activity, database);
            // We create another HashMap to allow JVM to garbageCollect the database instance
            MATRIX = new HashMap<>(database.matrix);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // This is the mega matrix which holds info inside each cell
    public static HashMap<String, List<Integer>> MATRIX = new HashMap<>();

    public static HashMap<String, HashMap<String, Integer>> TIMETABLES = new HashMap<>();

    public static List<String> LESSON_LIST = new ArrayList<>();

    /**
     * @param arrayList is the list of buildings from DB
     */
    private static void parseBuildingList(ArrayList<Building> arrayList) {

        final ArrayDeque<Building> resultList = new ArrayDeque<>(arrayList);

        // We pass from HashSet to an ordered ArrayList by reversing the list, look at the Comparator.
        ArrayList<String> dirtySortedFavouriteCodes = new ArrayList<>(mFavouriteBuildingSetCodes);
        Collections.sort(dirtySortedFavouriteCodes, (c1, c2) -> Integer.parseInt(c2.split("/")[0]) - Integer.parseInt(c1.split("/")[0]));

        // This is responsible for cleaning buildingCodes from their dirty indexes.
        ArrayList<String> cleanSortedFavouriteCodes = new ArrayList<>();
        for (String dirtyCode : dirtySortedFavouriteCodes) {
            cleanSortedFavouriteCodes.add(dirtyCode.split("/")[1]);
        }

        // Inside here we put each building in accordance to their code's position.
        Building[] tmpFavourites = new Building[mFavouriteBuildingSetCodes.size()];

        // Note that Iterator.remove() is the only safe way to modify a collection during iteration
        // DOCS at: http://docs.oracle.com/javase/tutorial/collections/interfaces/collection.html
        Iterator<Building> iterator = resultList.iterator();
        while (iterator.hasNext()) {
            Building building = iterator.next();
            if (isFavouriteBuilding(building.getCode())) {
                tmpFavourites[cleanSortedFavouriteCodes.indexOf(building.getCode())] = building;
                iterator.remove();
            }
        }

        mFavouriteBuildingList = new ArrayList<>(Arrays.asList(tmpFavourites));
        buildingList = new ArrayList<>(mFavouriteBuildingList);
        buildingList.addAll(resultList);
    }

    /**
     * This method re-elaborates classrooms in order to provide each one of them each info
     * and a light-reference to their relative building.
     */
    private static void parseClassroomList() {
        classroomList = new ArrayList<>();
        for (int i = 0; i < buildingList.size(); i++) {
            final Building building = buildingList.get(i);
            for (int j = 0; j < building.getAule().size(); j++) {
                final Classroom POJOClassroom = building.getAule().get(j);
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
    private static void parseCourseList(HashMap<String, HashMap<String, Integer>> map) {

        final ArrayDeque<Course> resultList = new ArrayDeque<>();
        for (String courseKey : map.keySet()) {
            final String[] parts = courseKey.split("_");
            Course course = new Course(parts[0], parts[1], courseKey);
            resultList.add(course);
        }

        // We pass from HashSet to an ordered ArrayList by reversing the list, look at the Comparator.
        ArrayList<String> dirtySortedFavouriteCourses = new ArrayList<>(mFavouriteCourseSet);
        Collections.sort(dirtySortedFavouriteCourses, (c1, c2) -> Integer.parseInt(c2.split("/")[0]) - Integer.parseInt(c1.split("/")[0]));

        // This is responsible for cleaning buildingCodes from their dirty indexes.
        ArrayList<String> cleanSortedFavouriteCodes = new ArrayList<>();
        for (String dirtyCode : dirtySortedFavouriteCourses) {
            cleanSortedFavouriteCodes.add(dirtyCode.split("/")[1]);
        }

        // Inside here we put each building in accordance to their code's position.
        Course[] tmpFavourites = new Course[mFavouriteCourseSet.size()];

        // Note that Iterator.remove() is the only safe way to modify a collection during iteration
        // DOCS at: http://docs.oracle.com/javase/tutorial/collections/interfaces/collection.html
        Iterator<Course> iterator = resultList.iterator();
        while (iterator.hasNext()) {
            Course course = iterator.next();
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
        int i = index / 157;
        switch (i) {
            case 1:
                return "tue";
            case 2:
                return "wed";
            case 3:
                return "thu";
            case 4:
                return "fri";
            default:
                return "mon";
        }
    }


    public static String getHourByIndex(int index) {
        return String.format(Locale.getDefault(), "%02d", (420 + index % 157 * 5) / 60)
                + ":" +
                String.format(Locale.getDefault(), "%02d", (420 + index % 157 * 5) % 60);
    }

    public static String getClassroomName(String code) {
        final String myBuildingCode = code.split("-")[0];
        final String myClassCode = code.split("-")[1];
        for (Building b : AppUtils.getBuildingList()) {
            if (myBuildingCode.equals(b.getCode())) {
                for (Classroom c : b.getAule()) {
                    if (myClassCode.equals(c.getCode())) {
                        return c.getName();
                    }
                }
            }
        }
        return code;
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
                yearResource = R.string.third;
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

        Toast.makeText(activity, courseType, Toast.LENGTH_SHORT).show();
        if (courseType.toLowerCase().equals("m")) {
            courseResource = R.string.master_degree;
        } else {
            // Else it's a three-year degree
            courseResource = R.string.first_level_degree;
        }
        return getStringByLocal(activity, yearResource) + getStringByLocal(activity, R.string.year)
                + getStringByLocal(activity, R.string.of) + getStringByLocal(activity, courseResource) + ". ";
    }

    public static int getCurrentTimeInt() {
        String hour = simpleDateFormat.format(new Date());
        int dayIndex = getCurrentDayIndex();
        return (dayIndex * 157) + (Integer.parseInt(hour.split(":")[0]) - 7) * 12 + (Integer.parseInt(hour.split(":")[1]) / 5);
    }

    public static int timeToInt() {
        String h = simpleDateFormat.format(new Date());
        int day = getCurrentDayIndex();
        int dayVar = day * 157;//day index
        int var = Math.min(157, Integer.parseInt(h.split(":")[0]) - 7) * 12 + (Integer.parseInt(h.split(":")[1]) / 5);//hour parsing
        if (var < 0) {
            return Math.min(dayVar, 629) % 629;
            //if hour is 00<h<07 than retun minimum  between dayvar and 628 (7:05 of friday,the maximum that day can assume in our week) %628 to get 0 if dayvar is superior(saturday,sunday)
        }
        if (dayVar + var < 785) {
            return dayVar + var;//a right value, day+hour index in our array
        }
        return 0;//index out of bound
    }
}