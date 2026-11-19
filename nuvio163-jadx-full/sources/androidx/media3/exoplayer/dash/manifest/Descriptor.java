package androidx.media3.exoplayer.dash.manifest;

import androidx.media3.common.util.UnstableApi;
import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class Descriptor {

    /* JADX INFO: renamed from: id, reason: collision with root package name */
    public final String f3082id;
    public final String schemeIdUri;
    public final String value;

    public Descriptor(String str, String str2, String str3) {
        this.schemeIdUri = str;
        this.value = str2;
        this.f3082id = str3;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && Descriptor.class == obj.getClass()) {
            Descriptor descriptor = (Descriptor) obj;
            if (Objects.equals(this.schemeIdUri, descriptor.schemeIdUri) && Objects.equals(this.value, descriptor.value) && Objects.equals(this.f3082id, descriptor.f3082id)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int iHashCode = this.schemeIdUri.hashCode() * 31;
        String str = this.value;
        int iHashCode2 = (iHashCode + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.f3082id;
        return iHashCode2 + (str2 != null ? str2.hashCode() : 0);
    }
}
