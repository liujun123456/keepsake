package com.shunlai.common.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Liu
 * @Date 2021/4/15
 * @mobile 18711832023
 */
public class PathItem implements Parcelable {
    public long id=0;
    public String path="";
    public long duration=0;
    public int type=1; //1图片 2视频

    public PathItem(long id, String path, long duration, int type){
        this.id=id;
        this.path=path;
        this.duration=duration;
        this.type=type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.path);
        dest.writeLong(this.duration);
        dest.writeInt(this.type);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readLong();
        this.path = source.readString();
        this.duration = source.readLong();
        this.type = source.readInt();
    }

    public PathItem() {
    }

    protected PathItem(Parcel in) {
        this.id = in.readLong();
        this.path = in.readString();
        this.duration = in.readLong();
        this.type = in.readInt();
    }

    public static final Creator<PathItem> CREATOR = new Creator<PathItem>() {
        @Override
        public PathItem createFromParcel(Parcel source) {
            return new PathItem(source);
        }

        @Override
        public PathItem[] newArray(int size) {
            return new PathItem[size];
        }
    };
}
