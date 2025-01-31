package com.homihq.db2rest.jdbc.service;

import com.homihq.db2rest.core.DbOperationService;
import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.service.CreateService;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.core.model.DbColumn;
import com.homihq.db2rest.core.model.DbTable;
import com.homihq.db2rest.jdbc.sql.CreateCreatorTemplate;
import com.homihq.db2rest.rest.create.dto.CreateContext;
import com.homihq.db2rest.rest.create.dto.CreateResponse;
import com.homihq.db2rest.jdbc.tsid.TSIDProcessor;
import com.homihq.db2rest.schema.SchemaCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.util.CollectionUtils.isEmpty;


@Slf4j
@RequiredArgsConstructor
public class JdbcCreateService implements CreateService {

    private final TSIDProcessor tsidProcessor;
    private final CreateCreatorTemplate createCreatorTemplate;
    private final SchemaCache schemaCache;
    private final DbOperationService dbOperationService;
    private final Dialect dialect;

    @Override
    @Transactional
    public CreateResponse save(String schemaName, String tableName, List<String> includedColumns,
                               Map<String, Object> data, boolean tsIdEnabled) {
        try {
            //1. get actual table
            DbTable dbTable = schemaCache.getTable(tableName);

            //2. determine the columns to be included in insert statement
            List<String> insertableColumns = isEmpty(includedColumns) ? new ArrayList<>(data.keySet().stream().toList()) :
                    new ArrayList<>(includedColumns);

            //3. check if tsId is enabled and add those values for PK.
            Map<String,Object> tsIdMap = null;
            if (tsIdEnabled) {
                List<DbColumn> pkColumns = dbTable.buildPkColumns();

                for(DbColumn pkColumn : pkColumns) {
                    log.info("Adding primary key columns - {}", pkColumn.name());
                    insertableColumns.add(pkColumn.name());
                }

                tsIdMap = tsidProcessor.processTsId(data, pkColumns);
            }

            this.dialect.processTypes(dbTable, insertableColumns, data);

            CreateContext context = new CreateContext(dbTable, insertableColumns);
            String sql = createCreatorTemplate.createQuery(context);


            log.info("SQL - {}", sql);
            log.info("Data - {}", data);


            CreateResponse createResponse = dbOperationService.create(data, sql, dbTable);

            if(tsIdEnabled && Objects.isNull(createResponse.keys())) {
                return new CreateResponse(createResponse.row(), tsIdMap);
            }

            return createResponse;
        } catch (DataAccessException e) {

            log.error("Error", e);
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }


    }

}
