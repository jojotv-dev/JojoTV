package jcifs.netbios;

import java.net.InetAddress;
import jcifs.Address;
import jcifs.CIFSContext;

/* JADX INFO: loaded from: classes5.dex */
public class UniAddress implements Address {
    Object addr;
    String calledName;

    public UniAddress(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException();
        }
        this.addr = obj;
    }

    public static boolean isDotQuadIP(String str) {
        if (Character.isDigit(str.charAt(0))) {
            int length = str.length();
            char[] charArray = str.toCharArray();
            int i10 = 0;
            int i11 = 0;
            while (i10 < length) {
                int i12 = i10 + 1;
                if (!Character.isDigit(charArray[i10])) {
                    break;
                }
                if (i12 == length && i11 == 3) {
                    return true;
                }
                if (i12 >= length || charArray[i12] != '.') {
                    i10 = i12;
                } else {
                    i11++;
                    i10 += 2;
                }
            }
        }
        return false;
    }

    public boolean equals(Object obj) {
        return (obj instanceof UniAddress) && this.addr.equals(((UniAddress) obj).addr);
    }

    @Override // jcifs.Address
    public String firstCalledName() {
        Object obj = this.addr;
        if (obj instanceof NbtAddress) {
            return ((NbtAddress) obj).firstCalledName();
        }
        String hostName = ((InetAddress) obj).getHostName();
        this.calledName = hostName;
        if (isDotQuadIP(hostName)) {
            this.calledName = NbtAddress.SMBSERVER_NAME;
        } else {
            int iIndexOf = this.calledName.indexOf(46);
            if (iIndexOf > 1 && iIndexOf < 15) {
                this.calledName = this.calledName.substring(0, iIndexOf).toUpperCase();
            } else if (this.calledName.length() > 15) {
                this.calledName = NbtAddress.SMBSERVER_NAME;
            } else {
                this.calledName = this.calledName.toUpperCase();
            }
        }
        return this.calledName;
    }

    public Object getAddress() {
        return this.addr;
    }

    @Override // jcifs.Address
    public String getHostAddress() {
        Object obj = this.addr;
        return obj instanceof NbtAddress ? ((NbtAddress) obj).getHostAddress() : ((InetAddress) obj).getHostAddress();
    }

    @Override // jcifs.Address
    public String getHostName() {
        Object obj = this.addr;
        return obj instanceof NbtAddress ? ((NbtAddress) obj).getHostName() : ((InetAddress) obj).getHostName();
    }

    public int hashCode() {
        return this.addr.hashCode();
    }

    @Override // jcifs.Address
    public String nextCalledName(CIFSContext cIFSContext) {
        Object obj = this.addr;
        if (obj instanceof NbtAddress) {
            return ((NbtAddress) obj).nextCalledName(cIFSContext);
        }
        if (this.calledName == NbtAddress.SMBSERVER_NAME) {
            return null;
        }
        this.calledName = NbtAddress.SMBSERVER_NAME;
        return NbtAddress.SMBSERVER_NAME;
    }

    @Override // jcifs.Address
    public InetAddress toInetAddress() {
        Object obj = this.addr;
        if (obj instanceof Address) {
            return ((Address) obj).toInetAddress();
        }
        if (obj instanceof InetAddress) {
            return (InetAddress) obj;
        }
        return null;
    }

    public String toString() {
        return this.addr.toString();
    }

    @Override // jcifs.Address
    public <T extends Address> T unwrap(Class<T> cls) {
        Object obj = this.addr;
        if (obj instanceof Address) {
            return (T) ((Address) obj).unwrap(cls);
        }
        if (getClass().isAssignableFrom(cls)) {
            return this;
        }
        return null;
    }
}
