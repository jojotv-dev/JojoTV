package org.jsoup.parser;

import androidx.media3.exoplayer.upstream.CmcdConfiguration;
import androidx.media3.exoplayer.upstream.CmcdData;
import androidx.media3.extractor.text.ttml.TtmlNode;
import io.ktor.http.ContentType;
import io.ktor.http.LinkHeader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.jsoup.helper.Validate;
import org.jsoup.internal.SharedConstants;

/* JADX INFO: loaded from: classes5.dex */
public class TagSet {
    static final TagSet HtmlTagSet = initHtmlDefault();
    private ArrayList<Consumer<Tag>> customizers;
    private final TagSet source;
    private final Map<String, Map<String, Tag>> tags;

    private TagSet(TagSet tagSet, ArrayList<Consumer<Tag>> arrayList) {
        this.tags = new HashMap();
        this.source = tagSet;
        this.customizers = arrayList;
    }

    public static TagSet Html() {
        return new TagSet(HtmlTagSet, null);
    }

    private static ArrayList<Consumer<Tag>> copyCustomizers(TagSet tagSet) {
        if (tagSet.customizers == null) {
            return null;
        }
        return new ArrayList<>(tagSet.customizers);
    }

    private void doAdd(Tag tag) {
        ArrayList<Consumer<Tag>> arrayList = this.customizers;
        if (arrayList != null) {
            Iterator<Consumer<Tag>> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().accept(tag);
            }
        }
        this.tags.computeIfAbsent(tag.namespace, new e()).put(tag.tagName, tag);
    }

    public static TagSet initHtmlDefault() {
        String[] strArr = {"html", TtmlNode.TAG_HEAD, TtmlNode.TAG_BODY, "frameset", "script", "noscript", "style", "meta", "link", LinkHeader.Parameters.Title, "frame", "noframes", "section", "nav", "aside", "hgroup", "header", "footer", TtmlNode.TAG_P, "h1", "h2", "h3", "h4", "h5", "h6", "button", "ul", "ol", "pre", TtmlNode.TAG_DIV, "blockquote", "hr", "address", "figure", "figcaption", "form", "fieldset", "ins", "del", CmcdConfiguration.KEY_DEADLINE, "dt", "dd", "li", "table", "caption", "thead", "tfoot", "tbody", "colgroup", "col", "tr", "th", "td", "video", "audio", "canvas", "details", "menu", "plaintext", "template", "article", "main", TtmlNode.CENTER, "template", "dir", "applet", "marquee", "listing", "#root"};
        String[] strArr2 = {"object", TtmlNode.RUBY_BASE, ContentType.Font.TYPE, TtmlNode.TAG_TT, CmcdData.OBJECT_TYPE_INIT_SEGMENT, "b", "u", "big", "small", "em", "strong", "dfn", "code", "samp", "kbd", "var", "cite", "abbr", "time", "acronym", "mark", TtmlNode.ATTR_TTS_RUBY, "rt", "rp", "rtc", CmcdData.OBJECT_TYPE_AUDIO_ONLY, "img", "wbr", "map", "q", "sub", "sup", "bdo", "iframe", "embed", TtmlNode.TAG_SPAN, "input", "select", "textarea", "label", "optgroup", "option", "legend", "datalist", "keygen", "output", "progress", "meter", "area", "param", "source", "track", "summary", "command", "device", "area", "basefont", "bgsound", "menuitem", "param", "source", "track", "data", "bdi", CmcdData.STREAMING_FORMAT_SS, "strike", "nobr", "rb"};
        String[] strArr3 = {LinkHeader.Parameters.Title, CmcdData.OBJECT_TYPE_AUDIO_ONLY, TtmlNode.TAG_P, "h1", "h2", "h3", "h4", "h5", "h6", "pre", "address", "li", "th", "td", "script", "style", "ins", "del", CmcdData.STREAMING_FORMAT_SS, "button"};
        String[] strArr4 = {"meta", "link", TtmlNode.RUBY_BASE, "frame", "img", "br", "wbr", "embed", "hr", "input", "keygen", "col", "command", "device", "area", "basefont", "bgsound", "menuitem", "param", "source", "track"};
        String[] strArr5 = {"pre", "plaintext", LinkHeader.Parameters.Title, "textarea", "script"};
        String[] strArr6 = {LinkHeader.Parameters.Title, "textarea"};
        String[] strArr7 = SharedConstants.FormSubmitTags;
        final int i10 = 4;
        final int i11 = 8;
        final int i12 = 9;
        TagSet tagSet = new TagSet().setupTags(Parser.NamespaceHtml, strArr, new Consumer() { // from class: org.jsoup.parser.f
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Tag tag = (Tag) obj;
                switch (i10) {
                    case 0:
                        TagSet.lambda$initHtmlDefault$7(tag);
                        break;
                    case 1:
                        TagSet.lambda$initHtmlDefault$8(tag);
                        break;
                    case 2:
                        TagSet.lambda$initHtmlDefault$9(tag);
                        break;
                    case 3:
                        tag.set(0);
                        break;
                    case 4:
                        TagSet.lambda$initHtmlDefault$1(tag);
                        break;
                    case 5:
                        TagSet.lambda$initHtmlDefault$11(tag);
                        break;
                    case 6:
                        tag.set(0);
                        break;
                    case 7:
                        TagSet.lambda$initHtmlDefault$13(tag);
                        break;
                    case 8:
                        tag.set(0);
                        break;
                    case 9:
                        TagSet.lambda$initHtmlDefault$3(tag);
                        break;
                    case 10:
                        TagSet.lambda$initHtmlDefault$4(tag);
                        break;
                    case 11:
                        TagSet.lambda$initHtmlDefault$5(tag);
                        break;
                    default:
                        TagSet.lambda$initHtmlDefault$6(tag);
                        break;
                }
            }
        }).setupTags(Parser.NamespaceHtml, strArr2, new Consumer() { // from class: org.jsoup.parser.f
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Tag tag = (Tag) obj;
                switch (i11) {
                    case 0:
                        TagSet.lambda$initHtmlDefault$7(tag);
                        break;
                    case 1:
                        TagSet.lambda$initHtmlDefault$8(tag);
                        break;
                    case 2:
                        TagSet.lambda$initHtmlDefault$9(tag);
                        break;
                    case 3:
                        tag.set(0);
                        break;
                    case 4:
                        TagSet.lambda$initHtmlDefault$1(tag);
                        break;
                    case 5:
                        TagSet.lambda$initHtmlDefault$11(tag);
                        break;
                    case 6:
                        tag.set(0);
                        break;
                    case 7:
                        TagSet.lambda$initHtmlDefault$13(tag);
                        break;
                    case 8:
                        tag.set(0);
                        break;
                    case 9:
                        TagSet.lambda$initHtmlDefault$3(tag);
                        break;
                    case 10:
                        TagSet.lambda$initHtmlDefault$4(tag);
                        break;
                    case 11:
                        TagSet.lambda$initHtmlDefault$5(tag);
                        break;
                    default:
                        TagSet.lambda$initHtmlDefault$6(tag);
                        break;
                }
            }
        }).setupTags(Parser.NamespaceHtml, strArr3, new Consumer() { // from class: org.jsoup.parser.f
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Tag tag = (Tag) obj;
                switch (i12) {
                    case 0:
                        TagSet.lambda$initHtmlDefault$7(tag);
                        break;
                    case 1:
                        TagSet.lambda$initHtmlDefault$8(tag);
                        break;
                    case 2:
                        TagSet.lambda$initHtmlDefault$9(tag);
                        break;
                    case 3:
                        tag.set(0);
                        break;
                    case 4:
                        TagSet.lambda$initHtmlDefault$1(tag);
                        break;
                    case 5:
                        TagSet.lambda$initHtmlDefault$11(tag);
                        break;
                    case 6:
                        tag.set(0);
                        break;
                    case 7:
                        TagSet.lambda$initHtmlDefault$13(tag);
                        break;
                    case 8:
                        tag.set(0);
                        break;
                    case 9:
                        TagSet.lambda$initHtmlDefault$3(tag);
                        break;
                    case 10:
                        TagSet.lambda$initHtmlDefault$4(tag);
                        break;
                    case 11:
                        TagSet.lambda$initHtmlDefault$5(tag);
                        break;
                    default:
                        TagSet.lambda$initHtmlDefault$6(tag);
                        break;
                }
            }
        });
        final int i13 = 10;
        TagSet tagSet2 = tagSet.setupTags(Parser.NamespaceHtml, strArr4, new Consumer() { // from class: org.jsoup.parser.f
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Tag tag = (Tag) obj;
                switch (i13) {
                    case 0:
                        TagSet.lambda$initHtmlDefault$7(tag);
                        break;
                    case 1:
                        TagSet.lambda$initHtmlDefault$8(tag);
                        break;
                    case 2:
                        TagSet.lambda$initHtmlDefault$9(tag);
                        break;
                    case 3:
                        tag.set(0);
                        break;
                    case 4:
                        TagSet.lambda$initHtmlDefault$1(tag);
                        break;
                    case 5:
                        TagSet.lambda$initHtmlDefault$11(tag);
                        break;
                    case 6:
                        tag.set(0);
                        break;
                    case 7:
                        TagSet.lambda$initHtmlDefault$13(tag);
                        break;
                    case 8:
                        tag.set(0);
                        break;
                    case 9:
                        TagSet.lambda$initHtmlDefault$3(tag);
                        break;
                    case 10:
                        TagSet.lambda$initHtmlDefault$4(tag);
                        break;
                    case 11:
                        TagSet.lambda$initHtmlDefault$5(tag);
                        break;
                    default:
                        TagSet.lambda$initHtmlDefault$6(tag);
                        break;
                }
            }
        });
        final int i14 = 11;
        TagSet tagSet3 = tagSet2.setupTags(Parser.NamespaceHtml, strArr5, new Consumer() { // from class: org.jsoup.parser.f
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Tag tag = (Tag) obj;
                switch (i14) {
                    case 0:
                        TagSet.lambda$initHtmlDefault$7(tag);
                        break;
                    case 1:
                        TagSet.lambda$initHtmlDefault$8(tag);
                        break;
                    case 2:
                        TagSet.lambda$initHtmlDefault$9(tag);
                        break;
                    case 3:
                        tag.set(0);
                        break;
                    case 4:
                        TagSet.lambda$initHtmlDefault$1(tag);
                        break;
                    case 5:
                        TagSet.lambda$initHtmlDefault$11(tag);
                        break;
                    case 6:
                        tag.set(0);
                        break;
                    case 7:
                        TagSet.lambda$initHtmlDefault$13(tag);
                        break;
                    case 8:
                        tag.set(0);
                        break;
                    case 9:
                        TagSet.lambda$initHtmlDefault$3(tag);
                        break;
                    case 10:
                        TagSet.lambda$initHtmlDefault$4(tag);
                        break;
                    case 11:
                        TagSet.lambda$initHtmlDefault$5(tag);
                        break;
                    default:
                        TagSet.lambda$initHtmlDefault$6(tag);
                        break;
                }
            }
        });
        final int i15 = 12;
        TagSet tagSet4 = tagSet3.setupTags(Parser.NamespaceHtml, strArr6, new Consumer() { // from class: org.jsoup.parser.f
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Tag tag = (Tag) obj;
                switch (i15) {
                    case 0:
                        TagSet.lambda$initHtmlDefault$7(tag);
                        break;
                    case 1:
                        TagSet.lambda$initHtmlDefault$8(tag);
                        break;
                    case 2:
                        TagSet.lambda$initHtmlDefault$9(tag);
                        break;
                    case 3:
                        tag.set(0);
                        break;
                    case 4:
                        TagSet.lambda$initHtmlDefault$1(tag);
                        break;
                    case 5:
                        TagSet.lambda$initHtmlDefault$11(tag);
                        break;
                    case 6:
                        tag.set(0);
                        break;
                    case 7:
                        TagSet.lambda$initHtmlDefault$13(tag);
                        break;
                    case 8:
                        tag.set(0);
                        break;
                    case 9:
                        TagSet.lambda$initHtmlDefault$3(tag);
                        break;
                    case 10:
                        TagSet.lambda$initHtmlDefault$4(tag);
                        break;
                    case 11:
                        TagSet.lambda$initHtmlDefault$5(tag);
                        break;
                    default:
                        TagSet.lambda$initHtmlDefault$6(tag);
                        break;
                }
            }
        });
        final int i16 = 0;
        TagSet tagSet5 = tagSet4.setupTags(Parser.NamespaceHtml, new String[]{"iframe", "noembed", "noframes", "script", "style", "xmp"}, new Consumer() { // from class: org.jsoup.parser.f
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Tag tag = (Tag) obj;
                switch (i16) {
                    case 0:
                        TagSet.lambda$initHtmlDefault$7(tag);
                        break;
                    case 1:
                        TagSet.lambda$initHtmlDefault$8(tag);
                        break;
                    case 2:
                        TagSet.lambda$initHtmlDefault$9(tag);
                        break;
                    case 3:
                        tag.set(0);
                        break;
                    case 4:
                        TagSet.lambda$initHtmlDefault$1(tag);
                        break;
                    case 5:
                        TagSet.lambda$initHtmlDefault$11(tag);
                        break;
                    case 6:
                        tag.set(0);
                        break;
                    case 7:
                        TagSet.lambda$initHtmlDefault$13(tag);
                        break;
                    case 8:
                        tag.set(0);
                        break;
                    case 9:
                        TagSet.lambda$initHtmlDefault$3(tag);
                        break;
                    case 10:
                        TagSet.lambda$initHtmlDefault$4(tag);
                        break;
                    case 11:
                        TagSet.lambda$initHtmlDefault$5(tag);
                        break;
                    default:
                        TagSet.lambda$initHtmlDefault$6(tag);
                        break;
                }
            }
        });
        final int i17 = 1;
        TagSet tagSet6 = tagSet5.setupTags(Parser.NamespaceHtml, strArr7, new Consumer() { // from class: org.jsoup.parser.f
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Tag tag = (Tag) obj;
                switch (i17) {
                    case 0:
                        TagSet.lambda$initHtmlDefault$7(tag);
                        break;
                    case 1:
                        TagSet.lambda$initHtmlDefault$8(tag);
                        break;
                    case 2:
                        TagSet.lambda$initHtmlDefault$9(tag);
                        break;
                    case 3:
                        tag.set(0);
                        break;
                    case 4:
                        TagSet.lambda$initHtmlDefault$1(tag);
                        break;
                    case 5:
                        TagSet.lambda$initHtmlDefault$11(tag);
                        break;
                    case 6:
                        tag.set(0);
                        break;
                    case 7:
                        TagSet.lambda$initHtmlDefault$13(tag);
                        break;
                    case 8:
                        tag.set(0);
                        break;
                    case 9:
                        TagSet.lambda$initHtmlDefault$3(tag);
                        break;
                    case 10:
                        TagSet.lambda$initHtmlDefault$4(tag);
                        break;
                    case 11:
                        TagSet.lambda$initHtmlDefault$5(tag);
                        break;
                    default:
                        TagSet.lambda$initHtmlDefault$6(tag);
                        break;
                }
            }
        });
        final int i18 = 2;
        final int i19 = 3;
        TagSet tagSet7 = tagSet6.setupTags(Parser.NamespaceMathml, new String[]{"math"}, new Consumer() { // from class: org.jsoup.parser.f
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Tag tag = (Tag) obj;
                switch (i18) {
                    case 0:
                        TagSet.lambda$initHtmlDefault$7(tag);
                        break;
                    case 1:
                        TagSet.lambda$initHtmlDefault$8(tag);
                        break;
                    case 2:
                        TagSet.lambda$initHtmlDefault$9(tag);
                        break;
                    case 3:
                        tag.set(0);
                        break;
                    case 4:
                        TagSet.lambda$initHtmlDefault$1(tag);
                        break;
                    case 5:
                        TagSet.lambda$initHtmlDefault$11(tag);
                        break;
                    case 6:
                        tag.set(0);
                        break;
                    case 7:
                        TagSet.lambda$initHtmlDefault$13(tag);
                        break;
                    case 8:
                        tag.set(0);
                        break;
                    case 9:
                        TagSet.lambda$initHtmlDefault$3(tag);
                        break;
                    case 10:
                        TagSet.lambda$initHtmlDefault$4(tag);
                        break;
                    case 11:
                        TagSet.lambda$initHtmlDefault$5(tag);
                        break;
                    default:
                        TagSet.lambda$initHtmlDefault$6(tag);
                        break;
                }
            }
        }).setupTags(Parser.NamespaceMathml, new String[]{"mi", "mo", "msup", "mn", "mtext"}, new Consumer() { // from class: org.jsoup.parser.f
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Tag tag = (Tag) obj;
                switch (i19) {
                    case 0:
                        TagSet.lambda$initHtmlDefault$7(tag);
                        break;
                    case 1:
                        TagSet.lambda$initHtmlDefault$8(tag);
                        break;
                    case 2:
                        TagSet.lambda$initHtmlDefault$9(tag);
                        break;
                    case 3:
                        tag.set(0);
                        break;
                    case 4:
                        TagSet.lambda$initHtmlDefault$1(tag);
                        break;
                    case 5:
                        TagSet.lambda$initHtmlDefault$11(tag);
                        break;
                    case 6:
                        tag.set(0);
                        break;
                    case 7:
                        TagSet.lambda$initHtmlDefault$13(tag);
                        break;
                    case 8:
                        tag.set(0);
                        break;
                    case 9:
                        TagSet.lambda$initHtmlDefault$3(tag);
                        break;
                    case 10:
                        TagSet.lambda$initHtmlDefault$4(tag);
                        break;
                    case 11:
                        TagSet.lambda$initHtmlDefault$5(tag);
                        break;
                    default:
                        TagSet.lambda$initHtmlDefault$6(tag);
                        break;
                }
            }
        });
        final int i20 = 5;
        final int i21 = 6;
        TagSet tagSet8 = tagSet7.setupTags(Parser.NamespaceSvg, new String[]{"svg", "femerge", "femergenode"}, new Consumer() { // from class: org.jsoup.parser.f
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Tag tag = (Tag) obj;
                switch (i20) {
                    case 0:
                        TagSet.lambda$initHtmlDefault$7(tag);
                        break;
                    case 1:
                        TagSet.lambda$initHtmlDefault$8(tag);
                        break;
                    case 2:
                        TagSet.lambda$initHtmlDefault$9(tag);
                        break;
                    case 3:
                        tag.set(0);
                        break;
                    case 4:
                        TagSet.lambda$initHtmlDefault$1(tag);
                        break;
                    case 5:
                        TagSet.lambda$initHtmlDefault$11(tag);
                        break;
                    case 6:
                        tag.set(0);
                        break;
                    case 7:
                        TagSet.lambda$initHtmlDefault$13(tag);
                        break;
                    case 8:
                        tag.set(0);
                        break;
                    case 9:
                        TagSet.lambda$initHtmlDefault$3(tag);
                        break;
                    case 10:
                        TagSet.lambda$initHtmlDefault$4(tag);
                        break;
                    case 11:
                        TagSet.lambda$initHtmlDefault$5(tag);
                        break;
                    default:
                        TagSet.lambda$initHtmlDefault$6(tag);
                        break;
                }
            }
        }).setupTags(Parser.NamespaceSvg, new String[]{"text"}, new Consumer() { // from class: org.jsoup.parser.f
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Tag tag = (Tag) obj;
                switch (i21) {
                    case 0:
                        TagSet.lambda$initHtmlDefault$7(tag);
                        break;
                    case 1:
                        TagSet.lambda$initHtmlDefault$8(tag);
                        break;
                    case 2:
                        TagSet.lambda$initHtmlDefault$9(tag);
                        break;
                    case 3:
                        tag.set(0);
                        break;
                    case 4:
                        TagSet.lambda$initHtmlDefault$1(tag);
                        break;
                    case 5:
                        TagSet.lambda$initHtmlDefault$11(tag);
                        break;
                    case 6:
                        tag.set(0);
                        break;
                    case 7:
                        TagSet.lambda$initHtmlDefault$13(tag);
                        break;
                    case 8:
                        tag.set(0);
                        break;
                    case 9:
                        TagSet.lambda$initHtmlDefault$3(tag);
                        break;
                    case 10:
                        TagSet.lambda$initHtmlDefault$4(tag);
                        break;
                    case 11:
                        TagSet.lambda$initHtmlDefault$5(tag);
                        break;
                    default:
                        TagSet.lambda$initHtmlDefault$6(tag);
                        break;
                }
            }
        });
        final int i22 = 7;
        return tagSet8.setupTags(Parser.NamespaceSvg, new String[]{"script"}, new Consumer() { // from class: org.jsoup.parser.f
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Tag tag = (Tag) obj;
                switch (i22) {
                    case 0:
                        TagSet.lambda$initHtmlDefault$7(tag);
                        break;
                    case 1:
                        TagSet.lambda$initHtmlDefault$8(tag);
                        break;
                    case 2:
                        TagSet.lambda$initHtmlDefault$9(tag);
                        break;
                    case 3:
                        tag.set(0);
                        break;
                    case 4:
                        TagSet.lambda$initHtmlDefault$1(tag);
                        break;
                    case 5:
                        TagSet.lambda$initHtmlDefault$11(tag);
                        break;
                    case 6:
                        tag.set(0);
                        break;
                    case 7:
                        TagSet.lambda$initHtmlDefault$13(tag);
                        break;
                    case 8:
                        tag.set(0);
                        break;
                    case 9:
                        TagSet.lambda$initHtmlDefault$3(tag);
                        break;
                    case 10:
                        TagSet.lambda$initHtmlDefault$4(tag);
                        break;
                    case 11:
                        TagSet.lambda$initHtmlDefault$5(tag);
                        break;
                    default:
                        TagSet.lambda$initHtmlDefault$6(tag);
                        break;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Map lambda$doAdd$0(String str) {
        return new HashMap();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$initHtmlDefault$1(Tag tag) {
        tag.set(Tag.Block);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$initHtmlDefault$11(Tag tag) {
        tag.set(Tag.Block);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$initHtmlDefault$13(Tag tag) {
        tag.set(Tag.Data);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$initHtmlDefault$3(Tag tag) {
        tag.set(Tag.InlineContainer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$initHtmlDefault$4(Tag tag) {
        tag.set(Tag.Void);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$initHtmlDefault$5(Tag tag) {
        tag.set(Tag.PreserveWhitespace);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$initHtmlDefault$6(Tag tag) {
        tag.set(Tag.RcData);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$initHtmlDefault$7(Tag tag) {
        tag.set(Tag.Data);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$initHtmlDefault$8(Tag tag) {
        tag.set(Tag.FormSubmittable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$initHtmlDefault$9(Tag tag) {
        tag.set(Tag.Block);
    }

    private TagSet setupTags(String str, String[] strArr, Consumer<Tag> consumer) {
        for (String str2 : strArr) {
            Tag tag = get(str2, str);
            if (tag == null) {
                tag = new Tag(str2, str2, str);
                tag.options = 0;
                add(tag);
            }
            consumer.accept(tag);
        }
        return this;
    }

    public TagSet add(Tag tag) {
        tag.set(Tag.Known);
        doAdd(tag);
        return this;
    }

    public boolean equals(Object obj) {
        if (obj instanceof TagSet) {
            return Objects.equals(this.tags, ((TagSet) obj).tags);
        }
        return false;
    }

    public Tag get(String str, String str2) {
        Tag tag;
        Tag tag2;
        Validate.notNull(str);
        Validate.notNull(str2);
        Map<String, Tag> map = this.tags.get(str2);
        if (map != null && (tag2 = map.get(str)) != null) {
            return tag2;
        }
        TagSet tagSet = this.source;
        if (tagSet == null || (tag = tagSet.get(str, str2)) == null) {
            return null;
        }
        Tag tagClone = tag.clone();
        doAdd(tagClone);
        return tagClone;
    }

    public int hashCode() {
        return Objects.hashCode(this.tags);
    }

    public TagSet onNewTag(Consumer<Tag> consumer) {
        Validate.notNull(consumer);
        if (this.customizers == null) {
            this.customizers = new ArrayList<>();
        }
        this.customizers.add(consumer);
        return this;
    }

    public Tag valueOf(String str, String str2, String str3, boolean z2) {
        Validate.notNull(str);
        Validate.notNull(str3);
        String strTrim = str.trim();
        Validate.notEmpty(strTrim);
        Tag tag = get(strTrim, str3);
        if (tag != null) {
            return tag;
        }
        if (str2 == null) {
            str2 = ParseSettings.normalName(strTrim);
        }
        if (!z2) {
            strTrim = str2;
        }
        Tag tag2 = get(str2, str3);
        if (tag2 == null) {
            Tag tag3 = new Tag(strTrim, str2, str3);
            doAdd(tag3);
            return tag3;
        }
        if (!z2 || strTrim.equals(str2)) {
            return tag2;
        }
        Tag tagClone = tag2.clone();
        tagClone.tagName = strTrim;
        doAdd(tagClone);
        return tagClone;
    }

    public TagSet() {
        this(null, null);
    }

    public TagSet(TagSet tagSet) {
        this(tagSet.source, copyCustomizers(tagSet));
        if (tagSet.tags.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Map<String, Tag>> entry : tagSet.tags.entrySet()) {
            HashMap map = new HashMap(entry.getValue().size());
            for (Map.Entry<String, Tag> entry2 : entry.getValue().entrySet()) {
                map.put(entry2.getKey(), entry2.getValue().clone());
            }
            this.tags.put(entry.getKey(), map);
        }
    }

    public Tag valueOf(String str, String str2, ParseSettings parseSettings) {
        return valueOf(str, null, str2, parseSettings.preserveTagCase());
    }

    public Tag valueOf(String str, String str2) {
        return valueOf(str, str2, ParseSettings.preserveCase);
    }
}
