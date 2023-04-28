package com.example.postergenerator;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

//Holds navigation and toolbar so all activities have it.
public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout drawerLayout;

    protected void createToolbar(int titleID)
    {
        //Find the views
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.navigation);

        //setToolbars
        toolbar.setTitle(titleID);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        //set Navigation
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }//createToolbar

    public void StartIntent(Class activityClass) // use by both navigation and toolbar menu items
    {
        if (!activityClass.getName().equals(this.getClass().getName())) //If on the page already, do nothing.
        {
            Intent intent = new Intent(this, activityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // no need to multiple new activities
            this.startActivity(intent);
        }
    }//StartIntent

    @Override
    public boolean onNavigationItemSelected(MenuItem item) //navigation item selected
    {
        Class activityClass = null;
        switch(item.getItemId())
        {
            case R.id.nav_settings:
            {
                activityClass = SettingsActivity.class;
                break;
            }
            case R.id.nav_presets:
            {
                activityClass = PresetsActivity.class;
                break;
            }
            case R.id.nav_poster:
                activityClass = MainActivity.class;
        }

        StartIntent(activityClass);

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }//onNavigationItemSelected

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Class activityClass = null;
        switch(item.getItemId())
        {
            case R.id.action_settings:
            {
                activityClass = SettingsActivity.class;
                break;
            }
            case R.id.action_presets:
            {
                activityClass = PresetsActivity.class;
                break;
            }
            case R.id.action_postergenerator:
                activityClass = MainActivity.class;
        }

        StartIntent(activityClass);
        return true;
    }//onOptionsItemSelected
}//BaseActivity