/*
 * Copyright 1998-2009 Linux.org.ru
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.org.linux.site;

import java.io.Serializable;
import java.sql.*;

public class Section implements Serializable {
  private final String name;
  private final boolean browsable;
  private final boolean imagepost;
  private final boolean moderate;
  private final int id;
  private final boolean votepoll;
  public static final int SCROLL_NOSCROLL = 0;
  public static final int SCROLL_SECTION = 1;
  public static final int SCROLL_GROUP = 2;

  public Section(Connection db, int id) throws SQLException, BadSectionException {
    this.id = id;

    Statement st = db.createStatement();
    ResultSet rs = st.executeQuery(
        "SELECT name, browsable, imagepost, vote, moderate " +
            "FROM sections " +
            "WHERE id="+id
    );

    if (!rs.next()) {
      throw new BadSectionException(id);
    }

    name = rs.getString("name");
    browsable = rs.getBoolean("browsable");
    imagepost = rs.getBoolean("imagepost");
    votepoll = rs.getBoolean("vote");
    moderate = rs.getBoolean("moderate");
  }

  public String getName() {
    return name;
  }

  public boolean isBrowsable() {
    return browsable;
  }

  public boolean isImagepost() {
    return imagepost;
  }

  public boolean isVotePoll() {
    return votepoll;
  }

  public static int getScrollMode(int sectionid) {
    switch (sectionid) {
      case 1: /* news*/
      case 3: /* screenshots */
      case 5: /* poll */
        return SCROLL_SECTION;
      case 2: /* forum */
        return SCROLL_GROUP;
      default:
        return SCROLL_NOSCROLL;
    }
  }

  public int getId() {
    return id;
  }

  public boolean isPremoderated() {
    return moderate;
  }

  public String getAddText() {
    if (id==4) {
      return "Добавить ссылку";
    } else {
      return "Добавить сообщение";
    }
  }

  public boolean isForum() {
    return id==2;
  }

  public String getTitle() {
    return name;
  }

  public Timestamp getLastCommitdate(Connection db) throws SQLException {
    Statement st = null;
    ResultSet rs = null;

    try {
      st = db.createStatement();

      rs = st.executeQuery("select max(commitdate) from topics,groups where section=" + id + " and topics.groupid=groups.id");

      if (!rs.next()) {
        return null;
      } else {
        return rs.getTimestamp("max");
      }
    } finally {
      if (rs!=null) {
        rs.close();
      }

      if (st!=null) {
        st.close();
      }
    }
  }
}
