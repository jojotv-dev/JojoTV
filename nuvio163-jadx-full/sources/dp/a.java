package dp;

import androidx.media3.extractor.ts.PsExtractor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import zo.c;

/* JADX INFO: loaded from: classes5.dex */
public abstract class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final List f13425a;

    static {
        pp.a[] aVarArr = {new pp.a("10.jpg", -1, 1200, 1), new pp.a("101.jpg", 90, -1, 3), new pp.a("170.jpg", 422, -1, 2), new pp.a("171.jpg", 646, -1, 2), new pp.a("20.jpg", -1, 1024, 1), new pp.a("200.jpg", 420, -1, 2), new pp.a("201.jpg", 280, -1, 2), new pp.a("202.jpg", 140, -1, 3), new pp.a("204.jpg", 360, -1, 2), new pp.a("205.jpg", PsExtractor.VIDEO_STREAM_MASK, -1, 2), new pp.a("206.jpg", 180, -1, 2), new pp.a("207.jpg", 120, -1, 3), new pp.a("43.jpg", 100, -1, 3), new pp.a("44.jpg", 200, -1, 2)};
        ArrayList arrayList = new ArrayList(14);
        for (int i10 = 0; i10 < 14; i10++) {
            pp.a aVar = aVarArr[i10];
            Objects.requireNonNull(aVar);
            arrayList.add(aVar);
        }
        f13425a = Collections.unmodifiableList(arrayList);
    }

    public static boolean a(String str) throws c {
        if (str.toLowerCase().matches("https?://.+\\.bandcamp\\.com(/.*)?")) {
            return true;
        }
        if (str.toLowerCase().matches("https?://bandcamp\\.com(/.*)?")) {
            return false;
        }
        try {
            try {
                pp.c.h(str);
                throw null;
            } catch (IndexOutOfBoundsException | NullPointerException unused) {
                return false;
            }
        } catch (IOException unused2) {
            throw new c("Could not determine whether URL is custom domain (not available? network error?)");
        }
    }
}
