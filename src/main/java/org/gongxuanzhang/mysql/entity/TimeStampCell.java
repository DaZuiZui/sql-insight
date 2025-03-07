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

package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.core.Planning;
import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 * time stamp 单元格
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
@Planning("还不支持时间戳呢")
public class TimeStampCell implements Cell<Long> {

    private final ColumnType type = ColumnType.TIMESTAMP;

    private final Long value;

    @Override
    public byte[] toBytes() {
        throw new UnsupportedOperationException("还不支持");
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public PrimaryKey toPrimaryKey() throws MySQLException {
        return null;
    }
}
