package com.example.postergenerator;


import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

//Setting Page
public class SettingsActivity extends BaseActivity
{
    //Views
    private ImageView posterImage;
    private View posterConstraint;
    private TextView posterTitleView;
    private View line;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        super.createToolbar(R.string.settings);

        //Find poster view and inflate poster xml into it.
        //Views
        ConstraintLayout posterView = findViewById(R.id.posterView);
        posterConstraint = getLayoutInflater().inflate(R.layout.poster_constraint, posterView, false); //
        posterView.addView(posterConstraint);

        //View inside posters.
        posterImage = posterView.findViewById(R.id.posterImage);
        posterTitleView = posterView.findViewById(R.id.posterTitleView);
        line = posterView.findViewById(R.id.line);

        /* THE SETTINGS VIEWS */
        SwitchCompat spaceOnSwitch = findViewById(R.id.titleSpaceSwitch);
        SwitchCompat lineSwitch = findViewById(R.id.lineSwitch);
        SwitchCompat cropSwitch = findViewById(R.id.centerCropSwitch);
        Spinner backgroundColorSpinner = findViewById(R.id.backgroundColorSpinner);
        Spinner fileTypeSpinner = findViewById(R.id.fileTypeSpinner);

        /* SET CHECKS AND VIEWS BASED ON PREEXISTING SETTINGS (also works for orientation change!) */
        spaceOnSwitch.setChecked(Settings.spaceOn);
        lineSwitch.setChecked(Settings.lineOn);
        cropSwitch.setChecked(Settings.cropOn);
        posterTitleView.setText(spaceOnSwitch.isChecked() ? R.string.placeholder_space : R.string.placeholder);
        line.setVisibility((lineSwitch.isChecked() ? View.VISIBLE : View.INVISIBLE));
        posterImage.setScaleType(cropSwitch.isChecked() ? ImageView.ScaleType.CENTER_CROP : ImageView.ScaleType.FIT_XY);
        if (Settings.photoUri != null)
            posterImage.setImageURI(Settings.photoUri);


        /* SWITCH LISTENER */
        CompoundButton.OnCheckedChangeListener onCheck = this::switchChecked; // use the switchChecked method reference

        //Add listener to the switch cases.
        spaceOnSwitch.setOnCheckedChangeListener(onCheck);
        lineSwitch.setOnCheckedChangeListener(onCheck);
        cropSwitch.setOnCheckedChangeListener(onCheck);

        // SPINNERS
        //In the future, I could  make the settings into an array / hashmap, and change it from there to make it set up based on loops.
        // BACKGROUND COLOR SPINNER
        SetUpSpinner(R.array.colors, backgroundColorSpinner, Settings.backgroundColor);
        backgroundColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Change setting
                TextView textView = (TextView) view;

                if (textView != null) // error handling upon orientation change.
                {
                    String color = textView.getText().toString();
                    Settings.backgroundColor = color;

                    //Also change the poster display
                    int colorId = getResources().getIdentifier(color, "color", getPackageName());
                    posterConstraint.setBackgroundResource(colorId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        //FILE TYPE SPINNER
        SetUpSpinner(R.array.fileType, fileTypeSpinner, Settings.fileType);
        fileTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view;

                if (textView != null) // error handling upon orientation change.
                    Settings.fileType = textView.getText().toString(); //set fileType settings
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }//onCreate

    @Override
    public void onResume() // changes image in case it changes.
    {
        super.onResume();
        if (Settings.photoUri != null)
            posterImage.setImageURI(Settings.photoUri);
    }//onResume

    public void SetUpSpinner(int arrayID, Spinner view, String currentSetting) // set up the adapters
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,arrayID, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        view.setAdapter(adapter);
        view.setSelection(adapter.getPosition(currentSetting)); // set the color.
    }//SetUpSpinner

    public void switchChecked(View view, boolean isChecked) // switch is moved; do an action based on the view.
    {
        switch(view.getId())
        {
            case R.id.titleSpaceSwitch:
                Settings.spaceOn = isChecked;
                posterTitleView.setText(isChecked ? R.string.placeholder_space : R.string.placeholder); // change display
                return;
            case R.id.lineSwitch:
                Settings.lineOn = isChecked;
                line.setVisibility((isChecked ? View.VISIBLE : View.INVISIBLE)); //change display
                return;
            case R.id.centerCropSwitch:
                Settings.cropOn = isChecked;
                posterImage.setScaleType(isChecked ? ImageView.ScaleType.CENTER_CROP : ImageView.ScaleType.FIT_XY); //change display
        }
    }//switchChecked
}//SettingActivity