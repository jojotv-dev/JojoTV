package a9;

import java.util.EnumSet;
import java.util.Iterator;
import jcifs.internal.smb2.Smb2Constants;

/* JADX INFO: loaded from: classes4.dex */
public enum i {
    UNKNOWN(0),
    SMB_2_0_2(Smb2Constants.SMB2_DIALECT_0202),
    SMB_2_1(Smb2Constants.SMB2_DIALECT_0210),
    /* JADX INFO: Fake field, exist only in values array */
    SMB_2XX(Smb2Constants.SMB2_DIALECT_ANY),
    SMB_3_0(Smb2Constants.SMB2_DIALECT_0300),
    SMB_3_0_2(Smb2Constants.SMB2_DIALECT_0302),
    SMB_3_1_1(Smb2Constants.SMB2_DIALECT_0311);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f657b;

    i(int i10) {
        this.f657b = i10;
    }

    public static boolean b(EnumSet enumSet) {
        Iterator it = enumSet.iterator();
        while (it.hasNext()) {
            if (((i) it.next()).a()) {
                return true;
            }
        }
        return false;
    }

    public final boolean a() {
        return this == SMB_3_0 || this == SMB_3_0_2 || this == SMB_3_1_1;
    }
}
