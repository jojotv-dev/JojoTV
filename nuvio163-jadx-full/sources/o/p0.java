package o;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
public final class p0 extends View.BaseSavedState {
    public static final Parcelable.Creator<p0> CREATOR = new android.support.v4.media.g(11);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public boolean f43280b;

    @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i10) {
        super.writeToParcel(parcel, i10);
        parcel.writeByte(this.f43280b ? (byte) 1 : (byte) 0);
    }
}
