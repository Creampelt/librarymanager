package com.lunasystems.librarymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;


public class DBBookStore implements BookStore {
    static final String TAG = "DBBookStore";
    Context context;
    BookDbHelper db;
    DBBookStore(Context c) {
        context = c;
        db = new BookDbHelper(c);
        Log.d(TAG, "Created DBBookStore");
    }

    void putBook(Book b) {
        Log.d(TAG, "Creating DBBookWriter");
        new DBBookWriter(db).execute(b);
    }

    Book getBook(String title) {

    }

    public class BookDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Library.db";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + LibraryContract.BookEntry.TABLE_NAME + " (" +
                        LibraryContract.BookEntry._ID + " INTEGER PRIMARY KEY," +
                        LibraryContract.BookEntry.TITLE + " TEXT," +
                        LibraryContract.BookEntry.AUTHOR + " TEXT)";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + LibraryContract.BookEntry.TABLE_NAME;

        public BookDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public class DBBookWriter extends AsyncTask<Book, Integer, Integer>
        public static final int E_BAD_PARAMS = -1;

        BookDbHelper mDbHelper = null;

        DBBookWriter(BookDbHelper h) {
            mDbHelper = h;
        }

        protected Integer doInBackground(Book... books) {
            if (books.length != 1) {
                //Handle this error condition...
                Log.e(TAG, "doInBackground: Wrong number of books (expected 1), #ofBooks = "
                        + books.length);
                return new Integer(E_BAD_PARAMS);
            }

            Book myBook = books[0];
            Log.d(TAG, "doInBackground: myBook = " + myBook);

            // Gets the data repository in write mode
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(LibraryContract.BookEntry.TITLE, myBook.getTitle());
            values.put(LibraryContract.BookEntry.AUTHOR, myBook.getAuthor());

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(LibraryContract.BookEntry.TABLE_NAME, null, values);
            Log.d(TAG, "doInBackground: Inserted book: " + values);
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Integer result) {
            //showDialog("Downloaded " + result + " bytes");
        }

    }

}