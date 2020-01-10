package simpleapi2.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;
import simpleapi2.dto.user.UserDTO;
import simpleapi2.entity.user.UserEntity;
import simpleapi2.service.user.UserService;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private UserDTO userDTO;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        userDTO = new UserDTO();
        userDTO.setUsername("test DTO");
        userDTO.setEmail("test@test.com");
        userDTO.setAddress("test address");
        userDTO.setLoyalty(10);

        userEntity = new UserEntity();
        userEntity.setUsername("test Entity");
        userEntity.setEmail("test@test.com");
        userEntity.setAddress("test address");
    }

    @Test
    void testGetAllUser() throws Exception {
        ArrayList<UserDTO> userDTOS = new ArrayList<>();
        userDTOS.add(userDTO);

        when(userService.getUser()).thenReturn(userDTOS);

        mvc.perform(get("/api/users")
                .header("Authentication", "simple_api_key_for_authentication")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].username").value(userDTO.getUsername()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetUser() throws Exception {

        when(userService.getUser(anyString())).thenReturn(userDTO);

        mvc.perform(get("/api/users/test")
                .header("Authentication", "simple_api_key_for_authentication")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(userDTO.getUsername()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetUser_userNotFound() throws Exception {

        when(userService.getUser(anyString())).thenReturn(null);

        mvc.perform(get("/api/users/test")
                .header("Authentication", "simple_api_key_for_authentication")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser() throws Exception {

        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        mvc.perform(post("/api/users")
                .header("Authentication", "simple_api_key_for_authentication")
                .content(asJsonString(userDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(userDTO.getUsername()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testCreateUser_userNotFound_internalError() throws Exception {

        when(userService.createUser(any(UserDTO.class))).thenReturn(null);

        mvc.perform(post("/api/users")
                .header("Authentication", "simple_api_key_for_authentication")
                .content(asJsonString(userDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdateUser() throws Exception {

        when(userService.updateUser(anyString(), any(UserDTO.class))).thenReturn(userDTO);


        mvc.perform(put("/api/users/test")
                .header("Authentication", "simple_api_key_for_authentication")
                .content(asJsonString(userDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(userDTO.getUsername()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdateUser_userNotFound_internalError() throws Exception {

        when(userService.updateUser(anyString(), any(UserDTO.class))).thenReturn(null);

        mvc.perform(put("/api/users/test")
                .header("Authentication", "simple_api_key_for_authentication")
                .content(asJsonString(userDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testDeleteUser() throws Exception {

        when(userService.deleteUser(anyString())).thenReturn(true);

        mvc.perform(delete("/api/users/test")
                .header("Authentication", "simple_api_key_for_authentication")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser_userNotFound_internalError() throws Exception {

        when(userService.deleteUser(anyString())).thenReturn(false);

        mvc.perform(delete("/api/users/test")
                .header("Authentication", "simple_api_key_for_authentication")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}