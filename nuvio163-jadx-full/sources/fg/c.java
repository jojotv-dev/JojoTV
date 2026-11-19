package fg;

import androidx.media3.extractor.text.ttml.TtmlNode;
import aq.f;
import aq.t;
import com.uwetrottmann.tmdb2.entities.MediaResultsPage;

/* JADX INFO: loaded from: classes4.dex */
public interface c {
    @f("search/multi")
    xp.f<MediaResultsPage> a(@t("query") String str, @t("page") Integer num, @t("language") String str2, @t(TtmlNode.TAG_REGION) String str3, @t("include_adult") Boolean bool);
}
