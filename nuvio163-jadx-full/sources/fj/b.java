package fj;

import android.view.accessibility.AccessibilityNodeInfo;

/* JADX INFO: loaded from: classes5.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public Object f18085a;

    public static b a(int i10, int i11) {
        AccessibilityNodeInfo.CollectionInfo collectionInfoObtain = AccessibilityNodeInfo.CollectionInfo.obtain(i10, i11, false, 0);
        b bVar = new b();
        bVar.f18085a = collectionInfoObtain;
        return bVar;
    }
}
