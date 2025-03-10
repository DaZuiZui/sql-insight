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

/**
 * 变长列表
 *
 * @author gongxuanzhang
 **/
public class Variables implements ByteSwappable {


    byte[] varBytes;

    public Variables(byte[] varBytes) {
        this.varBytes = varBytes;
    }

    /**
     * 变长列表
     **/
    public int length() {
        return varBytes.length;
    }

    @Override
    public byte[] toBytes() {
        return varBytes;
    }

    public byte get(int index) {
        return this.varBytes[index];
    }

    /**
     * 所有变长字段的总长度
     **/
    public int variableLength() {
        int sumLength = 0;
        for (byte varByte : this.varBytes) {
            sumLength += varByte;
        }
        return sumLength;
    }

}
