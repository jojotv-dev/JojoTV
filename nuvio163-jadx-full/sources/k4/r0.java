package k4;

import android.view.ContentInfo;
import android.view.View;
import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
public abstract class r0 {
    public static String[] a(View view) {
        return view.getReceiveContentMimeTypes();
    }

    public static h b(View view, h hVar) {
        ContentInfo contentInfoA = hVar.f27775a.a();
        Objects.requireNonNull(contentInfoA);
        ContentInfo contentInfoPerformReceiveContent = view.performReceiveContent(contentInfoA);
        if (contentInfoPerformReceiveContent == null) {
            return null;
        }
        return contentInfoPerformReceiveContent == contentInfoA ? hVar : new h(new d(contentInfoPerformReceiveContent));
    }
}
