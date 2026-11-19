package kotlin.io.path;

import java.nio.file.Path;
import kotlin.jvm.functions.Function3;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class f implements Function3 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f31733b;

    public /* synthetic */ f(int i10) {
        this.f31733b = i10;
    }

    @Override // kotlin.jvm.functions.Function3
    public final Object invoke(Object obj, Object obj2, Object obj3) {
        Path path = (Path) obj;
        Path path2 = (Path) obj2;
        Exception exc = (Exception) obj3;
        switch (this.f31733b) {
            case 0:
                return PathsKt__PathRecursiveFunctionsKt.copyToRecursively$lambda$0$PathsKt__PathRecursiveFunctionsKt(path, path2, exc);
            default:
                return PathsKt__PathRecursiveFunctionsKt.copyToRecursively$lambda$2$PathsKt__PathRecursiveFunctionsKt(path, path2, exc);
        }
    }
}
