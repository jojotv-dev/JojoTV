package n7;

import androidx.media3.exoplayer.upstream.CmcdData;
import androidx.media3.extractor.text.ttml.TtmlNode;
import io.ktor.http.LinkHeader;
import java.util.HashMap;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class h2 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final h2 f39622b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final h2 f39623c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final h2 f39624e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final h2 f39625i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final HashMap f39626m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final /* synthetic */ h2[] f39627n;

    /* JADX INFO: Fake field, exist only in values array */
    h2 EF1;

    static {
        h2 h2Var = new h2("svg", 0);
        h2 h2Var2 = new h2(CmcdData.OBJECT_TYPE_AUDIO_ONLY, 1);
        h2 h2Var3 = new h2(TtmlNode.TEXT_EMPHASIS_MARK_CIRCLE, 2);
        h2 h2Var4 = new h2("clipPath", 3);
        h2 h2Var5 = new h2("defs", 4);
        h2 h2Var6 = new h2("desc", 5);
        f39622b = h2Var6;
        h2 h2Var7 = new h2("ellipse", 6);
        h2 h2Var8 = new h2("g", 7);
        h2 h2Var9 = new h2("image", 8);
        h2 h2Var10 = new h2("line", 9);
        h2 h2Var11 = new h2("linearGradient", 10);
        h2 h2Var12 = new h2("marker", 11);
        h2 h2Var13 = new h2("mask", 12);
        h2 h2Var14 = new h2("path", 13);
        h2 h2Var15 = new h2("pattern", 14);
        h2 h2Var16 = new h2("polygon", 15);
        h2 h2Var17 = new h2("polyline", 16);
        h2 h2Var18 = new h2("radialGradient", 17);
        h2 h2Var19 = new h2("rect", 18);
        h2 h2Var20 = new h2("solidColor", 19);
        h2 h2Var21 = new h2("stop", 20);
        h2 h2Var22 = new h2("style", 21);
        h2 h2Var23 = new h2("SWITCH", 22);
        f39623c = h2Var23;
        h2 h2Var24 = new h2("symbol", 23);
        h2 h2Var25 = new h2("text", 24);
        h2 h2Var26 = new h2("textPath", 25);
        h2 h2Var27 = new h2(LinkHeader.Parameters.Title, 26);
        f39624e = h2Var27;
        h2 h2Var28 = new h2("tref", 27);
        h2 h2Var29 = new h2("tspan", 28);
        h2 h2Var30 = new h2("use", 29);
        h2 h2Var31 = new h2("view", 30);
        h2 h2Var32 = new h2("UNSUPPORTED", 31);
        f39625i = h2Var32;
        f39627n = new h2[]{h2Var, h2Var2, h2Var3, h2Var4, h2Var5, h2Var6, h2Var7, h2Var8, h2Var9, h2Var10, h2Var11, h2Var12, h2Var13, h2Var14, h2Var15, h2Var16, h2Var17, h2Var18, h2Var19, h2Var20, h2Var21, h2Var22, h2Var23, h2Var24, h2Var25, h2Var26, h2Var27, h2Var28, h2Var29, h2Var30, h2Var31, h2Var32};
        f39626m = new HashMap();
        for (h2 h2Var33 : values()) {
            if (h2Var33 == f39623c) {
                f39626m.put("switch", h2Var33);
            } else if (h2Var33 != f39625i) {
                f39626m.put(h2Var33.name(), h2Var33);
            }
        }
    }

    public static h2 valueOf(String str) {
        return (h2) Enum.valueOf(h2.class, str);
    }

    public static h2[] values() {
        return (h2[]) f39627n.clone();
    }
}
