package com.sterbsociety.orarisapienza.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceManager;

/**
 * See "http://developer.android.com/design/patterns/settings.html"
 * Android Design: Settings for design guidelines and the
 * "http://developer.android.com/guide/topics/ui/settings.html"
 * API Guide for more information on developing a Settings UI.
 */
@SuppressWarnings("deprecation")
public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_ANIMATION_SWITCH = "allow_animations";
    public static final String KEY_PREF_UPDATE_SWITCH = "allow_updates";
    public static final String KEY_PREF_EXIT_SWITCH = "allow_confirm_exit";
    public static final String KEY_PREF_NOTIFICATION_SWITCH = "allow_notifications";
    public static final String KEY_PREF_VIBRATION_SWITCH = "allow_vibration";
    public static final String KEY_PREF_RINGTONE = "ringtone_pref";
    public static final String KEY_PREF_THEME = "pref_theme";
    public static final String KEY_PREF_LANGUAGE = "lang_pref";
    private static final String KEY_PREF_APP_VERSION = "version";
    private static final String KEY_BASE_THEME = "base_theme";
    private static final String KEY_GENERAL = "general";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_ABOUT = "about";
    private static final int REQUEST_CODE_ALERT_RINGTONE = 142;
    private static Preference ringtonePref, updatesPref, animationsPref, exitPref, notificationsPref,
                                vibrationPref, appVersionPref, baseTheme, general, notification, about;
    private static ActionBar mActionBar;
    private static SwitchPreference themePreference;
    private static ListPreference languagePreference;

    // todo implement clear favourites function in settings

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyTheme(SettingsActivity.this);
        super.onCreate(savedInstanceState);

        AppUtils.setLocale(SettingsActivity.this);
        AppUtils.hideSystemUI(getWindow().getDecorView());
        setupActionBar();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
        findViewById(android.R.id.content).setFitsSystemWindows(true);
    }

    /**
     * Here an inner class is used since we only have one main prefsFragment (not using headers .. ).
     */
    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initPreferences();
        }


        /**
         * This method is responsible for keeping preferences' summaries updated and the summaries setup.
         */
        private void initPreferences() {

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            bindPreferenceSummaryToValue(findPreference(KEY_PREF_RINGTONE));
            bindPreferenceSummaryToValue(findPreference(KEY_PREF_LANGUAGE));

            ringtonePref = findPreference(KEY_PREF_RINGTONE);
            themePreference = (SwitchPreference) findPreference(KEY_PREF_THEME);
            languagePreference = (ListPreference) findPreference(KEY_PREF_LANGUAGE);
            ringtonePref.setSummary(AppUtils.getCurrentRingtoneTitle(getActivity()));
            updatesPref = findPreference(KEY_PREF_UPDATE_SWITCH);
            animationsPref = findPreference(KEY_PREF_ANIMATION_SWITCH);
            exitPref = findPreference(KEY_PREF_EXIT_SWITCH);
            notificationsPref = findPreference(KEY_PREF_NOTIFICATION_SWITCH);
            vibrationPref = findPreference(KEY_PREF_VIBRATION_SWITCH);
            appVersionPref = findPreference(KEY_PREF_APP_VERSION);
            baseTheme = findPreference(KEY_BASE_THEME);
            general = findPreference(KEY_GENERAL);
            notification = findPreference(KEY_NOTIFICATIONS);
            about = findPreference(KEY_ABOUT);

            String userLang = AppUtils.getCurrentLanguage();

            languagePreference.setValue(userLang);
            languagePreference.setSummary(languagePreference.getEntry());
        }

        /**
         * This method is responsible for the correct behaviour of ringtone picker, workaround found here:
         * https://issuetracker.google.com/issues/37057453#c2
         */
        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (preference.getKey().equals(KEY_PREF_RINGTONE)) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI);

                String existingValue = getRingtonePreferenceValue(preference.getContext());
                if (existingValue != null) {
                    if (existingValue.length() == 0) {
                        // Select "Silent"
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                    } else {
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(existingValue));
                    }
                } else {
                    // No ringtone has been selected, set to the default
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
                }

                startActivityForResult(intent, REQUEST_CODE_ALERT_RINGTONE);
                return true;
            } else {
                return super.onPreferenceTreeClick(preferenceScreen, preference);
            }
        }


        /**
         * This method returns the currentRingtone's title , required above.
         */
        private String getRingtonePreferenceValue(Context mContext) {
            Uri currentRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(mContext.getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
            Ringtone currentRingtone = RingtoneManager.getRingtone(mContext, currentRingtoneUri);
            return currentRingtone.getTitle(mContext);
        }

        /**
         * Here we allow the user to choice his preferred ringtone.
         */
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_CODE_ALERT_RINGTONE && data != null) {
                Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (ringtone != null) {
                    setRingtonePreferenceValue(ringtone.toString());
                    ringtonePref.setSummary(AppUtils.getTitleOf(ringtone, getActivity()));
                } else {
                    // "Silent" was selected
                    setRingtonePreferenceValue("");
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

        private void setRingtonePreferenceValue(String currentRingtone) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(KEY_PREF_RINGTONE, currentRingtone);
            editor.apply();
        }
    }


    /**
     * @param preference is the Preference we want to keep always up to date.
     * This utility method keeps the preference summary updated when the user
     * makes his choice.
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    return true;

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        if (name.contains(".ogg"))
                            preference.setSummary(name.substring(0, name.length()-4));
                        else
                            preference.setSummary(name);
                    }
                }
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case KEY_PREF_THEME:
                AppUtils.changeTheme();
                finish();
                startActivity(new Intent(this, this.getClass()));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case KEY_PREF_LANGUAGE:
                translateOnDemand(languagePreference.getValue());
                languagePreference.setNegativeButtonText(AppUtils.getStringByLocal(SettingsActivity.this, R.string.cancel, languagePreference.getValue()));
                break;
        }
    }

    /**
     * @param targetLanguage is the language we want to use
     * This method allows us not to recreate() the activity, which increases performances.
     */
    private void translateOnDemand(String targetLanguage) {

        themePreference.setTitle(AppUtils.getStringByLocal(this, R.string.theme_title, targetLanguage));
        themePreference.setSummary(AppUtils.getStringByLocal(this, R.string.theme_desc, targetLanguage));

        updatesPref.setTitle(AppUtils.getStringByLocal(this, R.string.sync_title, targetLanguage));
        updatesPref.setSummary(AppUtils.getStringByLocal(this, R.string.sync_desc, targetLanguage));

        languagePreference.setTitle(AppUtils.getStringByLocal(this, R.string.lang_title, targetLanguage));

        animationsPref.setTitle(AppUtils.getStringByLocal(this, R.string.animations_title, targetLanguage));
        animationsPref.setSummary(AppUtils.getStringByLocal(this, R.string.animations_desc, targetLanguage));

        exitPref.setTitle(AppUtils.getStringByLocal(this, R.string.exit_box_title, targetLanguage));
        exitPref.setSummary(AppUtils.getStringByLocal(this, R.string.exit_box_desc, targetLanguage));

        notificationsPref.setTitle(AppUtils.getStringByLocal(this, R.string.notifications_title, targetLanguage));

        ringtonePref.setTitle(AppUtils.getStringByLocal(this, R.string.ringtone_title, targetLanguage));
        ringtonePref.setSummary(AppUtils.getStringByLocal(this, R.string.ringtone_desc, targetLanguage));

        vibrationPref.setTitle(AppUtils.getStringByLocal(this, R.string.vibration_title, targetLanguage));

        appVersionPref.setTitle(AppUtils.getStringByLocal(this, R.string.app_version, targetLanguage));

        mActionBar.setTitle(AppUtils.getStringByLocal(this, R.string.title_activity_settings , targetLanguage));

        baseTheme.setTitle(AppUtils.getStringByLocal(this, R.string.base_theme, targetLanguage));

        general.setTitle(AppUtils.getStringByLocal(this, R.string.general, targetLanguage));

        notification.setTitle(AppUtils.getStringByLocal(this, R.string.notifications, targetLanguage));

        about.setTitle(AppUtils.getStringByLocal(this, R.string.about, targetLanguage));

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            checkSharedPreferences();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        checkSharedPreferences();
        super.onBackPressed();
    }

    private void setupActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(AppUtils.getStringByLocal(this, R.string.title_activity_settings));
        }
    }

    /**
     * This method is used to check whether the MainActivity need to be recreated in order to apply changes.
     */
    private void checkSharedPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        if (sharedPref.getBoolean(SettingsActivity.KEY_PREF_ANIMATION_SWITCH, false) != AppUtils.areAnimationsAllowed()
                || !sharedPref.getString(SettingsActivity.KEY_PREF_LANGUAGE, "").equals(AppUtils.getCurrentLanguage())) {
            AppUtils.scheduleReboot();
        }
    }

}
