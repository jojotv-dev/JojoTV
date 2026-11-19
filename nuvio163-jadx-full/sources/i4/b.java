package i4;

/* JADX INFO: loaded from: classes3.dex */
public final class b {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final String f23772b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final String f23773c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final b f23774d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final b f23775e;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f23776a;

    static {
        f9.a aVar = f.f23784c;
        f23772b = Character.toString((char) 8206);
        f23773c = Character.toString((char) 8207);
        f23774d = new b(false);
        f23775e = new b(true);
    }

    public b(boolean z2) {
        f9.a aVar = f.f23782a;
        this.f23776a = z2;
    }

    public static int a(CharSequence charSequence) {
        byte directionality;
        a aVar = new a(charSequence);
        aVar.f23770c = 0;
        int i10 = 0;
        int i11 = 0;
        int i12 = 0;
        while (true) {
            int i13 = aVar.f23770c;
            if (i13 < aVar.f23769b && i10 == 0) {
                CharSequence charSequence2 = aVar.f23768a;
                char cCharAt = charSequence2.charAt(i13);
                aVar.f23771d = cCharAt;
                if (Character.isHighSurrogate(cCharAt)) {
                    int iCodePointAt = Character.codePointAt(charSequence2, aVar.f23770c);
                    aVar.f23770c = Character.charCount(iCodePointAt) + aVar.f23770c;
                    directionality = Character.getDirectionality(iCodePointAt);
                } else {
                    aVar.f23770c++;
                    char c5 = aVar.f23771d;
                    directionality = c5 < 1792 ? a.f23767e[c5] : Character.getDirectionality(c5);
                }
                if (directionality != 0) {
                    if (directionality == 1 || directionality == 2) {
                        if (i12 == 0) {
                            return 1;
                        }
                    } else if (directionality != 9) {
                        switch (directionality) {
                            case 14:
                            case 15:
                                i12++;
                                i11 = -1;
                                continue;
                            case 16:
                            case 17:
                                i12++;
                                i11 = 1;
                                continue;
                            case 18:
                                i12--;
                                i11 = 0;
                                continue;
                        }
                    }
                } else if (i12 == 0) {
                    return -1;
                }
                i10 = i12;
            }
        }
        if (i10 != 0) {
            if (i11 == 0) {
                while (aVar.f23770c > 0) {
                    switch (aVar.a()) {
                        case 14:
                        case 15:
                            if (i10 == i12) {
                                return -1;
                            }
                            i12--;
                            break;
                        case 16:
                        case 17:
                            if (i10 == i12) {
                                return 1;
                            }
                            i12--;
                            break;
                        case 18:
                            i12++;
                            break;
                        default:
                            break;
                    }
                }
            } else {
                return i11;
            }
        }
        return 0;
    }

    public static int b(CharSequence charSequence) {
        a aVar = new a(charSequence);
        aVar.f23770c = aVar.f23769b;
        int i10 = 0;
        while (true) {
            int i11 = i10;
            while (aVar.f23770c > 0) {
                byte bA = aVar.a();
                if (bA == 0) {
                    if (i10 == 0) {
                        return -1;
                    }
                    if (i11 == 0) {
                    }
                } else if (bA == 1 || bA == 2) {
                    if (i10 == 0) {
                        return 1;
                    }
                    if (i11 == 0) {
                    }
                } else if (bA != 9) {
                    switch (bA) {
                        case 14:
                        case 15:
                            if (i11 == i10) {
                                return -1;
                            }
                            i10--;
                            break;
                        case 16:
                        case 17:
                            if (i11 == i10) {
                                return 1;
                            }
                            i10--;
                            break;
                        case 18:
                            i10++;
                            break;
                        default:
                            if (i11 != 0) {
                            }
                            break;
                    }
                } else {
                    continue;
                }
            }
            return 0;
        }
    }
}
