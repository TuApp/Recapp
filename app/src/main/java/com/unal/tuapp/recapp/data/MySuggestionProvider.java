package com.unal.tuapp.recapp.data;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by andresgutierrez on 9/6/15.
 */
public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY= "com.unal.tuapp.recapp.data.MySuggestionProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY,MODE);
    }
}
