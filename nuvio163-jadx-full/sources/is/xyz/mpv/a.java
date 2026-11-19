package is.xyz.mpv;

import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class a implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f26414b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MPV f26415c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ String f26416e;

    public /* synthetic */ a(MPV mpv, String str, int i10) {
        this.f26414b = i10;
        this.f26415c = mpv;
        this.f26416e = str;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f26414b) {
            case 0:
                return MPV.PropertyFlowAccessor.getIntFlow$lambda$2(this.f26415c, this.f26416e);
            case 1:
                return MPV.PropertyFlowAccessor.getFloatFlow$lambda$8(this.f26415c, this.f26416e);
            case 2:
                return MPV.PropertyFlowAccessor.getStringFlow$lambda$17(this.f26415c, this.f26416e);
            case 3:
                return MPV.PropertyFlowAccessor.getDoubleFlow$lambda$11(this.f26415c, this.f26416e);
            case 4:
                return MPV.PropertyFlowAccessor.getNodeFlow$lambda$20(this.f26415c, this.f26416e);
            case 5:
                return MPV.PropertyFlowAccessor.getLongFlow$lambda$5(this.f26415c, this.f26416e);
            default:
                return MPV.PropertyFlowAccessor.getBooleanFlow$lambda$14(this.f26415c, this.f26416e);
        }
    }
}
