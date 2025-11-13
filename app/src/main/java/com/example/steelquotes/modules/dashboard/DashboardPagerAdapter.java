package com.example.steelquotes.modules.dashboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DashboardPagerAdapter extends FragmentStateAdapter {

    public static final int TAB_DAY = 0;
    public static final int TAB_WEEK = 1;
    public static final int TAB_MONTH = 2;
    public static final int TAB_YEAR = 3;

    public DashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case TAB_DAY:
                return new FragmentCurrentDayEstimates();
            case TAB_WEEK:
                return new FragmentCurrentWeekEstimates();
            case TAB_MONTH:
                return new FragmentCurrentMonthEstimates();
            case TAB_YEAR:
                return new FragmentCurrentYearEstimates();
            default:
                throw new IllegalArgumentException("Invalid tab position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Total number of tabs
    }
}