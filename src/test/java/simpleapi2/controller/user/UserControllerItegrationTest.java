package simpleapi2.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import simpleapi2.Api2Application;
import simpleapi2.entity.user.UserEntity;
import simpleapi2.io.request.UserSignUpRequest;
import simpleapi2.io.request.UserUpdateRequest;
import simpleapi2.repository.user.IUserRepository;

import java.text.ParseException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Api2Application.class)
@AutoConfigureMockMvc
class UserControllerItegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private IUserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8888));

    @Test
    void testUpdateUser() throws Exception {

        createTestUser("email@1.com", "username1");

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("update@email.com");
        updateRequest.setAddress("test");

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put("/api/users/username1")
                .header("Authentication", "simple_api_key_for_authentication")
                .content(asJsonString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(updateRequest.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address").value(updateRequest.getAddress()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetAllUser() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get("/api/users")
                .header("Authentication", "simple_api_key_for_authentication")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetUser() throws Exception {
        WireMockServer wireMockServer = new WireMockServer();
        configureFor("localhost", 8090);

        stubFor(get(urlEqualTo("/api/loyalty/username1"))
                .willReturn(aResponse()
                        .withHeader("Authentication", "simple_api_key_for_authentication")
                        .withBody("Welcome to Baeldung!")));

        wireMockServer.start();

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get("/api/users/username1")
                .header("Authentication", "simple_api_key_for_authentication")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("username1"))
                .andDo(MockMvcResultHandlers.print());

        wireMockServer.stop();
    }

    @Test
    void testCreateUser() throws Exception {

        UserSignUpRequest signUpRequest = new UserSignUpRequest();
        signUpRequest.setUsername("testCreateUser");
        signUpRequest.setEmail("test@CreateUser.com");
        signUpRequest.setAddress("testCreateUser");

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post("/api/users")
                .header("Authentication", "simple_api_key_for_authentication")
                .content(asJsonString(signUpRequest))
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(req)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(signUpRequest.getUsername()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testDeleteUser() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.delete("/api/users/username1")
                .header("Authentication", "simple_api_key_for_authentication")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(req)
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    private void createTestUser(String email, String username) throws ParseException {

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setUsername(username);
        userEntity.setUserId(username);
        userEntity.setAddress("");

        userRepository.saveAndFlush(userEntity);
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setupMockServer(String userId) {
        WireMockRule wireMockRule = new WireMockRule((8081));
        wireMockRule.stubFor(get(urlEqualTo("/api/" + userId + "/loyalty"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Authentication", "simple_api_key_for_authentication")
                        .withBody("10")));

    }
}