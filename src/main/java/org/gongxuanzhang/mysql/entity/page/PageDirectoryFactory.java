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

import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.entity.BeanSupplier;

import java.nio.ByteBuffer;

/**
 * PageDirectory 工厂
 * {@link PageDirectory}
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public class PageDirectoryFactory implements ByteBeanSwapper<PageDirectory>, BeanSupplier<PageDirectory> {


    @Override
    public PageDirectory swap(byte[] bytes) {
        PageDirectory bean = new PageDirectory();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        short[] slots = new short[bytes.length / 2];
        for (int i = 0; i < (bytes.length / 2); i++) {
            slots[i] = buffer.getShort();
        }
        bean.slots = slots;
        return bean;
    }


    /**
     * 初始化页目录有两个槽
     * 一个是最小记录 一个是最大记录
     * 偏移量
     *
     * @return builder
     **/
    @Override
    public PageDirectory create() {
        PageDirectory pageDirectory = new PageDirectory();
        short[] slots = new short[2];
        slots[0] = (short) ConstantSize.INFIMUM.offset();
        slots[1] = (short) ConstantSize.SUPREMUM.offset();
        pageDirectory.setSlots(slots);
        return pageDirectory;
    }

}
