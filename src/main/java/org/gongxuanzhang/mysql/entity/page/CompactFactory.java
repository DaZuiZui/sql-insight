/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.entity.page;

import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.tool.BitUtils;

import java.nio.ByteBuffer;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class CompactFactory implements ByteBeanFactory<Compact> {

    @Override
    public Compact swap(Compact bean, byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte[] headBuffer = new byte[ConstantSize.RECORD_HEADER.getSize()];
        buffer.get(headBuffer);
        bean.recordHeader = new RecordHeader(headBuffer);
        byte varLength = buffer.get();
        VariablesFactory variablesFactory = new VariablesFactory();
        if (varLength == 0) {
            variablesFactory.swap(bean.variables, new byte[0]);
        } else {
            byte[] varBytes = new byte[varLength];
            buffer.get(varBytes);
            variablesFactory.swap(bean.variables, varBytes);
        }
        bean.nullValues = new CompactNullValue(buffer.getShort());
        byte[] candidateBuffer = new byte[6];
        buffer.get(candidateBuffer);
        bean.rowId = BitUtils.joinLong(candidateBuffer);
        buffer.get(candidateBuffer);
        bean.transactionId = BitUtils.joinLong(candidateBuffer);
        candidateBuffer = new byte[7];
        buffer.get(candidateBuffer);
        bean.rollPointer = BitUtils.joinLong(candidateBuffer);
        bean.body = new byte[buffer.remaining()];
        buffer.get(bean.body);
        return bean;
    }

    @Override
    public Compact create() {
        return null;
    }
}
