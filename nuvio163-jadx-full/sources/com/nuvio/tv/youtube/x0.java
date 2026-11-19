package com.nuvio.tv.youtube;

import kotlin.jvm.functions.Function0;
import x2.n2;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class x0 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f10719b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ n2 f10720c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ g1.n1 f10721e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ g1.n1 f10722i;

    public /* synthetic */ x0(n2 n2Var, g1.n1 n1Var, g1.n1 n1Var2, int i10) {
        this.f10719b = i10;
        this.f10720c = n2Var;
        this.f10721e = n1Var;
        this.f10722i = n1Var2;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f10719b) {
            case 0:
                return YouTubeScreenKt.YouTubeItemMenuDialog$lambda$12$0(this.f10720c, this.f10721e, this.f10722i);
            default:
                return YouTubeScreenKt.YouTubeItemMenuDialog$lambda$14$3$0(this.f10720c, this.f10721e, this.f10722i);
        }
    }
}
