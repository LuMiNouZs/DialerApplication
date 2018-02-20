package com.android.product.komotalk;

import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

//import com.android.product.dialerapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Product on 25/06/2015.
 */
public class TabFragmentContact extends Fragment {

    private String[] mProjection;
    private byte[] photoByte = null;
    public Context mContext;
    public ArrayList<Contact> mList = null;
    public ArrayList<Contact> listpicOrigin;
    public LayoutInflater mInflater;
    //private ValueAdapter valueAdapter;
    private TextWatcher mSearchTw;
    private String callPhoneNumber, callDuration, callDate, callName,phoneNumberHistory;
    private DatabaseHandler dbHelper;
    private String checkCall = "";
    private String TAG = "Check Call";
    private String keyword = "+";
    private String dialNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.activity_contact,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = new DatabaseHandler(getActivity());

        final EditText tbSearch = (EditText) getActivity().findViewById(R.id.tbSearchContact);

        mProjection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Photo.PHOTO
        };

        final Cursor cursor = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                mProjection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        final int nameIndex = cursor.getColumnIndex(mProjection[0]);
        int numberIndex = cursor.getColumnIndex(mProjection[1]);
        int photoIndex = cursor.getColumnIndex(mProjection[2]);

       final ArrayList contacts = new ArrayList();

        int position = 0;
        boolean isSeparator = false;
        while (cursor.moveToNext()) {
            isSeparator = false;

            String name = cursor.getString(nameIndex);
            String number = cursor.getString(numberIndex);
            String photo = cursor.getString(photoIndex);
            char[] nameArray;

            // If it is the first item then need a separator
            if (position == 0) {
                isSeparator = true;
                nameArray = name.toCharArray();
            } else {
                // Move to previous
                cursor.moveToPrevious();

                // Get the previous contact's name
                String previousName = cursor.getString(nameIndex);

                // Convert the previous and current contact names
                // into char arrays
                char[] previousNameArray = previousName.toCharArray();
                nameArray = name.toCharArray();

                // Compare the first character of previous and current contact names
                if (nameArray[0] != previousNameArray[0]) {
                    isSeparator = true;
                }

                // Don't forget to move to next
                // which is basically the current item
                cursor.moveToNext();
            }

            // Need a separator? Then create a Contact
            // object and save it's name as the section
            // header while pass null as the phone number
            if (isSeparator) {
               Contact contact = new Contact(String.valueOf(nameArray[0]), null, null, isSeparator);
                contacts.add(contact);

            }

            // Create a Contact object to store the name/number details
            Contact contact = new Contact(name, number ,photo, false);
            contacts.add(contact);
            position++;
        }

        // Creating our custom adapter
        final ContactCustomAdapter adapter = new ContactCustomAdapter(getActivity(), contacts);

        // Create the list view and bind the adapter
        final ListView listView = (ListView) getActivity().findViewById(R.id.lv_contacts);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Toast.makeText(getActivity(),contact.getmNumber(),Toast.LENGTH_LONG).show();


                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_contact_detail);
                dialog.setCancelable(true);

                final TextView txtName = (TextView) dialog.findViewById(R.id.txtContactName);
                final TextView txtPhoneNumber = (TextView) dialog.findViewById(R.id.txtMobilePhone);
                txtName.setText(mList.get(position).getName());
                txtPhoneNumber.setText(mList.get(position).getNumber());

                ImageButton ibtnDialMobile = (ImageButton) dialog.findViewById(R.id.ibtnDialMobile);
                ibtnDialMobile.setOnClickListener(new View.OnClickListener() {
                                                      public void onClick(View v) {
                                                          try {
                                                              //Check Sign "001"
                                                              if( txtPhoneNumber.getText().toString().substring(0,3).equals("001")){
                                                                  dialNumber = txtPhoneNumber.getText().toString().substring(3);
                                                                  Log.d("PhoneNumberCut : ", dialNumber);
                                                              }else{
                                                                  dialNumber = txtPhoneNumber.getText().toString();
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
                                                              //convertZero = txtPhoneNumber.getText().toString().substring(1);
                                                              //Dial --> 3100 66xx xxx xxxx
                                                              //Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:*310066" + convertZero));
                                                              //Dial --> 75000 66xx xxx xxxx
                                                              //Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:*7500066" + convertZero));
                                                              //Dial --> 4000, 0xx xxx xxxx, #
                                                              phoneNumberHistory = txtPhoneNumber.getText().toString().trim();
                                                              Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*7502000," + dialNumber)));
                                                              Log.d("Insert: ", "Inserting ..");
                                                              startActivity(intent);
                                                              callName = txtName.getText().toString();
                                                              ContentResolver contentResolver = getActivity().getContentResolver();
                                                              CallLogObserver mObserver = new CallLogObserver(new Handler(), getActivity());
                                                              //contentResolver.unregisterContentObserver(mObserver);
                                                              contentResolver.registerContentObserver(Uri.parse("content://call_log/calls"), true, mObserver);
                                                              //contentResolver.unregisterContentObserver(mObserver);
                                                              //contentResolver.delete(Uri.parse("content://call_log/calls"), CallLog.Calls.CACHED_NAME + "=?", new String[]{mObserver.toString()});
                                                          } catch (Exception e) {
                                                              Log.e("Demo application", "Failed to invoke call", e);
                                                          }

                                                          dialog.cancel();

                                                      }
                                                  }

                );

                dialog.show();

            }
        });

        //setting adapter to autoTextView
        tbSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {

                return false;
            }
        });
        tbSearch.addTextChangedListener(listView);
        tbSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = tbSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }



    public class ContactCustomAdapter extends BaseAdapter {
       // private Context mContext;
       // public ArrayList<Contact> mList;

        // View Type for Separators
        private static final int ITEM_VIEW_TYPE_SEPARATOR = 0;
        // View Type for Regular rows
        private static final int ITEM_VIEW_TYPE_REGULAR = 1;
        // Types of Views that need to be handled
        // -- Separators and Regular rows --
        private static final int ITEM_VIEW_TYPE_COUNT = 2;

        public ContactCustomAdapter(Context context, ArrayList list) {
            mContext = context;
            mList = list;
            mInflater = LayoutInflater.from(mContext);
            listpicOrigin = new ArrayList<Contact>();
            listpicOrigin.addAll(mList);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return ITEM_VIEW_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            boolean isSection = mList.get(position).mIsSeparator;

            if (isSection) {
                return ITEM_VIEW_TYPE_SEPARATOR;
            } else {
                return ITEM_VIEW_TYPE_REGULAR;
            }
        }

        @Override
        public boolean isEnabled(int position) {
            return getItemViewType(position) != ITEM_VIEW_TYPE_SEPARATOR;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;

            Contact contact = mList.get(position);
            int itemViewType = getItemViewType(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (itemViewType == ITEM_VIEW_TYPE_SEPARATOR) {
                    // If its a section ?
                    view = inflater.inflate(R.layout.contact_section_header, null);
                } else {
                    // Regular row
                    view = inflater.inflate(R.layout.item_contact, null);
                }
            } else {
                view = convertView;
            }


            if (itemViewType == ITEM_VIEW_TYPE_SEPARATOR) {
                // If separator

                TextView separatorView = (TextView) view.findViewById(R.id.separator);
                separatorView.setText(contact.mName);
            } else {
                // If regular

                // Set contact name and number
                TextView contactNameView = (TextView) view.findViewById(R.id.txtName);
                //TextView phoneNumberView = (TextView) view.findViewById(R.id.phone_number);
                ImageView contactPhoto = (ImageView) view.findViewById(R.id.imPhoto);

                contactNameView.setText(contact.mName);
                //phoneNumberView.setText( contact.mNumber );
                // contactPhoto.setImageURI(contact.mPhoto);
            }

            return view;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase();
            mList.clear();
             if (charText.length() == 0) {
                 mList.addAll(listpicOrigin);
             } else {
                 for (Contact ct : listpicOrigin) {
                     if (ct.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                         mList.add(ct);
                         }
                     }
                 }
             notifyDataSetChanged();
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
                            long duration = c.getLong(c.getColumnIndex(CallLog.Calls.DURATION));
                            //String date = c.getString(c.getColumnIndex(CallLog.Calls.DATE));
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
                            String strDate = sdf.format(calendar.getTime());
                            callDate = strDate.toString();

                            Log.i(TAG, "numer = " + number + " type = " + type + " duration = " + duration);
                            //Toast.makeText(getActivity(), "number = " + number + " type = " + type + " duration = " + duration, Toast.LENGTH_SHORT).show();
                            if(phoneNumberHistory != checkCall) {
                                dbHelper.addHistory(new History(callName, phoneNumberHistory.trim(), String.valueOf(duration), callDate));
                                checkCall = phoneNumberHistory;
                            }
                        }
                    }
                    c.close();
                } else {
                    Log.e(TAG,"Call Logs Cursor is Empty");
                    //Toast.makeText(getActivity(), "Call Logs Cursor is Empty", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error on onChange : "+ e.toString());
                //Toast.makeText(getActivity(), "Error on onChange : "+ e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    public static void deleteLastCallLog(Context context, String phoneNumber) {

        try {
            //Thread.sleep(4000);
            String strNumberOne[] = { phoneNumber };
            Cursor cursor = context.getContentResolver().query(
                    CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.NUMBER + " = ? ", strNumberOne, CallLog.Calls.DATE + " DESC");

            if (cursor.moveToFirst()) {
                int idOfRowToDelete = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
                int foo = context.getContentResolver().delete(
                        CallLog.Calls.CONTENT_URI,
                        CallLog.Calls._ID + " = ? ",
                        new String[] { String.valueOf(idOfRowToDelete) });

            }
        } catch (Exception ex) {
            Log.v("deleteNumber",
                    "Exception, unable to remove # from call log: "
                            + ex.toString());
        }
    }
}
