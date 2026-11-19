package jcifs;

import java.net.InetAddress;

/* JADX INFO: loaded from: classes5.dex */
public interface NameServiceClient {
    Address[] getAllByName(String str, boolean z2);

    Address getByName(String str);

    Address getByName(String str, boolean z2);

    NetbiosAddress getLocalHost();

    NetbiosName getLocalName();

    NetbiosAddress[] getNbtAllByAddress(String str);

    NetbiosAddress[] getNbtAllByAddress(String str, int i10, String str2);

    NetbiosAddress[] getNbtAllByAddress(NetbiosAddress netbiosAddress);

    NetbiosAddress[] getNbtAllByName(String str, int i10, String str2, InetAddress inetAddress);

    NetbiosAddress getNbtByName(String str);

    NetbiosAddress getNbtByName(String str, int i10, String str2);

    NetbiosAddress getNbtByName(String str, int i10, String str2, InetAddress inetAddress);

    NetbiosAddress[] getNodeStatus(NetbiosAddress netbiosAddress);

    NetbiosName getUnknownName();
}
