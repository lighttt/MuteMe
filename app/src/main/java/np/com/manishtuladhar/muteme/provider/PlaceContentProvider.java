package np.com.manishtuladhar.muteme.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PlaceContentProvider extends ContentProvider {

    private static final String TAG = "PlaceContentProvider";

    private static final int PLACES = 100;
    private static final int PLACES_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher()
    {
        //initialize
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //add uri matches
        uriMatcher.addURI(PlaceContract.AUTHORITY, PlaceContract.PATH_PLACES,PLACES);
        uriMatcher.addURI(PlaceContract.AUTHORITY, PlaceContract.PATH_PLACES+ "/#", PLACES_ID);
        return uriMatcher;
    }

    //db helper
    private PlaceDBHelper placeDBHelper;

    @Override
    public boolean onCreate() {
        placeDBHelper = new PlaceDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String order) {
        final SQLiteDatabase db = placeDBHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match)
        {
            case PLACES:
                retCursor = db.query(PlaceContract.PlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        order);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + match);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = placeDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri retUri;
        switch (match)
        {
            case PLACES:
                long id = db.insert(PlaceContract.PlaceEntry.TABLE_NAME,null,contentValues);
                if(id>0)
                {
                    retUri = ContentUris.withAppendedId(PlaceContract.PlaceEntry.CONTENT_URI,id);
                }
                else{
                    throw new SQLException("Unexpected value: " + id);
                }
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + match);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = placeDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int placeDeleted;
        switch (match)
        {
            case PLACES_ID:
                String id = uri.getPathSegments().get(1);
                placeDeleted = db.delete(PlaceContract.PlaceEntry.TABLE_NAME,"_id=?",
                        new String[]{id});
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + match);
        }
        if(placeDeleted!=0)
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return placeDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = placeDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int placeUpdated;
        switch (match)
        {
            case PLACES_ID:
                String id = uri.getPathSegments().get(1);
                placeUpdated = db.update(PlaceContract.PlaceEntry.TABLE_NAME,contentValues,
                        "_id=?",
                        new String[]{id});
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + match);
        }
        if(placeUpdated!=0)
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return placeUpdated;
    }
}
