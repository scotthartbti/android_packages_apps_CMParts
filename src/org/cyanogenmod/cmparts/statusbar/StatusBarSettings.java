/*
 * Copyright (C) 2014-2015 The CyanogenMod Project
 *               2017 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cyanogenmod.cmparts.statusbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.content.DialogInterface;

import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;

import java.util.Date;

import cyanogenmod.preference.CMSystemSettingListPreference;

import org.cyanogenmod.cmparts.R;
import org.cyanogenmod.cmparts.utils.DeviceUtils;
import org.cyanogenmod.cmparts.SettingsPreferenceFragment;

public class StatusBarSettings extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String STATUS_BAR_CLOCK_STYLE = "status_bar_clock";
    private static final String STATUS_BAR_AM_PM = "status_bar_am_pm";
    private static final String STATUS_BAR_DATE = "status_bar_date";
    private static final String STATUS_BAR_DATE_STYLE = "status_bar_date_style";
    private static final String STATUS_BAR_DATE_POSITION = "clock_date_position";
    private static final String STATUS_BAR_DATE_FORMAT = "status_bar_date_format";
    private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String STATUS_BAR_QUICK_QS_PULLDOWN = "qs_quick_pulldown";
    private static final String KEY_SHOW_FOURG = "show_fourg";
    private static final String STATUS_BAR_CLOCK_FONT_STYLE = "font_style";
    private static final String STATUS_BAR_CLOCK_FONT_SIZE  = "status_bar_clock_font_size";
    private static final String PREF_STATUS_BAR_WEATHER = "status_bar_weather";
    private static final String PREF_CATEGORY_INDICATORS = "pref_category_indicators";
    private static final String WEATHER_SERVICE_PACKAGE = "org.omnirom.omnijaws";

    private static final int STATUS_BAR_BATTERY_STYLE_HIDDEN = 4;
    private static final int STATUS_BAR_BATTERY_STYLE_TEXT = 6;

    public static final int CLOCK_DATE_STYLE_LOWERCASE = 1;
    public static final int CLOCK_DATE_STYLE_UPPERCASE = 2;
    private static final int CUSTOM_CLOCK_DATE_FORMAT_INDEX = 18;

    private CMSystemSettingListPreference mQuickPulldown;
    private CMSystemSettingListPreference mStatusBarClock;
    private CMSystemSettingListPreference mStatusBarAmPm;
    private CMSystemSettingListPreference mStatusBarDate;
    private CMSystemSettingListPreference mStatusBarDateStyle;
    private CMSystemSettingListPreference mStatusBarDatePosition;
    private CMSystemSettingListPreference mStatusBarDateFormat;
    private CMSystemSettingListPreference mStatusBarBattery;
    private CMSystemSettingListPreference mStatusBarBatteryShowPercent;
    private SwitchPreference mShowFourG;
    private CMSystemSettingListPreference mFontStyle;
    private CMSystemSettingListPreference mStatusBarClockFontSize;
    private ListPreference mStatusBarWeather;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.status_bar_settings);

	PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();
	PreferenceCategory categoryIndicators =
                (PreferenceCategory) prefSet.findPreference(PREF_CATEGORY_INDICATORS);

        mStatusBarClock = (CMSystemSettingListPreference) findPreference(STATUS_BAR_CLOCK_STYLE);
        mStatusBarClock.setOnPreferenceChangeListener(this);

	mStatusBarBatteryShowPercent =
	    (CMSystemSettingListPreference) findPreference(STATUS_BAR_SHOW_BATTERY_PERCENT);

        mStatusBarAmPm = (CMSystemSettingListPreference) findPreference(STATUS_BAR_AM_PM);
        if (DateFormat.is24HourFormat(getActivity())) {
            mStatusBarAmPm.setEnabled(false);
            mStatusBarAmPm.setSummary(R.string.status_bar_am_pm_info);
        }

	mStatusBarDate = (CMSystemSettingListPreference) findPreference(STATUS_BAR_DATE);
        int showDate = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_DATE, 0);
        mStatusBarDate.setValue(String.valueOf(showDate));
        mStatusBarDate.setSummary(mStatusBarDate.getEntry());
        mStatusBarDate.setOnPreferenceChangeListener(this);

	mStatusBarDateStyle =
                (CMSystemSettingListPreference) findPreference(STATUS_BAR_DATE_STYLE);
        int dateStyle = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_DATE_STYLE, 0);
        mStatusBarDateStyle.setValue(String.valueOf(dateStyle));
        mStatusBarDateStyle.setSummary(mStatusBarDateStyle.getEntry());
        mStatusBarDateStyle.setOnPreferenceChangeListener(this);

	mStatusBarDatePosition =
                (CMSystemSettingListPreference) findPreference(STATUS_BAR_DATE_POSITION);
        int datePosition = Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_CLOCK_DATE_POSITION, 0);
        mStatusBarDatePosition.setValue(String.valueOf(datePosition));
        mStatusBarDatePosition.setSummary(mStatusBarDatePosition.getEntry());
        mStatusBarDatePosition.setOnPreferenceChangeListener(this);

	mFontStyle =
                (CMSystemSettingListPreference) findPreference(STATUS_BAR_CLOCK_FONT_STYLE);
        int fontStyle = Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_CLOCK_FONT_STYLE, 0);
        mFontStyle.setValue(String.valueOf(fontStyle));
        mFontStyle.setSummary(mFontStyle.getEntry());
        mFontStyle.setOnPreferenceChangeListener(this);

	mStatusBarClockFontSize =
                (CMSystemSettingListPreference) findPreference(STATUS_BAR_CLOCK_FONT_SIZE);
        int fontSize = Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_CLOCK_FONT_SIZE, 14);
        mStatusBarClockFontSize.setValue(String.valueOf(fontSize));
        mStatusBarClockFontSize.setSummary(mStatusBarClockFontSize.getEntry());
        mStatusBarClockFontSize.setOnPreferenceChangeListener(this);

	mStatusBarDateFormat =
                (CMSystemSettingListPreference) findPreference(STATUS_BAR_DATE_FORMAT);
        String dateFormat = Settings.System.getString(resolver,
                Settings.System.STATUS_BAR_DATE_FORMAT);
        if (dateFormat == null) {
            dateFormat = "EEE";
        }
        mStatusBarDateFormat.setValue(dateFormat);
        mStatusBarDateFormat.setOnPreferenceChangeListener(this);
        mStatusBarDateFormat.setSummary(DateFormat.format(dateFormat, new Date()));

	// Show 4G
        mShowFourG = (SwitchPreference) prefSet.findPreference(KEY_SHOW_FOURG);
        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            categoryIndicators.removePreference(mShowFourG);
        }

        parseClockDateFormats();

	mStatusBarBattery =
                (CMSystemSettingListPreference) findPreference(STATUS_BAR_BATTERY_STYLE);
        mStatusBarBattery.setOnPreferenceChangeListener(this);
	enableStatusBarBatteryDependents(mStatusBarBattery.getIntValue(2));

        mQuickPulldown =
                (CMSystemSettingListPreference) findPreference(STATUS_BAR_QUICK_QS_PULLDOWN);
        mQuickPulldown.setOnPreferenceChangeListener(this);
        updateQuickPulldownSummary(mQuickPulldown.getIntValue(0));


        // Status bar weather
        mStatusBarWeather = (ListPreference) prefSet.findPreference(PREF_STATUS_BAR_WEATHER);
        if (mStatusBarWeather != null && (!DeviceUtils.isPackageInstalled(WEATHER_SERVICE_PACKAGE, pm))) {
            categoryIndicators.removePreference(mStatusBarWeather);
        } else {
            int temperatureShow = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, 0,
                UserHandle.USER_CURRENT);
        mStatusBarWeather.setValue(String.valueOf(temperatureShow));
            if (temperatureShow == 0) {
                mStatusBarWeather.setSummary(R.string.statusbar_weather_summary);
            } else {
                mStatusBarWeather.setSummary(mStatusBarWeather.getEntry());
            }
            mStatusBarWeather.setOnPreferenceChangeListener(this);
        }

        setStatusBarDateDependencies();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Adjust status bar preferences for RTL
        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            mStatusBarClock.setEntries(R.array.status_bar_clock_position_entries_rtl);
            mQuickPulldown.setEntries(R.array.status_bar_quick_qs_pulldown_entries_rtl);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        // If we didn't handle it, let preferences handle it.
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mStatusBarDate) {
            int statusBarDate = Integer.valueOf((String) newValue);
            int index = mStatusBarDate.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    getActivity().getContentResolver(), STATUS_BAR_DATE, statusBarDate);
            mStatusBarDate.setSummary(mStatusBarDate.getEntries()[index]);
            setStatusBarDateDependencies();
            return true;
	} else if (preference == mStatusBarWeather) {
            int temperatureShow = Integer.valueOf((String) newValue);
            int index = mStatusBarWeather.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP,
                    temperatureShow, UserHandle.USER_CURRENT);
            if (temperatureShow == 0) {
                mStatusBarWeather.setSummary(R.string.statusbar_weather_summary);
            } else {
                mStatusBarWeather.setSummary(
                        mStatusBarWeather.getEntries()[index]);
            }
            return true;
        } else if (preference == mStatusBarDateStyle) {
            int statusBarDateStyle = Integer.parseInt((String) newValue);
            int index = mStatusBarDateStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    getActivity().getContentResolver(), STATUS_BAR_DATE_STYLE, statusBarDateStyle);
            mStatusBarDateStyle.setSummary(mStatusBarDateStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarDatePosition) {
            int statusBarDatePosition = Integer.parseInt((String) newValue);
            int index = mStatusBarDatePosition.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    getActivity().getContentResolver(), Settings.System.STATUSBAR_CLOCK_DATE_POSITION, statusBarDatePosition);
            mStatusBarDatePosition.setSummary(mStatusBarDatePosition.getEntries()[index]);
            return true;
        } else if (preference == mFontStyle) {
            int clockFontStyle = Integer.parseInt((String) newValue);
            int index = mFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    getActivity().getContentResolver(), Settings.System.STATUSBAR_CLOCK_FONT_STYLE, clockFontStyle);
            mFontStyle.setSummary(mFontStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarClockFontSize) {
            int clockFontSize = Integer.parseInt((String) newValue);
            int index = mStatusBarClockFontSize.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    getActivity().getContentResolver(), Settings.System.STATUSBAR_CLOCK_FONT_SIZE, clockFontSize);
            mStatusBarClockFontSize.setSummary(mStatusBarClockFontSize.getEntries()[index]);
            return true;
        } else if (preference ==  mStatusBarDateFormat) {
            int index = mStatusBarDateFormat.findIndexOfValue((String) newValue);
            if (index == CUSTOM_CLOCK_DATE_FORMAT_INDEX) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(R.string.status_bar_date_string_edittext_title);
                alert.setMessage(R.string.status_bar_date_string_edittext_summary);

                final EditText input = new EditText(getActivity());
                String oldText = Settings.System.getString(
                    getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_DATE_FORMAT);
                if (oldText != null) {
                    input.setText(oldText);
                }
                alert.setView(input);

                alert.setPositiveButton(R.string.menu_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        String value = input.getText().toString();
                        if (value.equals("")) {
                            return;
                        }
                        Settings.System.putString(getActivity().getContentResolver(),
                            Settings.System.STATUS_BAR_DATE_FORMAT, value);

                        mStatusBarDateFormat.setSummary(DateFormat.format(value, new Date()));

                        return;
                    }
                });

                alert.setNegativeButton(R.string.menu_cancel,
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        return;
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();
            } else {
                if ((String) newValue != null) {
                    Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_DATE_FORMAT, (String) newValue);
                    mStatusBarDateFormat.setSummary(
                            DateFormat.format((String) newValue, new Date()));
                }
            }
            return true;
	} else {
            int value = Integer.parseInt((String) newValue);
            if (preference == mQuickPulldown) {
                updateQuickPulldownSummary(value);
            } else if (preference == mStatusBarBattery) {
                enableStatusBarBatteryDependents(value);
	    }

            return true;
	}
    }

    private void enableStatusBarBatteryDependents(int batteryIconStyle) {
        mStatusBarBatteryShowPercent.setEnabled(
                batteryIconStyle != STATUS_BAR_BATTERY_STYLE_HIDDEN
                && batteryIconStyle != STATUS_BAR_BATTERY_STYLE_TEXT);
    }

    private void setStatusBarDateDependencies() {
        int showDate = Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUS_BAR_DATE, 0);
        /*if (false) {// todo: check whether clock is hidden
            mStatusBarDate.setEnabled(false);
            mStatusBarDateStyle.setEnabled(false);
            mStatusBarDatePosition.setEnabled(false);
            mStatusBarDateFormat.setEnabled(false);
        } else {*/
            mStatusBarDate.setEnabled(true);
            mStatusBarDateStyle.setEnabled(showDate != 0);
            mStatusBarDatePosition.setEnabled(showDate != 0);
            mStatusBarDateFormat.setEnabled(showDate != 0);
        //}
    }

    private void parseClockDateFormats() {
        // Parse and repopulate mClockDateFormats's entries based on current date.
        String[] dateEntries = getResources().getStringArray(R.array.status_bar_date_format_entries_values);
        CharSequence parsedDateEntries[];
        parsedDateEntries = new String[dateEntries.length];
        Date now = new Date();

        int lastEntry = dateEntries.length - 1;
        int dateFormat = Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUS_BAR_DATE_STYLE, 0);
        for (int i = 0; i < dateEntries.length; i++) {
            if (i == lastEntry) {
                parsedDateEntries[i] = dateEntries[i];
            } else {
                String newDate;
                CharSequence dateString = DateFormat.format(dateEntries[i], now);
                if (dateFormat == CLOCK_DATE_STYLE_LOWERCASE) {
                    newDate = dateString.toString().toLowerCase();
                } else if (dateFormat == CLOCK_DATE_STYLE_UPPERCASE) {
                    newDate = dateString.toString().toUpperCase();
                } else {
                    newDate = dateString.toString();
                }

                parsedDateEntries[i] = newDate;
            }
        }
        mStatusBarDateFormat.setEntries(parsedDateEntries);
    }

    private void updateQuickPulldownSummary(int value) {
        mQuickPulldown.setSummary(value == 0
                ? R.string.status_bar_quick_qs_pulldown_off
                : R.string.status_bar_quick_qs_pulldown_summary);
    }
}
