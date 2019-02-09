package com.lpi.itineraires.report;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.lpi.itineraires.R;
import com.lpi.itineraires.utils.Utils;

public class ReportActivity extends AppCompatActivity
{

/**
 * The {@link android.support.v4.view.PagerAdapter} that will provide
 * fragments for each of the sections. We use a
 * {@link FragmentPagerAdapter} derivative, which will keep every
 * loaded fragment in memory. If this becomes too memory intensive, it
 * may be best to switch to a
 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
 */
private SectionsPagerAdapter mSectionsPagerAdapter;

/**
 * The {@link ViewPager} that will host the section contents.
 */
private ViewPager mViewPager;

@Override
protected void onCreate(Bundle savedInstanceState)
{
	Utils.setTheme(this);

	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_report);

	Toolbar toolbar = findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	// Create the adapter that will return a fragment for each of the three
	// primary sections of the activity.
	mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

	// Set up the ViewPager with the sections adapter.
	mViewPager = findViewById(R.id.container);
	mViewPager.setAdapter(mSectionsPagerAdapter);

	TabLayout tabLayout = findViewById(R.id.tabs);
	tabLayout.setupWithViewPager(mViewPager);

	FloatingActionButton fab = findViewById(R.id.fab);
	fab.setOnClickListener(new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			Fragment f = mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
			if ( f instanceof ReportFragment)
				((ReportFragment) f).Vide();
		}
	});

}


@Override
public boolean onOptionsItemSelected(@NonNull MenuItem item)
{
	// Handle action bar item clicks here. The action bar will
	// automatically handle clicks on the Home/Up button, so long
	// as you specify a parent activity in AndroidManifest.xml.
	int id = item.getItemId();

	//noinspection SimplifiableIfStatement
	if (id == R.id.action_settings)
	{
		return true;
	}

	return super.onOptionsItemSelected(item);
}


/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter
{
	private ReportFragment[] _fragments;

	public SectionsPagerAdapter(FragmentManager fm)
	{
		super(fm);
		_fragments = new ReportFragment[2];
		_fragments[0] = HistoriqueFragment.newInstance();
		_fragments[1] = TracesFragment.newInstance();
	}

	@Override
	public Fragment getItem(int position)
	{
		// getItem is called to instantiate the fragment for the given page.
		// Return a PlaceholderFragment (defined as a static inner class below).
		if (position < _fragments.length)
			return _fragments[position];

		return _fragments[0];
	}

	@Override
	public int getCount()
	{
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		switch (position)
		{
			case 0:
				return "Historique";
			case 1:
				return "Traces";
		}
		return null;
	}
}

}
