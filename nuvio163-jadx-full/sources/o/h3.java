package o;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import com.jojo.tv.R;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes3.dex */
public final class h3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Object f43191a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f43192b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Object f43193c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final Object f43194d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final Object f43195e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final Serializable f43196f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final Object f43197g;

    /* JADX WARN: Type inference failed for: r2v0, types: [int[], java.io.Serializable] */
    public h3(Context context) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.f43194d = layoutParams;
        this.f43195e = new Rect();
        this.f43196f = new int[2];
        this.f43197g = new int[2];
        this.f43191a = context;
        View viewInflate = LayoutInflater.from(context).inflate(R.layout.abc_tooltip, (ViewGroup) null);
        this.f43192b = viewInflate;
        this.f43193c = (TextView) viewInflate.findViewById(R.id.message);
        layoutParams.setTitle(h3.class.getSimpleName());
        layoutParams.packageName = context.getPackageName();
        layoutParams.type = 1002;
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.format = -3;
        layoutParams.windowAnimations = R.style.Animation_AppCompat_Tooltip;
        layoutParams.flags = 24;
    }

    public h3(List channels, Set favoriteUrls, List channelHistory, Set hiddenGroups, Set hiddenChannels, String filterKey, List customLists) {
        Intrinsics.checkNotNullParameter(channels, "channels");
        Intrinsics.checkNotNullParameter(favoriteUrls, "favoriteUrls");
        Intrinsics.checkNotNullParameter(channelHistory, "channelHistory");
        Intrinsics.checkNotNullParameter(hiddenGroups, "hiddenGroups");
        Intrinsics.checkNotNullParameter(hiddenChannels, "hiddenChannels");
        Intrinsics.checkNotNullParameter(filterKey, "filterKey");
        Intrinsics.checkNotNullParameter(customLists, "customLists");
        this.f43191a = channels;
        this.f43192b = favoriteUrls;
        this.f43193c = channelHistory;
        this.f43194d = hiddenGroups;
        this.f43195e = hiddenChannels;
        this.f43196f = filterKey;
        this.f43197g = customLists;
    }
}
