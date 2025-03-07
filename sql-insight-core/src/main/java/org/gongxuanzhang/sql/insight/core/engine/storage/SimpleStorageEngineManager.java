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

package org.gongxuanzhang.sql.insight.core.engine.storage;

import org.gongxuanzhang.sql.insight.core.engine.StorageEngineManager;
import org.gongxuanzhang.sql.insight.core.exception.DuplicationEngineNameException;
import org.gongxuanzhang.sql.insight.core.exception.EngineNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class SimpleStorageEngineManager implements StorageEngineManager {

    private final Map<String, StorageEngine> storageEngineMap = new ConcurrentHashMap<>();

    @Override
    public List<StorageEngine> allEngine() {
        return new ArrayList<>(storageEngineMap.values());
    }

    @Override
    public void registerEngine(StorageEngine engine) {
        if (storageEngineMap.putIfAbsent(engine.getName(), engine) != null) {
            throw new DuplicationEngineNameException("engine " + engine.getName() + "already register ");
        }
    }

    @Override
    public StorageEngine selectEngine(String engineName) {
        StorageEngine engine = storageEngineMap.get(engineName);
        if (engine == null) {
            throw new EngineNotFoundException(engineName);
        }
        return engine;
    }
}
