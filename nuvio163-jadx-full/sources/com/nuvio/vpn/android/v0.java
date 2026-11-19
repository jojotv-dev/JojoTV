package com.nuvio.vpn.android;

import android.widget.RadioGroup;
import com.jojo.tv.R;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class v0 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f11012b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ VpnProfilesActivity f11013c;

    public /* synthetic */ v0(VpnProfilesActivity vpnProfilesActivity, int i10) {
        this.f11012b = i10;
        this.f11013c = vpnProfilesActivity;
    }

    @Override // java.lang.Runnable
    public final void run() {
        int i10;
        int i11 = this.f11012b;
        VpnProfilesActivity vpnProfilesActivity = this.f11013c;
        switch (i11) {
            case 0:
                int i12 = VpnProfilesActivity.M;
                vpnProfilesActivity.i(false);
                vpnProfilesActivity.f10816y.setText(R.string.vpn_profiles_storage_failed);
                break;
            case 1:
                int i13 = vpnProfilesActivity.L;
                if (i13 == 0) {
                    vpnProfilesActivity.f10804m.clearCheck();
                } else {
                    RadioGroup radioGroup = vpnProfilesActivity.f10804m;
                    if (i13 == 4) {
                        i10 = vpnProfilesActivity.f10813v;
                    } else if (i13 == 3) {
                        i10 = vpnProfilesActivity.f10812u;
                    } else {
                        i10 = i13 == 2 ? vpnProfilesActivity.f10811t : vpnProfilesActivity.f10810s;
                    }
                    radioGroup.check(i10);
                }
                vpnProfilesActivity.i(false);
                vpnProfilesActivity.f10816y.setText(R.string.vpn_scope_failed);
                break;
            case 2:
                vpnProfilesActivity.f10816y.setText(R.string.vpn_credentials_failed);
                break;
            case 3:
                int i14 = VpnProfilesActivity.M;
                vpnProfilesActivity.b(202);
                break;
            default:
                int i15 = VpnProfilesActivity.M;
                vpnProfilesActivity.b(201);
                break;
        }
    }
}
