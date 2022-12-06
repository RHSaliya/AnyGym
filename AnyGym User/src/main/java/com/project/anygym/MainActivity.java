package com.project.anygym;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.AppBarConfiguration;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.anygym.DataHolder.myDataHolder;
import com.project.anygym.MainFragments.ActivityFragment;
import com.project.anygym.MainFragments.GymsFragment;
import com.project.anygym.MainFragments.MapFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int GPS_REQUEST = 23;
    public static BottomNavigationView bottomNavigationView;
    //Fragments
    private final GymsFragment gymsFragment = new GymsFragment();
    private final ActivityFragment activityFragment = new ActivityFragment();
    private final MapFragment mapsFragment = new MapFragment();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment active = gymsFragment;
    private TextView navName, navEmail, navCredit, navCity;
    private AppBarConfiguration drawerAppBarConfiguration, bottomAppBarConfiguration;
    private ImageView navHeaderImage;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar myToolbar;
    private Intent intent;
    public static boolean isGPS;
    private SearchView searchView;
    private SearchView.SearchAutoComplete searchAutoComplete;
    private boolean doubleBackPressFlag = false;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-----------------------------------------Find Views
        myToolbar = findViewById(R.id.myToolbar);
        navName = findViewById(R.id.nav_name);
        navEmail = findViewById(R.id.nav_email);
        navCredit = findViewById(R.id.navCreditTV);
        navCity = findViewById(R.id.navCityTV);
        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        handler = new Handler();

        myDataHolder.gymDBREF = FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE);

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

        getData();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //-----------------------------------------Bottom Navigation
                fragmentManager.beginTransaction().add(R.id.mainFrame, gymsFragment).commit();
                fragmentManager.beginTransaction().add(R.id.mainFrame, mapsFragment).hide(mapsFragment).commit();
                fragmentManager.beginTransaction().add(R.id.mainFrame, activityFragment).hide(activityFragment).commit();
                bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_gyms:
                                fragmentManager.beginTransaction().hide(active).show(gymsFragment).commit();
                                active = gymsFragment;
                                return true;

                            case R.id.action_map:
                                fragmentManager.beginTransaction().hide(active).show(mapsFragment).commit();
                                active = mapsFragment;
                                return true;

                            case R.id.action_activity:
                                fragmentManager.beginTransaction().hide(active).show(activityFragment).commit();
                                active = activityFragment;
                                return true;
                        }
                        return false;
                    }
                });
            }
        }, 100);
    }


    private void getData() {

        //-----------------------------------------User Data
        if (myDataHolder.userDataHolder.getEmail() == null) {
            navName.setText(myDataHolder.userDataHolder.getName());
            navEmail.setText(myDataHolder.userDataHolder.getEmail());
        } else {
            navName.setText("Welcome\n" + myDataHolder.userDataHolder.getName());
            navEmail.setVisibility(View.GONE);
        }

        FirebaseDatabase.getInstance().getReference(myDataHolder.USER_DATABASE_PATH + myDataHolder.userDataHolder.getMobile()).child("credit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                navCredit.setText("Credit : " + (long) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //-----------------------------------------Navigation Header Data
        if (myDataHolder.userDataHolder.getIsProfile()) {
            navHeaderImage = findViewById(R.id.navHeaderImage);
            final File imgfile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile.jpg");
            if (!imgfile.exists()) {
                final long ONE_MEGABYTE = 1024 * 1024;
                myDataHolder.profileStorageREF.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        try {
                            OutputStream fOut = new FileOutputStream(imgfile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                            fOut.close();
                        } catch (IOException ignored) {
                        }
                    }
                });
            }
            Glide.with(getApplicationContext()).load(myDataHolder.PROFILE_PATH).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(navHeaderImage);
        }

        navCity.setText(getSharedPreferences("UserPref", MODE_PRIVATE).getString("ref","NONE").replace("-",", "));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem membershipItem = menu.findItem(R.id.action_memberships);

        membershipItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                intent = new Intent(getApplicationContext(), MembershipActivity.class);
                startActivity(intent);
                return true;
            }
        });

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        final List<String> list = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(myDataHolder.REGISTERED_GYMS_PATH).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    list.add(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, list);

        searchAutoComplete.setAdapter(adapter);

        SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String searchString = (String) parent.getItemAtPosition(position);
                searchAutoComplete.setText(searchString);
                if (active == gymsFragment) {
                    gymsFragment.onSearch(searchString, 1);
                } else if (active == mapsFragment) {
                    mapsFragment.onSearch(searchString, 1);
                } else if (active == activityFragment) {
                    activityFragment.onSearch(searchString, 1);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (active == gymsFragment) {
                    gymsFragment.onSearch(query, 1);
                } else if (active == mapsFragment) {
                    mapsFragment.onSearch(query,1);
                } else if (active == activityFragment) {
                    activityFragment.onSearch(query,1);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (active == gymsFragment) {
                    gymsFragment.onSearch(newText, 2);
                } else if (active == mapsFragment) {
                    mapsFragment.onSearch(newText,2);
                } else if (active == activityFragment) {
                    activityFragment.onSearch(newText,2);
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    public void onProfileClick(View view) {
        intent = new Intent(getApplicationContext(), UserActivity.class);
        intent.putExtra("mode", "update");
        startActivity(intent);
    }

    public void onLogoutClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AppTheme));
        builder.setTitle("SignOut?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        new File(myDataHolder.PROFILE_PATH).delete();
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

    public void tvShareClick(View view) {
        intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Shared by " + myDataHolder.userDataHolder.getName() + "\nhttps://drive.google.com/open?id=135g572tFgQ2XWZGPG1bunvogSCHrd1WZ");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share AnyGym");
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    public void onAddCreditClick(View view) {
        intent = new Intent(getApplicationContext(), BuyCreditActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            case GPS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isGPS = true;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        navCredit.setText("Credit : " + myDataHolder.userDataHolder.getCredit());
        if (navHeaderImage != null) {
            if (myDataHolder.userDataHolder.getIsProfile()) {
                Glide.with(getApplicationContext()).load(myDataHolder.PROFILE_PATH).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(navHeaderImage);
            } else {
                navHeaderImage.setImageDrawable(getDrawable(R.drawable.ic_male_user_profile_picture));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (active != gymsFragment) {
            fragmentManager.beginTransaction().hide(active).show(gymsFragment).commit();
            active = gymsFragment;
        } else if (doubleBackPressFlag) {
            super.onBackPressed();
        } else {
            this.doubleBackPressFlag = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackPressFlag = false;
            }
        }, 2000);
    }

    public void onSettingsClick(View view) {
        intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    public void onSelectCityClick(View view) {
        intent = new Intent(getApplicationContext(), FirstTimeActivity.class);
        startActivity(intent);
    }

    public void tvAboutClick(View view) {
        drawer.closeDrawers();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialogue);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.about, viewGroup, false);
        TextView contact = dialogView.findViewById(R.id.contactUs);
        contact.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
