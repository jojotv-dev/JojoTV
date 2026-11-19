package com.uwetrottmann.tmdb2.entities;

import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public abstract class BaseResultsPage<T> {

    /* JADX INFO: renamed from: id, reason: collision with root package name */
    public Integer f11042id;
    public Integer page;
    public List<T> results;
    public Integer total_pages;
    public Integer total_results;
}
