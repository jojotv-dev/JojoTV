package jcifs.smb;

import java.util.HashMap;
import java.util.Map;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

/* JADX INFO: loaded from: classes5.dex */
class StaticJAASConfiguration extends Configuration {
    private Map<String, ?> options;

    public StaticJAASConfiguration() {
        this.options = new HashMap();
    }

    public AppConfigurationEntry[] getAppConfigurationEntry(String str) {
        return new AppConfigurationEntry[]{new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, this.options)};
    }

    public StaticJAASConfiguration(Map<String, ?> map) {
        this.options = map;
    }
}
