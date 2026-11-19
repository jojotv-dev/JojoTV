package com.liskovsoft.youtubeapi.service.data;

import com.liskovsoft.mediaserviceinterfaces.data.MediaItemStoryboard;
import com.liskovsoft.youtubeapi.formatbuilders.storyboard.YouTubeStoryParser;

/* JADX INFO: loaded from: classes4.dex */
public class YouTubeMediaItemStoryboard implements MediaItemStoryboard {
    private Size mSize;
    private final YouTubeStoryParser.Storyboard mStoryboard;

    public static class Size implements MediaItemStoryboard.Size {
        private final YouTubeStoryParser.Size mSize;

        public Size(YouTubeStoryParser.Size size) {
            this.mSize = size;
        }

        @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaItemStoryboard.Size
        public int getColCount() {
            return this.mSize.getColCount();
        }

        @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaItemStoryboard.Size
        public int getDurationEachMS() {
            return this.mSize.getDurationEachMS();
        }

        @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaItemStoryboard.Size
        public int getHeight() {
            return this.mSize.getHeight();
        }

        @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaItemStoryboard.Size
        public int getRowCount() {
            return this.mSize.getRowCount();
        }

        @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaItemStoryboard.Size
        public int getStartNum() {
            return this.mSize.getStartNum();
        }

        @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaItemStoryboard.Size
        public int getWidth() {
            return this.mSize.getWidth();
        }
    }

    public YouTubeMediaItemStoryboard(YouTubeStoryParser.Storyboard storyboard) {
        this.mStoryboard = storyboard;
    }

    public static MediaItemStoryboard from(YouTubeStoryParser.Storyboard storyboard) {
        if (storyboard == null) {
            return null;
        }
        return new YouTubeMediaItemStoryboard(storyboard);
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaItemStoryboard
    public int getGroupDurationMS() {
        return this.mStoryboard.getGroupDurationMS();
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaItemStoryboard
    public String getGroupUrl(int i10) {
        return this.mStoryboard.getGroupUrl(this.mSize.getStartNum() + i10);
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaItemStoryboard
    public Size getGroupSize() {
        if (this.mSize == null) {
            this.mSize = new Size(this.mStoryboard.getGroupSize());
        }
        return this.mSize;
    }
}
