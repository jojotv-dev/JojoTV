package androidx.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.jojo.tv.R;
import p5.a;

/* JADX INFO: loaded from: classes3.dex */
public class SeekBarPreference extends Preference {

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public final int f3802p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public final int f3803q;

    public SeekBarPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, R.attr.seekBarPreferenceStyle);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, a.f44343i, R.attr.seekBarPreferenceStyle, 0);
        int i10 = typedArrayObtainStyledAttributes.getInt(3, 0);
        int i11 = typedArrayObtainStyledAttributes.getInt(1, 100);
        i11 = i11 < i10 ? i10 : i11;
        if (i11 != this.f3802p) {
            this.f3802p = i11;
        }
        int i12 = typedArrayObtainStyledAttributes.getInt(4, 0);
        if (i12 != this.f3803q) {
            this.f3803q = Math.min(this.f3802p - i10, Math.abs(i12));
        }
        typedArrayObtainStyledAttributes.getBoolean(2, true);
        typedArrayObtainStyledAttributes.getBoolean(5, false);
        typedArrayObtainStyledAttributes.getBoolean(6, false);
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // androidx.preference.Preference
    public final Object c(TypedArray typedArray, int i10) {
        return Integer.valueOf(typedArray.getInt(i10, 0));
    }
}
