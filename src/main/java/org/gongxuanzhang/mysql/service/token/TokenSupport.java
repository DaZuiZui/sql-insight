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

package org.gongxuanzhang.mysql.service.token;

import org.gongxuanzhang.mysql.annotation.DependOnContext;
import org.gongxuanzhang.mysql.core.FromBox;
import org.gongxuanzhang.mysql.core.OrderBox;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.core.TableInfoBox;
import org.gongxuanzhang.mysql.core.WhereBox;
import org.gongxuanzhang.mysql.core.select.Condition;
import org.gongxuanzhang.mysql.core.select.Order;
import org.gongxuanzhang.mysql.core.select.OrderEnum;
import org.gongxuanzhang.mysql.core.select.SingleFrom;
import org.gongxuanzhang.mysql.core.select.Where;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.tool.Context;
import org.gongxuanzhang.mysql.tool.Pair;
import org.gongxuanzhang.mysql.tool.ThrowableRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.gongxuanzhang.mysql.tool.ExceptionThrower.throwSqlAnalysis;

/**
 * token 辅助类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class TokenSupport {

    private static final int FLAG_LENGTH = 'z' + 1;

    private static final byte[] FLAGS = new byte[FLAG_LENGTH];

    private static final byte DIGIT = 0x01;

    private static final byte ALPHABET = 0x02;

    private static final byte IDENTIFIER = 0x04;

    private static final Map<String, TokenKind> SWAP_TOKEN_KIND = new HashMap<>();


    static {
        for (int ch = '0'; ch <= '9'; ch++) {
            FLAGS[ch] |= DIGIT | IDENTIFIER;
        }
        for (int ch = 'A'; ch <= 'Z'; ch++) {
            FLAGS[ch] |= ALPHABET | IDENTIFIER;
        }
        for (int ch = 'a'; ch <= 'z'; ch++) {
            FLAGS[ch] |= ALPHABET | IDENTIFIER;
        }
        FLAGS['_'] |= IDENTIFIER;
        FLAGS['$'] |= IDENTIFIER;
        Arrays.stream(TokenKind.values()).filter(TokenKind::canSwap).forEach((kind) -> {
            SWAP_TOKEN_KIND.put(kind.toString(), kind);
        });
    }

    /**
     * 判断字符是否是数字
     *
     * @param c char
     * @return true是数字
     **/
    public static boolean isDigit(char c) {
        if (c >= FLAG_LENGTH) {
            return false;
        }
        return (FLAGS[c] & DIGIT) != 0;
    }


    /**
     * @param c char
     * @return true是字母
     **/
    public static boolean isAlphabet(char c) {
        if (c >= FLAG_LENGTH) {
            return false;
        }
        return (FLAGS[c] & ALPHABET) != 0;
    }


    /**
     * 是否是标识符
     *
     * @param c char
     * @return true是标识符
     **/
    public static boolean isIdentifier(char c) {
        if (c >= FLAG_LENGTH) {
            return false;
        }
        return (FLAGS[c] & IDENTIFIER) != 0;
    }


    /**
     * 尝试交换关键字
     *
     * @param sqlToken var sql token
     * @return 如果可以交换，返回交换之后的关键字 sql token 如果不能交换，返回原token
     **/
    public static SqlToken swapKeyword(SqlToken sqlToken) {
        if (sqlToken.getTokenKind() != TokenKind.VAR) {
            return sqlToken;
        }
        TokenKind tokenKind = SWAP_TOKEN_KIND.get(sqlToken.getValue().toUpperCase());
        if (tokenKind == null) {
            return sqlToken;
        }
        return new SqlToken(tokenKind, tokenKind.toString());
    }

    /**
     * 判断token类型是否是目标类型
     *
     * @param tokenKind token类型
     * @param target    目标类型
     * @return true是目标类型  false不是目标类型
     **/
    public static boolean isTokenKind(TokenKind tokenKind, TokenKind... target) {
        if (target == null || target.length == 0) {
            throw new NullPointerException("目标类型不能为空");
        }
        for (TokenKind kind : target) {
            if (kind == tokenKind) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解析from
     * todo 目前只支持单表解析
     *
     * @return 返回偏移量
     **/
    public static int fillFrom(FromBox fromBox, List<SqlToken> sqlTokenList) throws MySQLException {
        SingleFrom from = new SingleFrom();
        TokenSupport.mustTokenKind(sqlTokenList.get(0), TokenKind.FROM);
        fromBox.setFrom(from);
        int tableOffset = TokenSupport.fillTableInfo(from, sqlTokenList, 1);
        // offset from keyword
        return tableOffset + 1;
    }

    /**
     * 解析where
     *
     * @return 返回偏移量
     */
    public static int fillWhere(WhereBox whereBox, List<SqlToken> sqlTokenList) throws MySQLException {
        Where where = new Where();
        whereBox.setWhere(where);
        if (sqlTokenList.isEmpty() || TokenSupport.isNotTokenKind(sqlTokenList.get(0), TokenKind.WHERE)) {
            return 0;
        }
        int offset = 1;
        boolean and = true;
        List<SqlToken> subTokenList = new ArrayList<>();
        whereAnalysis:
        while (offset < sqlTokenList.size()) {
            SqlToken currentSqlToken = sqlTokenList.get(offset);
            switch (currentSqlToken.getTokenKind()) {
                case AND:
                    fillWhereCondition(subTokenList, where, and);
                    and = true;
                    break;
                case OR:
                    fillWhereCondition(subTokenList, where, and);
                    and = false;
                    break;
                case LITERACY:
                case VAR:
                case NUMBER:
                case EQUALS:
                case NE:
                case GT:
                case GTE:
                case LT:
                case LTE:
                    subTokenList.add(currentSqlToken);
                    break;
                default:
                    break whereAnalysis;

            }
            offset++;
        }
        fillWhereCondition(subTokenList, where, and);
        return offset;
    }

    /**
     * 解析并填充order
     *
     * @return 偏移量
     **/
    public static int fillOrderBy(OrderBox orderBox, List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        Order<?> order = orderBox.getOrder();
        if (sqlTokenList.isEmpty() || isNotTokenKind(sqlTokenList.get(0), TokenKind.ORDER)) {
            return 0;
        }
        mustTokenKind(sqlTokenList.get(1), TokenKind.BY);
        int offset = 2;
        while (offset < sqlTokenList.size()) {
            offset += doFillOrder(order, sqlTokenList, offset);
        }
        return offset;
    }

    private static int doFillOrder(Order<?> order, List<SqlToken> sqlTokenList, int offset) throws SqlAnalysisException {
        String col = getString(sqlTokenList.get(offset));
        int newOffset = 1;
        if (offset + newOffset == sqlTokenList.size()) {
            order.addOrder(col);
            return 1;
        }
        if (TokenSupport.isTokenKind(sqlTokenList.get(offset + newOffset), TokenKind.COMMA)) {
            order.addOrder(col);
            return 2;
        }
        if (TokenSupport.isTokenKind(sqlTokenList.get(offset + newOffset), TokenKind.DESC)) {
            order.addOrder(col, OrderEnum.desc);
        } else if (TokenSupport.isTokenKind(sqlTokenList.get(offset + newOffset), TokenKind.ASC)) {
            order.addOrder(col, OrderEnum.asc);
        } else {
            throw new SqlAnalysisException(sqlTokenList.get(offset + newOffset).getValue() + "错误");
        }
        newOffset++;
        if (offset + newOffset == sqlTokenList.size()) {
            return newOffset;
        }
        TokenSupport.mustTokenKind(sqlTokenList.get(offset + newOffset), TokenKind.COMMA);
        return newOffset + 1;
    }


    private static void fillWhereCondition(List<SqlToken> subTokenList, Where where, boolean and) throws MySQLException {
        where.addCondition(and ? Condition.and(subTokenList) : Condition.or(subTokenList));
        subTokenList.clear();
    }

    public static boolean isTokenKind(SqlToken sqlToken, TokenKind... target) {
        return isTokenKind(sqlToken.getTokenKind(), target);
    }

    public static boolean isNotTokenKind(TokenKind tokenKind, TokenKind... target) {
        return isTokenKind(tokenKind, target);
    }

    public static boolean isNotTokenKind(SqlToken sqlToken, TokenKind... target) {
        return !isTokenKind(sqlToken, target);
    }

    /**
     * 通过一个sql token var 拿到一个结果 要求token必须是var
     *
     * @return token的值
     * @throws SqlAnalysisException 如果不是 var token 抛出异常
     **/
    public static String getMustVar(SqlToken sqlToken) throws SqlAnalysisException {
        if (!isTokenKind(sqlToken, TokenKind.VAR)) {
            throw new SqlAnalysisException(sqlToken.getValue() + "解析错误");
        }
        return sqlToken.getValue();
    }

    /**
     * 通过一个sql token  拿到一个结果  要求token必须是LITERACY
     *
     * @param sqlToken sql token
     * @return token的值
     * @throws SqlAnalysisException 如果不是 LITERACY token 抛出异常
     **/
    public static String getMustLiteracy(SqlToken sqlToken) throws SqlAnalysisException {
        if (!isTokenKind(sqlToken, TokenKind.LITERACY)) {
            throw new SqlAnalysisException(sqlToken.getValue() + "解析错误");
        }
        return sqlToken.getValue();
    }

    /**
     * 通过一个sql token  拿到一个结果  要求token必须是LITERACY或者是var
     *
     * @param sqlToken sql token
     * @return token的值
     * @throws SqlAnalysisException 如果不是 LITERACY或者var token 抛出异常
     **/
    public static String getString(SqlToken sqlToken) throws SqlAnalysisException {
        if (!isTokenKind(sqlToken, TokenKind.LITERACY, TokenKind.VAR)) {
            throw new SqlAnalysisException(sqlToken.getValue() + "解析错误");
        }
        return sqlToken.getValue();
    }

    /**
     * 功能类似于{@link TokenSupport#getString(SqlToken)}
     * 只是当不是目标类型时返回null
     **/
    public static String tryGetString(SqlToken sqlToken) {
        if (!isTokenKind(sqlToken, TokenKind.LITERACY, TokenKind.VAR)) {
            return null;
        }
        return sqlToken.getValue();
    }

    public static void mustTokenKind(SqlToken sqlToken, TokenKind... tokenKind) throws SqlAnalysisException {
        if (!isTokenKind(sqlToken, tokenKind)) {
            throwSqlAnalysis(sqlToken.getValue());
        }
    }


    /**
     * 分析token  解析出 数据库和表名
     * 填充到表信息中
     *
     * @param tokenList token 流
     * @param offset    token流从哪开始解析
     * @return pair key 使用了多少流中token  value 表信息
     **/
    @DependOnContext
    public static Pair<Integer, TableInfo> analysisTableInfo(List<SqlToken> tokenList, int offset) throws MySQLException {
        TableInfo tableInfo = new TableInfo();
        String candidate = TokenSupport.getMustVar(tokenList.get(offset));
        DatabaseInfo database;
        String tableName;
        if (offset + 1 < tokenList.size() && TokenSupport.isTokenKind(tokenList.get(offset + 1), TokenKind.DOT)) {
            database = new DatabaseInfo(candidate);
            tableName = TokenSupport.getString(tokenList.get(offset + 2));
            offset = 3;
        } else {
            database = SessionManager.currentSession().getDatabase();
            tableName = candidate;
            offset = 1;
        }
        tableInfo.setDatabase(database);
        tableInfo.setTableName(tableName);
        TableInfo realInfo = Context.getTableManager().select(tableInfo);
        return Pair.of(offset, realInfo);
    }

    /**
     * @return 返回偏移量
     **/
    @DependOnContext
    public static int fillTableInfo(TableInfoBox box, List<SqlToken> tokenList, int offset) throws MySQLException {
        Pair<Integer, TableInfo> pair = analysisTableInfo(tokenList, offset);
        box.setTableInfo(pair.getValue());
        return pair.getKey();
    }


    public static int fillTableInfo(TableInfoBox box, List<SqlToken> tokenList) throws MySQLException {
        return fillTableInfo(box, tokenList, 0);
    }


    public static TokenChain token(SqlToken token) {
        return new TokenChain(token);

    }

    /**
     * 辅助支持的链式调用的内部类
     **/
    public static class TokenChain {
        final SqlToken token;
        Map<TokenKind, ThrowableRunnable> actions;
        ThrowableRunnable elseAction;

        TokenChain(SqlToken token) {
            this.token = token;
            this.actions = new HashMap<>();
        }

        public When when(TokenKind tokenKind) {
            return new When(this, tokenKind);
        }

        public TokenChain elseRun(ThrowableRunnable elseAction) {
            this.elseAction = elseAction;
            return this;
        }

        public void get() throws MySQLException {
            ThrowableRunnable runnable = actions.get(token.getTokenKind());
            if (runnable != null) {
                runnable.run();
                return;
            }
            if (elseAction != null) {
                elseAction.run();
            }
        }

    }

    public static class When {
        final TokenChain chain;
        final TokenKind targetKind;

        public When(TokenChain chain, TokenKind targetKind) {
            this.chain = chain;
            this.targetKind = targetKind;
        }


        public TokenChain then(ThrowableRunnable runnable) {
            this.chain.actions.put(this.targetKind, runnable);
            return chain;
        }
    }
}
