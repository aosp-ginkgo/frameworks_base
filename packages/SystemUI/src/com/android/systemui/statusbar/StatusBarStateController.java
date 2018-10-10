/*
 * Copyright (C) 2014 The Android Open Source Project
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
 * limitations under the License
 */

package com.android.systemui.statusbar;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.annotation.IntDef;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Tracks and reports on {@link StatusBarState}.
 */
public class StatusBarStateController {
    private static final String TAG = "SbStateController";

    private static final int MAX_STATE = StatusBarState.FULLSCREEN_USER_SWITCHER;
    private static final int MIN_STATE = StatusBarState.SHADE;

    private static final Comparator <RankedListener> mComparator
            = (o1, o2) -> Integer.compare(o1.rank, o2.rank);

    private final ArrayList<RankedListener> mListeners = new ArrayList<>();
    private int mState;
    private int mLastState;
    private boolean mLeaveOpenOnKeyguardHide;

    // TODO: b/115739177 (remove this explicit ordering if we can)
    @Retention(SOURCE)
    @IntDef({RANK_STATUS_BAR, RANK_STATUS_BAR_WINDOW_CONTROLLER, RANK_STACK_SCROLLER, RANK_SHELF})
    public @interface SbStateListenerRank {}
    // This is the set of known dependencies when updating StatusBarState
    public static final int RANK_STATUS_BAR = 0;
    public static final int RANK_STATUS_BAR_WINDOW_CONTROLLER = 1;
    public static final int RANK_STACK_SCROLLER = 2;
    public static final int RANK_SHELF = 3;

    public int getState() {
        return mState;
    }

    public boolean setState(int state) {
        if (state > MAX_STATE || state < MIN_STATE) {
            throw new IllegalArgumentException("Invalid state " + state);
        }
        if (state == mState) {
            return false;
        }
        synchronized (mListeners) {
            for (RankedListener rl : new ArrayList<>(mListeners)) {
                rl.listener.onStatePreChange(mState, state);
            }
            mLastState = mState;
            mState = state;
            for (RankedListener rl : new ArrayList<>(mListeners)) {
                rl.listener.onStateChanged(mState);
            }

            for (RankedListener rl : new ArrayList<>(mListeners)) {
                rl.listener.onStatePostChange();
            }
        }

        return true;
    }

    public boolean goingToFullShade() {
        return mState == StatusBarState.SHADE && mLeaveOpenOnKeyguardHide;
    }

    public void setLeaveOpenOnKeyguardHide(boolean leaveOpen) {
        mLeaveOpenOnKeyguardHide = leaveOpen;
    }

    public boolean leaveOpenOnKeyguardHide() {
        return mLeaveOpenOnKeyguardHide;
    }

    public boolean fromShadeLocked() {
        return mLastState == StatusBarState.SHADE_LOCKED;
    }

    public void addListener(StateListener listener) {
        synchronized (mListeners) {
            addListenerInternalLocked(listener, Integer.MAX_VALUE);
        }
    }

    /**
     * Add a listener and a rank based on the priority of this message
     * @param listener the listener
     * @param rank the order in which you'd like to be called. Ranked listeners will be
     * notified before unranked, and we will sort ranked listeners from low to high
     *
     * @deprecated This method exists only to solve latent inter-dependencies from refactoring
     * StatusBarState out of StatusBar.java. Any new listeners should be built not to need ranking
     * (i.e., they are non-dependent on the order of operations of StatusBarState listeners).
     */
    public void addListener(StateListener listener, @SbStateListenerRank int rank) {
        synchronized (mListeners) {
            addListenerInternalLocked(listener, rank);
        }
    }

    @GuardedBy("mListeners")
    private void addListenerInternalLocked(StateListener listener, int rank) {
        // Protect against double-subscribe
        for (RankedListener rl : mListeners) {
            if (rl.listener.equals(listener)) {
                return;
            }
        }

        RankedListener rl = new RankedListener(listener, rank);
        mListeners.add(rl);
        mListeners.sort(mComparator);
    }

    public void removeListener(StateListener listener) {
        synchronized (mListeners) {
            mListeners.removeIf((it) -> it.listener.equals(listener));
        }
    }

    public static String describe(int state) {
        return StatusBarState.toShortString(state);
    }

    public interface StateListener {
        public default void onStatePreChange(int oldState, int newState) {
        }

        public default void onStatePostChange() {
        }

        public void onStateChanged(int newState);
    }

    private class RankedListener {
        private final StateListener listener;
        private final int rank;

        private RankedListener(StateListener l, int r) {
            listener = l;
            rank = r;
        }
    }
}
