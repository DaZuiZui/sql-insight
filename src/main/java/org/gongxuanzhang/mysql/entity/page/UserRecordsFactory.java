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

import org.gongxuanzhang.mysql.entity.BeanSupplier;

/**
 * UserRecords 用户组 工厂
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class UserRecordsFactory implements ByteBeanSwapper<UserRecords>, BeanSupplier<UserRecords> {


    @Override
    public UserRecords swap(byte[] bytes) {
        return new UserRecords(bytes);
    }

    /**
     * 新建的用户组没有任何信息
     * 页的使用情况在pageHeader中 {@link PageHeader}
     **/
    @Override
    public UserRecords create() {
        return new UserRecords(new byte[0]);
    }

}
