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

package org.gongxuanzhang.mysql.storage.innodb;

import org.gongxuanzhang.mysql.core.InnoDbPageSelector;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.Column;
import org.gongxuanzhang.mysql.entity.SelectRow;
import org.gongxuanzhang.mysql.entity.SingleSelectInfo;
import org.gongxuanzhang.mysql.entity.page.InnoDbPage;
import org.gongxuanzhang.mysql.entity.page.InnoDbPageFactory;
import org.gongxuanzhang.mysql.entity.page.InnodbPageOperator;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.SelectEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * innodb 的 查询引擎
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InnoDbSelect implements SelectEngine {


    @Override
    public Result select(SingleSelectInfo info) throws MySQLException {
        InnoDbPageSelector selector = InnoDbPageSelector.open(info);
        byte[] rootPageBuffer = selector.getRootPage();
        InnoDbPageFactory factory = InnoDbPageFactory.getInstance();
        InnoDbPage rootPage = factory.swap(rootPageBuffer);
        InnodbPageOperator rootInfo = new InnodbPageOperator(rootPage);
        return selectAll(rootInfo, info);
    }

    private Result selectAll(InnodbPageOperator pageInfoVisitor, SingleSelectInfo info) throws MySQLException {
        List<String> head = info.getFrom().getColumns().stream().map(Column::getName).collect(Collectors.toList());
        if (pageInfoVisitor.isDataPage()) {
            return Result.select(head,
                    pageInfoVisitor.showRows().stream().map(SelectRow::showMap).collect(Collectors.toList()));
        }
        List<SelectRow> data = new ArrayList<>();
        if (pageInfoVisitor.isIndexPage()) {
            while (pageInfoVisitor.nextPage() != null) {
                data.addAll(pageInfoVisitor.showRows());
            }
        }
        return Result.select(head, data.stream().map(SelectRow::showMap).collect(Collectors.toList()));
    }

    private Result singlePage(InnodbPageOperator pageInfoVisitor, SingleSelectInfo info) {
        return Result.select(info.getFrom().getColumns().stream().map(Column::getName).collect(Collectors.toList()),
                pageInfoVisitor.showRows().stream().map(SelectRow::showMap).collect(Collectors.toList()));

    }
}
