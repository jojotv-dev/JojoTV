package androidx.media3.session.legacy;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.media3.common.util.UnstableApi;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class LegacyParcelableUtil {
    private LegacyParcelableUtil() {
    }

    public static <T extends Parcelable, U extends Parcelable> T convert(U u10, Parcelable.Creator<T> creator) {
        if (u10 == null) {
            return null;
        }
        Parcelable parcelable = (Parcelable) maybeApplyMediaDescriptionParcelableBugWorkaround(u10);
        Parcel parcelObtain = Parcel.obtain();
        try {
            parcelable.writeToParcel(parcelObtain, 0);
            parcelObtain.setDataPosition(0);
            return (T) maybeApplyMediaDescriptionParcelableBugWorkaround(creator.createFromParcel(parcelObtain));
        } finally {
            parcelObtain.recycle();
        }
    }

    public static <T extends Parcelable, U extends Parcelable> ArrayList<T> convertList(List<U> list, Parcelable.Creator<T> creator) {
        if (list == null) {
            return null;
        }
        k8.a aVar = (ArrayList<T>) new ArrayList();
        for (int i10 = 0; i10 < list.size(); i10++) {
            aVar.add(convert(list.get(i10), creator));
        }
        return aVar;
    }

    private static <T> T maybeApplyMediaDescriptionParcelableBugWorkaround(T t10) {
        return t10;
    }

    private static android.support.v4.media.MediaDescriptionCompat rebuildMediaDescriptionCompat(android.support.v4.media.MediaDescriptionCompat mediaDescriptionCompat) {
        return new android.support.v4.media.MediaDescriptionCompat(mediaDescriptionCompat.f1875b, mediaDescriptionCompat.f1876c, mediaDescriptionCompat.f1877e, mediaDescriptionCompat.f1878i, mediaDescriptionCompat.f1879m, mediaDescriptionCompat.f1880n, mediaDescriptionCompat.f1881o, mediaDescriptionCompat.f1882p);
    }
}
