package ru.mail.tp.callbackpal;

import android.support.design.widget.TabLayout;


import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

	private Toolbar toolbar;
	private TabLayout tabLayout;
	private ViewPager viewPager;

//	private int[] tabIcons = {
//			R.drawable.ic_action_person,
//			R.drawable.ic_action_group,
//			R.drawable.ic_action_call
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);


		viewPager = (ViewPager) findViewById(R.id.viewpager);
		setupViewPager(viewPager);

		tabLayout = (TabLayout) findViewById(R.id.tablayout);
		tabLayout.setupWithViewPager(viewPager);

	}

	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new ContactsListFragment(), "One");
//		adapter.addFragment(new CallHistoryFragment(), "one ");
		adapter.addFragment(new CallHistoryFragment(), "Two");
		adapter.addFragment(new CallHistoryFragment(), "t3");
//		adapter.addFragment(new TabThreeFragment(), "Three");
		viewPager.setAdapter(adapter);

	}
}
