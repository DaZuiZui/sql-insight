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

package org.gongxuanzhang.sql.insight.core.engine.execute;

import org.gongxuanzhang.sql.insight.core.analysis.druid.DruidAnalyzer;
import org.gongxuanzhang.sql.insight.core.engine.SqlPipeline;
import org.gongxuanzhang.sql.insight.core.engine.StorageEngineManager;
import org.gongxuanzhang.sql.insight.core.engine.storage.SimpleStorageEngineManager;
import org.gongxuanzhang.sql.insight.core.optimizer.OptimizerImpl;

/**
 * Responsible for creating everything about sql
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class InsightFactory {

    private InsightFactory() {

    }

    private static volatile StorageEngineManager storageEngineManager;

    public static SqlPipeline createSqlPipeline() {
        SqlPipeline sqlPipeline = new SqlPipeline();
        OptimizerImpl optimizer = new OptimizerImpl();
        optimizer.setAnalyzer(new DruidAnalyzer());
        sqlPipeline.setOptimizer(optimizer);
        sqlPipeline.setExecuteEngine(createSingleEngine());
        return sqlPipeline;
    }

    public static SqlInsightExecuteEngine createSingleEngine() {
        SqlInsightExecuteEngine engine = new SqlInsightExecuteEngine();
        engine.setStorageEngineManager(shareStorageEngineManager());
        return engine;
    }


    private static StorageEngineManager shareStorageEngineManager() {
        if (storageEngineManager == null) {
            synchronized (InsightFactory.class) {
                if (storageEngineManager == null) {
                    storageEngineManager = new SimpleStorageEngineManager();
                }
            }
        }
        return storageEngineManager;
    }
}
