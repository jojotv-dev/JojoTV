package jcifs;

import java.util.EnumSet;
import java.util.Set;
import jcifs.internal.smb2.Smb2Constants;

/* JADX INFO: loaded from: classes5.dex */
public enum DialectVersion {
    SMB1,
    SMB202(Smb2Constants.SMB2_DIALECT_0202),
    SMB210(Smb2Constants.SMB2_DIALECT_0210),
    SMB300(Smb2Constants.SMB2_DIALECT_0300),
    SMB302(Smb2Constants.SMB2_DIALECT_0302),
    SMB311(Smb2Constants.SMB2_DIALECT_0311);

    private final int dialect;
    private final boolean smb2;

    DialectVersion() {
        this.smb2 = false;
        this.dialect = -1;
    }

    public static DialectVersion max(DialectVersion dialectVersion, DialectVersion dialectVersion2) {
        return dialectVersion.atLeast(dialectVersion2) ? dialectVersion : dialectVersion2;
    }

    public static DialectVersion min(DialectVersion dialectVersion, DialectVersion dialectVersion2) {
        return dialectVersion.atMost(dialectVersion2) ? dialectVersion : dialectVersion2;
    }

    public static Set<DialectVersion> range(DialectVersion dialectVersion, DialectVersion dialectVersion2) {
        EnumSet enumSetNoneOf = EnumSet.noneOf(DialectVersion.class);
        for (DialectVersion dialectVersion3 : values()) {
            if ((dialectVersion == null || dialectVersion3.atLeast(dialectVersion)) && (dialectVersion2 == null || dialectVersion3.atMost(dialectVersion2))) {
                enumSetNoneOf.add(dialectVersion3);
            }
        }
        return enumSetNoneOf;
    }

    public boolean atLeast(DialectVersion dialectVersion) {
        return ordinal() >= dialectVersion.ordinal();
    }

    public boolean atMost(DialectVersion dialectVersion) {
        return ordinal() <= dialectVersion.ordinal();
    }

    public final int getDialect() {
        if (this.smb2) {
            return this.dialect;
        }
        throw new UnsupportedOperationException();
    }

    public final boolean isSMB2() {
        return this.smb2;
    }

    DialectVersion(int i10) {
        this.smb2 = true;
        this.dialect = i10;
    }
}
