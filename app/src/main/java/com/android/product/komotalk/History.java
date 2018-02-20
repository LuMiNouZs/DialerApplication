package com.android.product.komotalk;

/**
 * Created by Product on 14/07/2015.
 */
public class History {
    // private variables
    int _id;
    String _name;
    String _phone_number;
    String _duration;
    String _date;


    // Empty constructor
    public History() {

    }

    // constructor
    public History(int id, String name, String phone_number,String duration,String date) {
        this._id = id;
        this._name = name;
        this._phone_number = phone_number;
        this._duration = duration;
        this._date = date;

    }

    // constructor
    public History(String name, String phone_number,String duration,String date) {
        this._name = name;
        this._phone_number = phone_number;
        this._duration = duration;
        this._date = date;

    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
    }

    // getting name
    public String getName() {
        return this._name;
    }

    // setting name
    public void setName(String name) {
        this._name = name;
    }

    // getting phone number
    public String getPhoneNumber() {
        return this._phone_number;
    }

    // setting phone number
    public void setPhoneNumber(String phone_number) {
        this._phone_number = phone_number;
    }

    public String get_duration() {
        return _duration;
    }

    public void set_duration(String _duration) {
        this._duration = _duration;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

}

