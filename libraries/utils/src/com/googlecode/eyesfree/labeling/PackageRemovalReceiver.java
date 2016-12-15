
package com.googlecode.eyesfree.labeling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.googlecode.eyesfree.labeling.PackageLabelsFetchRequest.OnLabelsFetchedListener;
import com.googlecode.eyesfree.utils.LogUtils;

import java.util.Collection;
import java.util.Map;

/**
 * {@link BroadcastReceiver} used to remove {@link Label}s when their
 * originating application is removed from the system.
 */
public class PackageRemovalReceiver extends BroadcastReceiver {

    public static final int MIN_API_LEVEL = CustomLabelManager.MIN_API_LEVEL;

    private static final IntentFilter INTENT_FILTER = new IntentFilter(
            Intent.ACTION_PACKAGE_REMOVED);
    static {
        INTENT_FILTER.addDataScheme("package");
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            final boolean isUpgrade = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
            if (!isUpgrade) {
                final CustomLabelManager labelManager = new CustomLabelManager(ctx);
                final String packageName = intent.getData().toString();
                LogUtils.log(
                        this, Log.VERBOSE, "Package %s removed.  Discarding associated labels.",
                        packageName);

                final OnLabelsFetchedListener callback = new OnLabelsFetchedListener() {
                    @Override
                    public void onLabelsFetched(Map<String, Label> results) {
                        // Remove each label that matches the removed package
                        // from the label database.
                        if ((results != null) && !results.isEmpty()) {
                            final Collection<Label> labels = results.values();
                            LogUtils.log(this, Log.VERBOSE, "Removing %d labels.", labels.size());
                            for (Label l : labels) {
                                labelManager.removeLabel(l);
                            }
                        }

                        labelManager.shutdown();
                    }
                };

                labelManager.getLabelsForPackageFromDatabase(packageName, callback);
            }
        }
    }

    public IntentFilter getFilter() {
        return INTENT_FILTER;
    }
}
