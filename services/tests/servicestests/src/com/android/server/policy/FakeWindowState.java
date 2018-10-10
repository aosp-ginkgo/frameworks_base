/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.android.server.policy;

import android.annotation.Nullable;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.IApplicationToken;
import android.view.WindowManager;

import com.android.server.wm.WindowFrames;
import com.android.server.wm.utils.WmDisplayCutout;

public class FakeWindowState implements WindowManagerPolicy.WindowState {

    private WindowFrames mWindowFrames = new WindowFrames();

    public WindowManager.LayoutParams attrs;
    public int displayId;
    public boolean isVoiceInteraction;
    public boolean inMultiWindowMode;
    public boolean visible = true;
    public int surfaceLayer = 1;
    public boolean isDimming = false;

    public boolean policyVisible = true;

    @Override
    public int getOwningUid() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String getOwningPackage() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void computeFrameLw() {
    }

    @Override
    public Rect getFrameLw() {
        return mWindowFrames.mParentFrame;
    }

    @Override
    public Rect getDisplayFrameLw() {
        return mWindowFrames.mDisplayFrame;
    }

    @Override
    public Rect getOverscanFrameLw() {
        return mWindowFrames.mOverscanFrame;
    }

    @Override
    public Rect getContentFrameLw() {
        return mWindowFrames.mContentFrame;
    }

    @Override
    public Rect getVisibleFrameLw() {
        return mWindowFrames.mVisibleFrame;
    }

    public Rect getStableFrame() {
        return mWindowFrames.mStableFrame;
    }

    public Rect getDecorFrame() {
        return mWindowFrames.mDecorFrame;
    }

    @Override
    public boolean getGivenInsetsPendingLw() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Rect getGivenContentInsetsLw() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Rect getGivenVisibleInsetsLw() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public WindowManager.LayoutParams getAttrs() {
        return attrs;
    }

    @Override
    public boolean getNeedsMenuLw(WindowManagerPolicy.WindowState bottom) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int getSystemUiVisibility() {
        return attrs.systemUiVisibility | attrs.subtreeSystemUiVisibility;
    }

    @Override
    public int getSurfaceLayer() {
        return surfaceLayer;
    }

    @Override
    public int getBaseType() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public IApplicationToken getAppToken() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isVoiceInteraction() {
        return isVoiceInteraction;
    }

    @Override
    public boolean hasAppShownWindows() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isVisibleLw() {
        return visible && policyVisible;
    }

    @Override
    public boolean isDisplayedLw() {
        return isVisibleLw();
    }

    @Override
    public boolean isAnimatingLw() {
        return false;
    }

    @Override
    public boolean canAffectSystemUiFlags() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isGoneForLayoutLw() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isDrawnLw() {
        return true;
    }

    @Override
    public boolean hasDrawnLw() {
        return true;
    }

    @Override
    public boolean hideLw(boolean doAnimation) {
        if (!policyVisible) {
            return false;
        }
        policyVisible = false;
        return true;
    }

    @Override
    public boolean showLw(boolean doAnimation) {
        if (policyVisible) {
            return false;
        }
        policyVisible = true;
        return true;
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public boolean isDefaultDisplay() {
        return displayId == Display.DEFAULT_DISPLAY;
    }

    @Override
    public boolean isDimming() {
        return isDimming;
    }

    @Override
    public int getWindowingMode() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isInMultiWindowMode() {
        return inMultiWindowMode;
    }

    @Override
    public int getRotationAnimationHint() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isInputMethodWindow() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int getDisplayId() {
        return displayId;
    }

    @Override
    public boolean canAcquireSleepToken() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean canReceiveKeys() { return false; }

    @Override
    public void writeIdentifierToProto(ProtoOutputStream proto, long fieldId){
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public WindowFrames getWindowFrames() {
        return mWindowFrames;
    }

    @Override
    public boolean isInputMethodTarget() {
        return false;
    }
}
