/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.marvin.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.marvin.talkback.R;
import com.google.android.marvin.talkback.ShortcutGestureAction;

/**
 * A utility class for user preferences related to TalkBack shortcut gestures.
 *
 * @author awdavis@google.com (Austin Davis)
 */
public class GesturePreferenceUtils {
    /** An array of directions representing each shortcut gesture. */
    private static final GestureDirection[] GESTURE_DIRECTIONS = {
            new GestureDirection(R.string.pref_shortcut_down_and_left_key,
                    R.string.pref_shortcut_down_and_left_default,
                    R.string.value_direction_down_and_left),
            new GestureDirection(R.string.pref_shortcut_down_and_right_key,
                    R.string.pref_shortcut_down_and_right_default,
                    R.string.value_direction_down_and_right),
            new GestureDirection(R.string.pref_shortcut_up_and_left_key,
                    R.string.pref_shortcut_up_and_left_default,
                    R.string.value_direction_up_and_left),
            new GestureDirection(R.string.pref_shortcut_up_and_right_key,
                    R.string.pref_shortcut_up_and_right_default,
                    R.string.value_direction_up_and_right),
            new GestureDirection(R.string.pref_shortcut_right_and_down_key,
                    R.string.pref_shortcut_right_and_down_default,
                    R.string.value_direction_right_and_down),
            new GestureDirection(R.string.pref_shortcut_right_and_up_key,
                    R.string.pref_shortcut_right_and_up_default,
                    R.string.value_direction_right_and_up),
            new GestureDirection(R.string.pref_shortcut_left_and_down_key,
                    R.string.pref_shortcut_left_and_down_default,
                    R.string.value_direction_left_and_down),
            new GestureDirection(R.string.pref_shortcut_left_and_up_key,
                    R.string.pref_shortcut_left_and_up_default,
                    R.string.value_direction_left_and_up)
    };

    /**
     * Gets the direction string ID associated with the given shortcut action.
     *
     * @param context The activity associated with the user preferences.
     * @param action The action the shortcut gesture should perform.
     * @return The resource ID of the direction string, or {@code -1} if no
     *         gesture is assigned to the action.
     */
    public static int getDirectionForAction(Context context, ShortcutGestureAction action) {
        for (GestureDirection direction : GESTURE_DIRECTIONS) {
            final ShortcutGestureAction currentDirectionAction = getActionForGesture(
                    context, direction.keyId, direction.defaultId);
            if (currentDirectionAction == action) {
                return direction.labelId;
            }
        }

        return -1;
    }

    /**
     * Determines the action mapped to a given gesture direction based on user
     * preferences.
     */
    protected static ShortcutGestureAction getActionForGesture(
            Context context, int shortcutPrefKey, int shortcutPrefDefault) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String key = context.getString(shortcutPrefKey);
        final String defaultValue = context.getString(shortcutPrefDefault);
        final String value = prefs.getString(key, defaultValue);
        return ShortcutGestureAction.safeValueOf(value);
    }

    /** Represents the direction of a swipe gesture and an associated action. */
    private static class GestureDirection {
        /** The preference shortcut key for the gesture direction. */
        public final int keyId;

        /** The default action associated with the gesture direction. */
        public final int defaultId;

        /** The ID of the user-facing label for the gesture direction. */
        public final int labelId;

        public GestureDirection(int key, int defaultValue, int label) {
            keyId = key;
            defaultId = defaultValue;
            labelId = label;
        }
    }
}
