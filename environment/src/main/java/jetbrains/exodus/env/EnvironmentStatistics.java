/**
 * Copyright 2010 - 2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.exodus.env;

import jetbrains.exodus.log.ReadBytesListener;
import jetbrains.exodus.management.Statistics;
import jetbrains.exodus.management.StatisticsItem;
import org.jetbrains.annotations.NotNull;

public class EnvironmentStatistics extends Statistics {

    public static final String BYTES_WRITTEN = "Bytes written";
    public static final String BYTES_READ = "Bytes read";
    public static final String BYTES_MOVED_BY_GC = "Bytes moved by GC";
    public static final String TRANSACTIONS = "Transactions";
    public static final String READONLY_TRANSACTIONS = "Read-only transactions";
    public static final String ACTIVE_TRANSACTIONS = "Active transactions";
    public static final String FLUSHED_TRANSACTIONS = "Flushed transactions";

    @NotNull
    private final EnvironmentImpl env;

    EnvironmentStatistics(@NotNull final EnvironmentImpl env) {
        this.env = env;
        getStatisticsItem(BYTES_WRITTEN).setTotal(env.getLog().getHighAddress());
        getStatisticsItem(BYTES_READ);
        getStatisticsItem(BYTES_MOVED_BY_GC);
        getStatisticsItem(TRANSACTIONS);
        getStatisticsItem(READONLY_TRANSACTIONS);
        getStatisticsItem(ACTIVE_TRANSACTIONS);
        getStatisticsItem(FLUSHED_TRANSACTIONS);
        env.getLog().addReadBytesListener(new ReadBytesListener() {
            @Override
            public void bytesRead(final byte[] bytes, final int count) {
                getStatisticsItem(BYTES_READ).addTotal(count);
            }
        });
    }

    @NotNull
    @Override
    protected StatisticsItem createNewItem(@NotNull final String statisticsName) {
        if (ACTIVE_TRANSACTIONS.equals(statisticsName)) {
            return new StatisticsItem(statisticsName) {
                @Override
                protected Long getAutoUpdatedTotal() {
                    return (long) env.activeTransactions();
                }
            };
        }
        return super.createNewItem(statisticsName);
    }
}