package com.swayam.animalquizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindString;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static final String GUESS = "KEY_NUMBER_OF_GUESSES";
    public static final String ANIMAL_TYPE = "KEY_TYPES_OF_ANIMALS";
    public static final String QUIZ_BACKGROUND_COLOR = "KEY_BACKGROUND_COLOR";
    public static final String QUIZ_FONT = "KEY_FONT";

    private boolean isSettingChanged = false;

    static Typeface allura;
    static Typeface amatic;
    static Typeface pacifico;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        allura = Typeface.createFromAsset(getAssets(),"fonts/allura.otf");
        amatic = Typeface.createFromAsset(getAssets(),"fonts/amatic.ttf");
        pacifico = Typeface.createFromAsset(getAssets(),"fonts/pacifico.ttf");

        PreferenceManager.setDefaultValues(this,R.xml.quiz_preferences,false);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}