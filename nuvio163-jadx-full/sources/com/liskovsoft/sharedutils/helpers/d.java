package com.liskovsoft.sharedutils.helpers;

import android.view.KeyEvent;
import android.view.View;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class d implements View.OnKeyListener {
    @Override // android.view.View.OnKeyListener
    public final boolean onKey(View view, int i10, KeyEvent keyEvent) {
        return KeyHelpers.lambda$fixEnterKey$0(view, i10, keyEvent);
    }
}
