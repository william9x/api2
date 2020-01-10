package simpleapi2.controller.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;
import simpleapi2.entity.user.UserEntity;
import simpleapi2.io.request.UserSignUpRequest;
import simpleapi2.io.request.UserUpdateRequest;
import simpleapi2.repository.user.IUserRepository;

import java.text.ParseException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private IUserRepository userRepository;

    private WireMockServer wireMockServer;
    private RestTemplate restTemplate;
    private ResponseEntity response;

    @Before
    public void setup() {
        wireMockServer = new WireMockServer(wireMockConfig().port(8081));
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());

        restTemplate = new RestTemplate();
        response = null;
    }

    @After
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testWireMockServer() {
        stubFor(get(urlEqualTo("/api/resource/"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", TEXT_PLAIN_VALUE)
                        .withBody("test")));

        response = restTemplate.getForEntity("http://localhost:8081/api/resource/", String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

        verify(getRequestedFor(urlMatching("/api/resource/.*")));
    }

    @Test
    public void testUpdateUser() throws Exception {

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
    public void testGetAllUser() throws Exception {

        String json = "{ \"point\" : 10 } ";
        JsonNode jsonNode = new ObjectMapper().readTree(json);

        stubFor(get(urlPathMatching("/api/loyalty/.*"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withJsonBody(jsonNode)
                ));

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get("/api/users")
                .header("Authentication", "simple_api_key_for_authentication")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetUser() throws Exception {

        String json = "{ \"point\" : 10 } ";
        JsonNode jsonNode = new ObjectMapper().readTree(json);

        stubFor(get(urlEqualTo("/api/loyalty/username1"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withJsonBody(jsonNode)
                ));

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get("/api/users/username1")
                .header("Authentication", "simple_api_key_for_authentication")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("username1"))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void testCreateUser() throws Exception {

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
    public void testDeleteUser() throws Exception {

        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.delete("/api/users/username1")
                .header("Authentication", "simple_api_key_for_authentication")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(req)
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    public void createTestUser(String email, String username) throws ParseException {

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

}