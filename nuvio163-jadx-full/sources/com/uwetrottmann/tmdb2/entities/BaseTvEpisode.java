package com.uwetrottmann.tmdb2.entities;

import com.uwetrottmann.tmdb2.enumerations.EpisodeType;
import java.util.Date;

/* JADX INFO: loaded from: classes4.dex */
public class BaseTvEpisode extends BaseTvEpisodeRatingObject {
    public Date air_date;
    public Integer episode_number;
    public EpisodeType episode_type;

    /* JADX INFO: renamed from: id, reason: collision with root package name */
    public Integer f11043id;
    public String name;
    public String overview;
    public String production_code;
    public Integer runtime;
    public Integer season_number;
    public String still_path;
    public Double vote_average;
    public Integer vote_count;
}
