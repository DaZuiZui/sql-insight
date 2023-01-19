package org.gongxuanzhang.mysql.storage.fool;

import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.analysis.dml.InsertAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.dml.InsertExecutor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.SqlTokenizer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FoolStorageEngineTest {


    @Test
    public void insert(@Autowired FoolStorageEngine engine) throws MySQLException {
        String sql = "insert into aa.user(id,name) values(1,'s')";
        InsertAnalysis insertAnalysis = new InsertAnalysis();
        SqlTokenizer sqlTokenizer = new SqlTokenizer(sql);
        List<SqlToken> process = sqlTokenizer.process();
        InsertExecutor analysis = (InsertExecutor)insertAnalysis.analysis(process);
        analysis.doExecute();
    }

}