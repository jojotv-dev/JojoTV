package com.liskovsoft.sharedutils.mylogger;

/* JADX INFO: loaded from: classes4.dex */
class SystemLogger extends MyLogger {
    @Override // com.liskovsoft.sharedutils.mylogger.MyLogger
    public void d(String str, String str2) {
        if (str2 != null) {
            android.util.Log.d(str, str2);
        }
    }

    @Override // com.liskovsoft.sharedutils.mylogger.MyLogger
    public void e(String str, String str2) {
        if (str2 != null) {
            android.util.Log.e(str, str2);
        }
    }

    @Override // com.liskovsoft.sharedutils.mylogger.MyLogger
    public String getLogType() {
        return Log.LOG_TYPE_SYSTEM;
    }

    @Override // com.liskovsoft.sharedutils.mylogger.MyLogger
    public void i(String str, String str2) {
        if (str2 != null) {
            android.util.Log.i(str, str2);
        }
    }

    @Override // com.liskovsoft.sharedutils.mylogger.MyLogger
    public void w(String str, String str2) {
        if (str2 != null) {
            android.util.Log.w(str, str2);
        }
    }
}
