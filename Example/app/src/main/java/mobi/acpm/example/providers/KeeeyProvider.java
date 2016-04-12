package mobi.acpm.example.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class KeeeyProvider extends ContentProvider {
    
    public KeeeyProvider() {
    }

    public static final String ID = "id";
    public static final String Name = "name";
    public static final String Password = "password";
    public static final String UserId = "userId";

    public static final String PROVIDER_NAME = "mobi.acpm.example.provider";

    private static HashMap<String, String> KEEEYS_PROJECTION_MAP;

    static final int KEEEYS = 1;
    static final int KEEEYS_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "keeey", KEEEYS);
        uriMatcher.addURI(PROVIDER_NAME, "keeey/#", KEEEYS_ID);
    }


    public static final Uri CONTENT_URI_KEY = Uri.parse("content://"+PROVIDER_NAME+"/keeey");

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "provider";
    static final String KEEEYS_TABLE_NAME = "keeey";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + KEEEYS_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " UserId TEXT NOT NULL, " +
                    " Name TEXT NOT NULL, " +
                    " password TEXT NOT NULL);";

    
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  KEEEYS_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case KEEEYS:
                count = db.delete(KEEEYS_TABLE_NAME, selection, selectionArgs);
                break;

            case KEEEYS_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( KEEEYS_TABLE_NAME, ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(	KEEEYS_TABLE_NAME, "", values);
        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI_KEY, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(KEEEYS_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case KEEEYS:
                qb.setProjectionMap(KEEEYS_PROJECTION_MAP);
                break;

            case KEEEYS_ID:
                qb.appendWhere( ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on student names
             */
            sortOrder = Name;
        }
        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);

        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case KEEEYS:
                count = db.update(KEEEYS_TABLE_NAME, values, selection, selectionArgs);
                break;

            case KEEEYS_ID:
                count = db.update(KEEEYS_TABLE_NAME, values, ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
