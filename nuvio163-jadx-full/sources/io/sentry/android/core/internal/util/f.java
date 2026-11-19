package io.sentry.android.core.internal.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class f {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final f f24785c = new f();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final io.sentry.util.a f24786a = new io.sentry.util.a();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final ArrayList f24787b = new ArrayList();

    public final ArrayList a() {
        ArrayList arrayList = this.f24787b;
        io.sentry.util.a aVar = this.f24786a;
        aVar.g();
        try {
            if (!arrayList.isEmpty()) {
                aVar.close();
                return arrayList;
            }
            File[] fileArrListFiles = new File("/sys/devices/system/cpu").listFiles();
            if (fileArrListFiles == null) {
                ArrayList arrayList2 = new ArrayList();
                aVar.close();
                return arrayList2;
            }
            for (File file : fileArrListFiles) {
                if (file.getName().matches("cpu[0-9]+")) {
                    try {
                        String strO = io.sentry.util.b.o(new File(file, "cpufreq/cpuinfo_max_freq"));
                        if (strO != null) {
                            arrayList.add(Integer.valueOf((int) (Long.parseLong(strO.trim()) / 1000)));
                        }
                    } catch (IOException | NumberFormatException unused) {
                    }
                }
            }
            aVar.close();
            return arrayList;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }
}
