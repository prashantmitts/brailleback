/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.marvin.talkback.tutorial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.android.marvin.talkback.CursorGranularity;

/**
 * A receiver that listens for and responds to granularity changes.
 *
 * @author awdavis@google.com (Austin Davis)
 */
public class GranularityMonitor extends BroadcastReceiver {
    public static final String ACTION_GRANULARITY_CHANGED =
            "com.google.android.marvin.talkback.tutorial.GranularityChangedAction";
    public static final String EXTRA_GRANULARITY_KEY =
            "com.google.android.marvin.talkback.tutorial.GranularityExtra";

    /** The filter for which broadcast events this receiver should monitor. */
    public static final IntentFilter FILTER = new IntentFilter(ACTION_GRANULARITY_CHANGED);

    private GranularityChangeListener mListener;

    /**
     * Sets the {@link GranularityChangeListener} that will handle received
     * granularity changes.
     *
     * @param listener The listener that should handle granularity changes, or
     *            {@code null} if granularity changes should not be handled.
     */
    public void setListener(GranularityChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener == null) {
            return;
        }

        if (!ACTION_GRANULARITY_CHANGED.equals(intent.getAction())) {
            return;
        }

        if (intent.hasExtra(EXTRA_GRANULARITY_KEY)) {
            final int granularityKey = intent.getIntExtra(EXTRA_GRANULARITY_KEY, -1);
            final CursorGranularity granularity = CursorGranularity.fromKey(granularityKey);
            if (granularity == null) {
                throw new IllegalArgumentException("Intent has invalid granularity key extra.");
            }

            mListener.onGranularityChanged(granularity);
        } else {
            throw new IllegalArgumentException("Intent missing graunarity key extra.");
        }
    }

    /**
     * A delegate handling granularity changes.
     */
    public interface GranularityChangeListener {
        /**
         * Handles a granularity change.
         *
         * @param granularity The new granularity after the change occurred.
         */
        void onGranularityChanged(CursorGranularity granularity);
    }

}
