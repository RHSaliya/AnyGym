package com.project.anygymadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.project.anygymadmin.MainFragments.GymsFragment;
import com.project.anygymadmin.MainFragments.OwnerFragment;
import com.project.anygymadmin.MainFragments.UsersFragment;
import com.project.anygymadmin.MainFragments.VerificationFragment;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static BottomNavigationView bottomNavigationView;
    //Fragments
    public static final Fragment gymsFragment = new GymsFragment();
    public static final Fragment userFragment = new UsersFragment();
    public static final Fragment ownerFragment = new OwnerFragment();
    public static final Fragment verificationFragment = new VerificationFragment();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    public static Fragment active = gymsFragment;
    private FrameLayout mainFrame;
    private AppBarConfiguration drawerAppBarConfiguration, bottomAppBarConfiguration;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar myToolbar;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-----------------------------------------Find Views
        myToolbar = findViewById(R.id.myToolbar);
        mainFrame = findViewById(R.id.mainFrame);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);


        //-----------------------------------------Toolbar
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        //-----------------------------------------Drawer Navigation
        drawerAppBarConfiguration = new AppBarConfiguration.Builder()
                .setDrawerLayout(drawer)
                .build();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //-----------------------------------------Bottom Navigation
        fragmentManager.beginTransaction().add(R.id.mainFrame, gymsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.mainFrame, userFragment).hide(userFragment).commit();
        fragmentManager.beginTransaction().add(R.id.mainFrame, ownerFragment).hide(ownerFragment).commit();
        fragmentManager.beginTransaction().add(R.id.mainFrame, verificationFragment).hide(verificationFragment).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_gyms:
                        fragmentManager.beginTransaction().hide(active).show(gymsFragment).commit();
                        active = gymsFragment;
                        return true;

                    case R.id.action_users:
                        fragmentManager.beginTransaction().hide(active).show(userFragment).commit();
                        active = userFragment;
                        return true;

                    case R.id.action_verification:
                        fragmentManager.beginTransaction().hide(active).show(verificationFragment).commit();
                        active = verificationFragment;
                        return true;

                    case R.id.action_owners:
                        fragmentManager.beginTransaction().hide(active).show(ownerFragment).commit();
                        active = ownerFragment;
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        /*MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();*/
        MenuItem searchItem = menu.findItem(R.id.action_search);
        return super.onCreateOptionsMenu(menu);
    }

    public void onFirebaseClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://console.firebase.google.com/project/anygym-7d86d/overview"));
        startActivity(browserIntent);
    }

    public void onLogoutClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AppTheme));
        builder.setTitle("SignOut?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
