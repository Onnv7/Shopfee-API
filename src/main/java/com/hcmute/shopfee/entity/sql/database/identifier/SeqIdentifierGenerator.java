package com.hcmute.shopfee.entity.sql.database.identifier;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.EventType;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.java.spi.JavaTypeBasicAdaptor;
import org.hibernate.type.descriptor.jdbc.NumericJdbcType;
import org.hibernate.type.internal.NamedBasicTypeImpl;

import java.util.Properties;

public class SeqIdentifierGenerator implements IdentifierGenerator {
    public static final String VALUE_PREFIX_PARAMETER = "valuePrefix";
    public static final String VALUE_PREFIX_DEFAULT = "";
    private String valuePrefix;
    public static final String NUMBER_FORMAT_PARAMETER = "numberFormat";
    public static final String NUMBER_FORMAT_DEFAULT = "%d";
    private String numberFormat;
    public static final String ENTITY_NAME_PARAMETER = "entityName";
    public static final String ENTITY_NAME_DEFAULT = "";
    private String entityName;
    @Override
    public void configure(Type type, Properties parameters, ServiceRegistry serviceRegistry) {
        IdentifierGenerator.super.configure(new NamedBasicTypeImpl<>(new JavaTypeBasicAdaptor<>(Long.class),
                NumericJdbcType.INSTANCE, "long"), parameters, serviceRegistry);
        valuePrefix = ConfigurationHelper.getString(VALUE_PREFIX_PARAMETER, parameters, VALUE_PREFIX_DEFAULT);
        numberFormat = ConfigurationHelper.getString(NUMBER_FORMAT_PARAMETER, parameters, NUMBER_FORMAT_DEFAULT);
        entityName = ConfigurationHelper.getString(ENTITY_NAME_PARAMETER, parameters, ENTITY_NAME_DEFAULT);
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        String query = "SELECT e.id FROM " + entityName + " e ORDER BY e.id DESC";

        String lastId = (String) session.createQuery(query).setMaxResults(1).uniqueResult();

        if (lastId != null && lastId.startsWith(valuePrefix)) {
            int lastNumber = Integer.parseInt(lastId.substring(valuePrefix.length()));
            return valuePrefix + String.format(numberFormat, lastNumber + 1);
        } else {
            return valuePrefix + String.format(numberFormat, 1);
        }
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        return IdentifierGenerator.super.generate(session, owner, currentValue, eventType);
    }
}
