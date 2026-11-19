package k4;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.eclipsesource.v8.Platform;
import java.lang.reflect.Method;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class i {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Context f27785a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final j f27786b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public VelocityTracker f27787c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public float f27788d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f27789e = -1;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public int f27790f = -1;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public int f27791g = -1;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final int[] f27792h = {Integer.MAX_VALUE, 0};

    public i(Context context, j jVar) {
        this.f27785a = context;
        this.f27786b = jVar;
    }

    /* JADX WARN: Code duplicated, block: B:29:0x007b  */
    /* JADX WARN: Code duplicated, block: B:49:0x00c0  */
    public final void a(MotionEvent motionEvent, int i10) {
        char c5;
        char c10;
        int scaledMinimumFlingVelocity;
        int scaledMaximumFlingVelocity;
        boolean z2;
        int source = motionEvent.getSource();
        int deviceId = motionEvent.getDeviceId();
        int i11 = this.f27790f;
        int[] iArr = this.f27792h;
        if (i11 == source && this.f27791g == deviceId && this.f27789e == i10) {
            z2 = false;
            c5 = 1;
            c10 = 0;
        } else {
            Context context = this.f27785a;
            ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
            int deviceId2 = motionEvent.getDeviceId();
            int source2 = motionEvent.getSource();
            int i12 = Build.VERSION.SDK_INT;
            c5 = 1;
            c10 = 0;
            if (i12 >= 34) {
                Method method = u0.f27849a;
                scaledMinimumFlingVelocity = d0.c(viewConfiguration, deviceId2, i10, source2);
            } else {
                Method method2 = u0.f27849a;
                InputDevice device = InputDevice.getDevice(deviceId2);
                if (device == null || device.getMotionRange(i10, source2) == null) {
                    scaledMinimumFlingVelocity = Integer.MAX_VALUE;
                } else {
                    Resources resources = context.getResources();
                    int identifier = (source2 == 4194304 && i10 == 26) ? resources.getIdentifier("config_viewMinRotaryEncoderFlingVelocity", "dimen", Platform.ANDROID) : -1;
                    Objects.requireNonNull(viewConfiguration);
                    if (identifier == -1) {
                        scaledMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
                    } else if (identifier == 0 || (scaledMinimumFlingVelocity = resources.getDimensionPixelSize(identifier)) < 0) {
                        scaledMinimumFlingVelocity = Integer.MAX_VALUE;
                    }
                }
            }
            iArr[0] = scaledMinimumFlingVelocity;
            int deviceId3 = motionEvent.getDeviceId();
            int source3 = motionEvent.getSource();
            if (i12 >= 34) {
                scaledMaximumFlingVelocity = d0.b(viewConfiguration, deviceId3, i10, source3);
            } else {
                InputDevice device2 = InputDevice.getDevice(deviceId3);
                if (device2 == null || device2.getMotionRange(i10, source3) == null) {
                    scaledMaximumFlingVelocity = Integer.MIN_VALUE;
                } else {
                    Resources resources2 = context.getResources();
                    int identifier2 = (source3 == 4194304 && i10 == 26) ? resources2.getIdentifier("config_viewMaxRotaryEncoderFlingVelocity", "dimen", Platform.ANDROID) : -1;
                    Objects.requireNonNull(viewConfiguration);
                    if (identifier2 == -1) {
                        scaledMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
                    } else if (identifier2 == 0 || (scaledMaximumFlingVelocity = resources2.getDimensionPixelSize(identifier2)) < 0) {
                        scaledMaximumFlingVelocity = Integer.MIN_VALUE;
                    }
                }
            }
            iArr[1] = scaledMaximumFlingVelocity;
            this.f27790f = source;
            this.f27791g = deviceId;
            this.f27789e = i10;
            z2 = true;
        }
        if (iArr[c10] == Integer.MAX_VALUE) {
            VelocityTracker velocityTracker = this.f27787c;
            if (velocityTracker != null) {
                velocityTracker.recycle();
                this.f27787c = null;
                return;
            }
            return;
        }
        if (this.f27787c == null) {
            this.f27787c = VelocityTracker.obtain();
        }
        VelocityTracker velocityTracker2 = this.f27787c;
        e0.a(velocityTracker2, motionEvent);
        e0.b(velocityTracker2);
        float fC = e0.c(velocityTracker2, i10);
        j jVar = this.f27786b;
        float fU = jVar.u() * fC;
        float fSignum = Math.signum(fU);
        if (z2 || (fSignum != Math.signum(this.f27788d) && fSignum != 0.0f)) {
            jVar.w();
        }
        if (Math.abs(fU) < iArr[c10]) {
            return;
        }
        int i13 = iArr[c5];
        float fMax = Math.max(-i13, Math.min(fU, i13));
        this.f27788d = jVar.q(fMax) ? fMax : 0.0f;
    }
}
