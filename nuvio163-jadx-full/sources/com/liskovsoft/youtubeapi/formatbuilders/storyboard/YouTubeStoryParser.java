package com.liskovsoft.youtubeapi.formatbuilders.storyboard;

import a0.i;
import com.liskovsoft.sharedutils.mylogger.Log;
import h1.f;
import java.util.ArrayList;
import java.util.List;
import o2.a;
import w.h0;

/* JADX INFO: loaded from: classes4.dex */
public class YouTubeStoryParser {
    private static final String SECTION_DELIM = "\\|";
    private static final String SPEC_DELIM = "#";
    private static final String TAG = "YouTubeStoryParser";
    private int mSegmentDurationUs;
    private final String mSpec;
    private int mStartSegmentNum;

    public class Size {
        private int mColsCount;
        private int mDurationEachMS;
        private int mHeight;
        private String mImageName;
        private int mQuality;
        private int mRowsCount;
        private String mSignature;
        private int mStartNum;
        private int mWidth;

        public Size() {
        }

        public int getColCount() {
            return this.mColsCount;
        }

        public int getDurationEachMS() {
            return this.mDurationEachMS;
        }

        public int getHeight() {
            return this.mHeight;
        }

        public int getRowCount() {
            return this.mRowsCount;
        }

        public int getStartNum() {
            return this.mStartNum;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public String toString() {
            return f.j(this.mQuality, "}", a.w(this.mWidth, "{width: ", ", height: ", ", quality: ", this.mHeight));
        }
    }

    public class Storyboard {
        private static final String IMG_NAME_VAR = "$N";
        private static final String IMG_NUM_VAR = "$M";
        private static final String INDEX_VAR = "$L";
        private static final int MIN_WIDTH = 4;
        private static final String SIGNATURE_PARAM = "sigh";
        private String mBaseUrl;
        private List<Size> mSizes = new ArrayList();
        private int mCachedIdx = -1;
        private int mCachedLenMS = -1;

        public Storyboard() {
        }

        private int chooseBestSizeIdx() {
            int i10 = this.mCachedIdx;
            if (i10 != -1) {
                return i10;
            }
            int iIndexOf = -1;
            int i11 = 0;
            for (Size size : this.mSizes) {
                if (i11 < size.mWidth || (i11 == size.mWidth && this.mSizes.get(iIndexOf).mColsCount < size.mColsCount)) {
                    if (size.mColsCount >= 4) {
                        i11 = size.mWidth;
                        iIndexOf = this.mSizes.indexOf(size);
                    }
                }
            }
            int i12 = iIndexOf != -1 ? iIndexOf : 0;
            this.mCachedIdx = i12;
            return i12;
        }

        public int getGroupDurationMS() {
            int i10 = this.mCachedLenMS;
            if (i10 != -1) {
                return i10;
            }
            Size size = this.mSizes.get(chooseBestSizeIdx());
            int i11 = size.mDurationEachMS * size.mColsCount * size.mRowsCount;
            this.mCachedLenMS = i11;
            return i11;
        }

        public Size getGroupSize() {
            return this.mSizes.get(chooseBestSizeIdx());
        }

        public String getGroupUrl(int i10) {
            String strReplace = this.mBaseUrl.replace("\\", "");
            int iChooseBestSizeIdx = chooseBestSizeIdx();
            Size size = this.mSizes.get(iChooseBestSizeIdx);
            String strReplace2 = strReplace.replace(INDEX_VAR, String.valueOf(iChooseBestSizeIdx));
            if (size.mImageName != null) {
                strReplace2 = strReplace2.replace(IMG_NAME_VAR, size.mImageName);
            }
            String strReplace3 = strReplace2.replace(IMG_NUM_VAR, String.valueOf(i10));
            if (size.mSignature == null) {
                return strReplace3;
            }
            StringBuilder sbS = i.s(strReplace3.contains("?") ? strReplace3.concat("&") : strReplace3.concat("?"), "sigh=");
            sbS.append(size.mSignature);
            return sbS.toString();
        }
    }

    private YouTubeStoryParser(String str) {
        this.mSpec = str;
    }

    public static YouTubeStoryParser from(String str) {
        return new YouTubeStoryParser(str);
    }

    private Storyboard parseLiveStoryboard(String str, String[] strArr) {
        Storyboard storyboard = new Storyboard();
        String[] strArrSplit = strArr[0].split(SPEC_DELIM);
        if (strArrSplit.length != 5) {
            Log.e(TAG, h0.k("Error inside spec: ", str), new Object[0]);
            return null;
        }
        storyboard.mBaseUrl = strArrSplit[0];
        Size size = new Size();
        size.mWidth = Integer.parseInt(strArrSplit[1]);
        size.mHeight = Integer.parseInt(strArrSplit[2]);
        size.mColsCount = Integer.parseInt(strArrSplit[3]);
        size.mRowsCount = Integer.parseInt(strArrSplit[4]);
        size.mDurationEachMS = this.mSegmentDurationUs / 1000;
        size.mStartNum = this.mStartSegmentNum;
        storyboard.mSizes.add(size);
        return storyboard;
    }

    private Storyboard parseRegularStoryboard(String str, String[] strArr) {
        Storyboard storyboard = new Storyboard();
        storyboard.mBaseUrl = strArr[0];
        for (int i10 = 1; i10 < strArr.length; i10++) {
            String[] strArrSplit = strArr[i10].split(SPEC_DELIM);
            if (strArrSplit.length != 8) {
                Log.e(TAG, h0.k("Error inside spec: ", str), new Object[0]);
                return null;
            }
            Size size = new Size();
            size.mWidth = Integer.parseInt(strArrSplit[0]);
            size.mHeight = Integer.parseInt(strArrSplit[1]);
            size.mQuality = Integer.parseInt(strArrSplit[2]);
            size.mColsCount = Integer.parseInt(strArrSplit[3]);
            size.mRowsCount = Integer.parseInt(strArrSplit[4]);
            size.mDurationEachMS = Integer.parseInt(strArrSplit[5]);
            size.mImageName = strArrSplit[6];
            size.mSignature = strArrSplit[7];
            storyboard.mSizes.add(size);
        }
        return storyboard;
    }

    private Storyboard parseStoryboardSpec(String str) {
        String[] strArrSplit = str.split(SECTION_DELIM);
        return strArrSplit.length == 1 ? parseLiveStoryboard(str, strArrSplit) : parseRegularStoryboard(str, strArrSplit);
    }

    public Storyboard extractStory() {
        String str = this.mSpec;
        if (str == null) {
            return null;
        }
        return parseStoryboardSpec(str);
    }

    public void setSegmentDurationUs(int i10) {
        this.mSegmentDurationUs = i10;
    }

    public void setStartSegmentNum(int i10) {
        this.mStartSegmentNum = i10;
    }
}
