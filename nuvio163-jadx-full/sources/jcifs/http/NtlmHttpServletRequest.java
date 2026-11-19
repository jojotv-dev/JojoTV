package jcifs.http;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/* JADX INFO: loaded from: classes5.dex */
class NtlmHttpServletRequest extends HttpServletRequestWrapper {
    Principal principal;

    public NtlmHttpServletRequest(HttpServletRequest httpServletRequest, Principal principal) {
        super(httpServletRequest);
        this.principal = principal;
    }

    public String getAuthType() {
        return "NTLM";
    }

    public String getRemoteUser() {
        return this.principal.getName();
    }

    public Principal getUserPrincipal() {
        return this.principal;
    }
}
