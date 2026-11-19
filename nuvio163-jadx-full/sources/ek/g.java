package ek;

import java.security.Permission;
import java.util.HashSet;

/* JADX INFO: loaded from: classes5.dex */
public final class g extends Permission {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final HashSet f15763b;

    public g(String str) {
        super(str);
        HashSet hashSet = new HashSet();
        this.f15763b = hashSet;
        hashSet.add(str);
    }

    public final boolean equals(Object obj) {
        return (obj instanceof g) && this.f15763b.equals(((g) obj).f15763b);
    }

    @Override // java.security.Permission
    public final String getActions() {
        return this.f15763b.toString();
    }

    public final int hashCode() {
        return this.f15763b.hashCode();
    }

    @Override // java.security.Permission
    public final boolean implies(Permission permission) {
        if (!(permission instanceof g)) {
            return false;
        }
        g gVar = (g) permission;
        return getName().equals(gVar.getName()) || this.f15763b.containsAll(gVar.f15763b);
    }
}
