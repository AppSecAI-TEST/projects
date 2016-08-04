package fi.aalto.itmc.mobilesensingcommon.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by laptop on 4/24/16.
 */
public class ParcelableStringList implements Parcelable{
    private List<String> mData;

    public ParcelableStringList(List<String> mData) {
        this.mData = mData;
    }

    protected ParcelableStringList(Parcel in) {
        mData = new LinkedList<String>();
        in.readStringList(mData);
    }

    public static final Creator<ParcelableStringList> CREATOR = new Creator<ParcelableStringList>() {
        @Override
        public ParcelableStringList createFromParcel(Parcel in) {
            return new ParcelableStringList(in);
        }

        @Override
        public ParcelableStringList[] newArray(int size) {
            return new ParcelableStringList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(mData);
    }

    public List<String> getData(){
        return mData != null ? mData : new LinkedList<String>();
    }
}
