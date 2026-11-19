package androidx.recyclerview.widget;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public final class w implements Parcelable {
    public static final Parcelable.Creator<w> CREATOR = new android.support.v4.media.g(5);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f4124b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f4125c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public boolean f4126e;

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i10) {
        parcel.writeInt(this.f4124b);
        parcel.writeInt(this.f4125c);
        parcel.writeInt(this.f4126e ? 1 : 0);
    }
}
