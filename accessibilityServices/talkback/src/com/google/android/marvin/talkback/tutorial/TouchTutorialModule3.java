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

import com.google.android.marvin.talkback.FullScreenReadController;
import com.google.android.marvin.talkback.R;
import com.google.android.marvin.talkback.ShortcutGestureAction;
import com.google.android.marvin.talkback.SpeechController;
import com.google.android.marvin.talkback.tutorial.ContextMenuMonitor.ContextMenuListener;
import com.google.android.marvin.talkback.tutorial.GestureActionMonitor.GestureActionListener;

/**
 * A tutorial lesson that introduces the local and global context menus.
 *
 * @author awdavis@google.com (Austin Davis)
 */
@SuppressLint("ViewConstructor")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
class TouchTutorialModule3 extends TutorialModule {
    /**
     * A monitor used for tracking and handling actions associated with
     * service-level gestures.
     */
    private final GestureActionMonitor mGestureActionMonitor = new GestureActionMonitor();

    /** A monitor used for tracking and handling context menu actions. */
    private final ContextMenuMonitor mContextMenuMonitor = new ContextMenuMonitor();

    /** A delegate used for detecting the global context menu gesture. */
    private final GestureActionListener mGlobalContextMenuGestureDelegate =
            new GestureActionListener() {
        @Override
        public void onGestureAction(ShortcutGestureAction action) {
            if (action == ShortcutGestureAction.TALKBACK_BREAKOUT) {
                mGestureActionMonitor.setListener(null);
                installTriggerDelayedWithFeedback(new Runnable() {
                    @Override
                    public void run() {
                        onTrigger1();
                    }
                });
            }
        }
    };

    /**
     * A delegate used for detecting that the user performed a continuous
     * reading action starting from the top of the screen.
     */
    private final ContextMenuListener mReadFromTopDelegate = new ContextMenuListener() {
        @Override
        public void onShow(int menuId) {
            // Do nothing.
        }

        @Override
        public void onHide(int menuId) {
            // The user hid the Global Context Menu without selecting the
            // "Read from top" menu item
            mContextMenuMonitor.setListener(null);
            installTriggerDelayed(new Runnable() {
                @Override
                public void run() {
                    onTrigger2Hidden();
                }
            });
        }

        @Override
        public void onItemClick(int itemId) {
            if (itemId == R.id.read_from_top) {
                mContextMenuMonitor.setListener(null);
                installTriggerDelayedWithFeedback(new Runnable() {
                    @Override
                    public void run() {
                        // Don't actually perform a full continuous reading.
                        interruptContinuousReading();

                        onTrigger2();
                    }
                });
            }
        }
    };

    /** A delegate used for detecting the local context menu gesture. */
    private final GestureActionListener mLocalContextMenuGestureDelegate =
            new GestureActionListener() {
        @Override
        public void onGestureAction(ShortcutGestureAction action) {
            if (action == ShortcutGestureAction.LOCAL_BREAKOUT) {
                mGestureActionMonitor.setListener(null);
                installTriggerDelayedWithFeedback(new Runnable() {
                    @Override
                    public void run() {
                        onTrigger3();
                    }
                });
            }
        }
    };

    /** A delegate used for detecting that the local context menu was hidden. */
    private final ContextMenuListener mLocalContextMenuHiddenDelegate = new ContextMenuListener() {
        @Override
        public void onShow(int menuId) {
            // Do nothing.
        }

        @Override
        public void onHide(int menuId) {
            if (menuId != R.menu.local_context_menu) {
                return;
            }

            mContextMenuMonitor.setListener(null);
            installTriggerDelayedWithFeedback(new Runnable() {
                @Override
                public void run() {
                    onTrigger4();
                }
            });
        }

        @Override
        public void onItemClick(int itemId) {
            // Do nothing.
        }
    };

    public TouchTutorialModule3(AccessibilityTutorialActivity parentTutorial) {
        super(parentTutorial, R.string.accessibility_tutorial_lesson_3_title);

        setSkipVisible(false);
        setBackVisible(true);
        setNextVisible(true);
        setFinishVisible(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        onTrigger0();
    }

    @Override
    public void onPause() {
        unregisterReceivers();
    }

    @Override
    public void onResume() {
        registerReceivers();
    }

    @Override
    public void onStop() {
        super.onStop();

        mGestureActionMonitor.setListener(null);
        mContextMenuMonitor.setListener(null);
    }

    private void onTrigger0() {
        final int direction = getGestureDirectionForRequiredAction(
                ShortcutGestureAction.TALKBACK_BREAKOUT);
        if (direction < 0) {
            return;
        }

        addInstruction(R.string.accessibility_tutorial_lesson_3_text_1, true,
                getContext().getString(direction));

        // Next trigger is a global context menu gesture.
        mGestureActionMonitor.setListener(mGlobalContextMenuGestureDelegate);
    }

    private void onTrigger1() {
        addInstruction(R.string.accessibility_tutorial_lesson_3_text_2, true,
                getContext().getString(R.string.shortcut_read_from_top));

        // Next trigger is a read from top command.
        mContextMenuMonitor.setListener(mReadFromTopDelegate);
    }

    private void onTrigger2Hidden() {
        final int direction = getGestureDirectionForRequiredAction(
                ShortcutGestureAction.TALKBACK_BREAKOUT);
        if (direction < 0) {
            return;
        }

        addInstruction(R.string.accessibility_tutorial_lesson_3_text_3_hidden, true,
                getContext().getString(direction));

        // Next trigger is a global context menu gesture.
        mGestureActionMonitor.setListener(mGlobalContextMenuGestureDelegate);
    }

    private void onTrigger2() {
        final int direction = getGestureDirectionForRequiredAction(
                ShortcutGestureAction.LOCAL_BREAKOUT);
        if (direction < 0) {
            return;
        }

        addInstruction(R.string.accessibility_tutorial_lesson_3_text_3, true,
                getContext().getString(direction));

        // Next trigger is a local context menu gesture.
        mGestureActionMonitor.setListener(mLocalContextMenuGestureDelegate);
    }

    private void onTrigger3() {
        addInstruction(R.string.accessibility_tutorial_lesson_3_text_4, true);

        // Next trigger is a cancel button press on the local context menu.
        mContextMenuMonitor.setListener(mLocalContextMenuHiddenDelegate);
    }

    private void onTrigger4() {
        // This is the last trigger in this lesson.
        addInstruction(R.string.accessibility_tutorial_lesson_3_text_5, true,
                getContext().getString(R.string.accessibility_tutorial_next));
    }

    private void interruptSpeech() {
        final SpeechController speechController = getParentTutorial().getSpeechController();
        if (speechController != null) {
            speechController.interrupt();
        }
    }

    private void interruptContinuousReading() {
        final FullScreenReadController fullScreenReadController =
                getParentTutorial().getFullScreenReadController();
        if (fullScreenReadController != null) {
            fullScreenReadController.interrupt();
        }

        interruptSpeech();
    }

    private void registerReceivers() {
        registerReceiver(mGestureActionMonitor, GestureActionMonitor.FILTER);
        registerReceiver(mContextMenuMonitor, ContextMenuMonitor.FILTER);
    }

    private void unregisterReceivers() {
        unregisterReceiver(mGestureActionMonitor);
        unregisterReceiver(mContextMenuMonitor);
    }
}
