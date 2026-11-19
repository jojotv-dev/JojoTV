package com.nuvio.tv.youtube;

import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class c0 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f10356b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Function0 f10357c;

    public /* synthetic */ c0(int i10, Function0 function0) {
        this.f10356b = i10;
        this.f10357c = function0;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f10356b) {
            case 0:
                return YouTubePlayerPanelKt.YouTubePlayerPanel$lambda$15$0(this.f10357c);
            case 1:
                return YouTubeScreenKt.SuggestionList$lambda$0$0$0$1$0$0(this.f10357c);
            case 2:
                return YouTubeScreenKt.AccountsPanel$lambda$2$0$1$0(this.f10357c);
            case 3:
                return YouTubeScreenKt.AccountsPanel$lambda$2$0$2$0(this.f10357c);
            default:
                return YouTubeScreenKt.YouTubeScreen$lambda$57$0(this.f10357c);
        }
    }
}
