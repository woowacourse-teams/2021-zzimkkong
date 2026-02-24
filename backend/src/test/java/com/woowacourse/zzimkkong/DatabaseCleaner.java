package com.woowacourse.zzimkkong;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
@Profile("test")
public class DatabaseCleaner implements InitializingBean {
    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .map(entry -> {
                    String tableName;
                    Class<?> javaType = entry.getJavaType();
                    Table tableAnnotation = javaType.getAnnotation(Table.class);

                    if (tableAnnotation != null && !tableAnnotation.name().isEmpty()) {
                        tableName = tableAnnotation.name();
                    } else {
                        // Convert camelCase to snake_case
                        String className = entry.getName();
                        tableName = className.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
                    }
                    return tableName;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN id RESTART WITH 1").executeUpdate();
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
