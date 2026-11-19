package g1;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public final class s1 implements Parcelable.Creator {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f18582a;

    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        switch (this.f18582a) {
            case 0:
                return new t1(parcel.readFloat());
            case 1:
                return new u1(parcel.readInt());
            default:
                return new v1(parcel.readLong());
        }
    }

    @Override // android.os.Parcelable.Creator
    public final Object[] newArray(int i10) {
        switch (this.f18582a) {
            case 0:
                return new t1[i10];
            case 1:
                return new u1[i10];
            default:
                return new v1[i10];
        }
    }
}
