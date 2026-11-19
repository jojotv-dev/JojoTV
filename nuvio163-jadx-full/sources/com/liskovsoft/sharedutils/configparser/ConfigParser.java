package com.liskovsoft.sharedutils.configparser;

/* JADX INFO: loaded from: classes4.dex */
public interface ConfigParser {
    String get(String str);

    String[] getArray(String str);

    boolean getBoolean(String str);
}
