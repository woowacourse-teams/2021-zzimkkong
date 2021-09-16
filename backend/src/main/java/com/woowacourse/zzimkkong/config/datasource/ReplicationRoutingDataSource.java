package com.woowacourse.zzimkkong.config.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;
import java.util.stream.Collectors;

import static com.woowacourse.zzimkkong.config.datasource.CustomDataSourceConfig.MASTER;
import static com.woowacourse.zzimkkong.config.datasource.CustomDataSourceConfig.SLAVE;

public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {
    private CircularList<String> dataSourceNameList;

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);

        dataSourceNameList = new CircularList<>(
                targetDataSources.keySet()
                        .stream()
                        .map(Object::toString)
                        .filter(string -> string.contains(SLAVE))
                        .collect(Collectors.toList())
        );
    }

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        if (isReadOnly) {
            logger.info("Connection Slave");
            return dataSourceNameList.getOne();
        } else {
            logger.info("Connection Master");
            return MASTER;
        }
    }
}
