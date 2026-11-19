package ig;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes4.dex */
public final class a {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final Pattern f24152c = Pattern.compile("(\\w+)\\s*=\\s*([^\\s#][^#]*)");

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final Pattern f24153d = Pattern.compile("\\s*,\\s*");

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f24154a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f24155b;

    public a(String str, String str2) {
        this.f24154a = str;
        this.f24155b = str2;
    }

    public static Optional a(CharSequence charSequence) {
        Matcher matcher = f24152c.matcher(charSequence);
        return !matcher.matches() ? Optional.empty() : Optional.of(new a(matcher.group(1), matcher.group(2)));
    }
}
