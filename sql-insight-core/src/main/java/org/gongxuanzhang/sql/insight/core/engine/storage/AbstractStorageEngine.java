/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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

package org.gongxuanzhang.sql.insight.core.engine.storage;

import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.result.ResultInterface;

/**
 * template for storage engine
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class AbstractStorageEngine implements StorageEngine {


    @Override
    public ResultInterface createTable(Table table) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultInterface dropTable(Table table) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultInterface truncateTable(Table table) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultInterface insert() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultInterface update() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultInterface delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultInterface query() {
        throw new UnsupportedOperationException();
    }
}
