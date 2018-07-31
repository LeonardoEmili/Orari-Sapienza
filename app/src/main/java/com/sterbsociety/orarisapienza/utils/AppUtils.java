package com.sterbsociety.orarisapienza.utils;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.R;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.core.app.ActivityCompat;

public class AppUtils {

    private static final int PICK_FROM_GALLERY = 1;

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
}
