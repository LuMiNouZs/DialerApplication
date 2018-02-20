package com.android.product.komotalk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

//import com.android.product.dialerapplication.R;


public class MainActivity extends Activity implements View.OnClickListener,View.OnTouchListener{

    private ImageButton ibtnOne,ibtnTwo,ibtnThree,ibtnFour,ibtnFive,ibtnSix,ibtnSeven,ibtnEight,ibtnNine,ibtnZero,ibtnAddContact,ibtnDial,ibtnDelete;
    private EditText tbPhoneNumber;
    private static long back_pressed;
    private CountDownTimer mTimer;
    static long startTime, endTime, totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialWidget();

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
        ibtnAddContact.setOnClickListener(this);
        ibtnDial.setOnClickListener(this);
        ibtnDelete.setOnTouchListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initialWidget(){

        ibtnOne = (ImageButton) findViewById(R.id.ibtnOne);
        ibtnTwo = (ImageButton) findViewById(R.id.ibtnTwo);
        ibtnThree = (ImageButton) findViewById(R.id.ibtnThree);
        ibtnFour = (ImageButton) findViewById(R.id.ibtnFour);
        ibtnFive = (ImageButton) findViewById(R.id.ibtnFive);
        ibtnSix = (ImageButton) findViewById(R.id.ibtnSix);
        ibtnSeven = (ImageButton) findViewById(R.id.ibtnSeven);
        ibtnEight = (ImageButton) findViewById(R.id.ibtnEight);
        ibtnNine = (ImageButton) findViewById(R.id.ibtnNine);
        ibtnZero = (ImageButton) findViewById(R.id.ibtnZero);
        ibtnAddContact = (ImageButton) findViewById(R.id.ibtnAddContact);
        ibtnDial = (ImageButton) findViewById(R.id.ibtnDial);
        ibtnDelete = (ImageButton) findViewById(R.id.ibtnDelete);
        tbPhoneNumber = (EditText) findViewById(R.id.tbPhoneNumber);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtnOne:
                tbPhoneNumber.setText(tbPhoneNumber.getText()+"1");
                break;
            case R.id.ibtnTwo:
                tbPhoneNumber.setText(tbPhoneNumber.getText()+"2");
                break;
            case R.id.ibtnThree:
                tbPhoneNumber.setText(tbPhoneNumber.getText()+"3");
                break;
            case R.id.ibtnFour:
                tbPhoneNumber.setText(tbPhoneNumber.getText()+"4");
                break;
            case R.id.ibtnFive:
                tbPhoneNumber.setText(tbPhoneNumber.getText()+"5");
                break;
            case R.id.ibtnSix:
                tbPhoneNumber.setText(tbPhoneNumber.getText()+"6");
                break;
            case R.id.ibtnSeven:
                tbPhoneNumber.setText(tbPhoneNumber.getText()+"7");
                break;
            case R.id.ibtnEight:
                tbPhoneNumber.setText(tbPhoneNumber.getText()+"8");
                break;
            case R.id.ibtnNine:
                tbPhoneNumber.setText(tbPhoneNumber.getText()+"9");
                break;
            case R.id.ibtnZero:
                tbPhoneNumber.setText(tbPhoneNumber.getText()+"0");
                break;
            case R.id.ibtnStar:
                tbPhoneNumber.setText(tbPhoneNumber.getText()+"*");
                break;
            case R.id.ibtnShap:
                tbPhoneNumber.setText(tbPhoneNumber.getText()+"#");
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
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:*7502000" + tbPhoneNumber.getText() + "," + Uri.encode("#")));
                        Log.d("Insert: ", "Inserting ..");
                        startActivity(intent);
                        tbPhoneNumber.setText("");
                    } catch (Exception e) {
                        Log.e("Demo application", "Failed to invoke call", e);
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Please input phone number for dial.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
