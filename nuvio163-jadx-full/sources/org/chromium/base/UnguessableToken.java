package org.chromium.base;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.media.g;
import org.chromium.base.annotations.CalledByNative;

/* JADX INFO: loaded from: classes5.dex */
public class UnguessableToken implements Parcelable {
    public static final Parcelable.Creator<UnguessableToken> CREATOR = new g(14);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f44133b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f44134c;

    public UnguessableToken(long j10, long j11) {
        this.f44133b = j10;
        this.f44134c = j11;
    }

    @CalledByNative
    public static UnguessableToken create(long j10, long j11) {
        return new UnguessableToken(j10, j11);
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    public final boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            UnguessableToken unguessableToken = (UnguessableToken) obj;
            if (unguessableToken.f44133b == this.f44133b && unguessableToken.f44134c == this.f44134c) {
                return true;
            }
        }
        return false;
    }

    @CalledByNative
    public long getHighForSerialization() {
        return this.f44133b;
    }

    @CalledByNative
    public long getLowForSerialization() {
        return this.f44134c;
    }

    public final int hashCode() {
        long j10 = this.f44134c;
        long j11 = this.f44133b;
        return (((int) (j10 ^ (j10 >>> 32))) * 31) + ((int) ((j11 >>> 32) ^ j11));
    }

    @CalledByNative
    public final UnguessableToken parcelAndUnparcelForTesting() {
        Parcel parcelObtain = Parcel.obtain();
        writeToParcel(parcelObtain, 0);
        parcelObtain.setDataPosition(0);
        UnguessableToken unguessableTokenCreateFromParcel = CREATOR.createFromParcel(parcelObtain);
        parcelObtain.recycle();
        return unguessableTokenCreateFromParcel;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i10) {
        parcel.writeLong(this.f44133b);
        parcel.writeLong(this.f44134c);
    }
}
