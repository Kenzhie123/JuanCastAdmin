package com.example.juancastadmin;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Home extends AppCompatActivity {

    FrameLayout H_CurrentFragmentContainer;
    BottomNavigationView H_BottomNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        H_CurrentFragmentContainer = findViewById(R.id.H_CurrentFragmentContainer);
        H_BottomNavigationBar = findViewById(R.id.H_BottomNavigationBar);



        H_BottomNavigationBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.menu_home)
                    changeFragment(new HomeFragment());
                if(item.getItemId() == R.id.menu_artists)
                    changeFragment(new ArtistsFragment());
                if(item.getItemId() == R.id.menu_poll)
                    changeFragment(new PollFragment());
                if(item.getItemId() == R.id.menu_profile)
                    changeFragment(new ProfileFragment());

                return true;
            }
        });

    }

    public void changeFragment(Fragment fragment)
    {
        FragmentManager fragmentManager  = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.H_CurrentFragmentContainer,fragment);
        fragmentTransaction.commit();
    }

}