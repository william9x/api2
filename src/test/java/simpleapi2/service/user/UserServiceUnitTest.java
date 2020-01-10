package simpleapi2.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import simpleapi2.dto.user.UserDTO;
import simpleapi2.entity.user.UserEntity;
import simpleapi2.repository.user.IUserRepository;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class UserServiceUnitTest {

    private UserEntity userEntity;
    private UserDTO userDTO;

    @InjectMocks
    private UserService userService;

    @Mock
    private IUserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        userDTO = new UserDTO();
        userDTO.setUsername("test DTO");
        userDTO.setEmail("test@test.com");
        userDTO.setAddress("test address");

        userEntity = new UserEntity();
        userEntity.setUsername("test Entity");
        userEntity.setEmail("test@test.com");
        userEntity.setAddress("test address");
    }

    @Test
    final void testGetAllUser() {

        ArrayList<UserEntity> userEntities = new ArrayList<>();
        userEntities.add(userEntity);

        when(userRepository.findAllByUsernameNotNull()).thenReturn(userEntities);

        ArrayList<UserDTO> userDTOS = userService.getUser();

        assertNotNull(userDTOS);
        assertEquals(1, userDTOS.size());
        assertEquals(userDTOS.get(0).getUsername(), userEntity.getUsername());
    }

    @Test
    final void testGetUser() {

        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        when(userRepository.findByUsername(anyString())).thenReturn(userEntity);

        UserDTO findUser = userService.getUser("");

        assertNotNull(findUser);
        assertEquals(userEntity.getUsername(), findUser.getUsername());

    }

    @Test
    final void testGetUser_userNotFound() {

        when(userRepository.findByUsername(anyString())).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        UserDTO findUser = userService.getUser("");

        assertNull(findUser);

    }

    @Test
    final void testCreateUser() {

        when(userRepository.findByUsername(anyString())).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDTO createUser = userService.createUser(userDTO);

        assertNotNull(createUser);
        assertEquals(userEntity.getUsername(), createUser.getUsername());
    }

    @Test
    final void testUpdateUser() {

        when(userRepository.findByUserId(anyString())).thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDTO updateUser = userService.updateUser(anyString(), new UserDTO());

        assertNotNull(updateUser);
        assertEquals(userEntity.getUsername(), updateUser.getUsername());
    }

    @Test
    final void testDeleteUser() {

        when(userRepository.findByUserId(anyString())).thenReturn(userEntity);

        assertDoesNotThrow(() -> userService.deleteUser(anyString()));
    }

}