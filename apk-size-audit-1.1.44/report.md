# Audit des tailles de fonctions — NuvioTVLauncher 1.1.44

- APK : `C:\Users\arnau\Desktop\NuvioTVLauncher_1.1.44.apk`
- Package Android : `com.jojo.tv`
- Version déclarée : `1.1.44` (code `1139`)
- Taille du fichier APK : **71,896,781 octets** (68.57 Mio)
- SHA-256 : `c808515c0e93bc527fb8caaf7256fc59bb2560063dee3a2a6fe4a588dd3545cb`
- Entrées ZIP : 1188
- SDK déclaré : min `24`, target `36`
- Classe Application : `com.nuvio.tv.NuvioApplication`
- Namespace applicatif visible dans le DEX : `com.nuvio.tv` (différent du package Android).

## Répartition des composants de l’APK (Mo)

Les tailles « ZIP » indiquent la place occupée par les fichiers compressés à l’intérieur de l’APK; les tailles « brut » indiquent leur taille après extraction. Les Mo ci-dessous sont décimaux (1 Mo = 1 000 000 octets).

| Composant | Fichiers | Dans l’APK (Mo) | Après extraction (Mo) |
|---|---:|---:|---:|
| Bibliothèques natives (.so) | 35 | 37.807 | 81.581 |
| Code DEX | 5 | 17.613 | 42.579 |
| Table des ressources (`resources.arsc`) | 1 | 7.597 | 7.597 |
| Ressources res/ | 822 | 3.909 | 4.929 |
| Assets/ | 64 | 3.360 | 7.411 |
| Autres dépendances et métadonnées | 28 | 1.281 | 1.489 |
| `META-INF/` : signatures et métadonnées | 232 | 0.156 | 0.608 |
| Manifeste Android (`AndroidManifest.xml`) | 1 | 0.006 | 0.027 |
| **Entrées ZIP au total** | **1188** | **71.729** | **146.221** |

La taille du fichier APK est **71.897 Mo**. L’écart de **0.168 Mo** correspond aux en-têtes/structures ZIP, alignements et autres données d’archive. La somme « après extraction » n’est pas la taille de l’APK téléchargé.

### Détail des fichiers DEX

| Fichier | Dans l’APK (Mo) | Après extraction (Mo) |
|---|---:|---:|
| `classes.dex` | 4.876 | 11.261 |
| `classes2.dex` | 0.134 | 0.292 |
| `classes3.dex` | 3.473 | 8.734 |
| `classes4.dex` | 6.391 | 15.010 |
| `classes5.dex` | 2.739 | 7.282 |

### Détail des 35 bibliothèques natives `.so`

| Bibliothèque (armeabi-v7a) | Dans l’APK (Mo) | Après extraction (Mo) |
|---|---:|---:|
| `libavcodec.so` | 6.055 | 11.640 |
| `libj2v8.so` | 5.689 | 14.785 |
| `libnuvio-http-vpn.so` | 4.206 | 10.583 |
| `libmediainfo.so` | 3.638 | 7.511 |
| `libmpv.so` | 2.331 | 5.244 |
| `libcronet.105.0.5195.68.so` | 2.220 | 3.460 |
| `libnuvio-openvpn2-engine.so` | 1.973 | 3.614 |
| `libavformat.so` | 1.640 | 3.209 |
| `libavfilter.so` | 1.568 | 3.453 |
| `libwg-go.so` | 1.353 | 3.175 |
| `libasskt.so` | 1.193 | 2.752 |
| `libffmpegJNI.so` | 0.926 | 1.684 |
| `libnuvio-openvpn.so` | 0.889 | 1.651 |
| `libgav1JNI.so` | 0.736 | 1.616 |
| `libconscrypt_jni.so` | 0.637 | 1.280 |
| `libxml2.so` | 0.555 | 1.266 |
| `libc++_shared.so` | 0.401 | 0.963 |
| `libquickjs.so` | 0.358 | 0.563 |
| `libswscale.so` | 0.333 | 0.778 |
| `libavutil.so` | 0.325 | 0.650 |
| `libmpeghJNI.so` | 0.318 | 0.761 |
| `libsentry.so` | 0.240 | 0.505 |
| `libiamf.so` | 0.086 | 0.184 |
| `libswresample.so` | 0.045 | 0.090 |
| `libwg.so` | 0.029 | 0.047 |
| `libspake2.so` | 0.018 | 0.026 |
| `libwg-quick.so` | 0.012 | 0.023 |
| `libplayer.so` | 0.011 | 0.024 |
| `libsentry-android.so` | 0.005 | 0.013 |
| `libandroidx.graphics.path.so` | 0.004 | 0.007 |
| `libavdevice.so` | 0.003 | 0.007 |
| `libmedia3_exoplayer_jni.so` | 0.003 | 0.005 |
| `libnuvio-openvpn2.so` | 0.002 | 0.005 |
| `libiamfJNI.so` | 0.002 | 0.005 |
| `libdatastore_shared_counter.so` | 0.002 | 0.004 |

### Types de ressources et assets

Les tailles par extension complètent les totaux `res/` et `assets/`; les extensions sous 0,001 Mo peuvent s’afficher à 0,000 Mo après arrondi.

| Dossier | Type | Fichiers | Dans l’APK (Mo) | Après extraction (Mo) |
|---|---|---:|---:|---:|
| `res/` | `.png` | 422 | 2.791 | 2.791 |
| `res/` | `.ttf` | 4 | 0.909 | 1.647 |
| `res/` | `.xml` | 365 | 0.160 | 0.373 |
| `res/` | `.svg` | 28 | 0.040 | 0.109 |
| `res/` | `.webp` | 1 | 0.006 | 0.006 |
| `res/` | `.pem` | 2 | 0.002 | 0.003 |
| `assets/` | `.ttf` | 1 | 2.890 | 6.366 |
| `assets/` | `.pem` | 1 | 0.129 | 0.225 |
| `assets/` | `.dm` | 2 | 0.102 | 0.102 |
| `assets/` | `.js` | 8 | 0.061 | 0.210 |
| `assets/` | `.prof` | 1 | 0.049 | 0.049 |
| `assets/` | `.txt` | 24 | 0.047 | 0.123 |
| `assets/` | `.list` | 1 | 0.042 | 0.133 |
| `assets/` | `.glsl` | 18 | 0.028 | 0.078 |
| `assets/` | `.json` | 2 | 0.007 | 0.110 |
| `assets/` | `.html` | 2 | 0.004 | 0.012 |
| `assets/` | `.profm` | 1 | 0.002 | 0.002 |
| `assets/` | `.md` | 1 | 0.000 | 0.001 |
| `assets/` | `.properties` | 2 | 0.000 | 0.000 |

### Autres fichiers hors DEX et ressources

| Emplacement | Fichiers | Dans l’APK (Mo) | Après extraction (Mo) |
|---|---:|---:|---:|
| `org` | 6 | 1.228 | 1.317 |
| `google` | 11 | 0.039 | 0.114 |
| `kotlin` | 8 | 0.012 | 0.053 |
| `(racine)` | 2 | 0.001 | 0.002 |
| `core` | 1 | 0.001 | 0.001 |

L’inventaire exhaustif entrée par entrée (les 1 188 fichiers) est dans `apk-components.csv`, avec le chemin, la catégorie, les octets et les Mo compressés/décompressés.

## Résumé du bytecode DEX

La taille d’une méthode DEX est mesurée par `insns_size × 2` : le nombre d’octets des instructions dans son `code_item`. Cela ne comprend pas les tables de chaînes/types partagées, les données de débogage ni les blocs auxiliaires; ce n’est donc pas la taille exclusive de la méthode dans l’APK compressé. Les méthodes abstraites et natives sans `code_item` valent 0 octet DEX.

- DEX : **5** fichiers; données DEX : 42,578,596 octets bruts, 17,613,344 octets compressés dans l’APK.
- Méthodes déclarées : **199,297**; méthodes avec bytecode : **190,153**.
- Total des octets d’instructions DEX : **18,809,890** (17.94 Mio).
- Taille moyenne / médiane / P95 des méthodes avec bytecode : 98.9 / 24.0 / 338 octets.

### Par fichier DEX

| DEX | Taille brute | Taille ZIP | Classes | Méthodes | Avec bytecode | Instructions |
|---|---:|---:|---:|---:|---:|---:|
| classes.dex | 11,260,868 | 4,875,519 | 7,259 | 52,481 | 50,481 | 5,279,468 |
| classes2.dex | 292,128 | 134,372 | 154 | 1,376 | 1,315 | 161,588 |
| classes3.dex | 8,734,116 | 3,473,096 | 7,336 | 54,056 | 49,730 | 2,895,366 |
| classes4.dex | 15,009,952 | 6,391,337 | 9,889 | 53,869 | 52,908 | 8,282,762 |
| classes5.dex | 7,281,532 | 2,739,020 | 6,596 | 37,515 | 35,719 | 2,190,706 |

### Plus grosses classes par octets d’instructions

| Classe | DEX | Méthodes | Avec bytecode | Total | Plus grosse méthode |
|---|---|---:|---:|---:|---:|
| `kotlin.collections.ArraysKt___ArraysKt` | classes.dex | 1677 | 1677 | 134,716 | 178 |
| `qe.u4` | classes4.dex | 114 | 114 | 90,304 | 24,176 |
| `sf.ab` | classes4.dex | 93 | 93 | 82,072 | 7,004 |
| `sf.f0` | classes4.dex | 72 | 72 | 65,546 | 3,100 |
| `kotlin.collections.unsigned.UArraysKt___UArraysKt` | classes5.dex | 705 | 705 | 56,532 | 190 |
| `gf.c2` | classes4.dex | 38 | 38 | 54,588 | 4,954 |
| `ue.n2` | classes4.dex | 60 | 60 | 54,152 | 9,020 |
| `hf.y0` | classes4.dex | 34 | 34 | 53,942 | 7,076 |
| `mf.ng` | classes.dex | 56 | 56 | 52,356 | 20,732 |
| `ne.g` | classes4.dex | 70 | 70 | 51,608 | 8,196 |
| `mf.l0` | classes4.dex | 44 | 44 | 50,946 | 14,808 |
| `mf.fd` | classes.dex | 51 | 51 | 49,610 | 25,116 |
| `com.nuvio.tv.youtube.YouTubeScreenKt` | classes4.dex | 352 | 352 | 47,150 | 5,368 |
| `qd.ba` | classes.dex | 53 | 53 | 46,212 | 6,692 |
| `hf.k8` | classes.dex | 52 | 52 | 44,920 | 23,408 |
| `ec.p2` | classes4.dex | 47 | 47 | 43,444 | 10,944 |
| `ne.q9` | classes4.dex | 30 | 30 | 41,580 | 9,558 |
| `fe.w6` | classes4.dex | 106 | 106 | 41,374 | 10,256 |
| `qf.p0` | classes.dex | 18 | 18 | 37,774 | 6,086 |
| `ne.y3` | classes4.dex | 31 | 31 | 34,124 | 14,082 |

### Espaces de noms les plus volumineux (préfixes visibles)

| Package / espace de noms | Classes | Méthodes | Octets d’instructions |
|---|---:|---:|---:|
| `androidx.media3` | 2,591 | 23,558 | 1,396,388 |
| `com.nuvio` | 1,716 | 14,892 | 1,223,216 |
| `mf` | 642 | 3,023 | 919,508 |
| `sf` | 573 | 2,104 | 715,368 |
| `kotlin.reflect` | 1,881 | 13,373 | 670,998 |
| `com.fasterxml` | 1,033 | 12,443 | 631,336 |
| `ne` | 331 | 1,379 | 566,382 |
| `com.lagradost` | 1,000 | 7,018 | 468,894 |
| `com.liskovsoft` | 1,059 | 11,722 | 464,524 |
| `kf` | 386 | 1,626 | 453,366 |
| `qe` | 278 | 1,063 | 413,158 |
| `hf` | 312 | 1,106 | 411,474 |
| `io.ktor` | 1,381 | 8,296 | 410,912 |
| `io.sentry` | 541 | 3,494 | 330,608 |
| `af` | 110 | 583 | 306,920 |
| `qd` | 361 | 1,533 | 299,926 |
| `kotlin.collections` | 153 | 4,409 | 278,888 |
| `gf` | 131 | 475 | 256,064 |
| `fe` | 216 | 886 | 249,812 |
| `ue` | 185 | 583 | 212,262 |

Le tableau des namespaces ci-dessus regroupe les classes par préfixe visible. Il ne sépare pas automatiquement le code de l’application des dépendances embarquées; les noms courts (par exemple `qe`, `mf`, `sf`) sont également susceptibles de contenir du code applicatif obfusqué.

### Sous-ensemble identifiable sous `com.nuvio.tv`

Ce comptage couvre uniquement les classes qui gardent ce namespace dans l’APK; il constitue donc un sous-ensemble identifiable, pas la taille totale de la logique de l’application.

- Classes : **1,668**.
- Méthodes déclarées : **14,708**; avec bytecode : **14,563**.
- Instructions DEX : **1,181,280 octets** (1.13 Mio).

| Rang | Octets | Classe et méthode | DEX |
|---:|---:|---|---|
| 1 | 10 736 | `com.nuvio.tv.data.remote.dto.PlaybackIssuePlaybackSettingsDtoJsonAdapter.fromJson` | classes4.dex |
| 2 | 9 388 | `com.nuvio.tv.youtube.YouTubePlayerPanelKt.YouTubePlayerPanel` | classes4.dex |
| 3 | 7 596 | `com.nuvio.tv.core.server.AddonWebPage.getHtml` | classes4.dex |
| 4 | 7 156 | `com.nuvio.tv.youtube.o1.invoke` | classes4.dex |
| 5 | 6 908 | `com.nuvio.tv.youtube.s.invoke` | classes4.dex |
| 6 | 6 548 | `com.nuvio.tv.data.local.z.invokeSuspend` | classes4.dex |
| 7 | 6 076 | `com.nuvio.tv.youtube.v0.invoke` | classes4.dex |
| 8 | 5 694 | `com.nuvio.tv.data.local.k5.a` | classes.dex |
| 9 | 5 368 | `com.nuvio.tv.youtube.YouTubeScreenKt.Content` | classes4.dex |
| 10 | 5 212 | `com.nuvio.tv.youtube.YouTubeScreenKt.ItemCard` | classes4.dex |
| 11 | 5 180 | `com.nuvio.tv.core.plugin.cloudstream.ExternalExtensionRunner.executeSearchBased` | classes.dex |
| 12 | 4 872 | `com.nuvio.tv.data.local.t.emit` | classes4.dex |
| 13 | 4 820 | `com.nuvio.tv.core.plugin.cloudstream.ExternalExtensionRunner.executeSearchBasedWithDiagnostics` | classes.dex |
| 14 | 4 606 | `com.nuvio.tv.youtube.YouTubeScreenKt.YouTubeScreen$lambda$62$0$0` | classes4.dex |
| 15 | 4 414 | `com.nuvio.tv.data.local.t.d` | classes4.dex |

L’APK est obfusqué : plusieurs classes et méthodes ont des noms courts, et les métadonnées de source relevées dans le DEX indiquent généralement un `SourceFile` générique. L’attribution aux fichiers Kotlin/Java d’origine ou à des fonctions métier nommées n’est donc pas fiable à partir de cet APK seul.

### 30 méthodes DEX les plus volumineuses

| Rang | Octets | Unités 16 bits | Classe et méthode | DEX |
|---:|---:|---:|---|---|
| 1 | 25,218 | 12,609 | `j$.time.format.f0.<clinit>(): void` | classes2.dex |
| 2 | 25,116 | 12,558 | `mf.fd.z(mf.ka, mf.c8, boolean): void` | classes.dex |
| 3 | 24,176 | 12,088 | `qe.u4.h0(qe.s8, g1.n1, g1.n1, he.p, kotlinx.coroutines.CoroutineScope, com.nuvio.tv.MainActivity, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, ug.s, kotlin.jvm.functions.Function0, g1.n1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, g0.g0, g1.i3, he.p, qe.s, b2.b0, kotlin.jvm.functions.Function0, g1.i3, g1.i3, g1.i3, g1.n1, g1.n1, g1.n1, g1.n1, b2.b0, b2.b0, b2.b0, g1.n1, g1.n1, g1.i3, g1.n1, g1.n1, g1.n1, g1.n1, g1.l1, g1.n1, g1.m1, g1.m1, g1.n1, g1.n1, b2.b0, g0.g0, java.util.List, g1.n1, qe.s, java.util.List, g1.l1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.l1, g1.n1, g1.l1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, g1.n1, java.util.Map, java.util.Map, java.util.Map, g1.l1, g1.i3, g1.i3, g1.i3, g1.n1, g1.i3, g1.n1, g1.n1, g1.n1, g1.n1, g1.l1, g1.n1, g1.o, int): kotlin.Unit` | classes4.dex |
| 4 | 23,704 | 11,852 | `j$.time.chrono.m.b0(): int[][]` | classes2.dex |
| 5 | 23,408 | 11,704 | `hf.k8.c(w1.q, java.lang.String, com.nuvio.tv.domain.model.Meta, hf.a1, kotlin.jvm.functions.Function0, java.util.Map, int, java.util.List, int, java.util.List, boolean, com.nuvio.tv.domain.model.LibrarySourceMode, com.nuvio.tv.domain.model.NextToWatch, boolean, java.util.Map, java.util.Set, java.util.Set, boolean, boolean, com.nuvio.tv.domain.model.EpisodeShuffleSettings, boolean, kotlin.jvm.functions.Function2, com.nuvio.tv.domain.model.EpisodeOptionsOverlayStyle, boolean, com.nuvio.tv.domain.model.HomeImdbRatingsVisibility, com.nuvio.tv.domain.model.DetailImdbRatingsVisibility, boolean, boolean, java.util.List, hf.ga, int, java.util.List, java.lang.String, java.util.Map, java.util.Map, boolean, java.lang.String, com.nuvio.tv.domain.model.MDBListRatings, java.util.List, boolean, java.lang.Float, java.util.List, int, int, boolean, boolean, java.lang.String, boolean, hf.y, com.nuvio.tv.domain.model.Video, com.nuvio.tv.domain.model.TraktCommentReview, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, boolean, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, java.lang.String, java.lang.String, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, java.lang.String, java.lang.String, java.lang.String, com.nuvio.tv.domain.model.MetaTrailer, int, long, kotlin.jvm.functions.Function3, kotlin.jvm.functions.Function2, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, int, int, int, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function3, kotlin.jvm.functions.Function4, kotlin.jvm.functions.Function3, kotlin.jvm.functions.Function1, g1.o, int, int, int): void` | classes.dex |
| 6 | 22,228 | 11,114 | `ne.t0.<clinit>(): void` | classes4.dex |
| 7 | 20,940 | 10,470 | `com.liskovsoft.sharedutils.locale.LocaleUtility.initScriptsByLocale(): void` | classes4.dex |
| 8 | 20,732 | 10,366 | `mf.ng.q(mf.lh, kotlin.jvm.functions.Function3, kotlin.jvm.functions.Function5, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function4, kotlin.jvm.functions.Function2, kotlin.jvm.functions.Function1, boolean, kotlin.jvm.functions.Function0, g1.o, int): void` | classes.dex |
| 9 | 20,416 | 10,208 | `nf.l2.invokeSuspend(java.lang.Object): java.lang.Object` | classes4.dex |
| 10 | 18,800 | 9,400 | `mf.zb.invokeSuspend(java.lang.Object): java.lang.Object` | classes.dex |
| 11 | 17,536 | 8,768 | `ed.d0.invokeSuspend(java.lang.Object): java.lang.Object` | classes.dex |
| 12 | 16,860 | 8,430 | `androidx.media3.exoplayer.upstream.DefaultBandwidthMeter.getInitialBitrateCountryGroupAssignment(java.lang.String): int[]` | classes.dex |
| 13 | 16,860 | 8,430 | `androidx.media3.exoplayer.upstream.experimental.ExperimentalBandwidthMeter.getInitialBitrateCountryGroupAssignment(java.lang.String): int[]` | classes3.dex |
| 14 | 15,922 | 7,961 | `sp.i.h(kf.v4, kf.nb, kf.c3, g1.i3, g1.i3, g1.i3, java.util.Map, java.util.Map, kotlin.jvm.functions.Function3, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, boolean, kotlin.jvm.functions.Function4, kotlin.jvm.functions.Function3, kotlin.jvm.functions.Function4, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function3, kotlin.jvm.functions.Function2, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function8, kotlin.jvm.functions.Function1, int, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function3, g1.o, int, int, int, int): void` | classes3.dex |
| 15 | 15,720 | 7,860 | `kf.o7.invokeSuspend(java.lang.Object): java.lang.Object` | classes.dex |
| 16 | 15,338 | 7,669 | `kf.m4.j(kf.f6, kotlin.jvm.functions.Function3, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function3, kotlin.jvm.functions.Function2, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, g1.o, int): void` | classes.dex |
| 17 | 14,920 | 7,460 | `sf.o9.i(qe.e7, b2.b0, sf.f8, g1.o, int, int): void` | classes4.dex |
| 18 | 14,808 | 7,404 | `mf.l0.N(mf.ka, java.lang.String, java.util.Map, com.nuvio.tv.data.local.k2, boolean, java.lang.String, long, kotlin.coroutines.jvm.internal.ContinuationImpl): java.lang.Object` | classes4.dex |
| 19 | 14,082 | 7,041 | `ne.y3.h(ne.t3): io.sentry.internal.debugmeta.c` | classes4.dex |
| 20 | 13,948 | 6,974 | `ze.m0.b(java.lang.String, kotlin.jvm.functions.Function3, kotlin.jvm.functions.Function3, g1.o, int): void` | classes4.dex |
| 21 | 13,864 | 6,932 | `fe.y4.invokeSuspend(java.lang.Object): java.lang.Object` | classes4.dex |
| 22 | 12,968 | 6,484 | `com.lagradost.cloudstream3.utils.SubtitleHelper.<clinit>(): void` | classes4.dex |
| 23 | 11,866 | 5,933 | `hf.k8.D(hf.da, java.lang.Integer, java.lang.Integer, int, java.lang.String, boolean, boolean, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function3, kotlin.jvm.functions.Function4, kotlin.jvm.functions.Function3, kotlin.jvm.functions.Function14, kotlin.jvm.functions.Function14, kotlin.jvm.functions.Function14, boolean, g1.o, int, int): void` | classes.dex |
| 24 | 10,944 | 5,472 | `ec.p2.a(kotlin.jvm.functions.Function5, kotlin.jvm.functions.Function1, ec.z3, g1.o, int): void` | classes4.dex |
| 25 | 10,736 | 5,368 | `com.nuvio.tv.data.remote.dto.PlaybackIssuePlaybackSettingsDtoJsonAdapter.fromJson(com.squareup.moshi.JsonReader): com.nuvio.tv.data.remote.dto.PlaybackIssuePlaybackSettingsDto` | classes4.dex |
| 26 | 10,728 | 5,364 | `qe.j1.b(java.lang.String, java.util.List, java.util.List, java.util.ArrayList, java.lang.String, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function0, float, java.lang.String, boolean, boolean, java.lang.String, boolean, java.lang.String, java.util.ArrayList, kotlin.jvm.functions.Function1, java.lang.String, java.lang.String, java.util.Set, he.r, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.Triple, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, kotlin.jvm.functions.Function0, g1.o, int): void` | classes4.dex |
| 27 | 10,668 | 5,334 | `io.sentry.e.a(io.sentry.h3, io.sentry.ILogger): java.lang.Object` | classes.dex |
| 28 | 10,464 | 5,232 | `io.sentry.clientreport.a.a(io.sentry.h3, io.sentry.ILogger): java.lang.Object` | classes.dex |
| 29 | 10,404 | 5,202 | `com.fasterxml.jackson.core.io.doubleparser.FastDoubleMath.<clinit>(): void` | classes3.dex |
| 30 | 10,256 | 5,128 | `fe.w6.t0(boolean, boolean, java.util.Set, boolean, kotlin.coroutines.jvm.internal.ContinuationImpl): java.lang.Object` | classes4.dex |

## Symboles de fonctions natives ELF

- Bibliothèques `.so` : **35**, toutes pour `armeabi-v7a`; taille brute totale : 81,581,232 octets; taille compressée ZIP : 37,806,538 octets.
- Symboles de fonction définis trouvés : **48,327**, dont 48,317 avec une taille `st_size` non nulle.
- `st_size` mesure les symboles ELF présents. Les bibliothèques dépouillées de leurs symboles ne permettent pas de reconstituer la taille de chaque fonction; les plages de symboles peuvent aussi se chevaucher. Ces valeurs ne sont pas comparables directement aux octets de bytecode DEX.

### Bibliothèques natives

| Bibliothèque | ABI | Taille brute | Fonctions symbolisées | Avec taille | Octets de plages non chevauchantes | Plus grand symbole |
|---|---|---:|---:|---:|---:|---:|
| `libj2v8.so` | armeabi-v7a | 14,784,832 | 31,110 | 31,103 | 5,787,960 | 38,520 |
| `libmediainfo.so` | armeabi-v7a | 7,511,480 | 2,987 | 2,987 | 1,527,941 | 29,688 |
| `libmpv.so` | armeabi-v7a | 5,243,976 | 1,405 | 1,405 | 509,028 | 19,156 |
| `libffmpegJNI.so` | armeabi-v7a | 1,683,720 | 963 | 963 | 454,630 | 22,484 |
| `libxml2.so` | armeabi-v7a | 1,265,516 | 1,536 | 1,536 | 410,280 | 7,372 |
| `libasskt.so` | armeabi-v7a | 2,752,212 | 1,236 | 1,236 | 375,214 | 17,832 |
| `libmpeghJNI.so` | armeabi-v7a | 760,736 | 577 | 577 | 313,488 | 11,436 |
| `libnuvio-openvpn2-engine.so` | armeabi-v7a | 3,614,268 | 1,000 | 1,000 | 299,600 | 36,860 |
| `libc++_shared.so` | armeabi-v7a | 963,028 | 1,584 | 1,583 | 234,515 | 4,100 |
| `libconscrypt_jni.so` | armeabi-v7a | 1,279,936 | 3,009 | 3,008 | 205,066 | 7,132 |
| `libavutil.so` | armeabi-v7a | 649,576 | 612 | 612 | 166,080 | 8,116 |
| `libavformat.so` | armeabi-v7a | 3,208,980 | 153 | 153 | 55,668 | 10,832 |
| `libavcodec.so` | armeabi-v7a | 11,639,864 | 179 | 179 | 53,976 | 6,696 |
| `libnuvio-openvpn.so` | armeabi-v7a | 1,650,572 | 108 | 108 | 50,592 | 9,032 |
| `libiamf.so` | armeabi-v7a | 183,516 | 154 | 154 | 41,672 | 8,820 |
| `libgav1JNI.so` | armeabi-v7a | 1,615,904 | 458 | 457 | 38,794 | 1,088 |
| `libquickjs.so` | armeabi-v7a | 562,676 | 282 | 282 | 37,796 | 3,168 |
| `libsentry.so` | armeabi-v7a | 505,236 | 365 | 365 | 34,090 | 1,312 |
| `libavfilter.so` | armeabi-v7a | 3,453,344 | 66 | 66 | 28,000 | 10,508 |
| `libswresample.so` | armeabi-v7a | 90,120 | 22 | 22 | 14,600 | 4,524 |
| `libswscale.so` | armeabi-v7a | 777,612 | 39 | 39 | 13,720 | 3,732 |
| `libplayer.so` | armeabi-v7a | 24,384 | 36 | 36 | 8,924 | 1,544 |
| `libwg-go.so` | armeabi-v7a | 3,174,788 | 59 | 59 | 7,188 | 404 |
| `libnuvio-http-vpn.so` | armeabi-v7a | 10,582,856 | 75 | 75 | 6,380 | 392 |
| `libsentry-android.so` | armeabi-v7a | 13,060 | 16 | 16 | 3,248 | 980 |
| `libcronet.105.0.5195.68.so` | armeabi-v7a | 3,459,584 | 260 | 260 | 2,516 | 60 |
| `libavdevice.so` | armeabi-v7a | 7,152 | 14 | 14 | 1,208 | 552 |
| `libmedia3_exoplayer_jni.so` | armeabi-v7a | 5,344 | 3 | 3 | 460 | 296 |
| `libdatastore_shared_counter.so` | armeabi-v7a | 4,416 | 9 | 9 | 380 | 132 |
| `libandroidx.graphics.path.so` | armeabi-v7a | 7,252 | 1 | 1 | 340 | 340 |
| `libiamfJNI.so` | armeabi-v7a | 4,868 | 8 | 8 | 298 | 116 |
| `libspake2.so` | armeabi-v7a | 25,864 | 1 | 1 | 128 | 128 |
| `libnuvio-openvpn2.so` | armeabi-v7a | 4,748 | 0 | 0 | 0 | 0 |
| `libwg-quick.so` | armeabi-v7a | 22,736 | 0 | 0 | 0 | 0 |
| `libwg.so` | armeabi-v7a | 47,076 | 0 | 0 | 0 | 0 |

### 20 plus gros symboles natifs avec taille connue

| Rang | Taille | Bibliothèque | Symbole |
|---:|---:|---|---|
| 1 | 38,520 | `libj2v8.so` (armeabi-v7a) | `_ZN2v88internal8compiler26MachineOperatorGlobalCacheC2Ev` |
| 2 | 36,860 | `libnuvio-openvpn2-engine.so` (armeabi-v7a) | `add_option` |
| 3 | 35,012 | `libj2v8.so` (armeabi-v7a) | `_ZN2v88internal7Genesis16InitializeGlobalENS0_6HandleINS0_14JSGlobalObjectEEENS2_INS0_10JSFunctionEEE` |
| 4 | 32,088 | `libj2v8.so` (armeabi-v7a) | `_ZN2v88internal8compiler13CodeGenerator23AssembleArchInstructionEPNS1_11InstructionE` |
| 5 | 29,688 | `libmediainfo.so` (armeabi-v7a) | `ff_vp9_decode_block` |
| 6 | 27,372 | `libmediainfo.so` (armeabi-v7a) | `ff_h264_decode_mb_cavlc` |
| 7 | 26,856 | `libmediainfo.so` (armeabi-v7a) | `ff_h264_decode_mb_cabac` |
| 8 | 25,248 | `libmediainfo.so` (armeabi-v7a) | `ff_h264_filter_mb_fast` |
| 9 | 22,484 | `libffmpegJNI.so` (armeabi-v7a) | `ff_mlp_rematrix_channel_arm` |
| 10 | 22,260 | `libj2v8.so` (armeabi-v7a) | `_ZN2v88internal15CallDescriptors24InitializeOncePerProcessEv` |
| 11 | 21,532 | `libj2v8.so` (armeabi-v7a) | `_ZN2v88internal20MarkCompactCollector22ProcessMarkingWorklistILNS1_29MarkingWorklistProcessingModeE0EEEjj` |
| 12 | 21,488 | `libj2v8.so` (armeabi-v7a) | `_ZN2v88internal20MarkCompactCollector22ProcessMarkingWorklistILNS1_29MarkingWorklistProcessingModeE1EEEjj` |
| 13 | 21,204 | `libffmpegJNI.so` (armeabi-v7a) | `ff_mlp_filter_channel_arm` |
| 14 | 20,120 | `libj2v8.so` (armeabi-v7a) | `_ZN2v88internal20MarkCompactCollector11VisitObjectENS0_10HeapObjectE` |
| 15 | 20,100 | `libj2v8.so` (armeabi-v7a) | `_ZN2v88internal20MarkCompactCollector13RevisitObjectENS0_10HeapObjectE` |
| 16 | 19,156 | `libmpv.so` (armeabi-v7a) | `TT_RunIns` |
| 17 | 17,832 | `libasskt.so` (armeabi-v7a) | `TT_RunIns` |
| 18 | 17,292 | `libj2v8.so` (armeabi-v7a) | `_ZN2v88internal8compiler29SimplifiedOperatorGlobalCacheC2Ev` |
| 19 | 15,728 | `libmediainfo.so` (armeabi-v7a) | `ff_yuv2rgb_c_init_tables` |
| 20 | 15,396 | `libj2v8.so` (armeabi-v7a) | `_ZN2v88internal18AstStringConstantsC1EPNS0_7IsolateEy` |

## Fichiers livrables

- `dex-methods.csv` : toutes les méthodes déclarées dans les DEX, triées par taille décroissante.
- `dex-classes.csv` : total de bytecode par classe.
- `native-functions.csv` : symboles ELF de type fonction trouvés dans les `.so`.
- `native-libraries.csv` : résumé des `.so` par ABI.

Analyse statique hors exécution; aucune méthode n’a été appelée ni mesurée en mémoire vive.
