package com.bookrealm.library.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReadingMarkControllerTest {

    private static final String DEV_SECRET = "dev-only-secret-please-change-in-production-0123456789abcdef";
    private static final SecretKey JWT_KEY = Keys.hmacShaKeyFor(DEV_SECRET.getBytes(StandardCharsets.UTF_8));

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void saveMarkWithoutToken_shouldReturn401() throws Exception {
        String body = """
            {
              "bookId": 1,
              "chapterId": 1,
              "paragraphId": 1,
              "paragraphSeq": 1,
              "markType": "highlight",
              "note": "这里是测试笔记"
            }
            """;

        MvcResult result = mockMvc.perform(post("/marks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isUnauthorized())
            .andReturn();
        var node = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals(40100, node.get("code").asInt());
        assertTrue(node.get("data").isNull());
        assertEquals("未登录或令牌无效", node.get("message").asText());
    }

    @Test
    void saveMarkWithInvalidToken_shouldReturn401() throws Exception {
        String body = """
            {
              "bookId": 1,
              "chapterId": 1,
              "paragraphId": 1,
              "paragraphSeq": 1,
              "markType": "highlight",
              "note": "这里是测试笔记"
            }
            """;

        MvcResult result = mockMvc.perform(post("/marks")
                .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isUnauthorized())
            .andReturn();
        var node = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals(40100, node.get("code").asInt());
    }

    @Test
    void saveAndListAndDeleteMark_shouldWork() throws Exception {
        Long userId = 921001L;
        String body = """
            {
              "bookId": 1,
              "chapterId": 1,
              "paragraphId": 1,
              "paragraphSeq": 1,
              "markType": "highlight",
              "note": "这里是测试笔记"
            }
            """;

        MvcResult saved = mockMvc.perform(post("/marks")
                .header(HttpHeaders.AUTHORIZATION, bearer(userId, 0))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();
        var savedNode = mapper.readTree(saved.getResponse().getContentAsString());
        assertEquals(0, savedNode.get("code").asInt());
        Long id = savedNode.get("data").get("id").asLong();
        assertEquals(userId.longValue(), savedNode.get("data").get("userId").asLong());
        assertEquals("这里是测试笔记", savedNode.get("data").get("note").asText());

        MvcResult listed = mockMvc.perform(get("/chapters/1/marks")
                .header(HttpHeaders.AUTHORIZATION, bearer(userId, 0))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        var listedNode = mapper.readTree(listed.getResponse().getContentAsString());
        assertEquals(0, listedNode.get("code").asInt());
        assertEquals(userId.longValue(), itemById(listedNode.get("data"), id).get("userId").asLong());

        MvcResult bookMarks = mockMvc.perform(get("/books/1/marks")
                .header(HttpHeaders.AUTHORIZATION, bearer(userId, 0))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        var bookMarksNode = mapper.readTree(bookMarks.getResponse().getContentAsString());
        assertEquals(userId.longValue(), itemById(bookMarksNode.get("data"), id).get("userId").asLong());

        MvcResult mine = mockMvc.perform(get("/users/123456789/marks")
                .header(HttpHeaders.AUTHORIZATION, bearer(userId, 0))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        var mineNode = mapper.readTree(mine.getResponse().getContentAsString());
        assertEquals(userId.longValue(), itemById(mineNode.get("data"), id).get("userId").asLong());

        MvcResult deleted = mockMvc.perform(delete("/marks/" + id)
                .header(HttpHeaders.AUTHORIZATION, bearer(userId, 0)))
            .andExpect(status().isOk())
            .andReturn();
        var deletedNode = mapper.readTree(deleted.getResponse().getContentAsString());
        assertTrue(deletedNode.get("data").asBoolean());
    }

    @Test
    void deleteOthersMark_shouldReturn403() throws Exception {
        Long ownerId = 921101L;
        Long otherUserId = 921102L;
        String body = """
            {
              "bookId": 1,
              "chapterId": 1,
              "paragraphId": 2,
              "paragraphSeq": 2,
              "markType": "highlight",
              "note": "只允许本人删除"
            }
            """;

        MvcResult saved = mockMvc.perform(post("/marks")
                .header(HttpHeaders.AUTHORIZATION, bearer(ownerId, 0))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();
        Long id = mapper.readTree(saved.getResponse().getContentAsString()).get("data").get("id").asLong();

        MvcResult deleted = mockMvc.perform(delete("/marks/" + id)
                .header(HttpHeaders.AUTHORIZATION, bearer(otherUserId, 0)))
            .andExpect(status().isForbidden())
            .andReturn();
        var deletedNode = mapper.readTree(deleted.getResponse().getContentAsString());
        assertEquals(40300, deletedNode.get("code").asInt());
        assertEquals("不能删除他人的标记", deletedNode.get("message").asText());
    }

    @Test
    void commentAndLikeFlow_shouldWork() throws Exception {
        Long ownerId = 921201L;
        Long likerId = 921202L;
        String body = """
            {
              "bookId": 1,
              "chapterId": 1,
              "paragraphId": 2,
              "content": "这段很适合做段评测试"
            }
            """;

        MvcResult saved = mockMvc.perform(post("/comments")
                .header(HttpHeaders.AUTHORIZATION, bearer(ownerId, 0))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();
        var savedNode = mapper.readTree(saved.getResponse().getContentAsString());
        assertEquals(0, savedNode.get("code").asInt());
        Long id = savedNode.get("data").get("id").asLong();
        assertEquals(ownerId.longValue(), savedNode.get("data").get("userId").asLong());
        assertEquals("这段很适合做段评测试", savedNode.get("data").get("content").asText());
        assertEquals(0, savedNode.get("data").get("likeCount").asLong());

        MvcResult liked = mockMvc.perform(post("/comments/" + id + "/like")
                .header(HttpHeaders.AUTHORIZATION, bearer(likerId, 0)))
            .andExpect(status().isOk())
            .andReturn();
        var likedNode = mapper.readTree(liked.getResponse().getContentAsString());
        assertEquals(1, likedNode.get("data").get("likeCount").asLong());
        assertTrue(likedNode.get("data").get("likedByMe").asBoolean());

        MvcResult interactions = mockMvc.perform(get("/paragraphs/2/interactions")
                .header(HttpHeaders.AUTHORIZATION, bearer(likerId, 0)))
            .andExpect(status().isOk())
            .andReturn();
        var interactionsNode = mapper.readTree(interactions.getResponse().getContentAsString());
        assertEquals(0, interactionsNode.get("code").asInt());
        JsonNode commentInInteractions = itemById(interactionsNode.get("data").get("comments"), id);
        assertTrue(commentInInteractions.get("likedByMe").asBoolean());

        MvcResult mine = mockMvc.perform(get("/users/123456789/comments")
                .header(HttpHeaders.AUTHORIZATION, bearer(ownerId, 0)))
            .andExpect(status().isOk())
            .andReturn();
        var mineNode = mapper.readTree(mine.getResponse().getContentAsString());
        assertEquals(ownerId.longValue(), itemById(mineNode.get("data"), id).get("userId").asLong());

        MvcResult unliked = mockMvc.perform(delete("/comments/" + id + "/like")
                .header(HttpHeaders.AUTHORIZATION, bearer(likerId, 0)))
            .andExpect(status().isOk())
            .andReturn();
        var unlikedNode = mapper.readTree(unliked.getResponse().getContentAsString());
        assertEquals(0, unlikedNode.get("data").get("likeCount").asLong());

        MvcResult deleted = mockMvc.perform(delete("/comments/" + id)
                .header(HttpHeaders.AUTHORIZATION, bearer(ownerId, 0)))
            .andExpect(status().isOk())
            .andReturn();
        var deletedNode = mapper.readTree(deleted.getResponse().getContentAsString());
        assertTrue(deletedNode.get("data").asBoolean());
    }

    @Test
    void blankComment_shouldReturnBusinessError() throws Exception {
        String body = """
            {
              "bookId": 1,
              "chapterId": 1,
              "paragraphId": 2,
              "content": "   "
            }
            """;

        MvcResult result = mockMvc.perform(post("/comments")
                .header(HttpHeaders.AUTHORIZATION, bearer(921301L, 0))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();
        var node = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals(40000, node.get("code").asInt());
    }

    @Test
    void optionalPublicInteractionAndCommentEndpoints_shouldAllowAnonymous() throws Exception {
        MvcResult interactions = mockMvc.perform(get("/paragraphs/2/interactions")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        assertEquals(0, mapper.readTree(interactions.getResponse().getContentAsString()).get("code").asInt());

        MvcResult paragraphComments = mockMvc.perform(get("/paragraphs/2/comments")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        assertEquals(0, mapper.readTree(paragraphComments.getResponse().getContentAsString()).get("code").asInt());

        MvcResult bookComments = mockMvc.perform(get("/books/1/comments")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        assertEquals(0, mapper.readTree(bookComments.getResponse().getContentAsString()).get("code").asInt());
    }

    private String bearer(Long userId, int role) {
        return "Bearer " + Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("role", role)
            .signWith(JWT_KEY)
            .compact();
    }

    private JsonNode itemById(JsonNode array, Long id) {
        for (JsonNode item : array) {
            if (item.get("id").asLong() == id.longValue()) {
                return item;
            }
        }
        fail("Expected item with id " + id + " in " + array);
        return null;
    }
}
