package com.android.product.komotalk;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

//import com.android.product.dialerapplication.R;

/**
 * Created by Product on 18/06/2015.
 */
public class ActionBarTabActivity extends Activity {
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab = actionBar.newTab();
        tab.setIcon(R.drawable.ic_history);
        TabListener<TabFragmentHistory> tab1 = new TabListener<TabFragmentHistory>(this, "History",TabFragmentHistory.class);
        tab.setTabListener(tab1);
        actionBar.addTab(tab,0, false);

        tab = actionBar.newTab();
        tab.setIcon(R.drawable.ic_tel);
        TabListener<TabFragmentDialer> tab2 = new TabListener<TabFragmentDialer>(this, "Dial",TabFragmentDialer.class);
        tab.setTabListener(tab2);
        actionBar.addTab(tab,1,true);

        tab = actionBar.newTab();
        tab.setIcon(R.drawable.ic_contact);
        TabListener<TabFragmentContact> tab3 = new TabListener<TabFragmentContact>(this, "Contact",TabFragmentContact.class);
        tab.setTabListener(tab3);
        actionBar.addTab(tab,2, false);


    }

    private class TabListener <T extends Fragment> implements ActionBar.TabListener{
        private Fragment mFragment;
        private  final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;


        public TabListener(Activity activity,String tag,Class<T> clz){
            mActivity = activity;
            mTag = tag;
            mClass =clz;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            if(mFragment == null){
                mFragment = Fragment.instantiate(mActivity,mClass.getName());
                fragmentTransaction.add(android.R.id.content,mFragment,mTag);
            }else{
                fragmentTransaction.attach(mFragment);
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            if(mFragment != null){
                fragmentTransaction.detach(mFragment);
            }
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
