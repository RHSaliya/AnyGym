package com.project.anygymowner;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.anygymowner.Adapter.GymsAdapter;
import com.project.anygymowner.DataHolder.myDataHolder;

import org.w3c.dom.Comment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private AppBarConfiguration drawerAppBarConfiguration, bottomAppBarConfiguration;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar myToolbar;
    private TextView navName, navEmail, navCredit;
    private ImageView navHeaderImage;
    private SearchView searchView;
    private SearchView.SearchAutoComplete searchAutoComplete;
    private Intent intent;

    private LinearLayout noGymsLL;
    private RecyclerView ownerGymsRV;
    private GymsAdapter gymsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Verify();
        myToolbar = findViewById(R.id.myToolbar);
        navName = findViewById(R.id.nav_name);
        navEmail = findViewById(R.id.nav_email);
        navCredit = findViewById(R.id.navCreditTV);
        navHeaderImage = findViewById(R.id.navHeaderImage);
        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);

        setData();

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        drawerAppBarConfiguration = new AppBarConfiguration.Builder()
                .setDrawerLayout(drawer)
                .build();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        noGymsLL = findViewById(R.id.noGymsLL);

        if (myDataHolder.ownerDataHolder.getOwnerGyms().size() == 0){
            noGymsLL.setVisibility(View.VISIBLE);
            return;
        }

        ownerGymsRV = findViewById(R.id.ownerGymsRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        ownerGymsRV.setLayoutManager(linearLayoutManager);
        gymsAdapter = new GymsAdapter(new ArrayList<>(myDataHolder.ownerDataHolder.getOwnerGyms().keySet()),new ArrayList<>(myDataHolder.ownerDataHolder.getOwnerGyms().values()));
        ownerGymsRV.setAdapter(gymsAdapter);
    }

    private void Verify() {
        if (myDataHolder.ownerDataHolder.isBlocked()) {
            linearLayout = findViewById(R.id.reviewLayout);
            linearLayout.setVisibility(View.VISIBLE);
            return;
        }
        FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH + myDataHolder.ownerDataHolder.getMobile() + "/blocked").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null && (boolean) dataSnapshot.getValue()) {
                    linearLayout = findViewById(R.id.reviewLayout);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setData() {

        //-----------------------------------------User Data
        if (myDataHolder.ownerDataHolder.getEmail() == null) {
            navName.setText(myDataHolder.ownerDataHolder.getName());
            navEmail.setText(myDataHolder.ownerDataHolder.getEmail());
        } else {
            navName.setText("Welcome\n" + myDataHolder.ownerDataHolder.getName());
            navEmail.setVisibility(View.GONE);
        }

        FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH + myDataHolder.ownerDataHolder.getMobile()).child("credit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                navCredit.setText("Credit : " + (long) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //-----------------------------------------Navigation Header Data
        if (myDataHolder.ownerDataHolder.isProfile()) {
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
                        } catch (IOException e) {
                        }
                    }
                });
            }
            Glide.with(getApplicationContext()).load(myDataHolder.PROFILE_PATH).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(navHeaderImage);
        }
        //-----------------------------------------GymDataHolder Data
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

    public void tvShareClick(View view) {
        intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Shared by " + myDataHolder.ownerDataHolder.getName() + "\nhttps://drive.google.com/open?id=135g572tFgQ2XWZGPG1bunvogSCHrd1WZ");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share AnyGym Owner");
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    public void onSettingsClick(View view) {
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
    }

    public void onProfileClick(View view) {
        intent = new Intent(getApplicationContext(), OwnerActivity.class);
        intent.putExtra("mode", "update");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem membershipItem = menu.findItem(R.id.action_notification);

        membershipItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                intent = new Intent(getApplicationContext(), CommentActivity.class);
                startActivity(intent);
                return true;
            }
        });

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        final List<String> list = new ArrayList<>(myDataHolder.ownerDataHolder.getOwnerGyms().keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, list);

        searchAutoComplete.setAdapter(adapter);

        SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String searchString = (String) parent.getItemAtPosition(position);
                searchAutoComplete.setText(searchString);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void tvAddGymClick(View view) {
        intent = new Intent(getApplicationContext(), AddGymInfoActivity.class);
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