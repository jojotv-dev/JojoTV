package com.nuvio.vpn.android;

import android.app.Notification;
import android.graphics.SurfaceTexture;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.ProviderMismatchException;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class r {
    public static /* synthetic */ Notification.Builder a(VpnConnectionService vpnConnectionService) {
        return new Notification.Builder(vpnConnectionService, "nuvio-vpn");
    }

    public static /* synthetic */ SurfaceTexture b() {
        return new SurfaceTexture(false);
    }

    public static /* synthetic */ ClosedWatchServiceException c() {
        return new ClosedWatchServiceException();
    }

    public static /* synthetic */ FileSystemAlreadyExistsException d(String str) {
        return new FileSystemAlreadyExistsException(str);
    }

    public static /* synthetic */ FileSystemException e() {
        return new FileSystemException("Failed to delete one or more files. See suppressed exceptions for details.");
    }

    public static /* synthetic */ FileSystemNotFoundException f(String str) {
        return new FileSystemNotFoundException(str);
    }

    public static /* synthetic */ ProviderMismatchException g() {
        return new ProviderMismatchException();
    }

    public static /* synthetic */ void h() {
    }

    public static /* synthetic */ void i() {
    }
}
