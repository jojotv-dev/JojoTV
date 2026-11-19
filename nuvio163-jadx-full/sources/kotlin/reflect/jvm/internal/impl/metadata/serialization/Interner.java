package kotlin.reflect.jvm.internal.impl.metadata.serialization;

import java.util.HashMap;
import kotlin.jvm.internal.SourceDebugExtension;

/* JADX INFO: loaded from: classes5.dex */
@SourceDebugExtension({"SMAP\nInterner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Interner.kt\norg/jetbrains/kotlin/metadata/serialization/Interner\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,30:1\n1#2:31\n1056#3:32\n*S KotlinDebug\n*F\n+ 1 Interner.kt\norg/jetbrains/kotlin/metadata/serialization/Interner\n*L\n13#1:32\n*E\n"})
public final class Interner<T> {
    private final int firstIndex;
    private final HashMap<T, Integer> interned;
    private final Interner<T> parent;

    private final Integer find(T t10) {
        Integer numFind;
        Interner<T> interner = this.parent;
        if (interner != null) {
            int size = interner.interned.size() + this.parent.firstIndex;
            int i10 = this.firstIndex;
        }
        Interner<T> interner2 = this.parent;
        return (interner2 == null || (numFind = interner2.find(t10)) == null) ? this.interned.get(t10) : numFind;
    }

    public final int intern(T t10) {
        Integer numFind = find(t10);
        if (numFind != null) {
            return numFind.intValue();
        }
        int size = this.interned.size() + this.firstIndex;
        this.interned.put(t10, Integer.valueOf(size));
        return size;
    }
}
