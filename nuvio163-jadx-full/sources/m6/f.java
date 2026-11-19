package m6;

import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineDispatcher;

/* JADX INFO: loaded from: classes.dex */
public abstract class f implements CoroutineContext {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final CoroutineContext f34944b;

    public f(CoroutineContext coroutineContext) {
        this.f34944b = coroutineContext;
    }

    public final boolean equals(Object obj) {
        return Intrinsics.areEqual(this.f34944b, obj);
    }

    @Override // kotlin.coroutines.CoroutineContext
    public final Object fold(Object obj, Function2 function2) {
        return this.f34944b.fold(obj, function2);
    }

    @Override // kotlin.coroutines.CoroutineContext
    public final CoroutineContext.Element get(CoroutineContext.Key key) {
        return this.f34944b.get(key);
    }

    public final int hashCode() {
        return this.f34944b.hashCode();
    }

    @Override // kotlin.coroutines.CoroutineContext
    public final CoroutineContext minusKey(CoroutineContext.Key key) {
        CoroutineContext coroutineContextMinusKey = this.f34944b.minusKey(key);
        int i10 = h.f34947b;
        CoroutineDispatcher.Companion companion = CoroutineDispatcher.INSTANCE;
        CoroutineDispatcher coroutineDispatcher = (CoroutineDispatcher) get(companion);
        CoroutineDispatcher coroutineDispatcher2 = (CoroutineDispatcher) coroutineContextMinusKey.get(companion);
        if ((coroutineDispatcher instanceof e) && !Intrinsics.areEqual(coroutineDispatcher, coroutineDispatcher2)) {
            ((e) coroutineDispatcher).f34943c = 0;
        }
        return new d(coroutineContextMinusKey);
    }

    @Override // kotlin.coroutines.CoroutineContext
    public final CoroutineContext plus(CoroutineContext coroutineContext) {
        CoroutineContext coroutineContextPlus = this.f34944b.plus(coroutineContext);
        int i10 = h.f34947b;
        CoroutineDispatcher.Companion companion = CoroutineDispatcher.INSTANCE;
        CoroutineDispatcher coroutineDispatcher = (CoroutineDispatcher) get(companion);
        CoroutineDispatcher coroutineDispatcher2 = (CoroutineDispatcher) coroutineContextPlus.get(companion);
        if ((coroutineDispatcher instanceof e) && !Intrinsics.areEqual(coroutineDispatcher, coroutineDispatcher2)) {
            ((e) coroutineDispatcher).f34943c = 0;
        }
        return new d(coroutineContextPlus);
    }

    public final String toString() {
        return "ForwardingCoroutineContext(delegate=" + this.f34944b + ")";
    }
}
