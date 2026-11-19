package androidx.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import b4.b;
import com.jojo.tv.R;
import mk.c;
import p5.a;

/* JADX INFO: loaded from: classes3.dex */
public class ListPreference extends DialogPreference {

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public final CharSequence[] f3793p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public final String f3794q;

    public ListPreference(Context context, AttributeSet attributeSet, int i10) {
        super(context, attributeSet, i10);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, a.f44338d, i10, 0);
        CharSequence[] textArray = typedArrayObtainStyledAttributes.getTextArray(2);
        this.f3793p = textArray == null ? typedArrayObtainStyledAttributes.getTextArray(0) : textArray;
        if (typedArrayObtainStyledAttributes.getTextArray(3) == null) {
            typedArrayObtainStyledAttributes.getTextArray(1);
        }
        if (typedArrayObtainStyledAttributes.getBoolean(4, typedArrayObtainStyledAttributes.getBoolean(4, false))) {
            if (c.f39185e == null) {
                c.f39185e = new c((byte) 0, 20);
            }
            this.f3801o = c.f39185e;
            b();
        }
        typedArrayObtainStyledAttributes.recycle();
        TypedArray typedArrayObtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, a.f44340f, i10, 0);
        String string = typedArrayObtainStyledAttributes2.getString(33);
        this.f3794q = string == null ? typedArrayObtainStyledAttributes2.getString(7) : string;
        typedArrayObtainStyledAttributes2.recycle();
    }

    @Override // androidx.preference.Preference
    public final CharSequence a() {
        c cVar = this.f3801o;
        if (cVar != null) {
            return cVar.f(this);
        }
        CharSequence charSequenceA = super.a();
        String str = this.f3794q;
        if (str != null) {
            String str2 = String.format(str, "");
            if (!TextUtils.equals(str2, charSequenceA)) {
                Log.w("ListPreference", "Setting a summary with a String formatting marker is no longer supported. You should use a SummaryProvider instead.");
                return str2;
            }
        }
        return charSequenceA;
    }

    @Override // androidx.preference.Preference
    public final Object c(TypedArray typedArray, int i10) {
        return typedArray.getString(i10);
    }

    public ListPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, b.a(context, R.attr.dialogPreferenceStyle, android.R.attr.dialogPreferenceStyle));
    }
}
