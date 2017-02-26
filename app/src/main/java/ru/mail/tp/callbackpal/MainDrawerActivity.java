package ru.mail.tp.callbackpal;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import ru.mail.tp.callbackpal.utils.SharedPreferenceHelper;

/**
 * Created by Martin on 25.02.2017.
 * martin00@yandex.ru
 */
public class MainDrawerActivity extends AppCompatActivity {
	private DrawerLayout mDrawer;
	private Toolbar toolbar;
	private NavigationView nvDrawer;
	private ActionBarDrawerToggle drawerToggle;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_drawer);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		nvDrawer = (NavigationView) findViewById(R.id.nvView);

		setSupportActionBar(toolbar);
		setupDrawerContent(nvDrawer);

		drawerToggle = setupDrawerToggle();
		mDrawer.addDrawerListener(drawerToggle);

		MenuItem item =  nvDrawer.getMenu().getItem(0);
		selectDrawerItem(item);
		TextView nvText = (TextView) nvDrawer.getHeaderView(0).findViewById(R.id.nav_caption);
		String phone = SharedPreferenceHelper.getValue(this, SharedPreferenceHelper.SHARED_PREF_VALUE_PHONE);
		nvText.setText(phone);
	}

	private ActionBarDrawerToggle setupDrawerToggle() {
		return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
	}

	private void signOut() {
		SharedPreferenceHelper.setValue(this, SharedPreferenceHelper.SHARED_PREF_VALUE_VALIDATION_STATUS, false);
		SharedPreferenceHelper.setValue(this, SharedPreferenceHelper.SHARED_PREF_VALUE_PHONE, null);
		//TODO: clear historyDB
	}

	private void setupDrawerContent(NavigationView navigationView) {
		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
						selectDrawerItem(menuItem);
						return true;
					}
				});
	}

	private void selectDrawerItem(MenuItem menuItem) {
		Fragment fragment = null;
		Class fragmentClass;
		switch (menuItem.getItemId()) {
			case R.id.nav_contacts_fragment:
				fragmentClass = ContactsListFragment.class;
				break;
			case R.id.nav_second_fragment:
				fragmentClass = FirstFragment.class;
				break;
			case R.id.nav_dialer_fragment:
				fragmentClass = DialerFragment.class;
				break;
			case R.id.nav_sign_out:
				signOut();
				Intent startSecondActivity = new Intent(this, SplashScreenActivity.class);
				startActivity(startSecondActivity);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				finish();
				return;
			default:
				fragmentClass = FirstFragment.class;
		}

		try {
			fragment = (Fragment) fragmentClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

		menuItem.setChecked(true);
		setTitle(menuItem.getTitle());
		mDrawer.closeDrawers();
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}
}
