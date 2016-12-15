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

package com.googlecode.eyesfree.braille.utils;

import android.os.SystemClock;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Trivial future implementation.
 * @param <V> Type of the future value.
 */
public class SimpleFuture<V> implements Future<V> {
    private boolean mDone = false;
    private boolean mCancelled = false;
    private V mResult;
    private Throwable mException;

    @Override
    public synchronized boolean isDone() {
        return mDone;
    }

    @Override
    public synchronized boolean isCancelled() {
        return mCancelled;
    }

    @Override
    public synchronized V get() throws
            InterruptedException, ExecutionException {
        while (!mDone) {
            wait();
        }
        return internalGet();
    }

    @Override
    public synchronized V get(long timeout, TimeUnit unit) throws
            InterruptedException, ExecutionException, TimeoutException {
        long endTime = SystemClock.uptimeMillis() + unit.toMillis(timeout);
        while (!mDone) {
            long timeRemaining = endTime - SystemClock.uptimeMillis();
            if (timeRemaining < 0) {
                throw new TimeoutException("future was not set in time");
            }
            wait(timeRemaining);
        }
        return internalGet();
    }

    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (!mDone) {
            mCancelled = true;
            mDone = true;
            notifyAll();
            return true;
        }
        return false;
    }

    public synchronized void set(V result) {
        if (!mDone) {
            mResult = result;
            mDone = true;
            notifyAll();
        }
    }

    public synchronized void setException(Throwable exception) {
        if (!mDone) {
            mException = exception;
            mDone = true;
            notifyAll();
        }
    }

    private V internalGet() throws ExecutionException {
        if (!mDone) {
            throw new IllegalStateException("future not done yet");
        } else if (mException != null) {
            throw new ExecutionException(mException);
        } else if (mCancelled) {
            throw new CancellationException();
        } else {
            return mResult;
        }
    }
}
