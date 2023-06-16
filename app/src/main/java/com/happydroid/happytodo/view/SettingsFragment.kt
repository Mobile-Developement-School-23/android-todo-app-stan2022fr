package com.happydroid.happytodo.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.happydroid.happytodo.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val darkThemePreference = findPreference<SwitchPreferenceCompat>("dark_theme")
        darkThemePreference?.setOnPreferenceChangeListener { preference, newValue ->
            // Обработка изменения настройки темы
            val isDarkThemeEnabled = newValue as Boolean
            if (isDarkThemeEnabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            true
        }

        val notificationPreference = findPreference<ListPreference>("notification")
        notificationPreference?.setOnPreferenceChangeListener { preference, newValue ->
            // Обработка изменения настройки уведомлений
            true
        }
    }


}