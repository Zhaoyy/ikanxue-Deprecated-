package com.mislead.ikanxue.app.db;

import android.content.Context;
import com.j256.ormlite.dao.Dao;
import com.mislead.ikanxue.app.model.ForumThreadTitleObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * FavorDao
 * Created by zhaoyy
 * on 15-10-17.
 */
public class FavorDao {

  private Context context;
  private Dao<ForumThreadTitleObject.ThreadListEntity, Integer> favorDao;
  private DBHelper dbHelper;

  public FavorDao(Context context) {
    this.context = context;
    dbHelper = DBHelper.getInstance(context);
    try {
      favorDao = dbHelper.getMyDao(ForumThreadTitleObject.ThreadListEntity.class);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void addFavor(ForumThreadTitleObject.ThreadListEntity entity) {
    try {
      favorDao.create(entity);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<ForumThreadTitleObject.ThreadListEntity> getFavors() {
    try {
      return favorDao.queryForAll();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new ArrayList<ForumThreadTitleObject.ThreadListEntity>();
  }
}
