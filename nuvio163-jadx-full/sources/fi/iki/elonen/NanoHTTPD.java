package fi.iki.elonen;

import androidx.media3.exoplayer.rtsp.RtspHeaders;
import dc.d1;
import io.ktor.http.ContentDisposition;
import io.ktor.sse.ServerSentEventKt;
import j$.util.DesugarTimeZone;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import o2.a;
import okhttp3.internal.http.HttpStatusCodesKt;
import org.jsoup.helper.HttpConnection;

/* JADX INFO: loaded from: classes4.dex */
public abstract class NanoHTTPD {
    public static final String MIME_HTML = "text/html";
    public static final String MIME_PLAINTEXT = "text/plain";
    protected static Map<String, String> MIME_TYPES = null;
    private static final String QUERY_STRING_PARAMETER = "NanoHttpd.QUERY_STRING";
    public static final int SOCKET_READ_TIMEOUT = 5000;
    protected AsyncRunner asyncRunner;
    private final String hostname;
    private final int myPort;
    private volatile ServerSocket myServerSocket;
    private Thread myThread;
    private ServerSocketFactory serverSocketFactory;
    private TempFileManagerFactory tempFileManagerFactory;
    private static final String CONTENT_DISPOSITION_REGEX = "([ |\t]*Content-Disposition[ |\t]*:)(.*)";
    private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile(CONTENT_DISPOSITION_REGEX, 2);
    private static final String CONTENT_TYPE_REGEX = "([ |\t]*content-type[ |\t]*:)(.*)";
    private static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile(CONTENT_TYPE_REGEX, 2);
    private static final String CONTENT_DISPOSITION_ATTRIBUTE_REGEX = "[ |\t]*([a-zA-Z]*)[ |\t]*=[ |\t]*['|\"]([^\"^']*)['|\"]";
    private static final Pattern CONTENT_DISPOSITION_ATTRIBUTE_PATTERN = Pattern.compile(CONTENT_DISPOSITION_ATTRIBUTE_REGEX);
    private static final Logger LOG = Logger.getLogger(NanoHTTPD.class.getName());

    public interface AsyncRunner {
        void closeAll();

        void closed(ClientHandler clientHandler);

        void exec(ClientHandler clientHandler);
    }

    public class ClientHandler implements Runnable {
        private final Socket acceptSocket;
        private final InputStream inputStream;

        public ClientHandler(InputStream inputStream, Socket socket) {
            this.inputStream = inputStream;
            this.acceptSocket = socket;
        }

        public void close() {
            NanoHTTPD.safeClose(this.inputStream);
            NanoHTTPD.safeClose(this.acceptSocket);
        }

        @Override // java.lang.Runnable
        public void run() throws Throwable {
            OutputStream outputStream = null;
            try {
                try {
                    OutputStream outputStream2 = this.acceptSocket.getOutputStream();
                    try {
                        HTTPSession hTTPSession = NanoHTTPD.this.new HTTPSession(NanoHTTPD.this.tempFileManagerFactory.create(), this.inputStream, outputStream2, this.acceptSocket.getInetAddress());
                        while (!this.acceptSocket.isClosed()) {
                            hTTPSession.execute();
                        }
                        NanoHTTPD.safeClose(outputStream2);
                    } catch (Exception e10) {
                        e = e10;
                        outputStream = outputStream2;
                        if ((!(e instanceof SocketException) || !"NanoHttpd Shutdown".equals(e.getMessage())) && !(e instanceof SocketTimeoutException)) {
                            NanoHTTPD.LOG.log(Level.SEVERE, "Communication with the client broken, or an bug in the handler code", (Throwable) e);
                        }
                        NanoHTTPD.safeClose(outputStream);
                    } catch (Throwable th2) {
                        th = th2;
                        outputStream = outputStream2;
                        NanoHTTPD.safeClose(outputStream);
                        NanoHTTPD.safeClose(this.inputStream);
                        NanoHTTPD.safeClose(this.acceptSocket);
                        NanoHTTPD.this.asyncRunner.closed(this);
                        throw th;
                    }
                } catch (Exception e11) {
                    e = e11;
                }
                NanoHTTPD.safeClose(this.inputStream);
                NanoHTTPD.safeClose(this.acceptSocket);
                NanoHTTPD.this.asyncRunner.closed(this);
            } catch (Throwable th3) {
                th = th3;
            }
        }
    }

    public static class ContentType {
        private static final String ASCII_ENCODING = "US-ASCII";
        private static final String MULTIPART_FORM_DATA_HEADER = "multipart/form-data";
        private final String boundary;
        private final String contentType;
        private final String contentTypeHeader;
        private final String encoding;
        private static final String CONTENT_REGEX = "[ |\t]*([^/^ ^;^,]+/[^ ^;^,]+)";
        private static final Pattern MIME_PATTERN = Pattern.compile(CONTENT_REGEX, 2);
        private static final String CHARSET_REGEX = "[ |\t]*(charset)[ |\t]*=[ |\t]*['|\"]?([^\"^'^;^,]*)['|\"]?";
        private static final Pattern CHARSET_PATTERN = Pattern.compile(CHARSET_REGEX, 2);
        private static final String BOUNDARY_REGEX = "[ |\t]*(boundary)[ |\t]*=[ |\t]*['|\"]?([^\"^'^;^,]*)['|\"]?";
        private static final Pattern BOUNDARY_PATTERN = Pattern.compile(BOUNDARY_REGEX, 2);

        public ContentType(String str) {
            this.contentTypeHeader = str;
            if (str != null) {
                this.contentType = getDetailFromContentHeader(str, MIME_PATTERN, "", 1);
                this.encoding = getDetailFromContentHeader(str, CHARSET_PATTERN, null, 2);
            } else {
                this.contentType = "";
                this.encoding = "UTF-8";
            }
            if ("multipart/form-data".equalsIgnoreCase(this.contentType)) {
                this.boundary = getDetailFromContentHeader(str, BOUNDARY_PATTERN, null, 2);
            } else {
                this.boundary = null;
            }
        }

        private String getDetailFromContentHeader(String str, Pattern pattern, String str2, int i10) {
            Matcher matcher = pattern.matcher(str);
            return matcher.find() ? matcher.group(i10) : str2;
        }

        public String getBoundary() {
            return this.boundary;
        }

        public String getContentType() {
            return this.contentType;
        }

        public String getContentTypeHeader() {
            return this.contentTypeHeader;
        }

        public String getEncoding() {
            String str = this.encoding;
            return str == null ? ASCII_ENCODING : str;
        }

        public boolean isMultipart() {
            return "multipart/form-data".equalsIgnoreCase(this.contentType);
        }

        public ContentType tryUTF8() {
            return this.encoding == null ? new ContentType(a.q(new StringBuilder(), this.contentTypeHeader, "; charset=UTF-8")) : this;
        }
    }

    public static class Cookie {

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private final String f18080e;

        /* JADX INFO: renamed from: n, reason: collision with root package name */
        private final String f18081n;

        /* JADX INFO: renamed from: v, reason: collision with root package name */
        private final String f18082v;

        public Cookie(String str, String str2) {
            this(str, str2, 30);
        }

        public static String getHTTPTime(int i10) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            simpleDateFormat.setTimeZone(DesugarTimeZone.getTimeZone("GMT"));
            calendar.add(5, i10);
            return simpleDateFormat.format(calendar.getTime());
        }

        public String getHTTPHeader() {
            return d1.h(this.f18081n, "=", this.f18082v, "; expires=", this.f18080e);
        }

        public Cookie(String str, String str2, int i10) {
            this.f18081n = str;
            this.f18082v = str2;
            this.f18080e = getHTTPTime(i10);
        }

        public Cookie(String str, String str2, String str3) {
            this.f18081n = str;
            this.f18082v = str2;
            this.f18080e = str3;
        }
    }

    public class CookieHandler implements Iterable<String> {
        private final HashMap<String, String> cookies = new HashMap<>();
        private final ArrayList<Cookie> queue = new ArrayList<>();

        public CookieHandler(Map<String, String> map) {
            String str = map.get("cookie");
            if (str != null) {
                for (String str2 : str.split(";")) {
                    String[] strArrSplit = str2.trim().split("=");
                    if (strArrSplit.length == 2) {
                        this.cookies.put(strArrSplit[0], strArrSplit[1]);
                    }
                }
            }
        }

        public void delete(String str) {
            set(str, "-delete-", -30);
        }

        @Override // java.lang.Iterable
        public Iterator<String> iterator() {
            return this.cookies.keySet().iterator();
        }

        public String read(String str) {
            return this.cookies.get(str);
        }

        public void set(Cookie cookie) {
            this.queue.add(cookie);
        }

        public void unloadQueue(Response response) {
            Iterator<Cookie> it = this.queue.iterator();
            while (it.hasNext()) {
                response.addHeader("Set-Cookie", it.next().getHTTPHeader());
            }
        }

        public void set(String str, String str2, int i10) {
            this.queue.add(new Cookie(str, str2, Cookie.getHTTPTime(i10)));
        }
    }

    public static class DefaultAsyncRunner implements AsyncRunner {
        private long requestCount;
        private final List<ClientHandler> running = Collections.synchronizedList(new ArrayList());

        @Override // fi.iki.elonen.NanoHTTPD.AsyncRunner
        public void closeAll() {
            Iterator it = new ArrayList(this.running).iterator();
            while (it.hasNext()) {
                ((ClientHandler) it.next()).close();
            }
        }

        @Override // fi.iki.elonen.NanoHTTPD.AsyncRunner
        public void closed(ClientHandler clientHandler) {
            this.running.remove(clientHandler);
        }

        @Override // fi.iki.elonen.NanoHTTPD.AsyncRunner
        public void exec(ClientHandler clientHandler) {
            this.requestCount++;
            Thread thread = new Thread(clientHandler);
            thread.setDaemon(true);
            thread.setName("NanoHttpd Request Processor (#" + this.requestCount + ")");
            this.running.add(clientHandler);
            thread.start();
        }

        public List<ClientHandler> getRunning() {
            return this.running;
        }
    }

    public static class DefaultServerSocketFactory implements ServerSocketFactory {
        @Override // fi.iki.elonen.NanoHTTPD.ServerSocketFactory
        public ServerSocket create() {
            return new ServerSocket();
        }
    }

    public static class DefaultTempFile implements TempFile {
        private final File file;
        private final OutputStream fstream;

        public DefaultTempFile(File file) throws IOException {
            File fileCreateTempFile = File.createTempFile("NanoHTTPD-", "", file);
            this.file = fileCreateTempFile;
            this.fstream = new FileOutputStream(fileCreateTempFile);
        }

        @Override // fi.iki.elonen.NanoHTTPD.TempFile
        public void delete() throws Exception {
            NanoHTTPD.safeClose(this.fstream);
            if (this.file.delete()) {
                return;
            }
            throw new Exception("could not delete temporary file: " + this.file.getAbsolutePath());
        }

        @Override // fi.iki.elonen.NanoHTTPD.TempFile
        public String getName() {
            return this.file.getAbsolutePath();
        }

        @Override // fi.iki.elonen.NanoHTTPD.TempFile
        public OutputStream open() {
            return this.fstream;
        }
    }

    public static class DefaultTempFileManager implements TempFileManager {
        private final List<TempFile> tempFiles;
        private final File tmpdir;

        public DefaultTempFileManager() {
            File file = new File(System.getProperty("java.io.tmpdir"));
            this.tmpdir = file;
            if (!file.exists()) {
                file.mkdirs();
            }
            this.tempFiles = new ArrayList();
        }

        @Override // fi.iki.elonen.NanoHTTPD.TempFileManager
        public void clear() {
            Iterator<TempFile> it = this.tempFiles.iterator();
            while (it.hasNext()) {
                try {
                    it.next().delete();
                } catch (Exception e10) {
                    NanoHTTPD.LOG.log(Level.WARNING, "could not delete file ", (Throwable) e10);
                }
            }
            this.tempFiles.clear();
        }

        @Override // fi.iki.elonen.NanoHTTPD.TempFileManager
        public TempFile createTempFile(String str) {
            DefaultTempFile defaultTempFile = new DefaultTempFile(this.tmpdir);
            this.tempFiles.add(defaultTempFile);
            return defaultTempFile;
        }
    }

    public class DefaultTempFileManagerFactory implements TempFileManagerFactory {
        private DefaultTempFileManagerFactory() {
        }

        @Override // fi.iki.elonen.NanoHTTPD.TempFileManagerFactory
        public TempFileManager create() {
            return new DefaultTempFileManager();
        }
    }

    public interface IHTTPSession {
        void execute();

        CookieHandler getCookies();

        Map<String, String> getHeaders();

        InputStream getInputStream();

        Method getMethod();

        Map<String, List<String>> getParameters();

        @Deprecated
        Map<String, String> getParms();

        String getQueryParameterString();

        String getRemoteHostName();

        String getRemoteIpAddress();

        String getUri();

        void parseBody(Map<String, String> map);
    }

    public enum Method {
        GET,
        PUT,
        POST,
        DELETE,
        HEAD,
        OPTIONS,
        TRACE,
        CONNECT,
        PATCH,
        PROPFIND,
        PROPPATCH,
        MKCOL,
        MOVE,
        COPY,
        LOCK,
        UNLOCK;

        public static Method lookup(String str) {
            if (str == null) {
                return null;
            }
            try {
                return valueOf(str);
            } catch (IllegalArgumentException unused) {
                return null;
            }
        }
    }

    public static class Response implements Closeable {
        private boolean chunkedTransfer;
        private long contentLength;
        private InputStream data;
        private boolean encodeAsGzip;
        private boolean keepAlive;
        private String mimeType;
        private Method requestMethod;
        private IStatus status;
        private final Map<String, String> header = new HashMap<String, String>() { // from class: fi.iki.elonen.NanoHTTPD.Response.1
            @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
            public String put(String str, String str2) {
                Response.this.lowerCaseHeader.put(str == null ? str : str.toLowerCase(), str2);
                return (String) super.put(str, str2);
            }
        };
        private final Map<String, String> lowerCaseHeader = new HashMap();

        public interface IStatus {
            String getDescription();

            int getRequestStatus();
        }

        public enum Status implements IStatus {
            SWITCH_PROTOCOL(HttpStatusCodesKt.HTTP_SWITCHING_PROTOCOLS, "Switching Protocols"),
            OK(200, "OK"),
            CREATED(201, "Created"),
            ACCEPTED(202, "Accepted"),
            NO_CONTENT(204, "No Content"),
            PARTIAL_CONTENT(206, "Partial Content"),
            MULTI_STATUS(207, "Multi-Status"),
            REDIRECT(301, "Moved Permanently"),
            FOUND(302, "Found"),
            REDIRECT_SEE_OTHER(303, "See Other"),
            NOT_MODIFIED(304, "Not Modified"),
            TEMPORARY_REDIRECT(HttpStatusCodesKt.HTTP_TEMP_REDIRECT, "Temporary Redirect"),
            BAD_REQUEST(400, "Bad Request"),
            UNAUTHORIZED(401, "Unauthorized"),
            FORBIDDEN(403, "Forbidden"),
            NOT_FOUND(404, "Not Found"),
            METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
            NOT_ACCEPTABLE(406, "Not Acceptable"),
            REQUEST_TIMEOUT(408, "Request Timeout"),
            CONFLICT(409, "Conflict"),
            GONE(410, "Gone"),
            LENGTH_REQUIRED(411, "Length Required"),
            PRECONDITION_FAILED(412, "Precondition Failed"),
            PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
            UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
            RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
            EXPECTATION_FAILED(417, "Expectation Failed"),
            TOO_MANY_REQUESTS(429, "Too Many Requests"),
            INTERNAL_ERROR(500, "Internal Server Error"),
            NOT_IMPLEMENTED(501, "Not Implemented"),
            SERVICE_UNAVAILABLE(503, "Service Unavailable"),
            UNSUPPORTED_HTTP_VERSION(505, "HTTP Version Not Supported");

            private final String description;
            private final int requestStatus;

            Status(int i10, String str) {
                this.requestStatus = i10;
                this.description = str;
            }

            public static Status lookup(int i10) {
                for (Status status : values()) {
                    if (status.getRequestStatus() == i10) {
                        return status;
                    }
                }
                return null;
            }

            @Override // fi.iki.elonen.NanoHTTPD.Response.IStatus
            public String getDescription() {
                return "" + this.requestStatus + " " + this.description;
            }

            @Override // fi.iki.elonen.NanoHTTPD.Response.IStatus
            public int getRequestStatus() {
                return this.requestStatus;
            }
        }

        public Response(IStatus iStatus, String str, InputStream inputStream, long j10) {
            this.status = iStatus;
            this.mimeType = str;
            if (inputStream == null) {
                this.data = new ByteArrayInputStream(new byte[0]);
                this.contentLength = 0L;
            } else {
                this.data = inputStream;
                this.contentLength = j10;
            }
            this.chunkedTransfer = this.contentLength < 0;
            this.keepAlive = true;
        }

        private void sendBody(OutputStream outputStream, long j10) throws IOException {
            byte[] bArr = new byte[(int) 16384];
            boolean z2 = j10 == -1;
            while (true) {
                if (j10 <= 0 && !z2) {
                    return;
                }
                int i10 = this.data.read(bArr, 0, (int) (z2 ? 16384L : Math.min(j10, 16384L)));
                if (i10 <= 0) {
                    return;
                }
                outputStream.write(bArr, 0, i10);
                if (!z2) {
                    j10 -= (long) i10;
                }
            }
        }

        private void sendBodyWithCorrectEncoding(OutputStream outputStream, long j10) throws IOException {
            if (!this.encodeAsGzip) {
                sendBody(outputStream, j10);
                return;
            }
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(outputStream);
            sendBody(gZIPOutputStream, -1L);
            gZIPOutputStream.finish();
        }

        private void sendBodyWithCorrectTransferAndEncoding(OutputStream outputStream, long j10) throws IOException {
            if (this.requestMethod == Method.HEAD || !this.chunkedTransfer) {
                sendBodyWithCorrectEncoding(outputStream, j10);
                return;
            }
            ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(outputStream);
            sendBodyWithCorrectEncoding(chunkedOutputStream, -1L);
            chunkedOutputStream.finish();
        }

        public void addHeader(String str, String str2) {
            this.header.put(str, str2);
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            InputStream inputStream = this.data;
            if (inputStream != null) {
                inputStream.close();
            }
        }

        public void closeConnection(boolean z2) {
            if (z2) {
                this.header.put("connection", "close");
            } else {
                this.header.remove("connection");
            }
        }

        public InputStream getData() {
            return this.data;
        }

        public String getHeader(String str) {
            return this.lowerCaseHeader.get(str.toLowerCase());
        }

        public String getMimeType() {
            return this.mimeType;
        }

        public Method getRequestMethod() {
            return this.requestMethod;
        }

        public IStatus getStatus() {
            return this.status;
        }

        public boolean isCloseConnection() {
            return "close".equals(getHeader("connection"));
        }

        public void printHeader(PrintWriter printWriter, String str, String str2) {
            printWriter.append((CharSequence) str).append(": ").append((CharSequence) str2).append(ServerSentEventKt.END_OF_LINE);
        }

        public void send(OutputStream outputStream) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            simpleDateFormat.setTimeZone(DesugarTimeZone.getTimeZone("GMT"));
            try {
                if (this.status == null) {
                    throw new Error("sendResponse(): Status can't be null.");
                }
                PrintWriter printWriter = new PrintWriter((Writer) new BufferedWriter(new OutputStreamWriter(outputStream, new ContentType(this.mimeType).getEncoding())), false);
                printWriter.append("HTTP/1.1 ").append((CharSequence) this.status.getDescription()).append(" \r\n");
                String str = this.mimeType;
                if (str != null) {
                    printHeader(printWriter, "Content-Type", str);
                }
                if (getHeader("date") == null) {
                    printHeader(printWriter, RtspHeaders.DATE, simpleDateFormat.format(new Date()));
                }
                for (Map.Entry<String, String> entry : this.header.entrySet()) {
                    printHeader(printWriter, entry.getKey(), entry.getValue());
                }
                if (getHeader("connection") == null) {
                    printHeader(printWriter, RtspHeaders.CONNECTION, this.keepAlive ? "keep-alive" : "close");
                }
                if (getHeader("content-length") != null) {
                    this.encodeAsGzip = false;
                }
                if (this.encodeAsGzip) {
                    printHeader(printWriter, "Content-Encoding", "gzip");
                    setChunkedTransfer(true);
                }
                long jSendContentLengthHeaderIfNotAlreadyPresent = this.data != null ? this.contentLength : 0L;
                if (this.requestMethod != Method.HEAD && this.chunkedTransfer) {
                    printHeader(printWriter, "Transfer-Encoding", "chunked");
                } else if (!this.encodeAsGzip) {
                    jSendContentLengthHeaderIfNotAlreadyPresent = sendContentLengthHeaderIfNotAlreadyPresent(printWriter, jSendContentLengthHeaderIfNotAlreadyPresent);
                }
                printWriter.append(ServerSentEventKt.END_OF_LINE);
                printWriter.flush();
                sendBodyWithCorrectTransferAndEncoding(outputStream, jSendContentLengthHeaderIfNotAlreadyPresent);
                outputStream.flush();
                NanoHTTPD.safeClose(this.data);
            } catch (IOException e10) {
                NanoHTTPD.LOG.log(Level.SEVERE, "Could not send response to the client", (Throwable) e10);
            }
        }

        public long sendContentLengthHeaderIfNotAlreadyPresent(PrintWriter printWriter, long j10) {
            String header = getHeader("content-length");
            if (header != null) {
                try {
                    j10 = Long.parseLong(header);
                } catch (NumberFormatException unused) {
                    NanoHTTPD.LOG.severe("content-length was no number ".concat(header));
                }
            }
            printWriter.print("Content-Length: " + j10 + ServerSentEventKt.END_OF_LINE);
            return j10;
        }

        public void setChunkedTransfer(boolean z2) {
            this.chunkedTransfer = z2;
        }

        public void setData(InputStream inputStream) {
            this.data = inputStream;
        }

        public void setGzipEncoding(boolean z2) {
            this.encodeAsGzip = z2;
        }

        public void setKeepAlive(boolean z2) {
            this.keepAlive = z2;
        }

        public void setMimeType(String str) {
            this.mimeType = str;
        }

        public void setRequestMethod(Method method) {
            this.requestMethod = method;
        }

        public void setStatus(IStatus iStatus) {
            this.status = iStatus;
        }

        public static class ChunkedOutputStream extends FilterOutputStream {
            public ChunkedOutputStream(OutputStream outputStream) {
                super(outputStream);
            }

            public void finish() throws IOException {
                ((FilterOutputStream) this).out.write("0\r\n\r\n".getBytes());
            }

            @Override // java.io.FilterOutputStream, java.io.OutputStream
            public void write(int i10) throws IOException {
                write(new byte[]{(byte) i10}, 0, 1);
            }

            @Override // java.io.FilterOutputStream, java.io.OutputStream
            public void write(byte[] bArr) throws IOException {
                write(bArr, 0, bArr.length);
            }

            @Override // java.io.FilterOutputStream, java.io.OutputStream
            public void write(byte[] bArr, int i10, int i11) throws IOException {
                if (i11 == 0) {
                    return;
                }
                ((FilterOutputStream) this).out.write(String.format("%x\r\n", Integer.valueOf(i11)).getBytes());
                ((FilterOutputStream) this).out.write(bArr, i10, i11);
                ((FilterOutputStream) this).out.write(ServerSentEventKt.END_OF_LINE.getBytes());
            }
        }
    }

    public static class SecureServerSocketFactory implements ServerSocketFactory {
        private String[] sslProtocols;
        private SSLServerSocketFactory sslServerSocketFactory;

        public SecureServerSocketFactory(SSLServerSocketFactory sSLServerSocketFactory, String[] strArr) {
            this.sslServerSocketFactory = sSLServerSocketFactory;
            this.sslProtocols = strArr;
        }

        @Override // fi.iki.elonen.NanoHTTPD.ServerSocketFactory
        public ServerSocket create() {
            SSLServerSocket sSLServerSocket = (SSLServerSocket) this.sslServerSocketFactory.createServerSocket();
            String[] strArr = this.sslProtocols;
            if (strArr != null) {
                sSLServerSocket.setEnabledProtocols(strArr);
            } else {
                sSLServerSocket.setEnabledProtocols(sSLServerSocket.getSupportedProtocols());
            }
            sSLServerSocket.setUseClientMode(false);
            sSLServerSocket.setWantClientAuth(false);
            sSLServerSocket.setNeedClientAuth(false);
            return sSLServerSocket;
        }
    }

    public class ServerRunnable implements Runnable {
        private IOException bindException;
        private boolean hasBinded = false;
        private final int timeout;

        public ServerRunnable(int i10) {
            this.timeout = i10;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                NanoHTTPD.this.myServerSocket.bind(NanoHTTPD.this.hostname != null ? new InetSocketAddress(NanoHTTPD.this.hostname, NanoHTTPD.this.myPort) : new InetSocketAddress(NanoHTTPD.this.myPort));
                this.hasBinded = true;
                do {
                    try {
                        Socket socketAccept = NanoHTTPD.this.myServerSocket.accept();
                        int i10 = this.timeout;
                        if (i10 > 0) {
                            socketAccept.setSoTimeout(i10);
                        }
                        InputStream inputStream = socketAccept.getInputStream();
                        NanoHTTPD nanoHTTPD = NanoHTTPD.this;
                        nanoHTTPD.asyncRunner.exec(nanoHTTPD.createClientHandler(socketAccept, inputStream));
                    } catch (IOException e10) {
                        NanoHTTPD.LOG.log(Level.FINE, "Communication with the client broken", (Throwable) e10);
                    }
                } while (!NanoHTTPD.this.myServerSocket.isClosed());
            } catch (IOException e11) {
                this.bindException = e11;
            }
        }
    }

    public interface ServerSocketFactory {
        ServerSocket create();
    }

    public interface TempFile {
        void delete();

        String getName();

        OutputStream open();
    }

    public interface TempFileManager {
        void clear();

        TempFile createTempFile(String str);
    }

    public interface TempFileManagerFactory {
        TempFileManager create();
    }

    public NanoHTTPD(int i10) {
        this(null, i10);
    }

    public static Map<String, List<String>> decodeParameters(Map<String, String> map) {
        return decodeParameters(map.get(QUERY_STRING_PARAMETER));
    }

    public static String decodePercent(String str) {
        try {
            return URLDecoder.decode(str, "UTF8");
        } catch (UnsupportedEncodingException e10) {
            LOG.log(Level.WARNING, "Encoding not supported, ignored", (Throwable) e10);
            return null;
        }
    }

    public static String getMimeTypeForFile(String str) {
        int iLastIndexOf = str.lastIndexOf(46);
        String str2 = iLastIndexOf >= 0 ? mimeTypes().get(str.substring(iLastIndexOf + 1).toLowerCase()) : null;
        return str2 == null ? "application/octet-stream" : str2;
    }

    private static void loadMimeTypes(Map<String, String> map, String str) {
        try {
            Enumeration<URL> resources = NanoHTTPD.class.getClassLoader().getResources(str);
            while (resources.hasMoreElements()) {
                URL urlNextElement = resources.nextElement();
                Properties properties = new Properties();
                InputStream inputStreamOpenStream = null;
                try {
                    try {
                        inputStreamOpenStream = urlNextElement.openStream();
                        properties.load(inputStreamOpenStream);
                    } catch (IOException e10) {
                        LOG.log(Level.SEVERE, "could not load mimetypes from " + urlNextElement, (Throwable) e10);
                    }
                    safeClose(inputStreamOpenStream);
                    map.putAll(properties);
                } catch (Throwable th2) {
                    safeClose(inputStreamOpenStream);
                    throw th2;
                }
            }
        } catch (IOException unused) {
            LOG.log(Level.INFO, "no mime types available at " + str);
        }
    }

    public static SSLServerSocketFactory makeSSLSocketFactory(KeyStore keyStore, KeyManager[] keyManagerArr) throws IOException {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            SSLContext sSLContext = SSLContext.getInstance("TLS");
            sSLContext.init(keyManagerArr, trustManagerFactory.getTrustManagers(), null);
            return sSLContext.getServerSocketFactory();
        } catch (Exception e10) {
            throw new IOException(e10.getMessage());
        }
    }

    public static Map<String, String> mimeTypes() {
        if (MIME_TYPES == null) {
            HashMap map = new HashMap();
            MIME_TYPES = map;
            loadMimeTypes(map, "META-INF/nanohttpd/default-mimetypes.properties");
            loadMimeTypes(MIME_TYPES, "META-INF/nanohttpd/mimetypes.properties");
            if (MIME_TYPES.isEmpty()) {
                LOG.log(Level.WARNING, "no mime types found in the classpath! please provide mimetypes.properties");
            }
        }
        return MIME_TYPES;
    }

    public static Response newChunkedResponse(Response.IStatus iStatus, String str, InputStream inputStream) {
        return new Response(iStatus, str, inputStream, -1L);
    }

    public static Response newFixedLengthResponse(Response.IStatus iStatus, String str, InputStream inputStream, long j10) {
        return new Response(iStatus, str, inputStream, j10);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void safeClose(Object obj) {
        if (obj != null) {
            try {
                if (obj instanceof Closeable) {
                    ((Closeable) obj).close();
                } else if (obj instanceof Socket) {
                    ((Socket) obj).close();
                } else {
                    if (!(obj instanceof ServerSocket)) {
                        throw new IllegalArgumentException("Unknown object to close");
                    }
                    ((ServerSocket) obj).close();
                }
            } catch (IOException e10) {
                LOG.log(Level.SEVERE, "Could not close", (Throwable) e10);
            }
        }
    }

    public synchronized void closeAllConnections() {
        stop();
    }

    public ClientHandler createClientHandler(Socket socket, InputStream inputStream) {
        return new ClientHandler(inputStream, socket);
    }

    public ServerRunnable createServerRunnable(int i10) {
        return new ServerRunnable(i10);
    }

    public String getHostname() {
        return this.hostname;
    }

    public final int getListeningPort() {
        if (this.myServerSocket == null) {
            return -1;
        }
        return this.myServerSocket.getLocalPort();
    }

    public ServerSocketFactory getServerSocketFactory() {
        return this.serverSocketFactory;
    }

    public TempFileManagerFactory getTempFileManagerFactory() {
        return this.tempFileManagerFactory;
    }

    public final boolean isAlive() {
        return wasStarted() && !this.myServerSocket.isClosed() && this.myThread.isAlive();
    }

    public void makeSecure(SSLServerSocketFactory sSLServerSocketFactory, String[] strArr) {
        this.serverSocketFactory = new SecureServerSocketFactory(sSLServerSocketFactory, strArr);
    }

    public Response serve(IHTTPSession iHTTPSession) {
        HashMap map = new HashMap();
        Method method = iHTTPSession.getMethod();
        if (Method.PUT.equals(method) || Method.POST.equals(method)) {
            try {
                iHTTPSession.parseBody(map);
            } catch (ResponseException e10) {
                return newFixedLengthResponse(e10.getStatus(), MIME_PLAINTEXT, e10.getMessage());
            } catch (IOException e11) {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + e11.getMessage());
            }
        }
        Map<String, String> parms = iHTTPSession.getParms();
        parms.put(QUERY_STRING_PARAMETER, iHTTPSession.getQueryParameterString());
        return serve(iHTTPSession.getUri(), method, iHTTPSession.getHeaders(), parms, map);
    }

    public void setAsyncRunner(AsyncRunner asyncRunner) {
        this.asyncRunner = asyncRunner;
    }

    public void setServerSocketFactory(ServerSocketFactory serverSocketFactory) {
        this.serverSocketFactory = serverSocketFactory;
    }

    public void setTempFileManagerFactory(TempFileManagerFactory tempFileManagerFactory) {
        this.tempFileManagerFactory = tempFileManagerFactory;
    }

    public void start() throws IOException {
        start(5000);
    }

    public void stop() {
        try {
            safeClose(this.myServerSocket);
            this.asyncRunner.closeAll();
            Thread thread = this.myThread;
            if (thread != null) {
                thread.join();
            }
        } catch (Exception e10) {
            LOG.log(Level.SEVERE, "Could not stop all connections", (Throwable) e10);
        }
    }

    public boolean useGzipWhenAccepted(Response response) {
        if (response.getMimeType() != null) {
            return response.getMimeType().toLowerCase().contains("text/") || response.getMimeType().toLowerCase().contains("/json");
        }
        return false;
    }

    public final boolean wasStarted() {
        return (this.myServerSocket == null || this.myThread == null) ? false : true;
    }

    public static final class ResponseException extends Exception {
        private static final long serialVersionUID = 6569838532917408380L;
        private final Response.Status status;

        public ResponseException(Response.Status status, String str) {
            super(str);
            this.status = status;
        }

        public Response.Status getStatus() {
            return this.status;
        }

        public ResponseException(Response.Status status, String str, Exception exc) {
            super(str, exc);
            this.status = status;
        }
    }

    public NanoHTTPD(String str, int i10) {
        this.serverSocketFactory = new DefaultServerSocketFactory();
        this.hostname = str;
        this.myPort = i10;
        setTempFileManagerFactory(new DefaultTempFileManagerFactory());
        setAsyncRunner(new DefaultAsyncRunner());
    }

    public static Map<String, List<String>> decodeParameters(String str) {
        HashMap map = new HashMap();
        if (str != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(str, "&");
            while (stringTokenizer.hasMoreTokens()) {
                String strNextToken = stringTokenizer.nextToken();
                int iIndexOf = strNextToken.indexOf(61);
                String strTrim = (iIndexOf >= 0 ? decodePercent(strNextToken.substring(0, iIndexOf)) : decodePercent(strNextToken)).trim();
                if (!map.containsKey(strTrim)) {
                    map.put(strTrim, new ArrayList());
                }
                String strDecodePercent = iIndexOf >= 0 ? decodePercent(strNextToken.substring(iIndexOf + 1)) : null;
                if (strDecodePercent != null) {
                    ((List) map.get(strTrim)).add(strDecodePercent);
                }
            }
        }
        return map;
    }

    public static Response newFixedLengthResponse(Response.IStatus iStatus, String str, String str2) {
        byte[] bytes;
        ContentType contentType = new ContentType(str);
        if (str2 == null) {
            return newFixedLengthResponse(iStatus, str, new ByteArrayInputStream(new byte[0]), 0L);
        }
        try {
            if (!Charset.forName(contentType.getEncoding()).newEncoder().canEncode(str2)) {
                contentType = contentType.tryUTF8();
            }
            bytes = str2.getBytes(contentType.getEncoding());
        } catch (UnsupportedEncodingException e10) {
            LOG.log(Level.SEVERE, "encoding problem, responding nothing", (Throwable) e10);
            bytes = new byte[0];
        }
        return newFixedLengthResponse(iStatus, contentType.getContentTypeHeader(), new ByteArrayInputStream(bytes), bytes.length);
    }

    public void start(int i10) throws IOException {
        start(i10, true);
    }

    public void start(int i10, boolean z2) throws IOException {
        this.myServerSocket = getServerSocketFactory().create();
        this.myServerSocket.setReuseAddress(true);
        ServerRunnable serverRunnableCreateServerRunnable = createServerRunnable(i10);
        Thread thread = new Thread(serverRunnableCreateServerRunnable);
        this.myThread = thread;
        thread.setDaemon(z2);
        this.myThread.setName("NanoHttpd Main Listener");
        this.myThread.start();
        while (!serverRunnableCreateServerRunnable.hasBinded && serverRunnableCreateServerRunnable.bindException == null) {
            try {
                Thread.sleep(10L);
            } catch (Throwable unused) {
            }
        }
        if (serverRunnableCreateServerRunnable.bindException != null) {
            throw serverRunnableCreateServerRunnable.bindException;
        }
    }

    public class HTTPSession implements IHTTPSession {
        public static final int BUFSIZE = 8192;
        public static final int MAX_HEADER_SIZE = 1024;
        private static final int MEMORY_STORE_LIMIT = 1024;
        private static final int REQUEST_BUFFER_LEN = 512;
        private CookieHandler cookies;
        private Map<String, String> headers;
        private final BufferedInputStream inputStream;
        private Method method;
        private final OutputStream outputStream;
        private Map<String, List<String>> parms;
        private String protocolVersion;
        private String queryParameterString;
        private String remoteHostname;
        private String remoteIp;
        private int rlen;
        private int splitbyte;
        private final TempFileManager tempFileManager;
        private String uri;

        public HTTPSession(TempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream) {
            this.tempFileManager = tempFileManager;
            this.inputStream = new BufferedInputStream(inputStream, 8192);
            this.outputStream = outputStream;
        }

        private void decodeHeader(BufferedReader bufferedReader, Map<String, String> map, Map<String, List<String>> map2, Map<String, String> map3) throws ResponseException {
            String strDecodePercent;
            try {
                String line = bufferedReader.readLine();
                if (line == null) {
                    return;
                }
                StringTokenizer stringTokenizer = new StringTokenizer(line);
                if (!stringTokenizer.hasMoreTokens()) {
                    throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html");
                }
                map.put("method", stringTokenizer.nextToken());
                if (!stringTokenizer.hasMoreTokens()) {
                    throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html");
                }
                String strNextToken = stringTokenizer.nextToken();
                int iIndexOf = strNextToken.indexOf(63);
                if (iIndexOf >= 0) {
                    decodeParms(strNextToken.substring(iIndexOf + 1), map2);
                    strDecodePercent = NanoHTTPD.decodePercent(strNextToken.substring(0, iIndexOf));
                } else {
                    strDecodePercent = NanoHTTPD.decodePercent(strNextToken);
                }
                if (stringTokenizer.hasMoreTokens()) {
                    this.protocolVersion = stringTokenizer.nextToken();
                } else {
                    this.protocolVersion = "HTTP/1.1";
                    NanoHTTPD.LOG.log(Level.FINE, "no protocol version specified, strange. Assuming HTTP/1.1.");
                }
                String line2 = bufferedReader.readLine();
                while (line2 != null && !line2.trim().isEmpty()) {
                    int iIndexOf2 = line2.indexOf(58);
                    if (iIndexOf2 >= 0) {
                        map3.put(line2.substring(0, iIndexOf2).trim().toLowerCase(Locale.US), line2.substring(iIndexOf2 + 1).trim());
                    }
                    line2 = bufferedReader.readLine();
                }
                map.put("uri", strDecodePercent);
            } catch (IOException e10) {
                throw new ResponseException(Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: IOException: " + e10.getMessage(), e10);
            }
        }

        private void decodeMultipartFormData(ContentType contentType, ByteBuffer byteBuffer, Map<String, List<String>> map, Map<String, String> map2) throws Throwable {
            int i10;
            try {
                int[] boundaryPositions = getBoundaryPositions(byteBuffer, contentType.getBoundary().getBytes());
                int i11 = 2;
                if (boundaryPositions.length < 2) {
                    throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but contains less than two boundary strings.");
                }
                int i12 = 1024;
                byte[] bArr = new byte[1024];
                int i13 = 0;
                int i14 = 0;
                int i15 = 0;
                while (true) {
                    int i16 = 1;
                    if (i14 >= boundaryPositions.length - 1) {
                        return;
                    }
                    byteBuffer.position(boundaryPositions[i14]);
                    int iRemaining = byteBuffer.remaining() < i12 ? byteBuffer.remaining() : i12;
                    byteBuffer.get(bArr, i13, iRemaining);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bArr, i13, iRemaining), Charset.forName(contentType.getEncoding())), iRemaining);
                    String line = bufferedReader.readLine();
                    if (line == null || !line.contains(contentType.getBoundary())) {
                        throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but chunk does not start with boundary.");
                    }
                    String line2 = bufferedReader.readLine();
                    String strGroup = null;
                    int i17 = i11;
                    String str = null;
                    String strTrim = null;
                    while (line2 != null && line2.trim().length() > 0) {
                        Matcher matcher = NanoHTTPD.CONTENT_DISPOSITION_PATTERN.matcher(line2);
                        if (matcher.matches()) {
                            Matcher matcher2 = NanoHTTPD.CONTENT_DISPOSITION_ATTRIBUTE_PATTERN.matcher(matcher.group(i11));
                            while (matcher2.find()) {
                                String strGroup2 = matcher2.group(i16);
                                if (ContentDisposition.Parameters.Name.equalsIgnoreCase(strGroup2)) {
                                    strGroup = matcher2.group(2);
                                } else if (ContentDisposition.Parameters.FileName.equalsIgnoreCase(strGroup2)) {
                                    String strGroup3 = matcher2.group(2);
                                    if (!strGroup3.isEmpty()) {
                                        if (i15 > 0) {
                                            strGroup = strGroup + String.valueOf(i15);
                                            i15++;
                                        } else {
                                            i15++;
                                        }
                                    }
                                    str = strGroup3;
                                }
                                i16 = 1;
                            }
                        }
                        Matcher matcher3 = NanoHTTPD.CONTENT_TYPE_PATTERN.matcher(line2);
                        if (matcher3.matches()) {
                            i10 = 2;
                            strTrim = matcher3.group(2).trim();
                        } else {
                            i10 = 2;
                        }
                        line2 = bufferedReader.readLine();
                        i17++;
                        i11 = i10;
                        i16 = 1;
                    }
                    int i18 = i11;
                    int iScipOverNewLine = 0;
                    while (true) {
                        int i19 = i17 - 1;
                        if (i17 <= 0) {
                            break;
                        }
                        iScipOverNewLine = scipOverNewLine(bArr, iScipOverNewLine);
                        i17 = i19;
                    }
                    if (iScipOverNewLine >= iRemaining - 4) {
                        throw new ResponseException(Response.Status.INTERNAL_ERROR, "Multipart header size exceeds MAX_HEADER_SIZE.");
                    }
                    int i20 = boundaryPositions[i14] + iScipOverNewLine;
                    i14++;
                    int i21 = boundaryPositions[i14] - 4;
                    byteBuffer.position(i20);
                    List<String> arrayList = map.get(strGroup);
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                        map.put(strGroup, arrayList);
                    }
                    if (strTrim == null) {
                        byte[] bArr2 = new byte[i21 - i20];
                        byteBuffer.get(bArr2);
                        arrayList.add(new String(bArr2, contentType.getEncoding()));
                    } else {
                        String strSaveTmpFile = saveTmpFile(byteBuffer, i20, i21 - i20, str);
                        if (map2.containsKey(strGroup)) {
                            int i22 = i18;
                            while (true) {
                                if (!map2.containsKey(strGroup + i22)) {
                                    break;
                                } else {
                                    i22++;
                                }
                            }
                            map2.put(strGroup + i22, strSaveTmpFile);
                        } else {
                            map2.put(strGroup, strSaveTmpFile);
                        }
                        arrayList.add(str);
                    }
                    i11 = i18;
                    i12 = 1024;
                    i13 = 0;
                }
            } catch (ResponseException e10) {
                throw e10;
            } catch (Exception e11) {
                throw new ResponseException(Response.Status.INTERNAL_ERROR, e11.toString());
            }
        }

        private void decodeParms(String str, Map<String, List<String>> map) {
            String strTrim;
            String strDecodePercent;
            if (str == null) {
                this.queryParameterString = "";
                return;
            }
            this.queryParameterString = str;
            StringTokenizer stringTokenizer = new StringTokenizer(str, "&");
            while (stringTokenizer.hasMoreTokens()) {
                String strNextToken = stringTokenizer.nextToken();
                int iIndexOf = strNextToken.indexOf(61);
                if (iIndexOf >= 0) {
                    strTrim = NanoHTTPD.decodePercent(strNextToken.substring(0, iIndexOf)).trim();
                    strDecodePercent = NanoHTTPD.decodePercent(strNextToken.substring(iIndexOf + 1));
                } else {
                    strTrim = NanoHTTPD.decodePercent(strNextToken).trim();
                    strDecodePercent = "";
                }
                List<String> arrayList = map.get(strTrim);
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                    map.put(strTrim, arrayList);
                }
                arrayList.add(strDecodePercent);
            }
        }

        private int findHeaderEnd(byte[] bArr, int i10) {
            int i11;
            int i12 = 0;
            while (true) {
                int i13 = i12 + 1;
                if (i13 >= i10) {
                    return 0;
                }
                byte b5 = bArr[i12];
                if (b5 == 13 && bArr[i13] == 10 && (i11 = i12 + 3) < i10 && bArr[i12 + 2] == 13 && bArr[i11] == 10) {
                    return i12 + 4;
                }
                if (b5 == 10 && bArr[i13] == 10) {
                    return i12 + 2;
                }
                i12 = i13;
            }
        }

        private int[] getBoundaryPositions(ByteBuffer byteBuffer, byte[] bArr) {
            int[] iArr = new int[0];
            if (byteBuffer.remaining() < bArr.length) {
                return iArr;
            }
            int length = bArr.length + 4096;
            byte[] bArr2 = new byte[length];
            int iRemaining = byteBuffer.remaining() < length ? byteBuffer.remaining() : length;
            byteBuffer.get(bArr2, 0, iRemaining);
            int length2 = iRemaining - bArr.length;
            int i10 = 0;
            do {
                for (int i11 = 0; i11 < length2; i11++) {
                    for (int i12 = 0; i12 < bArr.length && bArr2[i11 + i12] == bArr[i12]; i12++) {
                        if (i12 == bArr.length - 1) {
                            int[] iArr2 = new int[iArr.length + 1];
                            System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
                            iArr2[iArr.length] = i10 + i11;
                            iArr = iArr2;
                        }
                    }
                }
                i10 += length2;
                System.arraycopy(bArr2, length - bArr.length, bArr2, 0, bArr.length);
                length2 = length - bArr.length;
                if (byteBuffer.remaining() < length2) {
                    length2 = byteBuffer.remaining();
                }
                byteBuffer.get(bArr2, bArr.length, length2);
            } while (length2 > 0);
            return iArr;
        }

        private RandomAccessFile getTmpBucket() {
            try {
                return new RandomAccessFile(this.tempFileManager.createTempFile(null).getName(), "rw");
            } catch (Exception e10) {
                throw new Error(e10);
            }
        }

        private String saveTmpFile(ByteBuffer byteBuffer, int i10, int i11, String str) throws Throwable {
            if (i11 <= 0) {
                return "";
            }
            FileOutputStream fileOutputStream = null;
            try {
                try {
                    TempFile tempFileCreateTempFile = this.tempFileManager.createTempFile(str);
                    ByteBuffer byteBufferDuplicate = byteBuffer.duplicate();
                    FileOutputStream fileOutputStream2 = new FileOutputStream(tempFileCreateTempFile.getName());
                    try {
                        FileChannel channel = fileOutputStream2.getChannel();
                        byteBufferDuplicate.position(i10).limit(i10 + i11);
                        channel.write(byteBufferDuplicate.slice());
                        String name = tempFileCreateTempFile.getName();
                        NanoHTTPD.safeClose(fileOutputStream2);
                        return name;
                    } catch (Exception e10) {
                        e = e10;
                        fileOutputStream = fileOutputStream2;
                        throw new Error(e);
                    } catch (Throwable th2) {
                        th = th2;
                        fileOutputStream = fileOutputStream2;
                        NanoHTTPD.safeClose(fileOutputStream);
                        throw th;
                    }
                } catch (Exception e11) {
                    e = e11;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        }

        private int scipOverNewLine(byte[] bArr, int i10) {
            while (bArr[i10] != 10) {
                i10++;
            }
            return i10 + 1;
        }

        @Override // fi.iki.elonen.NanoHTTPD.IHTTPSession
        public void execute() {
            try {
                try {
                    try {
                        try {
                            try {
                                byte[] bArr = new byte[8192];
                                boolean z2 = false;
                                this.splitbyte = 0;
                                this.rlen = 0;
                                this.inputStream.mark(8192);
                                try {
                                    int i10 = this.inputStream.read(bArr, 0, 8192);
                                    if (i10 == -1) {
                                        NanoHTTPD.safeClose(this.inputStream);
                                        NanoHTTPD.safeClose(this.outputStream);
                                        throw new SocketException("NanoHttpd Shutdown");
                                    }
                                    while (i10 > 0) {
                                        int i11 = this.rlen + i10;
                                        this.rlen = i11;
                                        int iFindHeaderEnd = findHeaderEnd(bArr, i11);
                                        this.splitbyte = iFindHeaderEnd;
                                        if (iFindHeaderEnd > 0) {
                                            break;
                                        }
                                        BufferedInputStream bufferedInputStream = this.inputStream;
                                        int i12 = this.rlen;
                                        i10 = bufferedInputStream.read(bArr, i12, 8192 - i12);
                                    }
                                    if (this.splitbyte < this.rlen) {
                                        this.inputStream.reset();
                                        this.inputStream.skip(this.splitbyte);
                                    }
                                    this.parms = new HashMap();
                                    Map<String, String> map = this.headers;
                                    if (map == null) {
                                        this.headers = new HashMap();
                                    } else {
                                        map.clear();
                                    }
                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bArr, 0, this.rlen)));
                                    HashMap map2 = new HashMap();
                                    decodeHeader(bufferedReader, map2, this.parms, this.headers);
                                    String str = this.remoteIp;
                                    if (str != null) {
                                        this.headers.put("remote-addr", str);
                                        this.headers.put("http-client-ip", this.remoteIp);
                                    }
                                    Method methodLookup = Method.lookup((String) map2.get("method"));
                                    this.method = methodLookup;
                                    if (methodLookup == null) {
                                        throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Syntax error. HTTP verb " + ((String) map2.get("method")) + " unhandled.");
                                    }
                                    this.uri = (String) map2.get("uri");
                                    this.cookies = NanoHTTPD.this.new CookieHandler(this.headers);
                                    String str2 = this.headers.get("connection");
                                    boolean z10 = "HTTP/1.1".equals(this.protocolVersion) && (str2 == null || !str2.matches("(?i).*close.*"));
                                    Response responseServe = NanoHTTPD.this.serve(this);
                                    if (responseServe == null) {
                                        throw new ResponseException(Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: Serve() returned a null response.");
                                    }
                                    String str3 = this.headers.get("accept-encoding");
                                    this.cookies.unloadQueue(responseServe);
                                    responseServe.setRequestMethod(this.method);
                                    if (NanoHTTPD.this.useGzipWhenAccepted(responseServe) && str3 != null && str3.contains("gzip")) {
                                        z2 = true;
                                    }
                                    responseServe.setGzipEncoding(z2);
                                    responseServe.setKeepAlive(z10);
                                    responseServe.send(this.outputStream);
                                    if (!z10 || responseServe.isCloseConnection()) {
                                        throw new SocketException("NanoHttpd Shutdown");
                                    }
                                    NanoHTTPD.safeClose(responseServe);
                                    this.tempFileManager.clear();
                                } catch (SSLException e10) {
                                    throw e10;
                                } catch (IOException unused) {
                                    NanoHTTPD.safeClose(this.inputStream);
                                    NanoHTTPD.safeClose(this.outputStream);
                                    throw new SocketException("NanoHttpd Shutdown");
                                }
                            } catch (ResponseException e11) {
                                NanoHTTPD.newFixedLengthResponse(e11.getStatus(), NanoHTTPD.MIME_PLAINTEXT, e11.getMessage()).send(this.outputStream);
                                NanoHTTPD.safeClose(this.outputStream);
                                NanoHTTPD.safeClose(null);
                                this.tempFileManager.clear();
                            }
                        } catch (IOException e12) {
                            NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + e12.getMessage()).send(this.outputStream);
                            NanoHTTPD.safeClose(this.outputStream);
                            NanoHTTPD.safeClose(null);
                            this.tempFileManager.clear();
                        }
                    } catch (SocketTimeoutException e13) {
                        throw e13;
                    }
                } catch (SocketException e14) {
                    throw e14;
                } catch (SSLException e15) {
                    NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "SSL PROTOCOL FAILURE: " + e15.getMessage()).send(this.outputStream);
                    NanoHTTPD.safeClose(this.outputStream);
                    NanoHTTPD.safeClose(null);
                    this.tempFileManager.clear();
                }
            } catch (Throwable th2) {
                NanoHTTPD.safeClose(null);
                this.tempFileManager.clear();
                throw th2;
            }
        }

        public long getBodySize() {
            if (this.headers.containsKey("content-length")) {
                return Long.parseLong(this.headers.get("content-length"));
            }
            int i10 = this.splitbyte;
            int i11 = this.rlen;
            if (i10 < i11) {
                return i11 - i10;
            }
            return 0L;
        }

        @Override // fi.iki.elonen.NanoHTTPD.IHTTPSession
        public CookieHandler getCookies() {
            return this.cookies;
        }

        @Override // fi.iki.elonen.NanoHTTPD.IHTTPSession
        public final Map<String, String> getHeaders() {
            return this.headers;
        }

        @Override // fi.iki.elonen.NanoHTTPD.IHTTPSession
        public final InputStream getInputStream() {
            return this.inputStream;
        }

        @Override // fi.iki.elonen.NanoHTTPD.IHTTPSession
        public final Method getMethod() {
            return this.method;
        }

        @Override // fi.iki.elonen.NanoHTTPD.IHTTPSession
        public final Map<String, List<String>> getParameters() {
            return this.parms;
        }

        @Override // fi.iki.elonen.NanoHTTPD.IHTTPSession
        @Deprecated
        public final Map<String, String> getParms() {
            HashMap map = new HashMap();
            for (String str : this.parms.keySet()) {
                map.put(str, this.parms.get(str).get(0));
            }
            return map;
        }

        @Override // fi.iki.elonen.NanoHTTPD.IHTTPSession
        public String getQueryParameterString() {
            return this.queryParameterString;
        }

        @Override // fi.iki.elonen.NanoHTTPD.IHTTPSession
        public String getRemoteHostName() {
            return this.remoteHostname;
        }

        @Override // fi.iki.elonen.NanoHTTPD.IHTTPSession
        public String getRemoteIpAddress() {
            return this.remoteIp;
        }

        @Override // fi.iki.elonen.NanoHTTPD.IHTTPSession
        public final String getUri() {
            return this.uri;
        }

        @Override // fi.iki.elonen.NanoHTTPD.IHTTPSession
        public void parseBody(Map<String, String> map) throws Throwable {
            RandomAccessFile tmpBucket;
            ByteArrayOutputStream byteArrayOutputStream;
            DataOutput dataOutputStream;
            ByteBuffer map2;
            RandomAccessFile randomAccessFile = null;
            try {
                long bodySize = getBodySize();
                if (bodySize < 1024) {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                    tmpBucket = null;
                } else {
                    tmpBucket = getTmpBucket();
                    byteArrayOutputStream = null;
                    dataOutputStream = tmpBucket;
                }
                try {
                    byte[] bArr = new byte[512];
                    while (this.rlen >= 0 && bodySize > 0) {
                        int i10 = this.inputStream.read(bArr, 0, (int) Math.min(bodySize, 512L));
                        this.rlen = i10;
                        bodySize -= (long) i10;
                        if (i10 > 0) {
                            dataOutputStream.write(bArr, 0, i10);
                        }
                    }
                    if (byteArrayOutputStream != null) {
                        map2 = ByteBuffer.wrap(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size());
                    } else {
                        map2 = tmpBucket.getChannel().map(FileChannel.MapMode.READ_ONLY, 0L, tmpBucket.length());
                        tmpBucket.seek(0L);
                    }
                    if (Method.POST.equals(this.method)) {
                        ContentType contentType = new ContentType(this.headers.get("content-type"));
                        if (!contentType.isMultipart()) {
                            byte[] bArr2 = new byte[map2.remaining()];
                            map2.get(bArr2);
                            String strTrim = new String(bArr2, contentType.getEncoding()).trim();
                            if (HttpConnection.FORM_URL_ENCODED.equalsIgnoreCase(contentType.getContentType())) {
                                decodeParms(strTrim, this.parms);
                            } else if (strTrim.length() != 0) {
                                map.put("postData", strTrim);
                            }
                        } else {
                            if (contentType.getBoundary() == null) {
                                throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but boundary missing. Usage: GET /example/file.html");
                            }
                            decodeMultipartFormData(contentType, map2, this.parms, map);
                        }
                    } else if (Method.PUT.equals(this.method)) {
                        map.put("content", saveTmpFile(map2, 0, map2.limit(), null));
                    }
                    NanoHTTPD.safeClose(tmpBucket);
                } catch (Throwable th2) {
                    th = th2;
                    randomAccessFile = tmpBucket;
                    NanoHTTPD.safeClose(randomAccessFile);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        }

        public HTTPSession(TempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream, InetAddress inetAddress) {
            this.tempFileManager = tempFileManager;
            this.inputStream = new BufferedInputStream(inputStream, 8192);
            this.outputStream = outputStream;
            this.remoteIp = (inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress()) ? "127.0.0.1" : inetAddress.getHostAddress().toString();
            this.remoteHostname = (inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress()) ? "localhost" : inetAddress.getHostName().toString();
            this.headers = new HashMap();
        }
    }

    public static SSLServerSocketFactory makeSSLSocketFactory(KeyStore keyStore, KeyManagerFactory keyManagerFactory) throws IOException {
        try {
            return makeSSLSocketFactory(keyStore, keyManagerFactory.getKeyManagers());
        } catch (Exception e10) {
            throw new IOException(e10.getMessage());
        }
    }

    public static SSLServerSocketFactory makeSSLSocketFactory(String str, char[] cArr) throws IOException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream resourceAsStream = NanoHTTPD.class.getResourceAsStream(str);
            if (resourceAsStream == null) {
                throw new IOException("Unable to load keystore from classpath: " + str);
            }
            keyStore.load(resourceAsStream, cArr);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, cArr);
            return makeSSLSocketFactory(keyStore, keyManagerFactory);
        } catch (Exception e10) {
            throw new IOException(e10.getMessage());
        }
    }

    @Deprecated
    public Response serve(String str, Method method, Map<String, String> map, Map<String, String> map2, Map<String, String> map3) {
        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
    }

    public static Response newFixedLengthResponse(String str) {
        return newFixedLengthResponse(Response.Status.OK, MIME_HTML, str);
    }
}
