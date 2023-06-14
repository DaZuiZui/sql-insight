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

package org.gongxuanzhang.mysql.entity.page;

import org.gongxuanzhang.mysql.core.ByteSwappable;
import org.gongxuanzhang.mysql.entity.ShowLength;

/**
 * 抽象的用户记录，具体由行格式去实现
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public interface UserRecord extends ShowLength, ByteSwappable {


    /**
     * 记录头信息
     *
     * @return 记录头
     **/
    RecordHeader getRecordHeader();


}
