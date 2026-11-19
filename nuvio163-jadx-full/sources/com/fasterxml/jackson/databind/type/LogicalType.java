package com.fasterxml.jackson.databind.type;

import java.util.Collection;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public enum LogicalType {
    Array,
    Collection,
    Map,
    POJO,
    Untyped,
    Integer,
    Float,
    Boolean,
    Enum,
    Textual,
    Binary,
    DateTime,
    OtherScalar;

    public static LogicalType fromClass(Class<?> cls, LogicalType logicalType) {
        if (cls.isEnum()) {
            return Enum;
        }
        if (cls.isArray()) {
            return cls == byte[].class ? Binary : Array;
        }
        if (Collection.class.isAssignableFrom(cls)) {
            return Collection;
        }
        if (Map.class.isAssignableFrom(cls)) {
            return Map;
        }
        return cls == String.class ? Textual : logicalType;
    }
}
