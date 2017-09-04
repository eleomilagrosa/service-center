package com.fusiotec.servicecenterapi.servicecenter.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.claudiodegio.msv.SuggestionMaterialSearchView;
import com.fusiotec.servicecenterapi.servicecenter.adapters.CustomSearchAdapter;

import java.util.List;

/**
 * Created by Owner on 8/19/2017.
 */

public class CustomSuggestionSearch extends SuggestionMaterialSearchView implements View.OnClickListener {

    public CustomSuggestionSearch(Context context) {
        this(context, (AttributeSet)null);
    }

    public CustomSuggestionSearch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSuggestionSearch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onClick(View view) {
        super.onClick(view);
        int i = view.getId();
        if(i == com.claudiodegio.msv.R.id.bt_back) {
            this.closeSearch();
        } else if(i == com.claudiodegio.msv.R.id.ed_search_text) {
            super.onClick(view);
        } else if(i == com.claudiodegio.msv.R.id.v_overlay) {
            this.closeSearch();
        } else if(i == com.claudiodegio.msv.R.id.bt_clear) {
            this.closeSearch();
        }

    }
    public void setSuggestion(List<String> suggestions) {
        if(suggestions != null && !suggestions.isEmpty()) {
            CustomSearchAdapter adapter = new CustomSearchAdapter(this.getContext(), suggestions);
            this.setSuggestAdapter(adapter);
        }
    }

}
