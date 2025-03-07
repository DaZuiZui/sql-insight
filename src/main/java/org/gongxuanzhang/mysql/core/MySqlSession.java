/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/sql-insight/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 会话信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class MySqlSession {

    private final String id;

    private final Map<String, String> attr = new HashMap<>();

    private DatabaseInfo database;

    private String sql;

    public MySqlSession(String id) {
        this.id = id;
    }

    public Map<String, String> getAllAttr() {
        return Collections.unmodifiableMap(attr);
    }

    public void set(String key, String value) {
        attr.put(key, value);
    }

    /**
     * 切换数据库
     *
     * @return 切换成功返回true 切换失败或者没有转换返回false
     **/
    public boolean useDatabase(DatabaseInfo database) {
        boolean result = this.database != database;
        this.database = database;
        return result;
    }

    public String get(String key) {
        return attr.get(key);
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public DatabaseInfo getDatabase() throws MySQLException {
        if (this.database == null) {
            throw new MySQLException("无法获取 database");
        }
        return this.database;
    }


    @Override
    public String toString() {
        return "MySqlSession{" +
                "id='" + id + '\'' +
                '}';
    }
}
