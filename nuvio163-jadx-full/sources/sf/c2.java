package sf;

import com.nuvio.tv.domain.model.ExperienceMode;

/* JADX INFO: loaded from: classes4.dex */
public final class c2 implements e2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final ExperienceMode f54633a;

    public c2(ExperienceMode experienceMode) {
        this.f54633a = experienceMode;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof c2) && this.f54633a == ((c2) obj).f54633a;
    }

    public final int hashCode() {
        ExperienceMode experienceMode = this.f54633a;
        if (experienceMode == null) {
            return 0;
        }
        return experienceMode.hashCode();
    }

    public final String toString() {
        return "Loaded(mode=" + this.f54633a + ")";
    }
}
