package com.example.legislature.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.legislature.R;
import com.example.legislature.fragments.NotificationsFragment;
import com.example.legislature.fragments.SearchFragment;
import com.example.legislature.fragments.SettingsFragment;
import com.example.legislature.fragments.UserHomeFragment; // user home fragment (law feed, etc.)
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivityWithNav extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_nav);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Handle navigation item clicks
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        // Default selected item → home
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    // ✅ Handle bottom nav selection
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            selectedFragment = new UserHomeFragment(); // Show law feed / user home
        } else if (id == R.id.nav_search) {
            selectedFragment = new SearchFragment(); // Search lawyers or goods
        } else if (id == R.id.nav_notifications) {
            selectedFragment = new NotificationsFragment(); // Appointments updates
        } else if (id == R.id.nav_settings) {
            selectedFragment = new SettingsFragment(); // Logout/settings
        }

        return loadFragment(selectedFragment);
    }

    // ✅ Fragment loader
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
