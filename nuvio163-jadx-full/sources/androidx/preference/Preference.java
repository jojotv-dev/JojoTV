package androidx.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import b4.b;
import com.jojo.tv.R;
import mk.c;
import p5.a;

/* JADX INFO: loaded from: classes3.dex */
public class Preference implements Comparable<Preference> {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Context f3795b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f3796c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final CharSequence f3797e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final CharSequence f3798i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final String f3799m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final Object f3800n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public c f3801o;

    public Preference(Context context, AttributeSet attributeSet, int i10) {
        this.f3796c = Integer.MAX_VALUE;
        this.f3795b = context;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, a.f44340f, i10, 0);
        typedArrayObtainStyledAttributes.getResourceId(23, typedArrayObtainStyledAttributes.getResourceId(0, 0));
        String string = typedArrayObtainStyledAttributes.getString(26);
        this.f3799m = string == null ? typedArrayObtainStyledAttributes.getString(6) : string;
        CharSequence text = typedArrayObtainStyledAttributes.getText(34);
        this.f3797e = text == null ? typedArrayObtainStyledAttributes.getText(4) : text;
        CharSequence text2 = typedArrayObtainStyledAttributes.getText(33);
        this.f3798i = text2 == null ? typedArrayObtainStyledAttributes.getText(7) : text2;
        this.f3796c = typedArrayObtainStyledAttributes.getInt(28, typedArrayObtainStyledAttributes.getInt(8, Integer.MAX_VALUE));
        if (typedArrayObtainStyledAttributes.getString(22) == null) {
            typedArrayObtainStyledAttributes.getString(13);
        }
        typedArrayObtainStyledAttributes.getResourceId(27, typedArrayObtainStyledAttributes.getResourceId(3, R.layout.preference));
        typedArrayObtainStyledAttributes.getResourceId(35, typedArrayObtainStyledAttributes.getResourceId(9, 0));
        typedArrayObtainStyledAttributes.getBoolean(21, typedArrayObtainStyledAttributes.getBoolean(2, true));
        boolean z2 = typedArrayObtainStyledAttributes.getBoolean(30, typedArrayObtainStyledAttributes.getBoolean(5, true));
        typedArrayObtainStyledAttributes.getBoolean(29, typedArrayObtainStyledAttributes.getBoolean(1, true));
        if (typedArrayObtainStyledAttributes.getString(19) == null) {
            typedArrayObtainStyledAttributes.getString(10);
        }
        typedArrayObtainStyledAttributes.getBoolean(16, typedArrayObtainStyledAttributes.getBoolean(16, z2));
        typedArrayObtainStyledAttributes.getBoolean(17, typedArrayObtainStyledAttributes.getBoolean(17, z2));
        if (typedArrayObtainStyledAttributes.hasValue(18)) {
            this.f3800n = c(typedArrayObtainStyledAttributes, 18);
        } else if (typedArrayObtainStyledAttributes.hasValue(11)) {
            this.f3800n = c(typedArrayObtainStyledAttributes, 11);
        }
        typedArrayObtainStyledAttributes.getBoolean(31, typedArrayObtainStyledAttributes.getBoolean(12, true));
        if (typedArrayObtainStyledAttributes.hasValue(32)) {
            typedArrayObtainStyledAttributes.getBoolean(32, typedArrayObtainStyledAttributes.getBoolean(14, true));
        }
        typedArrayObtainStyledAttributes.getBoolean(24, typedArrayObtainStyledAttributes.getBoolean(15, false));
        typedArrayObtainStyledAttributes.getBoolean(25, typedArrayObtainStyledAttributes.getBoolean(25, true));
        typedArrayObtainStyledAttributes.getBoolean(20, typedArrayObtainStyledAttributes.getBoolean(20, false));
        typedArrayObtainStyledAttributes.recycle();
    }

    public CharSequence a() {
        c cVar = this.f3801o;
        return cVar != null ? cVar.f(this) : this.f3798i;
    }

    public void b() {
    }

    public Object c(TypedArray typedArray, int i10) {
        return null;
    }

    @Override // java.lang.Comparable
    public final int compareTo(Preference preference) {
        Preference preference2 = preference;
        int i10 = preference2.f3796c;
        CharSequence charSequence = preference2.f3797e;
        int i11 = this.f3796c;
        if (i11 != i10) {
            return i11 - i10;
        }
        CharSequence charSequence2 = this.f3797e;
        if (charSequence2 == charSequence) {
            return 0;
        }
        if (charSequence2 == null) {
            return 1;
        }
        if (charSequence == null) {
            return -1;
        }
        return charSequence2.toString().compareToIgnoreCase(charSequence.toString());
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder();
        CharSequence charSequence = this.f3797e;
        if (!TextUtils.isEmpty(charSequence)) {
            sb2.append(charSequence);
            sb2.append(' ');
        }
        CharSequence charSequenceA = a();
        if (!TextUtils.isEmpty(charSequenceA)) {
            sb2.append(charSequenceA);
            sb2.append(' ');
        }
        if (sb2.length() > 0) {
            sb2.setLength(sb2.length() - 1);
        }
        return sb2.toString();
    }

    public Preference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, b.a(context, R.attr.preferenceStyle, android.R.attr.preferenceStyle));
    }
}
