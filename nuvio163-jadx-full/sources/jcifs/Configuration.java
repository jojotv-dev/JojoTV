package jcifs;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.List;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes5.dex */
public interface Configuration {
    long getAttributeCacheTimeout();

    int getBatchLimit(String str);

    InetAddress getBroadcastAddress();

    int getBufferCacheSize();

    int getCapabilities();

    int getConnTimeout();

    String getDefaultDomain();

    String getDefaultPassword();

    String getDefaultUsername();

    long getDfsTtl();

    int getFlags2();

    String getGuestPassword();

    String getGuestUsername();

    int getLanManCompatibility();

    int getListCount();

    int getListSize();

    String getLmHostsFileName();

    InetAddress getLocalAddr();

    int getLocalPort();

    TimeZone getLocalTimezone();

    String getLogonShare();

    byte[] getMachineId();

    int getMaxMpxCount();

    int getMaxRequestRetries();

    int getMaximumBufferSize();

    DialectVersion getMaximumVersion();

    DialectVersion getMinimumVersion();

    String getNativeLanman();

    String getNativeOs();

    int getNetbiosCachePolicy();

    String getNetbiosHostname();

    InetAddress getNetbiosLocalAddress();

    int getNetbiosLocalPort();

    int getNetbiosRcvBufSize();

    int getNetbiosRetryCount();

    int getNetbiosRetryTimeout();

    String getNetbiosScope();

    int getNetbiosSndBufSize();

    int getNetbiosSoTimeout();

    int getNotifyBufferSize();

    String getOemEncoding();

    int getPid();

    SecureRandom getRandom();

    int getReceiveBufferSize();

    @Deprecated
    int getRecieveBufferSize();

    List<ResolverType> getResolveOrder();

    int getResponseTimeout();

    int getSendBufferSize();

    int getSessionLimit();

    int getSessionTimeout();

    int getSoTimeout();

    int getTransactionBufferSize();

    int getVcNumber();

    InetAddress[] getWinsServers();

    boolean isAllowCompound(String str);

    boolean isAllowGuestFallback();

    boolean isAllowNTLMFallback();

    boolean isDfsConvertToFQDN();

    boolean isDfsDisabled();

    boolean isDfsStrictView();

    boolean isDisablePlainTextPasswords();

    boolean isDisableSpnegoIntegrity();

    boolean isEncryptionEnabled();

    boolean isEnforceSpnegoIntegrity();

    boolean isForceExtendedSecurity();

    boolean isForceUnicode();

    boolean isIgnoreCopyToException();

    boolean isIpcSigningEnforced();

    boolean isPort139FailoverEnabled();

    boolean isRequireSecureNegotiate();

    boolean isSendNTLMTargetName();

    boolean isSigningEnabled();

    boolean isSigningEnforced();

    boolean isStrictResourceLifecycle();

    boolean isTraceResourceUsage();

    boolean isUseBatching();

    boolean isUseRawNTLM();

    boolean isUseSMB2OnlyNegotiation();

    boolean isUseUnicode();
}
