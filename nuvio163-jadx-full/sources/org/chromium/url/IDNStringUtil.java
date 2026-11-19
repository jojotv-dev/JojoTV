package org.chromium.url;

import java.net.IDN;
import org.chromium.base.annotations.CalledByNative;

/* JADX INFO: loaded from: classes5.dex */
public class IDNStringUtil {
    @CalledByNative
    public static String idnToASCII(String str) {
        try {
            return IDN.toASCII(str, 2);
        } catch (Exception unused) {
            return null;
        }
    }
}
