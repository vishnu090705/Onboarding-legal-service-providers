package com.example.legislature.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.legislature.R;
import com.example.legislature.fragments.ProviderHomeFragment;
import com.example.legislature.fragments.ProviderNotificationsFragment;
import com.example.legislature.fragments.SearchFragment;
import com.example.legislature.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProviderMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_provider);

        // Handle navigation item clicks
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        // Default selection → home
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        int id = item.getItemId();
        if (id == R.id.nav_home) {
            selectedFragment = new ProviderHomeFragment(); // show pending appointments
        } else if (id == R.id.nav_search) {
            selectedFragment = new SearchFragment(); // provider searches legal goods
        } else if (id == R.id.nav_notifications) {
            selectedFragment = new ProviderNotificationsFragment(); // accepted/declined
        } else if (id == R.id.nav_settings) {
            selectedFragment = new SettingsFragment(); // logout/settings
        }

        return loadFragment(selectedFragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_provider, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
