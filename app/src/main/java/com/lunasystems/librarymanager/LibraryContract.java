package com.lunasystems.librarymanager;

import android.provider.BaseColumns;

public final class LibraryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private LibraryContract() {}

    // Inner class that defines the table contents
    public static class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "library";
        public static final String TITLE = "title";
        public static final String AUTHOR = "author";
        public static final String OPENLIBRARYID = "openLibraryID";
    }
}
