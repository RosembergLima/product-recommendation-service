package pt.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pt.challenge.config.SecurityConfig;
import pt.challenge.dto.FeedbackRequest;
import pt.challenge.dto.FeedbackResponse;
import pt.challenge.dto.RecommendationResponse;
import pt.challenge.service.RecommendationService;
import pt.challenge.util.FeedbackType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
@Import(SecurityConfig.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecommendationService recommendationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void shouldGetRecommendations() throws Exception {
        String userId = "u1";
        RecommendationResponse response = new RecommendationResponse(List.of(), (short) 0);
        when(recommendationService.getRecommendations(userId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/recommendations/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalRecommendations").value(0));
    }

    @Test
    @WithMockUser
    void shouldPostFeedback() throws Exception {
        FeedbackRequest request = new FeedbackRequest("u1", "p1", FeedbackType.LIKED, "Good");
        FeedbackResponse response = new FeedbackResponse("success", "Recorded");
        
        when(recommendationService.save(any(FeedbackRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/recommendations/feedback")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenFeedbackRequestInvalid() throws Exception {
        FeedbackRequest request = new FeedbackRequest(null, "p1", FeedbackType.LIKED, "Good");

        mockMvc.perform(post("/api/recommendations/feedback")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/recommendations/u1"))
                .andExpect(status().isUnauthorized());
    }
}
