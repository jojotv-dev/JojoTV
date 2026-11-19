package io.sentry.transport;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/* JADX INFO: loaded from: classes5.dex */
public final class l extends Authenticator {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f26206a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f26207b;

    public l(String str, String str2) {
        this.f26206a = str;
        this.f26207b = str2;
    }

    @Override // java.net.Authenticator
    public final PasswordAuthentication getPasswordAuthentication() {
        if (getRequestorType() != Authenticator.RequestorType.PROXY) {
            return null;
        }
        return new PasswordAuthentication(this.f26206a, this.f26207b.toCharArray());
    }
}
