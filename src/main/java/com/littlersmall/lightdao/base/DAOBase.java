package com.littlersmall.lightdao.base;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import com.google.common.base.CaseFormat;
import com.littlersmall.lightdao.annotation.PrimaryKey;
import com.littlersmall.lightdao.utils.ReflectUtils;

/**
 * Created by littlersmall on 2017/4/20.
 */
public interface DAOBase<T> {
    Map<Class, RowMapper> ROW_MAPPER_MAP = new ConcurrentHashMap<>();
    Map<Class, String> TABLE_NAME_MAP = new ConcurrentHashMap<>();
    Map<Class, String> PRIMARY_KEY_NAME_MAP = new ConcurrentHashMap<>();

    Class<T> getClazz();

    @SuppressWarnings("unchecked")
    default RowMapper<T> getRowMapper() {
        return ROW_MAPPER_MAP.computeIfAbsent(getClazz(), BeanPropertyRowMapper::new);
    }

    default String getTableName() {
        return TABLE_NAME_MAP.computeIfAbsent(getClazz(), (clazz) -> {
            String tableName = clazz.getSimpleName();
            tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, tableName);

            return tableName;
        });
    }

    default String getPrimaryKeyName() {
        return PRIMARY_KEY_NAME_MAP.computeIfAbsent(getClazz(), (clazz) ->
                Arrays.stream(clazz.getDeclaredFields())
                .filter(field ->
                        field.getDeclaredAnnotation(PrimaryKey.class) != null)
                .findAny()
                .get()
                .getName());
    }

    default long getPrimaryKey(Object model) {
        return (long) ReflectUtils.getField(model, getPrimaryKeyName());
    }
}
