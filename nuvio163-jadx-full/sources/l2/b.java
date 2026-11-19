package l2;

/* JADX INFO: loaded from: classes.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f31833a;

    public final boolean equals(Object obj) {
        if (obj instanceof b) {
            return this.f31833a == ((b) obj).f31833a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f31833a);
    }

    public final String toString() {
        int i10 = this.f31833a;
        if (i10 == 16) {
            return "Confirm";
        }
        if (i10 == 6) {
            return "ContextClick";
        }
        if (i10 == 13) {
            return "GestureEnd";
        }
        if (i10 == 23) {
            return "GestureThresholdActivate";
        }
        if (i10 == 3) {
            return "KeyboardTap";
        }
        if (i10 == 0) {
            return "LongPress";
        }
        if (i10 == 17) {
            return "Reject";
        }
        if (i10 == 27) {
            return "SegmentFrequentTick";
        }
        if (i10 == 26) {
            return "SegmentTick";
        }
        if (i10 == 9) {
            return "TextHandleMove";
        }
        if (i10 == 22) {
            return "ToggleOff";
        }
        if (i10 == 21) {
            return "ToggleOn";
        }
        return i10 == 1 ? "VirtualKey" : "Invalid";
    }
}
