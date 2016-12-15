/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.google.android.marvin.talkback.menurules;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;

import com.google.android.marvin.talkback.R;
import com.google.android.marvin.talkback.TalkBackService;
import com.googlecode.eyesfree.labeling.CustomLabelManager;
import com.googlecode.eyesfree.labeling.Label;
import com.googlecode.eyesfree.labeling.LabelOperationUtils;
import com.googlecode.eyesfree.utils.AccessibilityNodeInfoUtils;
import com.googlecode.eyesfree.widget.RadialMenu;
import com.googlecode.eyesfree.widget.RadialMenuItem;

import java.util.LinkedList;
import java.util.List;

/**
 * Processes {@link ImageView} nodes without text.
 *
 * @author caseyburkhardt@google.com (Casey Burkhardt)
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class RuleUnlabeledImage implements NodeMenuRule {

    @Override
    public boolean accept(Context context, AccessibilityNodeInfoCompat node) {
        final AccessibilityNodeInfo unwrapped = (AccessibilityNodeInfo) node.getInfo();
        final boolean isImage = AccessibilityNodeInfoUtils.nodeMatchesClassByType(context, node,
                android.widget.ImageView.class);
        final boolean hasDescription = !TextUtils.isEmpty(
                AccessibilityNodeInfoUtils.getNodeText(node));
        final Pair<String, String> parsedId = CustomLabelManager.splitResourceName(
                unwrapped.getViewIdResourceName());
        final boolean hasParseableId = (parsedId != null);

        // TODO(caseyburkhardt): There are a number of views that have a
        // different resource namespace than their parent application. It's
        // likely we'll need to refine the database structure to accommodate
        // these while also allowing the user to modify them through TalkBack
        // settings. For now, we'll simply not allow labeling of such views.
        boolean isFromKnownApp = false;
        if (hasParseableId) {
            try {
                context.getPackageManager().getPackageInfo(parsedId.first, 0);
                isFromKnownApp = true;
            } catch (NameNotFoundException e) {
                // Do nothing.
            }
        }

        return (isImage && !hasDescription && hasParseableId && isFromKnownApp);
    }

    @Override
    public List<RadialMenuItem> getMenuItemsForNode(
            TalkBackService service, AccessibilityNodeInfoCompat node) {
        final AccessibilityNodeInfoCompat nodeCopy = AccessibilityNodeInfoCompat.obtain(node);
        // TODO(caseyburkhardt): Undo when the support library is fixed.
        final AccessibilityNodeInfo unwrappedCopy = (AccessibilityNodeInfo) nodeCopy.getInfo();
        final CustomLabelManager labelManager = service.getLabelManager();
        final List<RadialMenuItem> items = new LinkedList<RadialMenuItem>();

        final Label viewLabel = labelManager.getLabelForViewIdFromCache(
                unwrappedCopy.getViewIdResourceName());
        if (viewLabel == null) {
            final RadialMenuItem addLabel = new RadialMenuItem(service, RadialMenu.NONE,
                    R.id.labeling_breakout_add_label, RadialMenu.NONE,
                    service.getString(R.string.label_dialog_title_add));
            items.add(addLabel);
        } else {
            final RadialMenuItem editLabel = new RadialMenuItem(service, RadialMenu.NONE,
                    R.id.labeling_breakout_edit_label, RadialMenu.NONE,
                    service.getString(R.string.label_dialog_title_edit));
            final RadialMenuItem removeLabel = new RadialMenuItem(service, RadialMenu.NONE,
                    R.id.labeling_breakout_remove_label, RadialMenu.NONE,
                    service.getString(R.string.label_dialog_title_remove));
            items.add(editLabel);
            items.add(removeLabel);
        }

        for (MenuItem item : items) {
            item.setOnMenuItemClickListener(
                    new UnlabeledImageMenuItemClickListener(service, unwrappedCopy, viewLabel));
        }

        return items;
    }

    @Override
    public CharSequence getUserFriendlyMenuName(Context context) {
        return context.getString(R.string.title_labeling_controls);
    }

    @Override
    public boolean canCollapseMenu() {
        return true;
    }

    private static class UnlabeledImageMenuItemClickListener
            implements MenuItem.OnMenuItemClickListener {
        private final Context mContext;
        private final AccessibilityNodeInfo mNode;
        private final Label mExistingLabel;

        public UnlabeledImageMenuItemClickListener(
                TalkBackService service, AccessibilityNodeInfo node, Label label) {
            mContext = service;
            mNode = node;
            mExistingLabel = label;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item == null) {
                mNode.recycle();
                return true;
            }

            final int itemId = item.getItemId();

            if (itemId == R.id.labeling_breakout_add_label) {
                return LabelOperationUtils.startActivityAddLabelForNode(mContext, mNode);
            } else if (itemId == R.id.labeling_breakout_edit_label) {
                return LabelOperationUtils.startActivityEditLabel(mContext, mExistingLabel);
            } else if (itemId == R.id.labeling_breakout_remove_label) {
                return LabelOperationUtils.startActivityRemoveLabel(mContext, mExistingLabel);
            }

            mNode.recycle();
            return true;
        }
    }
}
