package bq;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes5.dex */
public abstract class e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static IBinder f6304a = null;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static wi.c f6305b = null;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static boolean f6306c = false;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static boolean f6307d = false;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final c f6308e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final a f6309f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final ArrayList f6310g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final ArrayList f6311h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final ArrayList f6312i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public static final Handler f6313j;

    static {
        c cVar = new c();
        cVar.attachInterface(cVar, "moe.shizuku.server.IShizukuApplication");
        f6308e = cVar;
        f6309f = new a();
        f6310g = new ArrayList();
        f6311h = new ArrayList();
        f6312i = new ArrayList();
        f6313j = new Handler(Looper.getMainLooper());
    }

    public static boolean a(IBinder iBinder, String str) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken("moe.shizuku.server.IShizukuService");
            c cVar = f6308e;
            cVar.getClass();
            parcelObtain.writeStrongBinder(cVar);
            parcelObtain.writeString(str);
            boolean zTransact = iBinder.transact(14, parcelObtain, parcelObtain2, 0);
            parcelObtain2.readException();
            return zTransact;
        } finally {
            parcelObtain2.recycle();
            parcelObtain.recycle();
        }
    }

    public static boolean b(IBinder iBinder, String str) {
        Bundle bundle = new Bundle();
        bundle.putInt("shizuku:attach-api-version", 13);
        bundle.putString("shizuku:attach-package-name", str);
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken("moe.shizuku.server.IShizukuService");
            c cVar = f6308e;
            cVar.getClass();
            parcelObtain.writeStrongBinder(cVar);
            parcelObtain.writeInt(1);
            bundle.writeToParcel(parcelObtain, 0);
            boolean zTransact = iBinder.transact(18, parcelObtain, parcelObtain2, 0);
            parcelObtain2.readException();
            return zTransact;
        } finally {
            parcelObtain2.recycle();
            parcelObtain.recycle();
        }
    }

    public static void c(IBinder iBinder, String str) {
        wi.c cVar;
        IBinder iBinder2 = f6304a;
        if (iBinder2 == iBinder) {
            return;
        }
        if (iBinder == null) {
            f6304a = null;
            f6305b = null;
            synchronized (f6310g) {
                try {
                    Iterator it = f6311h.iterator();
                    if (it.hasNext()) {
                        ((d) it.next()).getClass();
                        if (Looper.myLooper() != Looper.getMainLooper()) {
                            throw new ClassCastException();
                        }
                        throw new ClassCastException();
                    }
                } catch (Throwable th2) {
                    throw th2;
                }
            }
            return;
        }
        if (iBinder2 != null) {
            iBinder2.unlinkToDeath(f6309f, 0);
        }
        f6304a = iBinder;
        int i10 = wi.b.f62592c;
        IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface("moe.shizuku.server.IShizukuService");
        if (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof wi.c)) {
            wi.a aVar = new wi.a();
            aVar.f62591c = iBinder;
            cVar = aVar;
        } else {
            cVar = (wi.c) iInterfaceQueryLocalInterface;
        }
        f6305b = cVar;
        try {
            f6304a.linkToDeath(f6309f, 0);
        } catch (Throwable unused) {
            Log.i("ShizukuApplication", "attachApplication");
        }
        try {
            if (!b(f6304a, str) && !a(f6304a, str)) {
                f6307d = true;
            }
            Log.i("ShizukuApplication", "attachApplication");
        } catch (Throwable th3) {
            Log.w("ShizukuApplication", Log.getStackTraceString(th3));
        }
        if (f6307d) {
            d();
        }
    }

    public static void d() {
        ArrayList arrayList = f6310g;
        synchronized (arrayList) {
            try {
                Iterator it = arrayList.iterator();
                if (it.hasNext()) {
                    ((d) it.next()).getClass();
                    if (Looper.myLooper() != Looper.getMainLooper()) {
                        throw new ClassCastException();
                    }
                    throw new ClassCastException();
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }
}
