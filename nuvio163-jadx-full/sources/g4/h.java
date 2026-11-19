package g4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f18794a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f18795b;

    public h(int i10, List list) {
        this.f18794a = i10;
        this.f18795b = list;
    }

    public h() {
        this.f18794a = 1;
        this.f18795b = Collections.singletonList(null);
    }

    public h(ArrayList arrayList) {
        this.f18794a = 0;
        this.f18795b = arrayList;
    }
}
