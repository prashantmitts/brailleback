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

package com.google.android.marvin.talkback.tutorial;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.marvin.talkback.CursorGranularity;
import com.google.android.marvin.talkback.R;
import com.google.android.marvin.talkback.ShortcutGestureAction;
import com.google.android.marvin.talkback.tutorial.ContextMenuMonitor.ContextMenuListener;
import com.google.android.marvin.talkback.tutorial.GestureActionMonitor.GestureActionListener;
import com.google.android.marvin.talkback.tutorial.GranularityMonitor.GranularityChangeListener;

/**
 * A tutorial lesson that introduces reading text at different granularities.
 *
 * @author awdavis@google.com (Austin Davis)
 */
@SuppressLint("ViewConstructor")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
class TouchTutorialModule4 extends TutorialModule {
    /** A monitor used for tracking and handling service-level gestures. */
    private final GestureActionMonitor mGestureMonitor = new GestureActionMonitor();

    /** A monitor used for tracking and handling context menu actions. */
    private final ContextMenuMonitor mContextMenuMonitor = new ContextMenuMonitor();

    /** A monitor used for tracking and handling granularity changes. */
    private final GranularityMonitor mGranularityMonitor = new GranularityMonitor();

    /** A delegate used for detecting a focus event on the text view. */
    private final AccessibilityDelegate mTextViewFocusedDelegate = new AccessibilityDelegate() {
        @Override
        public boolean onRequestSendAccessibilityEvent(
                ViewGroup host, View child, AccessibilityEvent event) {
            if (child == mTextView) {
                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
                    mLayout.setAccessibilityDelegate(null);
                    installTriggerDelayedWithFeedback(new Runnable() {
                        @Override
                        public void run() {
                            onTrigger1();
                        }
                    });
                }
            }
            return super.onRequestSendAccessibilityEvent(host, child, event);
        }
    };

    /** A delegate used for detecting that the text view lost focus. */
    private final AccessibilityDelegate mTextViewFocusLostDelegate = new AccessibilityDelegate() {
        @Override
        public boolean onRequestSendAccessibilityEvent(
                ViewGroup host, View child, AccessibilityEvent event) {
            if (child == mTextView) {
                if (event.getEventType() ==
                        AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED) {
                    mGestureMonitor.setListener(null);
                    mLayout.setAccessibilityDelegate(null);
                    installTriggerDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onTrigger2FocusLost();
                        }
                    });
                }
            }
            return super.onRequestSendAccessibilityEvent(host, child, event);
        }
    };

    /** A delegate used for detecting the local context menu gesture. */
    private final GestureActionListener mLocalContextMenuGestureDelegate =
            new GestureActionListener() {
        @Override
        public void onGestureAction(ShortcutGestureAction gesture) {
            if (gesture == ShortcutGestureAction.LOCAL_BREAKOUT) {
                mLayout.setAccessibilityDelegate(null);
                mGestureMonitor.setListener(null);
                installTriggerDelayedWithFeedback(new Runnable() {
                    @Override
                    public void run() {
                        onTrigger2();
                    }
                });
            }
        }
    };

    /**
     * A delegate used for detecting that the user closed the Local Context
     * Menu without changing to character granularity.
     */
    private final ContextMenuListener mLocalContextMenuHiddenDelegate =
            new ContextMenuListener() {
        @Override
        public void onShow(int menuId) {
            // Do nothing.
        }

        @Override
        public void onHide(int menuId) {
            if (menuId != R.menu.local_context_menu) {
                return;
            }

            mGranularityMonitor.setListener(null);
            mContextMenuMonitor.setListener(null);

            installTriggerDelayed(new Runnable() {
                @Override
                public void run() {
                    onTrigger3MenuHidden();
                }
            });
        }

        @Override
        public void onItemClick(int itemId) {
            // Do nothing. Local Context Menu does not broadcast click events.
        }
    };

    /**
     * A delegate used for detecting that the user changed to character
     * granularity.
     */
    private final GranularityChangeListener mCharacterGranularityListener =
            new GranularityChangeListener() {
        @Override
        public void onGranularityChanged(CursorGranularity granularity) {
            if (CursorGranularity.CHARACTER.equals(granularity)) {
                mContextMenuMonitor.setListener(null);
                mGranularityMonitor.setListener(null);
                installTriggerDelayedWithFeedback(new Runnable() {
                    @Override
                    public void run() {
                        onTrigger3();
                    }
                });
            }
        }
    };

    /**
     * A delegate used for detecting that the user changed to a granularity
     * other than character granularity.
     */
    private final GranularityChangeListener mIncorrectGranularityListener =
            new GranularityChangeListener() {
        @Override
        public void onGranularityChanged(CursorGranularity granularity) {
            if (!CursorGranularity.CHARACTER.equals(granularity)) {
                mLayout.setAccessibilityDelegate(null);
                mGranularityMonitor.setListener(null);
                installTriggerDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onTrigger4GranularityChanged();
                    }
                });
            }
        }
    };

    /**
     * A delegate used for detecting that the user navigated backwards in the
     * text view three times at the current granularity.
     */
    private final AccessibilityDelegate mNavigateByGranularityDelegate =
            new AccessibilityDelegate() {
        @Override
        public boolean onRequestSendAccessibilityEvent(
                ViewGroup host, View child, AccessibilityEvent event) {
            if (child == mTextView) {
                if (event.getEventType() ==
                        AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY) {
                    mNavigationCount++;

                    if (mNavigationCount >= REQUIRED_NAVIGATION_COUNT) {
                        mGranularityMonitor.setListener(null);
                        mLayout.setAccessibilityDelegate(null);
                        installTriggerDelayedWithFeedback(new Runnable() {
                            @Override
                            public void run() {
                                onTrigger4();
                            }
                        });
                    }
                } else if (event.getEventType() ==
                        AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED) {
                    mGranularityMonitor.setListener(null);
                    mLayout.setAccessibilityDelegate(null);
                    installTriggerDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onTrigger2FocusLost();
                        }
                    });
                }
            }
            return super.onRequestSendAccessibilityEvent(host, child, event);
        }
    };

    private static final int REQUIRED_NAVIGATION_COUNT = 3;

    private LinearLayout mLayout;
    private TextView mTextView;

    private int mNavigationCount = 0;

    // If TouchTutorialModule5 is not supported, this could be the last lesson.
    private static final boolean sIsLastLesson =
            (Build.VERSION.SDK_INT < TouchTutorialModule5.MIN_API_LEVEL);

    public TouchTutorialModule4(AccessibilityTutorialActivity parentTutorial) {
        super(parentTutorial, R.layout.tutorial_lesson_4,
                R.string.accessibility_tutorial_lesson_4_title);

        mLayout = (LinearLayout) findViewById(R.id.accessibility_tutorial_lesson_4_layout);
        mTextView = (TextView) findViewById(R.id.accessibility_tutorial_lesson_4_text_view);

        setSkipVisible(false);
        setBackVisible(true);
        setNextVisible(!sIsLastLesson);
        setFinishVisible(sIsLastLesson);
    }

    @Override
    public void onPause() {
        /*
         * Because we start the lesson over on resume, we clear the delegates on
         * pause instead of on stop.
         */
        clearDelegates();

        unregisterReceivers();
    }

    @Override
    public void onResume() {
        registerReceivers();

        /*
         * For this module in particular, since the text box losing focus sends
         * the user back to trigger 0, it makes sense to force the user to start
         * over when the lesson resumes. Thus, we call trigger 0 on resume
         * instead of on start.
         */
        onTrigger0();
    }

    private void onTrigger0() {
        addInstruction(R.string.accessibility_tutorial_lesson_4_text_1, true);

        // Next trigger is a focus event on the text view.
        mLayout.setAccessibilityDelegate(mTextViewFocusedDelegate);
    }

    private void onTrigger1() {
        final int direction =
                getGestureDirectionForRequiredAction(ShortcutGestureAction.LOCAL_BREAKOUT);
        if (direction < 0) {
            return;
        }

        addInstruction(R.string.accessibility_tutorial_lesson_4_text_2, true,
                getContext().getString(direction));

        // Next trigger is a local context menu gesture.
        mGestureMonitor.setListener(mLocalContextMenuGestureDelegate);

        // Alternatively, the text view could lose focus.
        mLayout.setAccessibilityDelegate(mTextViewFocusLostDelegate);
    }

    private void onTrigger2FocusLost() {
        addInstruction(R.string.accessibility_tutorial_lesson_4_text_3_focus_lost, true);

        // Next trigger is a focus event on the text view.
        mLayout.setAccessibilityDelegate(mTextViewFocusedDelegate);
    }

    private void onTrigger2() {
        addInstruction(R.string.accessibility_tutorial_lesson_4_text_3, true,
                getContext().getString(R.string.granularity_character));

        // Next trigger is a character granularity button press on the local
        // context menu.
        mGranularityMonitor.setListener(mCharacterGranularityListener);

        // Alternatively, the user could hide the Local Context Menu without
        // changing the granularity.
        mContextMenuMonitor.setListener(mLocalContextMenuHiddenDelegate);
    }

    private void onTrigger3MenuHidden() {
        final int direction = getGestureDirectionForRequiredAction(
                ShortcutGestureAction.LOCAL_BREAKOUT);
        if (direction < 0) {
            return;
        }

        addInstruction(R.string.accessibility_tutorial_lesson_4_text_4_menu_hidden, true,
                getContext().getString(R.string.granularity_character),
                getContext().getString(direction));

        // Next trigger is a local context menu gesture.
        mGestureMonitor.setListener(mLocalContextMenuGestureDelegate);

        // Alternatively, the text view could lose focus.
        mLayout.setAccessibilityDelegate(mTextViewFocusLostDelegate);
    }

    private void onTrigger3() {
        addInstruction(R.string.accessibility_tutorial_lesson_4_text_4, true);

        mNavigationCount = 0;

        // Next trigger is three navigations by character granularity.
        // This also handles the case where the text view loses focus.
        mLayout.setAccessibilityDelegate(mNavigateByGranularityDelegate);

        // Alternatively, the user could change to a different granularity.
        mGranularityMonitor.setListener(mIncorrectGranularityListener);
    }

    private void onTrigger4GranularityChanged() {
        final int direction = getGestureDirectionForRequiredAction(
                ShortcutGestureAction.LOCAL_BREAKOUT);
        if (direction < 0) {
            return;
        }

        addInstruction(R.string.accessibility_tutorial_lesson_4_text_5_granularity_changed, true,
                getContext().getString(R.string.granularity_character),
                getContext().getString(direction));

        // Next trigger is a local context menu gesture.
        mGestureMonitor.setListener(mLocalContextMenuGestureDelegate);

        // Alternatively, the text view could lose focus.
        mLayout.setAccessibilityDelegate(mTextViewFocusLostDelegate);
    }

    private void onTrigger4() {
        final int buttonLabelResId = sIsLastLesson
                ? R.string.accessibility_tutorial_finish
                : R.string.accessibility_tutorial_next;
        final int instructionResId = sIsLastLesson
                ? R.string.accessibility_tutorial_lesson_4_text_5_finish
                : R.string.accessibility_tutorial_lesson_4_text_5_next;

        // This is the last trigger in this lesson.
        addInstruction(instructionResId, true, getContext().getString(buttonLabelResId));
    }

    private void clearDelegates() {
        mLayout.setAccessibilityDelegate(null);
        mContextMenuMonitor.setListener(null);
        mGestureMonitor.setListener(null);
        mGranularityMonitor.setListener(null);
    }

    private void registerReceivers() {
        registerReceiver(mGestureMonitor, GestureActionMonitor.FILTER);
        registerReceiver(mContextMenuMonitor, ContextMenuMonitor.FILTER);
        registerReceiver(mGranularityMonitor, GranularityMonitor.FILTER);
    }

    private void unregisterReceivers() {
        unregisterReceiver(mGestureMonitor);
        unregisterReceiver(mContextMenuMonitor);
        unregisterReceiver(mGranularityMonitor);
    }
}
