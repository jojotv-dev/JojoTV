package jcifs.config;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.List;
import java.util.TimeZone;
import jcifs.Configuration;
import jcifs.DialectVersion;
import jcifs.ResolverType;

/* JADX INFO: loaded from: classes5.dex */
public class DelegatingConfiguration implements Configuration {
    private final Configuration delegate;

    public DelegatingConfiguration(Configuration configuration) {
        this.delegate = configuration;
    }

    @Override // jcifs.Configuration
    public long getAttributeCacheTimeout() {
        return this.delegate.getAttributeCacheTimeout();
    }

    @Override // jcifs.Configuration
    public int getBatchLimit(String str) {
        return this.delegate.getBatchLimit(str);
    }

    @Override // jcifs.Configuration
    public InetAddress getBroadcastAddress() {
        return this.delegate.getBroadcastAddress();
    }

    @Override // jcifs.Configuration
    public int getBufferCacheSize() {
        return this.delegate.getBufferCacheSize();
    }

    @Override // jcifs.Configuration
    public int getCapabilities() {
        return this.delegate.getCapabilities();
    }

    @Override // jcifs.Configuration
    public int getConnTimeout() {
        return this.delegate.getConnTimeout();
    }

    @Override // jcifs.Configuration
    public String getDefaultDomain() {
        return this.delegate.getDefaultDomain();
    }

    @Override // jcifs.Configuration
    public String getDefaultPassword() {
        return this.delegate.getDefaultPassword();
    }

    @Override // jcifs.Configuration
    public String getDefaultUsername() {
        return this.delegate.getDefaultUsername();
    }

    @Override // jcifs.Configuration
    public long getDfsTtl() {
        return this.delegate.getDfsTtl();
    }

    @Override // jcifs.Configuration
    public int getFlags2() {
        return this.delegate.getFlags2();
    }

    @Override // jcifs.Configuration
    public String getGuestPassword() {
        return this.delegate.getGuestPassword();
    }

    @Override // jcifs.Configuration
    public String getGuestUsername() {
        return this.delegate.getGuestUsername();
    }

    @Override // jcifs.Configuration
    public int getLanManCompatibility() {
        return this.delegate.getLanManCompatibility();
    }

    @Override // jcifs.Configuration
    public int getListCount() {
        return this.delegate.getListCount();
    }

    @Override // jcifs.Configuration
    public int getListSize() {
        return this.delegate.getListSize();
    }

    @Override // jcifs.Configuration
    public String getLmHostsFileName() {
        return this.delegate.getLmHostsFileName();
    }

    @Override // jcifs.Configuration
    public InetAddress getLocalAddr() {
        return this.delegate.getLocalAddr();
    }

    @Override // jcifs.Configuration
    public int getLocalPort() {
        return this.delegate.getLocalPort();
    }

    @Override // jcifs.Configuration
    public TimeZone getLocalTimezone() {
        return this.delegate.getLocalTimezone();
    }

    @Override // jcifs.Configuration
    public String getLogonShare() {
        return this.delegate.getLogonShare();
    }

    @Override // jcifs.Configuration
    public byte[] getMachineId() {
        return this.delegate.getMachineId();
    }

    @Override // jcifs.Configuration
    public int getMaxMpxCount() {
        return this.delegate.getMaxMpxCount();
    }

    @Override // jcifs.Configuration
    public int getMaxRequestRetries() {
        return this.delegate.getMaxRequestRetries();
    }

    @Override // jcifs.Configuration
    public int getMaximumBufferSize() {
        return this.delegate.getMaximumBufferSize();
    }

    @Override // jcifs.Configuration
    public DialectVersion getMaximumVersion() {
        return this.delegate.getMaximumVersion();
    }

    @Override // jcifs.Configuration
    public DialectVersion getMinimumVersion() {
        return this.delegate.getMinimumVersion();
    }

    @Override // jcifs.Configuration
    public String getNativeLanman() {
        return this.delegate.getNativeLanman();
    }

    @Override // jcifs.Configuration
    public String getNativeOs() {
        return this.delegate.getNativeOs();
    }

    @Override // jcifs.Configuration
    public int getNetbiosCachePolicy() {
        return this.delegate.getNetbiosCachePolicy();
    }

    @Override // jcifs.Configuration
    public String getNetbiosHostname() {
        return this.delegate.getNetbiosHostname();
    }

    @Override // jcifs.Configuration
    public InetAddress getNetbiosLocalAddress() {
        return this.delegate.getNetbiosLocalAddress();
    }

    @Override // jcifs.Configuration
    public int getNetbiosLocalPort() {
        return this.delegate.getNetbiosLocalPort();
    }

    @Override // jcifs.Configuration
    public int getNetbiosRcvBufSize() {
        return this.delegate.getNetbiosRcvBufSize();
    }

    @Override // jcifs.Configuration
    public int getNetbiosRetryCount() {
        return this.delegate.getNetbiosRetryCount();
    }

    @Override // jcifs.Configuration
    public int getNetbiosRetryTimeout() {
        return this.delegate.getNetbiosRetryTimeout();
    }

    @Override // jcifs.Configuration
    public String getNetbiosScope() {
        return this.delegate.getNetbiosScope();
    }

    @Override // jcifs.Configuration
    public int getNetbiosSndBufSize() {
        return this.delegate.getNetbiosSndBufSize();
    }

    @Override // jcifs.Configuration
    public int getNetbiosSoTimeout() {
        return this.delegate.getNetbiosSoTimeout();
    }

    @Override // jcifs.Configuration
    public int getNotifyBufferSize() {
        return this.delegate.getNotifyBufferSize();
    }

    @Override // jcifs.Configuration
    public String getOemEncoding() {
        return this.delegate.getOemEncoding();
    }

    @Override // jcifs.Configuration
    public int getPid() {
        return this.delegate.getPid();
    }

    @Override // jcifs.Configuration
    public SecureRandom getRandom() {
        return this.delegate.getRandom();
    }

    @Override // jcifs.Configuration
    public int getReceiveBufferSize() {
        return this.delegate.getReceiveBufferSize();
    }

    @Override // jcifs.Configuration
    @Deprecated
    public int getRecieveBufferSize() {
        return this.delegate.getReceiveBufferSize();
    }

    @Override // jcifs.Configuration
    public List<ResolverType> getResolveOrder() {
        return this.delegate.getResolveOrder();
    }

    @Override // jcifs.Configuration
    public int getResponseTimeout() {
        return this.delegate.getResponseTimeout();
    }

    @Override // jcifs.Configuration
    public int getSendBufferSize() {
        return this.delegate.getSendBufferSize();
    }

    @Override // jcifs.Configuration
    public int getSessionLimit() {
        return this.delegate.getSessionLimit();
    }

    @Override // jcifs.Configuration
    public int getSessionTimeout() {
        return this.delegate.getSessionTimeout();
    }

    @Override // jcifs.Configuration
    public int getSoTimeout() {
        return this.delegate.getSoTimeout();
    }

    @Override // jcifs.Configuration
    public int getTransactionBufferSize() {
        return this.delegate.getTransactionBufferSize();
    }

    @Override // jcifs.Configuration
    public int getVcNumber() {
        return this.delegate.getVcNumber();
    }

    @Override // jcifs.Configuration
    public InetAddress[] getWinsServers() {
        return this.delegate.getWinsServers();
    }

    @Override // jcifs.Configuration
    public boolean isAllowCompound(String str) {
        return this.delegate.isAllowCompound(str);
    }

    @Override // jcifs.Configuration
    public boolean isAllowGuestFallback() {
        return this.delegate.isAllowGuestFallback();
    }

    @Override // jcifs.Configuration
    public boolean isAllowNTLMFallback() {
        return this.delegate.isAllowNTLMFallback();
    }

    @Override // jcifs.Configuration
    public boolean isDfsConvertToFQDN() {
        return this.delegate.isDfsConvertToFQDN();
    }

    @Override // jcifs.Configuration
    public boolean isDfsDisabled() {
        return this.delegate.isDfsDisabled();
    }

    @Override // jcifs.Configuration
    public boolean isDfsStrictView() {
        return this.delegate.isDfsStrictView();
    }

    @Override // jcifs.Configuration
    public boolean isDisablePlainTextPasswords() {
        return this.delegate.isDisablePlainTextPasswords();
    }

    @Override // jcifs.Configuration
    public boolean isDisableSpnegoIntegrity() {
        return this.delegate.isDisableSpnegoIntegrity();
    }

    @Override // jcifs.Configuration
    public boolean isEncryptionEnabled() {
        return this.delegate.isEncryptionEnabled();
    }

    @Override // jcifs.Configuration
    public boolean isEnforceSpnegoIntegrity() {
        return this.delegate.isEnforceSpnegoIntegrity();
    }

    @Override // jcifs.Configuration
    public boolean isForceExtendedSecurity() {
        return this.delegate.isForceExtendedSecurity();
    }

    @Override // jcifs.Configuration
    public boolean isForceUnicode() {
        return this.delegate.isForceUnicode();
    }

    @Override // jcifs.Configuration
    public boolean isIgnoreCopyToException() {
        return this.delegate.isIgnoreCopyToException();
    }

    @Override // jcifs.Configuration
    public boolean isIpcSigningEnforced() {
        return this.delegate.isIpcSigningEnforced();
    }

    @Override // jcifs.Configuration
    public boolean isPort139FailoverEnabled() {
        return this.delegate.isPort139FailoverEnabled();
    }

    @Override // jcifs.Configuration
    public boolean isRequireSecureNegotiate() {
        return this.delegate.isRequireSecureNegotiate();
    }

    @Override // jcifs.Configuration
    public boolean isSendNTLMTargetName() {
        return this.delegate.isSendNTLMTargetName();
    }

    @Override // jcifs.Configuration
    public boolean isSigningEnabled() {
        return this.delegate.isSigningEnabled();
    }

    @Override // jcifs.Configuration
    public boolean isSigningEnforced() {
        return this.delegate.isSigningEnforced();
    }

    @Override // jcifs.Configuration
    public boolean isStrictResourceLifecycle() {
        return this.delegate.isStrictResourceLifecycle();
    }

    @Override // jcifs.Configuration
    public boolean isTraceResourceUsage() {
        return this.delegate.isTraceResourceUsage();
    }

    @Override // jcifs.Configuration
    public boolean isUseBatching() {
        return this.delegate.isUseBatching();
    }

    @Override // jcifs.Configuration
    public boolean isUseRawNTLM() {
        return this.delegate.isUseRawNTLM();
    }

    @Override // jcifs.Configuration
    public boolean isUseSMB2OnlyNegotiation() {
        return this.delegate.isUseSMB2OnlyNegotiation();
    }

    @Override // jcifs.Configuration
    public boolean isUseUnicode() {
        return this.delegate.isUseUnicode();
    }
}
