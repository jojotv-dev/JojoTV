package jcifs.internal.dfs;

import h1.f;
import java.util.ArrayList;
import jcifs.Decodable;
import jcifs.RuntimeCIFSException;
import jcifs.internal.util.SMBUtil;
import jcifs.util.Strings;
import o2.a;

/* JADX INFO: loaded from: classes5.dex */
public class Referral implements Decodable {
    String altPath;
    int proximity;
    int rflags;
    int serverType;
    int size;
    int ttl;
    int version;
    String rpath = null;
    String node = null;
    String specialName = null;
    String[] expandedNames = new String[0];

    private static String readString(byte[] bArr, int i10, int i11) {
        if (i10 % 2 != 0) {
            i10++;
        }
        return Strings.fromUNIBytes(bArr, i10, Strings.findUNITermination(bArr, i10, i11));
    }

    @Override // jcifs.Decodable
    public int decode(byte[] bArr, int i10, int i11) {
        int int2 = SMBUtil.readInt2(bArr, i10);
        this.version = int2;
        if (int2 != 3 && int2 != 1) {
            throw new RuntimeCIFSException(f.j(this.version, " referral not supported. Please report this to jcifs at samba dot org.", new StringBuilder("Version ")));
        }
        this.size = SMBUtil.readInt2(bArr, i10 + 2);
        this.serverType = SMBUtil.readInt2(bArr, i10 + 4);
        this.rflags = SMBUtil.readInt2(bArr, i10 + 6);
        int i12 = i10 + 8;
        int i13 = this.version;
        if (i13 == 3) {
            this.proximity = SMBUtil.readInt2(bArr, i12);
            this.ttl = SMBUtil.readInt2(bArr, i10 + 10);
            int i14 = i10 + 12;
            if ((this.rflags & 2) == 0) {
                int int3 = SMBUtil.readInt2(bArr, i14);
                int int4 = SMBUtil.readInt2(bArr, i10 + 14);
                int int5 = SMBUtil.readInt2(bArr, i10 + 16);
                if (int3 > 0) {
                    this.rpath = readString(bArr, int3 + i10, i11);
                }
                if (int5 > 0) {
                    this.node = readString(bArr, int5 + i10, i11);
                }
                if (int4 > 0) {
                    this.altPath = readString(bArr, i10 + int4, i11);
                }
            } else {
                int int6 = SMBUtil.readInt2(bArr, i14);
                int int7 = SMBUtil.readInt2(bArr, i10 + 14);
                int int8 = SMBUtil.readInt2(bArr, i10 + 16);
                if (int6 > 0) {
                    this.specialName = readString(bArr, int6 + i10, i11);
                }
                if (int8 > 0) {
                    ArrayList arrayList = new ArrayList();
                    for (int i15 = 0; i15 < int7; i15++) {
                        String string = readString(bArr, i10 + int8, i11);
                        arrayList.add(string);
                        int8 += (string.length() * 2) + 2;
                    }
                    this.expandedNames = (String[]) arrayList.toArray(new String[arrayList.size()]);
                }
            }
        } else if (i13 == 1) {
            this.node = readString(bArr, i12, i11);
        }
        return this.size;
    }

    public final String getAltPath() {
        return this.altPath;
    }

    public final String[] getExpandedNames() {
        return this.expandedNames;
    }

    public final String getNode() {
        return this.node;
    }

    public final int getProximity() {
        return this.proximity;
    }

    public final int getRFlags() {
        return this.rflags;
    }

    public final String getRpath() {
        return this.rpath;
    }

    public final int getServerType() {
        return this.serverType;
    }

    public final int getSize() {
        return this.size;
    }

    public final String getSpecialName() {
        return this.specialName;
    }

    public final int getTtl() {
        return this.ttl;
    }

    public final int getVersion() {
        return this.version;
    }

    public String toString() {
        StringBuilder sb2 = new StringBuilder("Referral[version=");
        sb2.append(this.version);
        sb2.append(",size=");
        sb2.append(this.size);
        sb2.append(",serverType=");
        sb2.append(this.serverType);
        sb2.append(",flags=");
        sb2.append(this.rflags);
        sb2.append(",proximity=");
        sb2.append(this.proximity);
        sb2.append(",ttl=");
        sb2.append(this.ttl);
        sb2.append(",path=");
        sb2.append(this.rpath);
        sb2.append(",altPath=");
        sb2.append(this.altPath);
        sb2.append(",node=");
        return new String(a.q(sb2, this.node, "]"));
    }
}
