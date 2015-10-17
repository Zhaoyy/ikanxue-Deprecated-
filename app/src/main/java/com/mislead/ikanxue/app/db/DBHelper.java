package com.mislead.ikanxue.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mislead.ikanxue.app.model.ForumThreadTitleObject;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * DBHelper
 * Created by zhaoyy
 * on 15-10-17.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

  private static final int version = 1;
  private static final String name = "kanxue.db";

  private Map<String, Dao> daos = new HashMap<String, Dao>();

  // single instance
  private static DBHelper instance;

  public static DBHelper getInstance(Context context) {
    if (instance == null) {
      synchronized (DBHelper.class) {
        if (instance == null) {
          context = context.getApplicationContext();
          instance = new DBHelper(context);
        }
      }
    }
    return instance;
  }

  protected DBHelper(Context context) {
    super(context, name, null, version);
  }

  @Override public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
    try {
      TableUtils.createTable(connectionSource, ForumThreadTitleObject.ThreadListEntity.class);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion,
      int newVersion) {

    try {
      TableUtils.dropTable(connectionSource, ForumThreadTitleObject.ThreadListEntity.class, true);
      onCreate(database, connectionSource);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public synchronized Dao getMyDao(Class clazz) throws SQLException {
    String className = clazz.getSimpleName();

    Dao dao = null;

    if (daos.containsKey(className)) {
      dao = daos.get(className);
    } else {
      dao = super.getDao(clazz);
      daos.put(className, dao);
    }
    return dao;
  }

  @Override public void close() {
    super.close();

    for (String key : daos.keySet()) {
      Dao dao = daos.get(key);
      dao = null;
    }
  }
}
