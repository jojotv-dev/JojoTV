package oo;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Lambda;
import kotlin.jvm.internal.Ref;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends Lambda implements Function1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Ref.IntRef f43943b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Ref.IntRef f43944c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ String f43945e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ Ref.IntRef f43946i;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public b(Ref.IntRef intRef, Ref.IntRef intRef2, String str, Ref.IntRef intRef3) {
        super(1);
        this.f43943b = intRef;
        this.f43944c = intRef2;
        this.f43945e = str;
        this.f43946i = intRef3;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        boolean z2;
        String str;
        int i10;
        int iIntValue = ((Number) obj).intValue();
        Ref.IntRef intRef = this.f43943b;
        int i11 = intRef.element;
        Ref.IntRef intRef2 = this.f43944c;
        int i12 = intRef2.element;
        while (true) {
            int i13 = intRef.element;
            z2 = true;
            str = this.f43945e;
            if (i13 >= iIntValue || intRef2.element >= str.length()) {
                break;
            }
            char cCharAt = str.charAt(intRef2.element);
            Ref.IntRef intRef3 = this.f43946i;
            if (cCharAt != ' ') {
                if (cCharAt != '\t') {
                    break;
                }
                i10 = 4 - (intRef3.element % 4);
            } else {
                i10 = 1;
            }
            intRef.element += i10;
            intRef3.element += i10;
            intRef2.element++;
        }
        if (intRef2.element == str.length()) {
            intRef.element = Integer.MAX_VALUE;
        }
        int i14 = intRef.element;
        if (iIntValue <= i14) {
            intRef.element = i14 - iIntValue;
        } else {
            intRef2.element = i12;
            intRef.element = i11;
            z2 = false;
        }
        return Boolean.valueOf(z2);
    }
}
