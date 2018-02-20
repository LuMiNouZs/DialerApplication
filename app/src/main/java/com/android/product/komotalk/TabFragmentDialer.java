package com.android.product.komotalk;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

//import com.android.product.dialerapplication.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Product on 25/06/2015.
 */
public class TabFragmentDialer extends Fragment implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener {

    private ImageButton ibtnOne, ibtnTwo, ibtnThree, ibtnFour, ibtnFive, ibtnSix, ibtnSeven, ibtnEight, ibtnNine, ibtnZero, ibtnAddContact, ibtnDial, ibtnDelete, ibtnStar, ibtnShap;
    private EditText tbPhoneNumber;
    private String convertZero;
    private static long back_pressed;
    private CountDownTimer mTimer;
    static long startTime, endTime, totalTime;
    private String callPhoneNumber, callDuration, callDate, callName, phoneNumberHistory;
    private String Number;
    private int sectionCall = 0;
    private DatabaseHandler dbHelper;
    private String checkCall = "";
    private String TAG = "Check Call";
    private String keyword = "+";
    private String dialNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbHelper = new DatabaseHandler(getActivity());
        initialWidget();
        tbPhoneNumber.setInputType(InputType.TYPE_NULL);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tbPhoneNumber.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        ibtnOne.setOnClickListener(this);
        ibtnTwo.setOnClickListener(this);
        ibtnThree.setOnClickListener(this);
        ibtnFour.setOnClickListener(this);
        ibtnFive.setOnClickListener(this);
        ibtnSix.setOnClickListener(this);
        ibtnSeven.setOnClickListener(this);
        ibtnEight.setOnClickListener(this);
        ibtnNine.setOnClickListener(this);
        ibtnZero.setOnClickListener(this);
        ibtnStar.setOnClickListener(this);
        ibtnShap.setOnClickListener(this);
        ibtnAddContact.setOnClickListener(this);
        ibtnDial.setOnClickListener(this);
        ibtnDelete.setOnTouchListener(this);
        ibtnZero.setOnTouchListener(this);
        ibtnZero.setOnLongClickListener(this);

    }

    public void initialWidget() {

        ibtnOne = (ImageButton) getActivity().findViewById(R.id.ibtnOne);
        ibtnTwo = (ImageButton) getActivity().findViewById(R.id.ibtnTwo);
        ibtnThree = (ImageButton) getActivity().findViewById(R.id.ibtnThree);
        ibtnFour = (ImageButton) getActivity().findViewById(R.id.ibtnFour);
        ibtnFive = (ImageButton) getActivity().findViewById(R.id.ibtnFive);
        ibtnSix = (ImageButton) getActivity().findViewById(R.id.ibtnSix);
        ibtnSeven = (ImageButton) getActivity().findViewById(R.id.ibtnSeven);
        ibtnEight = (ImageButton) getActivity().findViewById(R.id.ibtnEight);
        ibtnNine = (ImageButton) getActivity().findViewById(R.id.ibtnNine);
        ibtnZero = (ImageButton) getActivity().findViewById(R.id.ibtnZero);
        ibtnStar = (ImageButton) getActivity().findViewById(R.id.ibtnStar);
        ibtnShap = (ImageButton) getActivity().findViewById(R.id.ibtnShap);
        ibtnAddContact = (ImageButton) getActivity().findViewById(R.id.ibtnAddContact);
        ibtnDial = (ImageButton) getActivity().findViewById(R.id.ibtnDial);
        ibtnDelete = (ImageButton) getActivity().findViewById(R.id.ibtnDelete);
        tbPhoneNumber = (EditText) getActivity().findViewById(R.id.tbPhoneNumber);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtnOne:
                tbPhoneNumber.setText(tbPhoneNumber.getText() + "1");
                break;
            case R.id.ibtnTwo:
                tbPhoneNumber.setText(tbPhoneNumber.getText() + "2");
                break;
            case R.id.ibtnThree:
                tbPhoneNumber.setText(tbPhoneNumber.getText() + "3");
                break;
            case R.id.ibtnFour:
                tbPhoneNumber.setText(tbPhoneNumber.getText() + "4");
                break;
            case R.id.ibtnFive:
                tbPhoneNumber.setText(tbPhoneNumber.getText() + "5");
                break;
            case R.id.ibtnSix:
                tbPhoneNumber.setText(tbPhoneNumber.getText() + "6");
                break;
            case R.id.ibtnSeven:
                tbPhoneNumber.setText(tbPhoneNumber.getText() + "7");
                break;
            case R.id.ibtnEight:
                tbPhoneNumber.setText(tbPhoneNumber.getText() + "8");
                break;
            case R.id.ibtnNine:
                tbPhoneNumber.setText(tbPhoneNumber.getText() + "9");
                break;
            case R.id.ibtnZero:
                tbPhoneNumber.setText(tbPhoneNumber.getText() + "0");
                break;
            case R.id.ibtnShap:
                tbPhoneNumber.setText(tbPhoneNumber.getText() + "#");
                break;
            case R.id.ibtnStar:
                tbPhoneNumber.setText(tbPhoneNumber.getText() + "*");
                break;
            case R.id.ibtnAddContact:
                Intent i = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                i.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                i.putExtra("finishActivityOnSaveCompleted", true);
                startActivity(i);
                break;
            case R.id.ibtnDial:
                if (tbPhoneNumber.length() != 0) {
                    try {
                        convertZero = tbPhoneNumber.getText().toString().substring(1);

                        //Dial --> 3100 66xx xxx xxxx
                        //Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:*310066" + convertZero));
                        //Dial --> 75000 66xx xxx xxxx
                        //Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:*7500066" + convertZero));
                        //Dial --> 4000, 0xx xxx xxxx, #
                        //Check Sign "001"
                        if( tbPhoneNumber.getText().toString().substring(0,3).equals("001")){
                            dialNumber = tbPhoneNumber.getText().toString().substring(3);
                            Log.d("PhoneNumberCut : ", dialNumber);
                        }else{
                            dialNumber = tbPhoneNumber.getText().toString();
                            Log.d("PhoneNumberNon : ", dialNumber);
                        }
                        //Check Sign "+"
                        int intIndex = dialNumber.toString().indexOf(keyword);
                        if (intIndex == -1) {
                            dialNumber = dialNumber.toString();
                            Log.d("PhoneNumber : ", dialNumber);
                        } else {
                            dialNumber = dialNumber.toString().substring(1);
                            Log.d("PhoneNumberPlus : ", dialNumber);
                        }
                        phoneNumberHistory = tbPhoneNumber.getText().toString();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode("*7502000" + dialNumber)));
                        Log.d("Insert: ", "Inserting ..");
                        startActivity(intent);
                        tbPhoneNumber.setText("");

                        ContentResolver contentResolver = getActivity().getContentResolver();
                        CallLogObserver mObserver = new CallLogObserver(new Handler(), getActivity());
                        contentResolver.registerContentObserver(Uri.parse("content://call_log/calls"), true, mObserver);
                    } catch (Exception e) {
                        Log.e("Demo application", "Failed to invoke call", e);
                    }
                } else {
                    Toast.makeText(getActivity(), "Please input phone number for dial.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.ibtnDelete:
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        deleteChar();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        mTimer.cancel();
                        break;
                }
                break;
        }
        return false;
    }

    public void deleteChar() {

        mTimer = new CountDownTimer(9999999, 200) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                if (tbPhoneNumber.getText().toString().length() > 0) {
                    String input = tbPhoneNumber.getText().toString();
                    input = input.substring(0, input.length() - 1);
                    tbPhoneNumber.setText(input);
                }

            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

            }
        };
        mTimer.start();
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.ibtnZero:
                tbPhoneNumber.setText(tbPhoneNumber.getText() + "+");
                break;
        }
        return true;
    }

    public class CallLogObserver extends ContentObserver {
        private Context context;

        public CallLogObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.i(TAG, "CallLogs Onhange()");
            //Toast.makeText(getActivity(), "CallLogs Onhange()", Toast.LENGTH_SHORT).show();
            try {
                Cursor c = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                        null, null, CallLog.Calls.DATE + " DESC");
                if (c != null) {
                    if (c.moveToFirst()) {
                        int type = Integer.parseInt(c.getString(c
                                .getColumnIndex(CallLog.Calls.TYPE)));
                    /*
                     * increase call counter for outgoing call only
                     */
                        if (type == 2) {
                            String name = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));
                            String number = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
                            long duration = c.getLong(c.getColumnIndex(CallLog.Calls.DURATION));
                            //String date = c.getString(c.getColumnIndex(CallLog.Calls.DATE));
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
                            String strDate = sdf.format(calendar.getTime());
                            callDate = strDate.toString();

                            Log.i(TAG, "numer = " + number + " type = " + type + " duration = " + duration);
                            //Toast.makeText(getActivity(), "number = " + number + " type = " + type + " duration = " + duration, Toast.LENGTH_SHORT).show();
                            if (phoneNumberHistory != checkCall) {
                                dbHelper.addHistory(new History(phoneNumberHistory.trim(), phoneNumberHistory.trim(), String.valueOf(duration), callDate));
                                checkCall = phoneNumberHistory;
                            }
                        }
                    }
                    c.close();
                } else {
                    Log.e(TAG, "Call Logs Cursor is Empty");
                    //Toast.makeText(getActivity(), "Call Logs Cursor is Empty", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error on onChange : " + e.toString());
                //Toast.makeText(getActivity(), "Error on onChange : "+ e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
