package at.willhaben.blog.performance.testing.reactive.config;

import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.java.AbstractReactiveCassandraConfiguration;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;

@EnableReactiveCassandraRepositories
public class CassandraConfiguration extends AbstractReactiveCassandraConfiguration {

   @Override
    protected String getKeyspaceName() {
        return "someKeySpace";
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.NONE;
    }
}
