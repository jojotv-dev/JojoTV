package kotlin.reflect.jvm.internal.impl.platform;

/* JADX INFO: loaded from: classes5.dex */
public abstract class SimplePlatform {
    private final String platformName;
    private final TargetPlatformVersion targetPlatformVersion;

    public String getTargetName() {
        return getTargetPlatformVersion().getDescription();
    }

    public TargetPlatformVersion getTargetPlatformVersion() {
        return this.targetPlatformVersion;
    }

    public String toString() {
        String targetName = getTargetName();
        if (targetName.length() <= 0) {
            return this.platformName;
        }
        return this.platformName + " (" + targetName + ')';
    }
}
