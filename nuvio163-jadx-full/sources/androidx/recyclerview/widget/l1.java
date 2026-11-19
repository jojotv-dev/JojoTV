package androidx.recyclerview.widget;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
public final class l1 implements Parcelable {
    public static final Parcelable.Creator<l1> CREATOR = new android.support.v4.media.g(6);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f3980b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f3981c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int[] f3982e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public boolean f3983i;

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    public final String toString() {
        return "FullSpanItem{mPosition=" + this.f3980b + ", mGapDir=" + this.f3981c + ", mHasUnwantedGapAfter=" + this.f3983i + ", mGapPerSpan=" + Arrays.toString(this.f3982e) + '}';
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i10) {
        parcel.writeInt(this.f3980b);
        parcel.writeInt(this.f3981c);
        parcel.writeInt(this.f3983i ? 1 : 0);
        int[] iArr = this.f3982e;
        if (iArr == null || iArr.length <= 0) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(iArr.length);
            parcel.writeIntArray(this.f3982e);
        }
    }
}
