package android.support.v4.os;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v4.media.g;

/* JADX INFO: loaded from: classes3.dex */
public class e implements Parcelable {
    public static final Parcelable.Creator<e> CREATOR = new g(4);
    final Handler mHandler;
    final boolean mLocal;
    b mReceiver;

    public e(Handler handler) {
        this.mLocal = true;
        this.mHandler = handler;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void onReceiveResult(int i10, Bundle bundle) {
    }

    public void send(int i10, Bundle bundle) {
        if (this.mLocal) {
            Handler handler = this.mHandler;
            if (handler != null) {
                handler.post(new d(this, i10, bundle));
                return;
            } else {
                onReceiveResult(i10, bundle);
                return;
            }
        }
        b bVar = this.mReceiver;
        if (bVar != null) {
            try {
                bVar.a(i10, bundle);
            } catch (RemoteException unused) {
            }
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i10) {
        synchronized (this) {
            try {
                if (this.mReceiver == null) {
                    this.mReceiver = new c(this);
                }
                parcel.writeStrongBinder(this.mReceiver.asBinder());
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    public e(Parcel parcel) {
        this.mLocal = false;
        b bVar = null;
        this.mHandler = null;
        IBinder strongBinder = parcel.readStrongBinder();
        int i10 = c.f1940d;
        if (strongBinder != null) {
            IInterface iInterfaceQueryLocalInterface = strongBinder.queryLocalInterface(b.f1939a);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof b)) {
                bVar = (b) iInterfaceQueryLocalInterface;
            } else {
                a aVar = new a();
                aVar.f1938c = strongBinder;
                bVar = aVar;
            }
        }
        this.mReceiver = bVar;
    }
}
