package com.homihq.db2rest.jdbc.processor;


import com.homihq.db2rest.rest.read.dto.ReadContext;
import com.homihq.db2rest.core.model.DbTable;
import com.homihq.db2rest.schema.SchemaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

//@Component
@Slf4j
@RequiredArgsConstructor
@Order(1)
public class RootTableProcessor implements ReadProcessor {

    private final SchemaManager schemaManager;
    @Override
    public void process(ReadContext readContext) {
        log.info("Processing root table");
        DbTable table =
        schemaManager.getTable(readContext.getTableName());

        readContext.setRoot(table);
    }
}
