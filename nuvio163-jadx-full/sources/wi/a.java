package wi;

import android.os.IBinder;
import android.os.Parcel;

/* JADX INFO: loaded from: classes5.dex */
public final class a implements c {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public IBinder f62591c;

    @Override // android.os.IInterface
    public final IBinder asBinder() {
        return this.f62591c;
    }

    public final boolean b() {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken("moe.shizuku.server.IShizukuService");
            if (!this.f62591c.transact(16, parcelObtain, parcelObtain2, 0)) {
                int i10 = b.f62592c;
            }
            parcelObtain2.readException();
            return parcelObtain2.readInt() != 0;
        } finally {
            parcelObtain2.recycle();
            parcelObtain.recycle();
        }
    }
}
