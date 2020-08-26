package np.com.manishtuladhar.muteme.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class PlaceContract {

    public static final String AUTHORITY = "np.com.manishtuladhar.muteme";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" +AUTHORITY);
    public static final String PATH_PLACES = "places";

    public static final class PlaceEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACES).build();

        public static final String TABLE_NAME = "places";
        public static final String COLUMN_PLACE_ID = "placeID";


    }

}
