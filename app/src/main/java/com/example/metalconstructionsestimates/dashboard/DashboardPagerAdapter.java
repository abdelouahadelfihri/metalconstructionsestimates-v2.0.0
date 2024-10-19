package com.example.metalconstructionsestimates.dashboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.fragment.app.FragmentActivity;

public class DashboardPagerAdapter extends FragmentStateAdapter {
    public DashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FragmentCurrentDayEstimates();
            case 1:
                return new FragmentCurrentWeekEstimates();
            case 2:
                return new FragmentCurrentMonthEstimates();
            case 3:
                return new FragmentCurrentYearEstimates();
            default:
                // Return a default fragment in case of invalid position
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Number of tabs
    }
}