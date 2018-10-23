/*
 * Copyright (C) 2018 justlive1
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package vip.justlive.oxygen.jdbc.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import vip.justlive.oxygen.jdbc.JdbcException;

/**
 * int 处理
 * <br>
 * 适用于sum count 返回单列整数值的sql
 *
 * @author wubo
 */
public class IntResultHandler implements ResultSetHandler<Integer> {

  public static final IntResultHandler INSTANCE = new IntResultHandler();

  @Override
  public Integer handle(ResultSet rs) {
    try {
      if (rs.next()) {
        Object value = rs.getObject(1);
        if (value != null) {
          return rs.getInt(1);
        }
      }
      return 0;
    } catch (SQLException e) {
      throw JdbcException.wrap(e);
    }
  }
}
