package n7;

import androidx.media3.extractor.text.ttml.TtmlNode;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class d {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final d f39568b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final d f39569c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ d[] f39570e;

    static {
        d dVar = new d(TtmlNode.COMBINE_ALL, 0);
        f39568b = dVar;
        d dVar2 = new d("aural", 1);
        d dVar3 = new d("braille", 2);
        d dVar4 = new d("embossed", 3);
        d dVar5 = new d("handheld", 4);
        d dVar6 = new d("print", 5);
        d dVar7 = new d("projection", 6);
        d dVar8 = new d("screen", 7);
        f39569c = dVar8;
        f39570e = new d[]{dVar, dVar2, dVar3, dVar4, dVar5, dVar6, dVar7, dVar8, new d("speech", 8), new d("tty", 9), new d("tv", 10)};
    }

    public static d valueOf(String str) {
        return (d) Enum.valueOf(d.class, str);
    }

    public static d[] values() {
        return (d[]) f39570e.clone();
    }
}
