package org.chromium.net;

import java.net.InetAddress;
import java.util.List;
import org.chromium.base.annotations.CalledByNative;

/* JADX INFO: loaded from: classes5.dex */
public class DnsStatus {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f44144a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f44145b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f44146c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final String f44147d;

    public DnsStatus(String str, String str2, List list, boolean z2) {
        this.f44144a = list;
        this.f44145b = z2;
        this.f44146c = str == null ? "" : str;
        this.f44147d = str2 == null ? "" : str2;
    }

    @CalledByNative
    public byte[][] getDnsServers() {
        List list = this.f44144a;
        byte[][] bArr = new byte[list.size()][];
        for (int i10 = 0; i10 < list.size(); i10++) {
            bArr[i10] = ((InetAddress) list.get(i10)).getAddress();
        }
        return bArr;
    }

    @CalledByNative
    public boolean getPrivateDnsActive() {
        return this.f44145b;
    }

    @CalledByNative
    public String getPrivateDnsServerName() {
        return this.f44146c;
    }

    @CalledByNative
    public String getSearchDomains() {
        return this.f44147d;
    }
}
