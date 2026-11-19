package androidx.recyclerview.widget;

import android.os.Parcel;
import android.os.Parcelable;
import o.b3;

/* JADX INFO: loaded from: classes.dex */
public final class y0 implements Parcelable.ClassLoaderCreator {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f4143a;

    public /* synthetic */ y0(int i10) {
        this.f4143a = i10;
    }

    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        switch (this.f4143a) {
            case 0:
                return new z0(parcel, null);
            case 1:
                return new b3(parcel, null);
            default:
                if (parcel.readParcelable(null) == null) {
                    return q4.b.f45643c;
                }
                throw new IllegalStateException("superState must be null");
        }
    }

    @Override // android.os.Parcelable.Creator
    public final Object[] newArray(int i10) {
        switch (this.f4143a) {
            case 0:
                return new z0[i10];
            case 1:
                return new b3[i10];
            default:
                return new q4.b[i10];
        }
    }

    @Override // android.os.Parcelable.ClassLoaderCreator
    public final Object createFromParcel(Parcel parcel, ClassLoader classLoader) {
        switch (this.f4143a) {
            case 0:
                return new z0(parcel, classLoader);
            case 1:
                return new b3(parcel, classLoader);
            default:
                if (parcel.readParcelable(classLoader) == null) {
                    return q4.b.f45643c;
                }
                throw new IllegalStateException("superState must be null");
        }
    }
}
