package com.bookrealm.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void listBooks_shouldReturnPage() throws Exception {
        MvcResult result = mockMvc.perform(get("/books")
                .param("q", "西游").param("page", "0").param("size", "5")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        var node = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals(0, node.get("code").asInt());
        assertEquals(1, node.get("data").get("items").size());
        assertEquals("西游记", node.get("data").get("items").get(0).get("title").asText());
    }

    @Test
    void getBook_shouldReturnDetail() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        var node = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals(0, node.get("code").asInt());
        assertEquals("西游记", node.get("data").get("title").asText());
        assertEquals(3, node.get("data").get("chapters").size());
    }

    @Test
    void getBook_shouldReturn404_whenNotFound() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
        var node = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals(40400, node.get("code").asInt());
    }
}
