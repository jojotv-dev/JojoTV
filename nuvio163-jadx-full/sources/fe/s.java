package fe;

import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes4.dex */
public final class s extends LinkedHashMap {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f17495b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f17496c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ s(float f10, int i10, boolean z2) {
        super(i10, f10, z2);
        this.f17495b = 2;
    }

    @Override // java.util.LinkedHashMap
    public final boolean removeEldestEntry(Map.Entry entry) {
        switch (this.f17495b) {
            case 0:
                return super.size() > this.f17496c;
            case 1:
                return super.size() > this.f17496c;
            default:
                int i10 = this.f17496c;
                return i10 >= 0 && size() > i10;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public s(int i10, int i11) {
        super(64, 0.75f, true);
        this.f17495b = i11;
        this.f17496c = i10;
        switch (i11) {
            case 1:
                super(i10, 0.75f, true);
                break;
            default:
                break;
        }
    }
}
