package com.example.postergenerator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class PresetsActivity extends BaseActivity
{
    //Views
    private TextView presetTitleView;
    private TextView presetQuoteView;

    //Variables
    private String presetTitle = null;
    private String presetQuote = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presets);
        super.createToolbar(R.string.presets);

        //Find the views
        presetTitleView = findViewById(R.id.chosenPresetTitleView);
        presetQuoteView = findViewById(R.id.chosenPresetQuoteView);

        //Set up the Recycle View items
        SetUpRecycleView(R.array.titles,R.id.recycleViewTitles);
        SetUpRecycleView(R.array.quotes,R.id.recycleViewQuotes);
    }

    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);

        bundle.putString("presetTitle",presetTitleView.getText().toString());
        bundle.putString("presetQuote",presetQuoteView.getText().toString());
    }//onSaveInstanceState

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        presetTitle = savedInstanceState.getString("presetTitle");
        presetQuote = savedInstanceState.getString("presetQuote");
        presetTitleView.setText(presetTitle);
        presetQuoteView.setText(presetQuote);
    }//onSaveInstanceState

    public void SetUpRecycleView(int arrayID,int recycleViewID)
    {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(getResources().getStringArray(arrayID))); //arraylist of string arrays
        ListAdapter listAdapterTitles = new ListAdapter(this, words); // ListAdapter

        //Recycle View
        RecyclerView recyclerViewTitles = findViewById(recycleViewID);

        //set adapter and layout manage
        recyclerViewTitles.setAdapter(listAdapterTitles);
        recyclerViewTitles.setLayoutManager(new LinearLayoutManager(this));
    }//SetUpRecycleView

    //GETTERS
    public String getPresetTitle() { return presetTitle; }
    public String getPresetQuote() {
        return presetQuote;
    }

    //SETTERS
    public void setPresetTitle(String presetTitle)
    {
        this.presetTitle = presetTitle;
        presetTitleView.setText(presetTitle);
    }//setPresetTitle

    public void setPresetQuote(String presetQuote)
    {
        this.presetQuote = presetQuote;
        presetQuoteView.setText(presetQuote);
    }//setPresetQuote

    public void goToMain(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // no need to create a new main activity

        //Put presets in bundle.
        intent.putExtra("presetTitle",presetTitle);
        intent.putExtra("presetQuote",presetQuote);

        startActivity(intent);
    }//goToMain
}//PresetsActivity