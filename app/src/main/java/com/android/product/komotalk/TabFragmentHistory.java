package com.android.product.komotalk;


import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.product.dialerapplication.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by Product on 25/06/2015.
 */
public class TabFragmentHistory extends Fragment {

    private TextView txtHistoryPhoneNumber, txtHistoryDuration;
    private ImageView imHistoryPhoto, imHistoryCalling;
    private ListView lvHistory;
    private ProgressDialog pDialog;
    private int _ID;
    private Date callDateTime = null;
    public static int[] drawable = {R.drawable.ic_calling_out, R.drawable.ic_calling_in, R.drawable.ic_calling_miss};
    private String callPhoneNumber, callDuration, callDate, callName, Number,phoneNumberHistory;
    private DatabaseHandler dbHelper;
    private SimpleCursorAdapter mAdapter;
    private MatrixCursor mMatrixCursor;
    static boolean flag = false;
    static long startTime, endTime, totalTime;
    int sectionCall = 0;

    private EditText tbContactName, tbContactPhoneNumber;
    private Button btnAddContact, btnCancelContact;

    private String[] mProjection;
    private byte[] photoByte = null;
    public Context mContext;
    public ArrayList<History> mList;

    private String checkCall = "";
    private String TAG = "Check Call";
    private String keyword = "+";
    private String signDialer = "001";
    private String dialNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbHelper = new DatabaseHandler(getActivity());

        mMatrixCursor = new MatrixCursor(new String[]{"_id", dbHelper.KEY_NAME, dbHelper.KEY_PHONE_NUMBER, dbHelper.KEY_DURATION, dbHelper.KEY_DATE});
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.item_history, null, new String[]{dbHelper.KEY_NAME, dbHelper.KEY_DURATION, dbHelper.KEY_DATE},
                new int[]{R.id.txtHistoryNumber,
                        R.id.txtHistoryDuration, R.id.txtHistoryDate}, 0);

        lvHistory = (ListView) getActivity().findViewById(R.id.lvHistory);
        lvHistory.setAdapter(mAdapter);

        AsyncLoadContacts asyncLoadContacts = new AsyncLoadContacts();
        asyncLoadContacts.execute();


        lvHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                mMatrixCursor.moveToPosition(position);
                final String historyName = mMatrixCursor.getString(mMatrixCursor.getColumnIndex(dbHelper.KEY_NAME));
                final String historyPhoneNumber = mMatrixCursor.getString(mMatrixCursor.getColumnIndex(dbHelper.KEY_PHONE_NUMBER));

                final Dialog dialog = new Dialog(getActivity());
                dialog.setTitle("Add Contact");
                dialog.setContentView(R.layout.dialog_add_contact);
                dialog.setCancelable(true);

                final EditText txtAddContact = (EditText) dialog.findViewById(R.id.tbAddContactName);
                final EditText txtAddPhoneNumber = (EditText) dialog.findViewById(R.id.tbAddContactPhoneNumber);
                Button btnAddConFirm = (Button) dialog.findViewById(R.id.btnAddContact);
                Button btnCancelAdd = (Button) dialog.findViewById(R.id.btnCancelAddContact);

                txtAddContact.setText(historyName);
                txtAddPhoneNumber.setText(historyPhoneNumber);

                btnAddConFirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (txtAddContact.length() != 0 && txtAddPhoneNumber.length() != 0) {
                            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                            int rawContactInsertIndex = ops.size();

                            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
                            ops.add(ContentProviderOperation
                                    .newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                    .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, txtAddContact.getText().toString()) // Name of the person
                                    .build());
                            ops.add(ContentProviderOperation
                                    .newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(
                                            ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                    .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, txtAddPhoneNumber.getText().toString()) // Number of the person
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); // Type of mobile number
                            try {
                                ContentProviderResult[] res = getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                            } catch (RemoteException e) {
                                // error
                            } catch (OperationApplicationException e) {
                                // error
                            }

                            Toast.makeText(getActivity(), "Add contact complete.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getActivity(), "Please insert ContactName Or ContactPhoneNumber.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });

                btnCancelAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                return false;
            }
        });

        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                mMatrixCursor.moveToPosition(position);
                final String historyName = mMatrixCursor.getString(mMatrixCursor.getColumnIndex(dbHelper.KEY_NAME));
                final String historyPhoneNumber = mMatrixCursor.getString(mMatrixCursor.getColumnIndex(dbHelper.KEY_PHONE_NUMBER));
                final String historyDuration = mMatrixCursor.getString(mMatrixCursor.getColumnIndex(dbHelper.KEY_DURATION));
                final String historyDate = mMatrixCursor.getString(mMatrixCursor.getColumnIndex(dbHelper.KEY_DATE));

                String Date = "";
                String Time = "";
                try {
                    String dateStr = historyDate;
                    DateFormat srcDf = new SimpleDateFormat("dd MMM yyyy HH:mm");
                    // parse the date string into Date object
                    Date dateD = srcDf.parse(dateStr);
                    DateFormat destDf = new SimpleDateFormat("dd MMM yyyy");
                    // format the date into another format
                    Date = destDf.format(dateD);

                    DateFormat srcTf = new SimpleDateFormat("dd MMM yyyy HH:mm");
                    // parse the date string into Date object
                    Date dateT = srcDf.parse(dateStr);
                    DateFormat destTf = new SimpleDateFormat("HH:mm");
                    // format the date into another format
                    Time = destTf.format(dateT);;

                }
                catch (ParseException e) {
                    e.printStackTrace();
                }

                final Dialog dialog = new Dialog(getActivity());
                dialog.setTitle(historyPhoneNumber.toString().trim());
                dialog.setContentView(R.layout.dialog_call_history);
                dialog.setCancelable(true);

                final TextView txtPhoneNumber = (TextView) dialog.findViewById(R.id.txtDialerHistoryNumber);
                final TextView txtDate = (TextView) dialog.findViewById(R.id.txtDialerHistoryDate);
                final TextView txtTime = (TextView) dialog.findViewById(R.id.txtDialerHistoryTime);
                final TextView txtDuration = (TextView) dialog.findViewById(R.id.txtDialerHistoryDuration);
                ImageButton ibtnCall = (ImageButton) dialog.findViewById(R.id.ibtnDialerHistoryCall);

                txtPhoneNumber.setText(historyName.toString());
                txtDuration.setText("DurationCall : " + historyDuration.toString());
                txtDate.setText(Date.toString());
                txtTime.setText("TimeCall : " + Time.toString());

                ibtnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            //Check Sign "001"
                            if( historyPhoneNumber.toString().substring(0,3).equals("001")){
                                dialNumber = historyPhoneNumber.toString().substring(3);
                                Log.d("PhoneNumberCut : ", dialNumber);
                            }else{
                                dialNumber = historyPhoneNumber.toString();
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
                            phoneNumberHistory = historyPhoneNumber.toString();
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*7502000" + dialNumber)));
                            callPhoneNumber = historyPhoneNumber.toString();
                            callName = historyName.toString();
                            startActivity(intent);
                            ContentResolver contentResolver = getActivity().getContentResolver();
                            CallLogObserver mObserver = new CallLogObserver(new Handler(), getActivity());
                            contentResolver.registerContentObserver(Uri.parse("content://call_log/calls"), true, mObserver);
                        } catch (Exception e) {
                            Log.e("Demo application", "Failed to invoke call", e);
                        }
                    }
                });

                dialog.show();;
            }
        });


    }

    private class AsyncLoadContacts extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Cursor doInBackground(Void... params) {

            try {
                Log.d("Reading: ", "Reading all contacts..");
                List<History> history = dbHelper.getAllHistory();
                for (History cn : history) {
                    String log = "Id: " + cn.getID() + " ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber() + ",Duration " + cn.get_duration();
                    // Writing Contacts to log
                    long contactId = cn.getID();
                    String displayName = cn.getName();
                    String phone_number = cn.getPhoneNumber();
                    String duration = cn.get_duration();
                    String date = cn.get_date();
                    String asDutarion = (Integer.parseInt(duration) / 60) + ":" + (Integer.parseInt(duration) % 60);
                    Log.d("Name: ", log);
                    mMatrixCursor.addRow(new Object[]{Long.toString(contactId), displayName, phone_number, asDutarion, date});
                }
            } catch (Exception e) {

            }
            return mMatrixCursor;
        }

        @Override
        protected void onPostExecute(Cursor result) {
            // Setting the cursor containing contacts to listview
            mAdapter.swapCursor(result);
            pDialog.dismiss();
        }
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
                            long duration = c.getLong(c .getColumnIndex(CallLog.Calls.DURATION));

                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
                            String strDate = sdf.format(calendar.getTime());
                            callDate = strDate.toString();
                            Log.i(TAG, "numer = " + number + " type = " + type + " duration = " + duration);
                            //Toast.makeText(getActivity(), "numer = " + number + " type = " + type + " duration = " + duration, Toast.LENGTH_SHORT).show();
                            if(phoneNumberHistory != checkCall) {
                                dbHelper.addHistory(new History(callName, phoneNumberHistory.trim(), String.valueOf(duration), callDate));
                                checkCall = phoneNumberHistory;
                            }
                        }
                    }
                    c.close();
                } else {
                    //Log.e(TAG,"Call Logs Cursor is Empty");
                    Toast.makeText(getActivity(), "Call Logs Cursor is Empty", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error on onChange : " + e.toString());
                //Toast.makeText(getActivity(), "Error on onChange : "+ e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

}
