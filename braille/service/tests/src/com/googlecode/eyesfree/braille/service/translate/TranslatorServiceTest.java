/*
 * Copyright 2013 Google Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.googlecode.eyesfree.braille.service.translate;

import com.googlecode.eyesfree.braille.translate.ITranslatorService;
import com.googlecode.eyesfree.braille.translate.ITranslatorServiceCallback;
import com.googlecode.eyesfree.braille.translate.TableInfo;
import com.googlecode.eyesfree.braille.translate.TranslationResult;
import com.googlecode.eyesfree.braille.translate.TranslatorClient;
import com.googlecode.eyesfree.braille.utils.SimpleFuture;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.test.MoreAsserts;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tests basic functionality of the translator service.
 */
@MediumTest
public class TranslatorServiceTest extends ServiceTestCase<TranslatorService> {

    private static final String ACTION_TRANSLATOR_SERVICE =
            "com.googlecode.eyesfree.braille.service.ACTION_TRANSLATOR_SERVICE";
    private static final String TEST_BRAILLE_TABLE_ID = "en-US-comp8";
    private static final int INIT_TIMEOUT_MILLIS = 5000;
    private ITranslatorService mServiceInterface;

    public TranslatorServiceTest() {
        super(TranslatorService.class);
    }

    @Override
    public void tearDown() throws Exception {
        mServiceInterface = null;
    }

    /** Tests that the translator service initializes successfully. */
    public void testInitCallback() throws Exception {
        assertNotNull(getServiceInterface());
    }

    /** Tests that all braille tables compile successfully. */
    public void testCheckAllTables() throws Exception {
        ITranslatorService service = getServiceInterface();
        for (TableInfo info : service.getTableInfos()) {
            assertTrue("failed braille table check: " + info.getId(),
                    service.checkTable(info.getId()));
        }
    }

    /** Tests that the translator service can translate computer braille. */
    public void testTranslateComputerBraille() throws Exception {
        ITranslatorService service = getServiceInterface();
        assertTrue("expected braille table check to succeed",
                service.checkTable(TEST_BRAILLE_TABLE_ID));
        TranslationResult result = service.translate("Hello!",
                TEST_BRAILLE_TABLE_ID, -1);
        MoreAsserts.assertEquals(
                new byte[] { 0x53, 0x11, 0x07, 0x07, 0x15, 0x2e },
                result.getCells());
        MoreAsserts.assertEquals(new int[] { 0, 1, 2, 3, 4, 5 },
                result.getTextToBraillePositions());
        MoreAsserts.assertEquals(new int[] { 0, 1, 2, 3, 4, 5 },
                result.getBrailleToTextPositions());
        assertEquals(-1, result.getCursorPosition());
    }

    /** Tests that the translator service can back-translate as well. */
    public void testBackTranslateComputerBraille() throws Exception {
        ITranslatorService service = getServiceInterface();
        assertTrue("expected braille table check to succeed",
                service.checkTable(TEST_BRAILLE_TABLE_ID));
        String result = service.backTranslate(
                new byte[] { 0x53, 0x11, 0x07, 0x07, 0x15, 0x2e },
                TEST_BRAILLE_TABLE_ID);
        assertEquals("Hello!", result);
    }

    /** Waits for the service to initialize, and returns an interface to it. */
    private ITranslatorService getServiceInterface() throws ExecutionException,
            InterruptedException, RemoteException, TimeoutException {
        if (mServiceInterface == null) {
            Intent serviceIntent = new Intent(ACTION_TRANSLATOR_SERVICE);
            IBinder binder = bindService(serviceIntent);
            final SimpleFuture<Integer> initStatusFuture =
                    new SimpleFuture<Integer>();
            mServiceInterface = ITranslatorService.Stub.asInterface(binder);
            mServiceInterface.setCallback(
                    new ITranslatorServiceCallback.Stub() {
                        @Override
                        public void onInit(int status) {
                            initStatusFuture.set(status);
                        }
                    });

            int status = initStatusFuture.get(INIT_TIMEOUT_MILLIS,
                    TimeUnit.MILLISECONDS);
            assertEquals(TranslatorClient.SUCCESS, status);
        }
        return mServiceInterface;
    }
}
