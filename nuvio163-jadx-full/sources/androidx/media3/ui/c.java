package androidx.media3.ui;

import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class c implements View.OnClickListener {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3762b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3763c;

    public /* synthetic */ c(Object obj, int i10) {
        this.f3762b = i10;
        this.f3763c = obj;
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        switch (this.f3762b) {
            case 0:
                ((PlayerControlView.AudioTrackSelectionAdapter) this.f3763c).lambda$onBindViewHolderAtZeroPosition$0(view);
                break;
            case 1:
                ((PlayerControlView.SettingViewHolder) this.f3763c).lambda$new$0(view);
                break;
            default:
                ((PlayerControlView.TextTrackSelectionAdapter) this.f3763c).lambda$onBindViewHolderAtZeroPosition$0(view);
                break;
        }
    }
}
