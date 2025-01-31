package com.homihq.db2rest.rest;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;


import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestWithResources
class PgReadControllerTest extends PostgreSQLBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/SINGLE_RESULT_ACTOR_QUERY.json")
    Map<String,Object> SINGLE_RESULT_ACTOR_QUERY;

    @GivenJsonResource("/testdata/BULK_RESULT_ACTOR_QUERY.json")
    Map<String,Object> BULK_RESULT_ACTOR_QUERY;

    @GivenJsonResource("/testdata/EMPTY_ACTOR_QUERY.json")
    Map<String,Object> EMPTY_ACTOR_QUERY;

    @Test
    @DisplayName("Test find all films - all columns.")
    void findAllFilms() throws Exception {

        mockMvc.perform(get("/film")
                        .accept(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(4),hasSize(8))))
                .andExpect(jsonPath("$[0].*", hasSize(14)))
                .andDo(document("pg-get-all-films-all-columns"));
    }


    @Test
    @DisplayName("Test find all films - 3 columns")
    void findAllFilmsWithThreeCols() throws Exception {
        mockMvc.perform(get("/film")
                        .contentType(APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .param("fields", "title,description,release_year")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(4),hasSize(8))))
                .andExpect(jsonPath("$[0].*", hasSize(3)))
                .andDo(document("pg-find-all-films-3-columns"));
    }

    @Test
    @DisplayName("Test find all films - with column alias")
    void findAllFilmsWithColumnAlias() throws Exception {
        mockMvc.perform(get("/film")
                        .contentType(APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .param("fields", "title,description,release_year:releaseYear")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(4),hasSize(8))))
                .andExpect(jsonPath("$[0].title", notNullValue()))
                .andExpect(jsonPath("$[0].description", notNullValue()))
                .andExpect(jsonPath("$[0].releaseYear", notNullValue()))
                .andDo(document("pg-find-all-films-with-column-alias"));
    }

    @Test
    @DisplayName("Test Custom Query with Single Result")
    void findCustomQuerySingleResult() throws Exception {

        mockMvc.perform(post("/query")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(SINGLE_RESULT_ACTOR_QUERY)))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name", equalTo("PENELOPE")))
                .andDo(document("pg-custom-query-single-result"));
    }

    @Test
    @DisplayName("Test Custom Query returns list of results")
    void findCustomQueryMultipleResult() throws Exception {

        mockMvc.perform(post("/query")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BULK_RESULT_ACTOR_QUERY)))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].first_name", equalTo("PENELOPE")))
                .andExpect(jsonPath("$[1].last_name", equalTo("WAHLBERG")))
                .andDo(document("pg-custom-query-list-result"));
    }

    @Test
    @DisplayName("Test Custom Query returns 400 - Bad Request")
    void findCustomQueryWithError400() throws Exception {

        mockMvc.perform(post("/query")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(EMPTY_ACTOR_QUERY)))
                // .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andDo(document("pg-custom-query-with-error-400"));
    }


    @Test
    @DisplayName("Test find one record")
    void findOneFilm() throws Exception {
        mockMvc.perform(get("/film/one")
                        .accept(APPLICATION_JSON)
                        .param("fields", "title")
                        .param("filter", "film_id==1")
                    )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("pg-get-one-film"));
    }
}
