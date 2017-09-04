package com.fusiotec.servicecenterapi.servicecenter.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.claudiodegio.msv.adapter.SearchSuggestRvAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Owner on 8/19/2017.
 */

public class CustomSearchAdapter extends SearchSuggestRvAdapter implements Filterable {
    private List<String> mSuggestions;
    private List<String> mSuggestionsFiltered;
    public CustomSearchAdapter(Context context, List<String> suggestions){
        super(context,suggestions);
        this.mSuggestions = suggestions;
        this.mSuggestionsFiltered = suggestions;
    }

    public Filter getFilter() {
        Filter filter = new Filter() {
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(!TextUtils.isEmpty(constraint)) {
                    ArrayList searchData = new ArrayList();
                    Iterator var4 = mSuggestions.iterator();

                    while(var4.hasNext()) {
                        String string = (String)var4.next();
                        if(string.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            searchData.add(string);
                        }
                    }

                    filterResults.values = searchData;
                    filterResults.count = searchData.size();
                }

                return filterResults;
            }

            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results.values != null) {
                    CustomSearchAdapter.this.mSuggestionsFiltered = (ArrayList)results.values;
                    CustomSearchAdapter.this.notifyDataSetChanged();
                }

            }
        };
        return filter;
    }
}
