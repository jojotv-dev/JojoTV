package oi;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes3.dex */
public final class l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f43838a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f43839b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final boolean f43840c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final boolean f43841d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final boolean f43842e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final String f43843f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final String f43844g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final boolean f43845h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final boolean f43846i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public final a f43847j;

    public l(boolean z2, boolean z10, boolean z11, boolean z12, boolean z13, String prettyPrintIndent, String classDiscriminator, boolean z14, boolean z15, a classDiscriminatorMode) {
        Intrinsics.checkNotNullParameter(prettyPrintIndent, "prettyPrintIndent");
        Intrinsics.checkNotNullParameter(classDiscriminator, "classDiscriminator");
        Intrinsics.checkNotNullParameter(classDiscriminatorMode, "classDiscriminatorMode");
        this.f43838a = z2;
        this.f43839b = z10;
        this.f43840c = z11;
        this.f43841d = z12;
        this.f43842e = z13;
        this.f43843f = prettyPrintIndent;
        this.f43844g = classDiscriminator;
        this.f43845h = z14;
        this.f43846i = z15;
        this.f43847j = classDiscriminatorMode;
    }

    public final String toString() {
        return "JsonConfiguration(encodeDefaults=" + this.f43838a + ", ignoreUnknownKeys=" + this.f43839b + ", isLenient=" + this.f43840c + ", allowStructuredMapKeys=" + this.f43841d + ", prettyPrint=false, explicitNulls=" + this.f43842e + ", prettyPrintIndent='" + this.f43843f + "', coerceInputValues=false, useArrayPolymorphism=false, classDiscriminator='" + this.f43844g + "', allowSpecialFloatingPointValues=" + this.f43845h + ", useAlternativeNames=" + this.f43846i + ", namingStrategy=null, decodeEnumsCaseInsensitive=false, allowTrailingComma=false, allowComments=false, classDiscriminatorMode=" + this.f43847j + ')';
    }
}
