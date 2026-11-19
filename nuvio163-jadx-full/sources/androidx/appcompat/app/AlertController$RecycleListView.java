package androidx.appcompat.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ListView;
import i.a;

/* JADX INFO: loaded from: classes3.dex */
public class AlertController$RecycleListView extends ListView {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f1946b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f1947c;

    public AlertController$RecycleListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, a.f23533t);
        this.f1947c = typedArrayObtainStyledAttributes.getDimensionPixelOffset(0, -1);
        this.f1946b = typedArrayObtainStyledAttributes.getDimensionPixelOffset(1, -1);
    }
}
