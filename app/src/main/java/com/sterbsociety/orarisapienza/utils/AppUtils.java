package com.sterbsociety.orarisapienza.utils;

import android.annotation.SuppressLint;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.sterbsociety.orarisapienza.MailTask;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.activities.SettingsActivity;
import com.sterbsociety.orarisapienza.adapter.SearchViewAdapter;
import com.sterbsociety.orarisapienza.model.Building;
import com.sterbsociety.orarisapienza.model.Classroom;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

public class AppUtils {

    private static final int PICK_FROM_GALLERY = 1;
    public static final String APP_VERSION = "1.0";

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
            view.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                public boolean onTouch(View v, MotionEvent event) {
                    AppUtils.hideSoftKeyboard(mActivity, mView);
                    return false;
                }
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
    private static String sCurrentRingtone, currentLanguage;
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

        // Here we have to parse all the information.
        createFakeBuildingsList(); // This has to be a parse
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
        mFavouriteCourseSet = new HashSet<>(sharedPreferences.getStringSet(KEY_PREF_COURSE_FAVOURITES, new HashSet<String>()));
        mFavouriteClassSet = new HashSet<>(sharedPreferences.getStringSet(KEY_PREF_CLASS_FAVOURITES, new HashSet<String>()));
        firstTimeStartUp = sharedPreferences.getBoolean(FIRST_TIME_FLAG, true);

        mFavouriteBuildingSetCodes = new HashSet<>(sharedPreferences.getStringSet(KEY_PREF_BUILDING_FAVOURITES, new HashSet<String>()));
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
                if (parts[1].equals(buildingCode))
                    buildingIndex = Integer.parseInt(parts[0]);
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
                        mFavouriteBuildingSetCodes.add(tmpHashSet.size() + "/" + parts[1]);
                    }
                }
            }

            mFavouriteBuildingList.remove(building);
            mFavouriteBuildingList.add(0, building);

            buildingList.remove(index);
            buildingList.add(0, building);
        }
    }

    public static void addClassToFavourites(Activity activity, String classId) {

        mFavouriteClassSet.add(classId);
        activity.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().putStringSet(KEY_PREF_CLASS_FAVOURITES, mFavouriteClassSet).apply();
    }

    public static void removeClassFromFavourites(Activity activity, String classId) {

        mFavouriteClassSet.remove(classId);
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

    // This value is just a custom bizarre value that means 'no distance limit' (half of 42 eheheh).
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

    public static void updateFilters(Intent data) {

        SELECTED_DAY_BTN_INDEX = data.getBooleanArrayExtra(KEY_FILTER_DAY);
        SELECTED_CLASS_BTN_INDEX = data.getIntExtra(KEY_FILTER_AVAILABILITY, SELECTED_CLASS_BTN_INDEX);
        MIN_HOUR = data.getIntExtra(KEY_FILTER_MIN_HOUR, MIN_HOUR);
        MAX_HOUR = data.getIntExtra(KEY_FILTER_MAX_HOUR, MAX_HOUR);
        DISTANCE_FROM_CURRENT_POSITION = data.getIntExtra(KEY_FILTER_DISTANCE, DISTANCE_FROM_CURRENT_POSITION);
    }

    private static List<Classroom> mClassesList;

    // todo This has to be made once at the start of the activity be looping over all of the classes present in the DB.
    public static ArrayList<Classroom> createClassesList() {
        ArrayList<Classroom> dataList = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            dataList.add(new Classroom("Aula P" + i, i + "" + (i * 42 + 79 / 2), 42));
        }
        mClassesList = new ArrayList<>(dataList);
        return dataList;
    }

    // todo this method is just to check the logic and see if it is correct.
    public static ArrayList<Classroom> getFakeFavouriteClasses() {
        final ArrayList<Classroom> dataList = createClassesList();
        ArrayList<Classroom> mResultList = new ArrayList<>();
        for (Classroom classroom : dataList) {
            if (mFavouriteClassSet.contains(classroom.getCode()))
                mResultList.add(classroom);
        }
        return mResultList;
    }

    public static List<Classroom> getClassesList() {
        return mClassesList;
    }

    private static List<String> mCoursesList;

    // todo This has to be made once at the start of the activity be looping over all of the courses present in the DB.
    public static void createCoursesList() {

        mCoursesList = new ArrayList<>(getFavouriteCourses());
        for (int i = 0; i < 30; i++) {
            String courseName = 26654 + i + " - Lesson name Lorem Ipsum";
            if (!hasBeenAlreadySearchedByUser(courseName)) {
                mCoursesList.add(courseName);
            }
        }
    }

    public static List<String> getCoursesList() {
        return mCoursesList;
    }

    public static List<String> getFavouriteCourses() {
        return new ArrayList<>(mFavouriteCourseSet);
    }

    public static boolean hasBeenAlreadySearchedByUser(String course) {
        for (String favouriteCourse : AppUtils.getFavouriteCourses()) {
            if (favouriteCourse.equals(course))
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

    public static void addCourseToFavourites(Context context, String courseName, SearchViewAdapter searchViewAdapter, int index) {

        if (!mFavouriteCourseSet.contains(courseName)) {
            mFavouriteCourseSet.add(courseName);
            context.getSharedPreferences(GENERAL_PREF, Context.MODE_PRIVATE).edit().putStringSet(KEY_PREF_COURSE_FAVOURITES, mFavouriteCourseSet).apply();

            String courseObject = mCoursesList.get(index);
            mCoursesList.remove(index);
            mCoursesList.add(0, courseObject);

            searchViewAdapter.updateSuggestions(mCoursesList);
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

    public static void createFakeBuildingsList() {

        // These lines below emulate a parse from file.
        final ArrayDeque<Building> resultList = new ArrayDeque<>();
        for (int i = 0; i < 100; i++) {
            Building building = new Building("Palazzo lorem" + i, 31415 + i + "", new iVec2(41.904472 + 0.40 * i, 12.512889 + 0.40 * i), "Viale universitá, 2" + i);
            resultList.add(building);
        }

        // We pass from HashSet to an ordered ArrayList by reversing the list, look at the Comparator.
        ArrayList<String> dirtySortedFavouriteCodes = new ArrayList<>(mFavouriteBuildingSetCodes);
        Collections.sort(dirtySortedFavouriteCodes, new Comparator<String>() {
            @Override
            public int compare(String c1, String c2) {
                return Integer.parseInt(c2.split("/")[0]) - Integer.parseInt(c1.split("/")[0]);
            }
        });

        // This is responsible for cleaning buildingCodes from their dirty indexes.
        ArrayList<String> cleanSortedFavouriteCodes = new ArrayList<>();
        for (String dirtyCode : dirtySortedFavouriteCodes) {
            cleanSortedFavouriteCodes.add(dirtyCode.split("/")[1]);
        }

        // Inside here we put each building in accordance to their code's position.
        Building[] tmpFavourites = new Building[mFavouriteBuildingSetCodes.size()];
        for (Building building : resultList) {
            if (isFavouriteBuilding(building.getCode())) {
                tmpFavourites[cleanSortedFavouriteCodes.indexOf(building.getCode())] = building;
                resultList.remove(building);
            }
        }

        mFavouriteBuildingList = new ArrayList<>(Arrays.asList(tmpFavourites));
        buildingList = new ArrayList<>(mFavouriteBuildingList);
        buildingList.addAll(resultList);
    }

    public static ArrayList<Building> getBuildingList() {
        return buildingList;
    }

    public static ArrayList<Building> getFavouriteBuildingList() {
        return new ArrayList<>(mFavouriteBuildingList);
    }
}