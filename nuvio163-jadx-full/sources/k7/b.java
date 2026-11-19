package k7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f27962a = 1;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f27963b;

    public b(ArrayList arrayList) {
        this.f27963b = Collections.unmodifiableList(arrayList);
    }

    public static List c(String str) {
        if (str.charAt(0) == '\\') {
            str = str.charAt(1) == '\\' ? str.substring(2) : str.substring(1);
        }
        return Arrays.asList(str.split("\\\\"));
    }

    public boolean a() {
        List list = this.f27963b;
        if (list.size() <= 1) {
            return false;
        }
        String str = (String) list.get(1);
        return "SYSVOL".equals(str) || "NETLOGON".equals(str);
    }

    public b b(String str, b bVar) {
        List list = bVar.f27963b;
        List listC = c(str);
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(list);
        int size = listC.size();
        while (true) {
            List list2 = this.f27963b;
            if (size >= list2.size()) {
                return new b((List) arrayList);
            }
            arrayList.add((String) list2.get(size));
            size++;
        }
    }

    public String d() {
        StringBuilder sb2 = new StringBuilder();
        for (String str : this.f27963b) {
            sb2.append("\\");
            sb2.append(str);
        }
        return sb2.toString();
    }

    public String toString() {
        switch (this.f27962a) {
            case 1:
                return kotlinx.coroutines.channels.a.g(new StringBuilder("DFSPath{"), this.f27963b, "}");
            default:
                return super.toString();
        }
    }

    public b(String str) {
        this.f27963b = c(str);
    }

    public b(List list) {
        this.f27963b = list;
    }
}
