package com.homihq.db2rest.d1.config;


import com.homihq.db2rest.core.config.Db2RestConfigProperties;
import com.homihq.db2rest.core.DbOperationService;
import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.service.*;
import com.homihq.db2rest.d1.D1Dialect;
import com.homihq.db2rest.d1.service.D1FunctionService;
import com.homihq.db2rest.d1.service.D1ProcedureService;
import com.homihq.db2rest.jdbc.processor.ReadProcessor;
import com.homihq.db2rest.jdbc.service.*;
import com.homihq.db2rest.jdbc.sql.CreateCreatorTemplate;
import com.homihq.db2rest.jdbc.sql.DeleteCreatorTemplate;
import com.homihq.db2rest.jdbc.sql.UpdateCreatorTemplate;
import com.homihq.db2rest.jdbc.tsid.TSIDProcessor;
import com.homihq.db2rest.rest.read.sql.QueryCreatorTemplate;
import com.homihq.db2rest.schema.SchemaCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



import java.util.List;


@Configuration
@ConditionalOnBean(D1Dialect.class)
public class D1ServiceConfiguration {
    //CREATE SERVICE

    @Bean
    public BulkCreateService bulkCreateService(TSIDProcessor tsidProcessor,
                                               CreateCreatorTemplate createCreatorTemplate,
                                               SchemaCache schemaCache,
                                               DbOperationService dbOperationService, Dialect dialect) {
        return new JdbcBulkCreateService(tsidProcessor, createCreatorTemplate, schemaCache, dbOperationService, dialect);
    }

    @Bean
    public CreateService createService(TSIDProcessor tsidProcessor,
                                       CreateCreatorTemplate createCreatorTemplate,
                                       SchemaCache schemaCache,
                                       DbOperationService dbOperationService, Dialect dialect) {
        return new JdbcCreateService(tsidProcessor, createCreatorTemplate, schemaCache, dbOperationService, dialect);
    }

    //QUERY SERVICE
    @Bean
    public CountQueryService countQueryService(
                                               QueryCreatorTemplate queryCreatorTemplate,
                                               List<ReadProcessor> processorList,
                                               DbOperationService dbOperationService) {
        return new JdbcCountQueryService(dbOperationService, processorList, queryCreatorTemplate);
    }

    @Bean
    public ExistsQueryService existsQueryService(
            QueryCreatorTemplate queryCreatorTemplate,
            List<ReadProcessor> processorList,
            DbOperationService dbOperationService) {
        return new JdbcExistsQueryService(dbOperationService, processorList, queryCreatorTemplate);
    }

    @Bean
    public FindOneService findOneService(
            QueryCreatorTemplate queryCreatorTemplate,
            List<ReadProcessor> processorList,
            DbOperationService dbOperationService) {
        return new JdbcFindOneService(queryCreatorTemplate, processorList, dbOperationService);
    }

    @Bean
    public CustomQueryService customQueryService(DbOperationService dbOperationService) {
        return new JdbcCustomQueryService(dbOperationService);
    }

    @Bean
    public ReadService readService(
            QueryCreatorTemplate queryCreatorTemplate,
            List<ReadProcessor> processorList,
            DbOperationService dbOperationService) {
        return new JdbcReadService(dbOperationService, processorList, queryCreatorTemplate);
    }

    //UPDATE SERVICE
    @Bean
    public UpdateService updateService(
            Db2RestConfigProperties db2RestConfigProperties,
            SchemaCache schemaCache,
            UpdateCreatorTemplate updateCreatorTemplate,
            DbOperationService dbOperationService, Dialect dialect) {
        return new JdbcUpdateService(db2RestConfigProperties, schemaCache, updateCreatorTemplate, dbOperationService, dialect);
    }


    //DELETE SERVICE
    @Bean
    public DeleteService deleteService(
            Db2RestConfigProperties db2RestConfigProperties,
    SchemaCache schemaCache,
    DeleteCreatorTemplate deleteCreatorTemplate,
    DbOperationService dbOperationService, Dialect dialect) {
        return new JdbcDeleteService(db2RestConfigProperties, schemaCache, deleteCreatorTemplate, dbOperationService, dialect);
    }

    //RPC
    @Bean
    public D1FunctionService d1FunctionService() {
        return new D1FunctionService();
    }

    @Bean
    public D1ProcedureService d1ProcedureService() {
        return new D1ProcedureService();
    }

}
