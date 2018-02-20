package com.android.product.komotalk;

/**
 * Created by Product on 29/06/2015.
 */
public class Contact {

    public String mName;
    public String mNumber;
    public String mPhoto;
    public boolean mIsSeparator;

    public Contact(String name, String number, String photo, boolean isSeparator) {
        mName = name;
        mNumber = number;
        mPhoto = photo;
        mIsSeparator = isSeparator;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public String getName() {
        return mName;
    }

    public String getNumber() {
        return mNumber;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setIsSection(boolean isSection) {
        mIsSeparator = isSection;
    }



}
