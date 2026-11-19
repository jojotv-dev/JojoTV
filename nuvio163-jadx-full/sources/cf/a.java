package cf;

import com.nuvio.tv.domain.model.ExperienceMode;

/* JADX INFO: loaded from: classes4.dex */
public final class a implements c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final ExperienceMode f6894a;

    public a(ExperienceMode experienceMode) {
        this.f6894a = experienceMode;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof a) && this.f6894a == ((a) obj).f6894a;
    }

    public final int hashCode() {
        ExperienceMode experienceMode = this.f6894a;
        if (experienceMode == null) {
            return 0;
        }
        return experienceMode.hashCode();
    }

    public final String toString() {
        return "Loaded(mode=" + this.f6894a + ")";
    }
}
