package simpleapi2.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import simpleapi2.Api2Application;
import simpleapi2.entity.user.UserEntity;
import simpleapi2.io.request.UserSignUpRequest;
import simpleapi2.io.request.UserUpdateRequest;
import simpleapi2.repository.user.IUserRepository;

import java.text.ParseException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void testUpdateUser() throws Exception {

        createTestUser("email@1.com", "username1");

        UserUpdateRequest req = new UserUpdateRequest();
        req.setEmail("update@email.com");
        req.setAddress("test");

        mvc.perform(put("/api/users/username1")
                .header("Authentication", "simple_api_key_for_authentication")
                .content(asJsonString(req))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(req.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address").value(req.getAddress()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetAllUser() throws Exception {

        mvc.perform(get("/api/users")
                .header("Authentication", "simple_api_key_for_authentication")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetUser() throws Exception {

        mvc.perform(get("/api/users/username1")
                .header("Authentication", "simple_api_key_for_authentication")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("username1"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testCreateUser() throws Exception {

        UserSignUpRequest req = new UserSignUpRequest();
        req.setUsername("testCreateUser");
        req.setEmail("test@CreateUser.com");
        req.setAddress("testCreateUser");

        mvc.perform(post("/api/users")
                .header("Authentication", "simple_api_key_for_authentication")
                .content(asJsonString(req))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(req.getUsername()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testDeleteUser() throws Exception {

        mvc.perform(delete("/api/users/username1")
                .header("Authentication", "simple_api_key_for_authentication")
                .contentType(MediaType.APPLICATION_JSON))
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

    private void setupMockServer(){
        WireMockRule wireMockRule = new WireMockRule((8081));
        wireMockRule.stubFor(get(urlEqualTo("/api/resource/"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", TEXT_PLAIN_VALUE)
                        .withBody("test")));

    }
}