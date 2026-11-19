package org.jsoup.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.jsoup.Connection;

/* JADX INFO: loaded from: classes5.dex */
class UrlConnectionExecutor extends RequestExecutor {
    HttpURLConnection conn;

    public UrlConnectionExecutor(HttpConnection.Request request, HttpConnection.Response response) {
        super(request, response);
    }

    private static HttpURLConnection createConnection(HttpConnection.Request request) throws ProtocolException {
        Proxy proxy = request.proxy();
        HttpURLConnection httpURLConnection = (HttpURLConnection) (proxy == null ? request.url().openConnection() : request.url().openConnection(proxy));
        httpURLConnection.setRequestMethod(request.method().name());
        httpURLConnection.setInstanceFollowRedirects(false);
        httpURLConnection.setConnectTimeout(request.timeout());
        httpURLConnection.setReadTimeout(request.timeout() / 2);
        if (httpURLConnection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) httpURLConnection;
            SSLContext sSLContext = request.sslContext;
            if (sSLContext != null) {
                httpsURLConnection.setSSLSocketFactory(sSLContext.getSocketFactory());
            } else if (request.sslSocketFactory() != null) {
                httpsURLConnection.setSSLSocketFactory(request.sslSocketFactory());
            }
        }
        RequestAuthenticator requestAuthenticator = request.authenticator;
        if (requestAuthenticator != null) {
            AuthenticationHandler.handler.enable(requestAuthenticator, httpURLConnection);
        }
        if (request.method().hasBody()) {
            httpURLConnection.setDoOutput(true);
        }
        CookieUtil.applyCookiesToRequest(request, new com.nuvio.tv.domain.model.a(httpURLConnection, 1));
        for (Map.Entry entry : request.multiHeaders().entrySet()) {
            Iterator it = ((List) entry.getValue()).iterator();
            while (it.hasNext()) {
                httpURLConnection.addRequestProperty((String) entry.getKey(), (String) it.next());
            }
        }
        return httpURLConnection;
    }

    private static LinkedHashMap<String, List<String>> createHeaderMap(HttpURLConnection httpURLConnection) {
        LinkedHashMap<String, List<String>> linkedHashMap = new LinkedHashMap<>();
        int i10 = 0;
        while (true) {
            String headerFieldKey = httpURLConnection.getHeaderFieldKey(i10);
            String headerField = httpURLConnection.getHeaderField(i10);
            if (headerFieldKey == null && headerField == null) {
                return linkedHashMap;
            }
            i10++;
            if (headerFieldKey != null && headerField != null) {
                linkedHashMap.computeIfAbsent(headerFieldKey, new a()).add(headerField);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ List lambda$createHeaderMap$0(String str) {
        return new ArrayList();
    }

    @Override // org.jsoup.helper.RequestExecutor
    public HttpConnection.Response execute() throws IOException {
        try {
            HttpURLConnection httpURLConnectionCreateConnection = createConnection(this.req);
            this.conn = httpURLConnectionCreateConnection;
            httpURLConnectionCreateConnection.connect();
            if (this.conn.getDoOutput()) {
                try {
                    OutputStream outputStream = this.conn.getOutputStream();
                    try {
                        HttpConnection.Response.writePost(this.req, outputStream);
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (Throwable th2) {
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (Throwable th3) {
                                th2.addSuppressed(th3);
                            }
                        }
                        throw th2;
                    }
                } catch (IOException e10) {
                    this.conn.disconnect();
                    throw e10;
                }
            }
            HttpConnection.Response response = new HttpConnection.Response(this.req);
            response.executor = this;
            response.method = Connection.Method.valueOf(this.conn.getRequestMethod());
            response.url = this.conn.getURL();
            response.statusCode = this.conn.getResponseCode();
            String responseMessage = this.conn.getResponseMessage();
            response.statusMessage = responseMessage;
            if (responseMessage == null) {
                response.statusMessage = "";
            }
            response.contentType = this.conn.getContentType();
            response.contentLength = this.conn.getContentLength();
            response.prepareResponse(createHeaderMap(this.conn), this.prevRes);
            return response;
        } catch (IOException e11) {
            safeClose();
            throw e11;
        }
    }

    @Override // org.jsoup.helper.RequestExecutor
    public InputStream responseBody() {
        HttpURLConnection httpURLConnection = this.conn;
        if (httpURLConnection != null) {
            return httpURLConnection.getErrorStream() != null ? this.conn.getErrorStream() : this.conn.getInputStream();
        }
        throw new IllegalStateException("Not yet executed");
    }

    @Override // org.jsoup.helper.RequestExecutor
    public void safeClose() {
        HttpURLConnection httpURLConnection = this.conn;
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
            this.conn = null;
        }
    }
}
