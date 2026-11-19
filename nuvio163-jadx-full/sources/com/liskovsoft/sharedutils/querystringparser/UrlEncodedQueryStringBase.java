package com.liskovsoft.sharedutils.querystringparser;

import io.ktor.http.ContentDisposition;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: classes4.dex */
class UrlEncodedQueryStringBase {
    private static final String PARSE_PARAMETER_SEPARATORS = String.valueOf(Separator.AMPERSAND) + Separator.SEMICOLON;
    private final Map<String, List<String>> queryMap = new LinkedHashMap();

    public enum Separator {
        AMPERSAND { // from class: com.liskovsoft.sharedutils.querystringparser.UrlEncodedQueryStringBase.Separator.1
            @Override // java.lang.Enum
            public String toString() {
                return "&";
            }
        },
        SEMICOLON { // from class: com.liskovsoft.sharedutils.querystringparser.UrlEncodedQueryStringBase.Separator.2
            @Override // java.lang.Enum
            public String toString() {
                return ";";
            }
        }
    }

    private UrlEncodedQueryStringBase() {
    }

    private void appendOrSet(String str, String str2, boolean z2) {
        if (str == null) {
            throw new NullPointerException(ContentDisposition.Parameters.Name);
        }
        if (z2) {
            List<String> list = this.queryMap.get(str);
            if (list != null) {
                list.add(str2);
                return;
            }
        } else if (str2 == null) {
            this.queryMap.remove(str);
            return;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(str2);
        this.queryMap.put(str, arrayList);
    }

    public static UrlEncodedQueryStringBase create() {
        return new UrlEncodedQueryStringBase();
    }

    public static UrlEncodedQueryStringBase parse(CharSequence charSequence) {
        UrlEncodedQueryStringBase urlEncodedQueryStringBase = new UrlEncodedQueryStringBase();
        urlEncodedQueryStringBase.appendOrSet(charSequence, true);
        return urlEncodedQueryStringBase;
    }

    public UrlEncodedQueryStringBase append(String str, String str2) {
        appendOrSet(str, str2, true);
        return this;
    }

    public URI apply(URI uri) {
        return apply(uri, Separator.AMPERSAND);
    }

    public boolean contains(String str) {
        return this.queryMap.containsKey(str);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof UrlEncodedQueryStringBase) {
            return toString().equals(((UrlEncodedQueryStringBase) obj).toString());
        }
        return false;
    }

    public String get(String str) {
        List<String> values = getValues(str);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    public Map<String, List<String>> getMap() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Map.Entry<String, List<String>> entry : this.queryMap.entrySet()) {
            linkedHashMap.put(entry.getKey(), new ArrayList(entry.getValue()));
        }
        return linkedHashMap;
    }

    public Iterator<String> getNames() {
        return this.queryMap.keySet().iterator();
    }

    public List<String> getValues(String str) {
        return this.queryMap.get(str);
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean isEmpty() {
        return this.queryMap.isEmpty();
    }

    public UrlEncodedQueryStringBase remove(String str) {
        appendOrSet(str, null, false);
        return this;
    }

    public UrlEncodedQueryStringBase set(String str, String str2) {
        appendOrSet(str, str2, false);
        return this;
    }

    public String toString() {
        return toString(Separator.AMPERSAND);
    }

    public static UrlEncodedQueryStringBase create(Map<String, List<String>> map) {
        UrlEncodedQueryStringBase urlEncodedQueryStringBase = new UrlEncodedQueryStringBase();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            urlEncodedQueryStringBase.queryMap.put(entry.getKey(), new ArrayList(entry.getValue()));
        }
        return urlEncodedQueryStringBase;
    }

    public UrlEncodedQueryStringBase append(String str, Number number) {
        appendOrSet(str, number.toString(), true);
        return this;
    }

    public URI apply(URI uri, Separator separator) {
        StringBuilder sb2 = new StringBuilder();
        if (uri.getScheme() != null) {
            sb2.append(uri.getScheme());
            sb2.append(':');
        }
        if (uri.getHost() != null) {
            sb2.append("//");
            if (uri.getUserInfo() != null) {
                sb2.append(uri.getUserInfo());
                sb2.append('@');
            }
            sb2.append(uri.getHost());
            if (uri.getPort() != -1) {
                sb2.append(':');
                sb2.append(uri.getPort());
            }
        } else if (uri.getAuthority() != null) {
            sb2.append("//");
            sb2.append(uri.getAuthority());
        }
        if (uri.getPath() != null) {
            sb2.append(uri.getPath());
        }
        String string = toString(separator);
        if (string.length() != 0) {
            sb2.append('?');
            sb2.append(string);
        }
        if (uri.getFragment() != null) {
            sb2.append('#');
            sb2.append(uri.getFragment());
        }
        try {
            return new URI(sb2.toString());
        } catch (URISyntaxException e10) {
            throw new RuntimeException(e10);
        }
    }

    public UrlEncodedQueryStringBase set(String str, Number number) {
        if (number == null) {
            remove(str);
            return this;
        }
        appendOrSet(str, number.toString(), false);
        return this;
    }

    public String toString(Separator separator) {
        StringBuilder sb2 = new StringBuilder();
        for (String str : this.queryMap.keySet()) {
            for (String str2 : this.queryMap.get(str)) {
                if (sb2.length() != 0) {
                    sb2.append(separator);
                }
                try {
                    sb2.append(URLEncoder.encode(str, "UTF-8"));
                    if (str2 != null) {
                        sb2.append('=');
                        sb2.append(URLEncoder.encode(str2, "UTF-8"));
                    }
                } catch (UnsupportedEncodingException e10) {
                    throw new RuntimeException(e10);
                }
            }
        }
        return sb2.toString();
    }

    public static UrlEncodedQueryStringBase parse(URI uri) {
        return parse(uri.getRawQuery());
    }

    public UrlEncodedQueryStringBase append(String str) {
        appendOrSet(str, true);
        return this;
    }

    public UrlEncodedQueryStringBase set(String str) {
        appendOrSet(str, false);
        return this;
    }

    private void appendOrSet(CharSequence charSequence, boolean z2) {
        String strSubstring;
        if (charSequence == null) {
            return;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(charSequence.toString(), PARSE_PARAMETER_SEPARATORS);
        HashSet hashSet = null;
        while (stringTokenizer.hasMoreTokens()) {
            String strNextToken = stringTokenizer.nextToken();
            int iIndexOf = strNextToken.indexOf(61);
            if (iIndexOf == -1) {
                strSubstring = null;
            } else {
                String strSubstring2 = strNextToken.substring(0, iIndexOf);
                strSubstring = strNextToken.substring(iIndexOf + 1);
                strNextToken = strSubstring2;
            }
            try {
                String strDecode = URLDecoder.decode(strNextToken, "UTF-8");
                if (!z2) {
                    if (hashSet == null) {
                        hashSet = new HashSet();
                    }
                    if (!hashSet.contains(strDecode)) {
                        remove(strDecode);
                    }
                    hashSet.add(strDecode);
                }
                if (strSubstring != null) {
                    strSubstring = URLDecoder.decode(strSubstring, "UTF-8");
                }
                appendOrSet(strDecode, strSubstring, true);
            } catch (UnsupportedEncodingException e10) {
                throw new RuntimeException(e10);
            }
        }
    }
}
