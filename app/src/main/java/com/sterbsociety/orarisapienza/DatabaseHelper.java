package com.sterbsociety.orarisapienza;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.sterbsociety.orarisapienza.models.Lesson;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


/**
 * This is Singleton class used to access from anywhere from this project.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;
    private static DataSnapshot mCurrentDataSnapshot;

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "courses.db";
    public static final String TABLE_NAME = "courses_detail";

    // Table columns
    private static final String KEY_ID = "id";
    private static final String KEY_COURSE_ID = "course_id";
    private static final String KEY_COURSE_NAME = "course_name";
    private static final String KEY_SUBJECT_NAME = "subject_name";
    private static final String KEY_START_LESSON = "start_lesson";
    private static final String KEY_END_LESSON = "end_lesson";
    private static final String KEY_DAY = "day";
    private static final String KEY_CLASSROOM = "classroom";
    private static final String KEY_PROFESSOR = "professor";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private static final String SQL_CREATE_COURSES_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_COURSE_ID + " INTEGER NOT NULL,"
            + KEY_COURSE_NAME + " TEXT,"
            + KEY_SUBJECT_NAME + " TEXT NOT NULL,"
            + KEY_START_LESSON + " TEXT NOT NULL,"
            + KEY_END_LESSON + " TEXT NOT NULL,"
            + KEY_DAY + " TEXT NOT NULL,"
            + KEY_CLASSROOM + " TEXT NOT NULL,"
            + KEY_PROFESSOR + " TEXT" + ")";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_COURSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseHelper(context);
        return instance;
    }

    public static boolean createDatabase(Activity activity, DataSnapshot dataSnapshot) {
        return getInstance(activity).createDB(activity, dataSnapshot);
    }

    /**
     * This method is responsible for the correct creation of the embedded db in the device.
     * We are inserting more than one record at a time by bathing inserts,
     * more info at:
     * https://medium.com/@JasonWyatt/squeezing-performance-from-sqlite-insertions-971aff98eef2
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean createDB(Activity activity, DataSnapshot currentDataSnapshot) {

        // Just updates its currentDataSnapshot whenever gets called.
        mCurrentDataSnapshot = currentDataSnapshot;

        // This will create the DB File
        File databaseFile = activity.getDatabasePath(DATABASE_NAME);
        databaseFile.mkdirs();
        databaseFile.delete();

        SQLiteDatabase encryptedDb = this.getWritableDatabase();
        Gson gson = new Gson();

        // This will hold all the rows of the table
        StringBuilder valuesBuilder = new StringBuilder();

        encryptedDb.beginTransaction();
        try {

            for (DataSnapshot childSnapshot : mCurrentDataSnapshot.getChildren()) {

                String jsonString = gson.toJson(childSnapshot.getValue());
                JSONObject jsonObject;
                jsonObject = new JSONObject(jsonString);
                String currentRow = "(" + jsonObject.get(KEY_COURSE_ID) + ",\"" + jsonObject.get(KEY_COURSE_NAME) + "\"," + "\""
                        + jsonObject.get(KEY_SUBJECT_NAME) + "\"," + "\"" + jsonObject.get(KEY_START_LESSON) + "\"," + "\""
                        + jsonObject.get(KEY_END_LESSON) + "\"," + "\"" + jsonObject.get(KEY_DAY) + "\"," + "\""
                        + jsonObject.get(KEY_CLASSROOM) + "\"," + "\"" + jsonObject.get(KEY_PROFESSOR) + "\"),";
                valuesBuilder.append(currentRow);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }

        String sqlQuery = "INSERT INTO " + TABLE_NAME + " ("
                + KEY_COURSE_ID + "," + KEY_COURSE_NAME + ","
                + KEY_SUBJECT_NAME + "," + KEY_START_LESSON + ","
                + KEY_END_LESSON + "," + KEY_DAY + ","
                + KEY_CLASSROOM + "," + KEY_PROFESSOR + ") VALUES "
                + valuesBuilder.toString().substring(0, valuesBuilder.length() - 1) + ";";

        encryptedDb.execSQL(sqlQuery);
        encryptedDb.setTransactionSuccessful();
        encryptedDb.endTransaction();
        encryptedDb.close();

        return true;
    }

    public ArrayList<Lesson> getAllCourses(Activity activity) {

        ArrayList<Lesson> lessonList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase mSQLiteDB;

        try {
            mSQLiteDB = this.getWritableDatabase();
        } catch (SQLiteException ex) {
            if (mCurrentDataSnapshot != null) {
                // If the password is wrong, then a new attempt is done.
                createDB(activity, mCurrentDataSnapshot);
                mSQLiteDB = this.getWritableDatabase();
            } else
                return null;
        }

        assert mSQLiteDB != null;
        Cursor mCursor = mSQLiteDB.rawQuery(selectQuery, null);

        if (mCursor.moveToFirst()) {
            final int classRoomIndex = mCursor.getColumnIndex(KEY_CLASSROOM);
            final int courseIdIndex = mCursor.getColumnIndex(KEY_COURSE_ID);
            final int courseNameIndex = mCursor.getColumnIndex(KEY_COURSE_NAME);
            final int dayIndex = mCursor.getColumnIndex(KEY_DAY);
            final int endLessonIndex = mCursor.getColumnIndex(KEY_END_LESSON);
            final int professorIndex = mCursor.getColumnIndex(KEY_PROFESSOR);
            final int startLessonIndex = mCursor.getColumnIndex(KEY_START_LESSON);
            final int subjectNameIndex = mCursor.getColumnIndex(KEY_SUBJECT_NAME);
            // If the Cursor is no empty
            do {
                Lesson lesson = new Lesson();
                lesson.setClassRoom(mCursor.getString(classRoomIndex));
                lesson.setCourseId(Integer.parseInt(mCursor.getString(courseIdIndex)));
                lesson.setCourseName(mCursor.getString(courseNameIndex));
                lesson.setDay(mCursor.getString(dayIndex));
                lesson.setEndLesson(mCursor.getString(endLessonIndex));
                lesson.setProfessor(mCursor.getString(professorIndex));
                lesson.setStartLesson(mCursor.getString(startLessonIndex));
                lesson.setSubjectName(mCursor.getString(subjectNameIndex));
                lessonList.add(lesson);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        mSQLiteDB.close();
        return lessonList;
    }

    public boolean offlineDBAvailable(Activity activity) {
        AppUtils.setLocale(activity);
        boolean outCome = activity.getDatabasePath(DATABASE_NAME).exists();
        if (outCome)
            return true;
        if (mCurrentDataSnapshot != null) {
            DatabaseHelper.getInstance(activity).createDB(activity, mCurrentDataSnapshot);
            return true;
        }

        StyleableToast.makeText(activity, activity.getString(R.string.data_not_sync_wait_please), Toast.LENGTH_LONG, R.style.errorToast).show();
        return false;
    }
}