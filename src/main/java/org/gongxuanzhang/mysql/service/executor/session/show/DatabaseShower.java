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

package org.gongxuanzhang.mysql.service.executor.session.show;

import org.gongxuanzhang.mysql.core.manager.DatabaseManager;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.tool.Context;

import java.util.List;
import java.util.stream.Collectors;

/**
 * show databases
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DatabaseShower implements Shower {


    @Override
    public Result show() {
        DatabaseManager databaseManager = Context.getDatabaseManager();
        List<DatabaseInfo> all = databaseManager.getAll();
        List<String> databases = all.stream().map(DatabaseInfo::getDatabaseName).collect(Collectors.toList());
        return Result.singleRow("database", databases);
    }
}
