package pt.challenge.client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import pt.challenge.dto.external.user.UserProfileDto;
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "external.services.user-profile.url=http://localhost:${wiremock.server.port}",
        "resilience4j.retry.instances.userProfile.wait-duration=10ms"
})
@ActiveProfiles("test")
class UserProfileClientIntegrationTest {

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        System.setProperty("wiremock.server.port", String.valueOf(wireMockServer.port()));
        configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Autowired
    private UserProfileClient userProfileClient;

    @Test
    void shouldFetchUserProfileSuccessfully() {
        String userId = "u1";
        stubFor(get(urlEqualTo("/api/users/" + userId + "/profile"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                  "userId": "u1",
                                  "preferences": {
                                    "categories": ["electronics"],
                                    "priceRange": {"min": 0, "max": 1000}
                                  }
                                }
                                """)));

        UserProfileDto response = userProfileClient.getUserProfile(userId);

        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.categories()).contains("electronics");
    }

    @Test
    void shouldRetryAndThenFallbackOnFailure() {
        String userId = "fail-user";
        stubFor(get(urlEqualTo("/api/users/" + userId + "/profile"))
                .willReturn(aResponse().withStatus(500)));

        UserProfileDto response = userProfileClient.getUserProfile(userId);

        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.categories()).isEmpty();
        
        // Verify retry happened (max-attempts: 3)
        verify(3, getRequestedFor(urlEqualTo("/api/users/" + userId + "/profile")));
    }
}
