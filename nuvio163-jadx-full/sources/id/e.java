package id;

import java.util.function.BiConsumer;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class e implements BiConsumer {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ Function2 f23904a;

    public e(Function2 function) {
        Intrinsics.checkNotNullParameter(function, "function");
        this.f23904a = function;
    }

    @Override // java.util.function.BiConsumer
    public final /* synthetic */ void accept(Object obj, Object obj2) {
        this.f23904a.invoke(obj, obj2);
    }
}
