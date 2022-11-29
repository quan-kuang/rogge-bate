package com.loyer.common.datasource.config;

import com.loyer.common.core.utils.common.StringUtil;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * jpa映射db命令规则
 *
 * @author kuangq
 * @date 2022-07-29 16:30
 */
public class CustomPhysicalNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return formatIdentifier(identifier);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return formatIdentifier(identifier);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return formatIdentifier(identifier);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return formatIdentifier(identifier);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return formatIdentifier(identifier);
    }

    private Identifier formatIdentifier(Identifier identifier) {
        if (identifier == null) {
            return null;
        }
        return new Identifier(StringUtil.formatUnderline(identifier.getText()), identifier.isQuoted());
    }
}