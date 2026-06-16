package com.bookrealm.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReadingMarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void saveAndListAndDeleteMark_shouldWork() throws Exception {
        String body = """
            {
              "userId": 10001,
              "bookId": 1,
              "chapterId": 1,
              "paragraphId": 1,
              "paragraphSeq": 1,
              "markType": "highlight",
              "note": "这里是测试笔记"
            }
            """;

        MvcResult saved = mockMvc.perform(post("/marks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();
        var savedNode = mapper.readTree(saved.getResponse().getContentAsString());
        assertEquals(0, savedNode.get("code").asInt());
        Long id = savedNode.get("data").get("id").asLong();
        assertEquals("这里是测试笔记", savedNode.get("data").get("note").asText());

        MvcResult listed = mockMvc.perform(get("/chapters/1/marks")
                .param("userId", "10001")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        var listedNode = mapper.readTree(listed.getResponse().getContentAsString());
        assertEquals(0, listedNode.get("code").asInt());
        assertTrue(listedNode.get("data").size() >= 1);

        MvcResult deleted = mockMvc.perform(delete("/marks/" + id)
                .param("userId", "10001"))
            .andExpect(status().isOk())
            .andReturn();
        var deletedNode = mapper.readTree(deleted.getResponse().getContentAsString());
        assertTrue(deletedNode.get("data").asBoolean());
    }

    @Test
    void commentAndLikeFlow_shouldWork() throws Exception {
        String body = """
            {
              "userId": 20001,
              "bookId": 1,
              "chapterId": 1,
              "paragraphId": 2,
              "content": "这段很适合做段评测试"
            }
            """;

        MvcResult saved = mockMvc.perform(post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();
        var savedNode = mapper.readTree(saved.getResponse().getContentAsString());
        assertEquals(0, savedNode.get("code").asInt());
        Long id = savedNode.get("data").get("id").asLong();
        assertEquals("这段很适合做段评测试", savedNode.get("data").get("content").asText());
        assertEquals(0, savedNode.get("data").get("likeCount").asLong());

        MvcResult liked = mockMvc.perform(post("/comments/" + id + "/like")
                .param("userId", "20002"))
            .andExpect(status().isOk())
            .andReturn();
        var likedNode = mapper.readTree(liked.getResponse().getContentAsString());
        assertEquals(1, likedNode.get("data").get("likeCount").asLong());
        assertTrue(likedNode.get("data").get("likedByMe").asBoolean());

        MvcResult interactions = mockMvc.perform(get("/paragraphs/2/interactions")
                .param("userId", "20002"))
            .andExpect(status().isOk())
            .andReturn();
        var interactionsNode = mapper.readTree(interactions.getResponse().getContentAsString());
        assertEquals(0, interactionsNode.get("code").asInt());
        assertTrue(interactionsNode.get("data").get("comments").size() >= 1);

        MvcResult mine = mockMvc.perform(get("/users/20001/comments"))
            .andExpect(status().isOk())
            .andReturn();
        var mineNode = mapper.readTree(mine.getResponse().getContentAsString());
        assertTrue(mineNode.get("data").size() >= 1);

        MvcResult unliked = mockMvc.perform(delete("/comments/" + id + "/like")
                .param("userId", "20002"))
            .andExpect(status().isOk())
            .andReturn();
        var unlikedNode = mapper.readTree(unliked.getResponse().getContentAsString());
        assertEquals(0, unlikedNode.get("data").get("likeCount").asLong());

        MvcResult deleted = mockMvc.perform(delete("/comments/" + id)
                .param("userId", "20001"))
            .andExpect(status().isOk())
            .andReturn();
        var deletedNode = mapper.readTree(deleted.getResponse().getContentAsString());
        assertTrue(deletedNode.get("data").asBoolean());
    }

    @Test
    void blankComment_shouldReturnBusinessError() throws Exception {
        String body = """
            {
              "userId": 20003,
              "bookId": 1,
              "chapterId": 1,
              "paragraphId": 2,
              "content": "   "
            }
            """;

        MvcResult result = mockMvc.perform(post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();
        var node = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals(40000, node.get("code").asInt());
    }
}
