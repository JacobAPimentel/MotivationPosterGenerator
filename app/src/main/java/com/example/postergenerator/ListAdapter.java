package com.example.postergenerator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//The list adapter
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder>
{
    //Variables
    private final ArrayList<String> strings;
    private final PresetsActivity context; // rather than using context, use only gallery.

    public ListAdapter(PresetsActivity context, ArrayList<String> strings)
    {
        this.context = context;
        this.strings = strings;
    }//MenuAdapter

    class ListViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView textView; //The actual view
        final ListAdapter adapter;

        public ListViewHolder(View itemView, ListAdapter adapter, int groupID)
        {
            super(itemView);
            textView = itemView.findViewById(R.id.recycleString); //find the textView
            this.adapter = adapter;

            textView.setOnClickListener(new View.OnClickListener()  //Set onClick listener for the textView button.
            {
                @Override
                public void onClick(View view)
                {
                    String presetString = textView.getText().toString(); //get the preset text.

                    switch(groupID) //Quotes or Title?
                    {
                        case R.id.recycleViewQuotes:
                            if (presetString.equals(context.getPresetQuote())) //same string; disable.
                                context.setPresetQuote(null);
                            else
                                context.setPresetQuote(presetString);
                            break;
                        default:
                            if (presetString.equals(context.getPresetTitle())) //same string; disable.
                                context.setPresetTitle(null);
                            else
                                context.setPresetTitle(presetString);
                    }
                }
            });
        }
    }//ListViewHolder

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) //Inflate the and create ViewHolder
    {
        View itemView;
        switch(parent.getId()) //Title or Quote view holder? (They have different layouts, need to inflate different XML!)
        {
            case R.id.recycleViewTitles:
                itemView = LayoutInflater.from(context).inflate(R.layout.titles_recycle_view,parent,false);
                break;
            default:
                itemView = LayoutInflater.from(context).inflate(R.layout.quotes_recycle_view,parent,false);
        }
        return new ListViewHolder(itemView, this, parent.getId());
    }//onCreateViewHolder

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) //Apply data to the viewHolder.
    {
        String currentString = strings.get(position);

        holder.textView.setText(currentString);
    }//onBindViewHolder

    @Override
    public int getItemCount() {
        return strings.size();
    }
}//ListAdapter
