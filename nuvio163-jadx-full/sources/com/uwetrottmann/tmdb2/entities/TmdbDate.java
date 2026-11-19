package com.uwetrottmann.tmdb2.entities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* JADX INFO: loaded from: classes4.dex */
public class TmdbDate {
    private static final ThreadLocal<DateFormat> TMDB_DATE_FORMAT = new ThreadLocal<DateFormat>() { // from class: com.uwetrottmann.tmdb2.entities.TmdbDate.1
        @Override // java.lang.ThreadLocal
        public final DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };
    private final Date date;

    public TmdbDate(Date date) {
        this.date = date;
    }

    public final String toString() {
        return TMDB_DATE_FORMAT.get().format(this.date);
    }

    public TmdbDate(String str) {
        Date date;
        try {
            date = TMDB_DATE_FORMAT.get().parse(str);
        } catch (ParseException unused) {
            date = null;
        }
        this.date = date;
    }
}
