package com.nuvio.tv.domain.model;

import java.net.HttpURLConnection;
import java.util.function.BiConsumer;
import kotlin.jvm.functions.Function2;
import rd.k;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class a implements BiConsumer {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f10272a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Object f10273b;

    public /* synthetic */ a(Object obj, int i10) {
        this.f10272a = i10;
        this.f10273b = obj;
    }

    @Override // java.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        switch (this.f10272a) {
            case 0:
                ((Function2) this.f10273b).invoke(obj, obj2);
                break;
            case 1:
                ((HttpURLConnection) this.f10273b).addRequestProperty((String) obj, (String) obj2);
                break;
            default:
                ((k) this.f10273b).invoke(obj, obj2);
                break;
        }
    }
}
