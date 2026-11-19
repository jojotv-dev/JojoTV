package g;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/* JADX INFO: loaded from: classes3.dex */
public final class h extends c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ i f18152a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ String f18153b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ h.b f18154c;

    public h(i iVar, String str, h.b bVar) {
        this.f18152a = iVar;
        this.f18153b = str;
        this.f18154c = bVar;
    }

    @Override // g.c
    public final void launch(Object obj, androidx.core.app.d dVar) throws Exception {
        i iVar = this.f18152a;
        ArrayList arrayList = iVar.f18158d;
        LinkedHashMap linkedHashMap = iVar.f18156b;
        String str = this.f18153b;
        Object obj2 = linkedHashMap.get(str);
        h.b bVar = this.f18154c;
        if (obj2 == null) {
            throw new IllegalStateException(("Attempting to launch an unregistered ActivityResultLauncher with contract " + bVar + " and input " + obj + ". You must ensure the ActivityResultLauncher is registered before calling launch().").toString());
        }
        int iIntValue = ((Number) obj2).intValue();
        arrayList.add(str);
        try {
            iVar.b(iIntValue, bVar, obj);
        } catch (Exception e10) {
            arrayList.remove(str);
            throw e10;
        }
    }

    @Override // g.c
    public final void unregister() {
        this.f18152a.f(this.f18153b);
    }
}
