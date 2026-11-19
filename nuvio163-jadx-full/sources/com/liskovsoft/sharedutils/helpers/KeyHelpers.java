package com.liskovsoft.sharedutils.helpers;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import androidx.media3.extractor.ts.TsExtractor;
import com.nuvio.tv.youtube.YouTubeViewModel;

/* JADX INFO: loaded from: classes4.dex */
public class KeyHelpers {
    private static final int KEYCODE_SVC_EXIT = 319;
    private static final int KEYCODE_TV_CONTENTS_MENU = 256;
    private static final int KEYCODE_TV_MEDIA_CONTEXT_MENU = 257;

    public static void fixEnterKey(EditText... editTextArr) {
        if (editTextArr == null || editTextArr.length == 0) {
            return;
        }
        for (EditText editText : editTextArr) {
            editText.setOnKeyListener(new d());
        }
    }

    public static void fixShowKeyboard(EditText... editTextArr) {
        if (editTextArr == null || editTextArr.length == 0) {
            return;
        }
        for (EditText editText : editTextArr) {
            if (editText.getOnFocusChangeListener() == null) {
                editText.setOnFocusChangeListener(new e());
            }
        }
    }

    public static boolean isAmbilightKey(int i10) {
        return i10 == KEYCODE_SVC_EXIT;
    }

    public static boolean isBackKey(int i10) {
        return i10 == 4 || i10 == 111;
    }

    public static boolean isConfirmKey(int i10) {
        return i10 == 23 || i10 == 62 || i10 == 66 || i10 == 96 || i10 == 160;
    }

    public static boolean isLeftRightKey(int i10) {
        return i10 == 21 || i10 == 22;
    }

    public static final boolean isMediaKey(int i10) {
        if (i10 == 79 || i10 == 130 || i10 == 126 || i10 == 127) {
            return true;
        }
        switch (i10) {
            case 85:
            case 86:
            case 87:
            case 88:
            case TsExtractor.TS_STREAM_TYPE_DVBSUBS /* 89 */:
            case YouTubeViewModel.WATCHED_PERCENT /* 90 */:
            case 91:
                return true;
            default:
                return false;
        }
    }

    public static boolean isMenuKey(int i10) {
        return i10 == 82 || i10 == 256 || i10 == 257 || i10 == 165;
    }

    public static boolean isNavigationKey(int i10) {
        return i10 == 21 || i10 == 22 || i10 == 19 || i10 == 20;
    }

    public static boolean isStopKey(int i10) {
        return i10 == 86;
    }

    public static boolean isTogglePlaybackKey(int i10) {
        return i10 == 85;
    }

    public static boolean isUpDownKey(int i10) {
        return i10 == 19 || i10 == 20;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$fixEnterKey$0(View view, int i10, KeyEvent keyEvent) {
        if (i10 != 66 && i10 != 160) {
            return false;
        }
        if (keyEvent.getAction() != 0) {
            return true;
        }
        Helpers.showKeyboardAlt(view.getContext(), view);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$fixShowKeyboard$1(View view, boolean z2) {
        if (z2) {
            Helpers.showKeyboardAlt(view.getContext(), view);
        }
    }

    public static KeyEvent newEvent(KeyEvent keyEvent, int i10) {
        return new KeyEvent(keyEvent.getDownTime(), keyEvent.getEventTime(), keyEvent.getAction(), i10, keyEvent.getRepeatCount(), keyEvent.getMetaState(), keyEvent.getDeviceId(), keyEvent.getScanCode(), keyEvent.getFlags(), keyEvent.getSource());
    }

    public static void press(Activity activity, int i10) {
        KeyEvent keyEventNewEvent = newEvent(0, i10);
        KeyEvent keyEventNewEvent2 = newEvent(1, i10);
        activity.dispatchKeyEvent(keyEventNewEvent);
        activity.dispatchKeyEvent(keyEventNewEvent2);
    }

    public static KeyEvent newEvent(int i10, int i11) {
        return new KeyEvent(i10, i11);
    }
}
