package ap;

import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends a {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final List f4203i;

    /* JADX WARN: Illegal instructions before constructor call */
    public b(a aVar) {
        String str = aVar.f4200b;
        String str2 = aVar.f4201c;
        String str3 = aVar.f4202e;
        List list = Collections.EMPTY_LIST;
        super(str, str2, str3);
        this.f4203i = Collections.unmodifiableList(list);
    }
}
