package simpleapi2.service.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import simpleapi2.dto.user.LoyaltyDTO;
import simpleapi2.dto.user.UserDTO;
import simpleapi2.entity.user.UserEntity;
import simpleapi2.repository.user.IUserRepository;

import java.util.ArrayList;
import java.util.Base64;

@Service
public class UserService implements IUserService {

    private final String URL_LOYALTY = "http://localhost:8081/api/loyalty/";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public ArrayList<UserDTO> getUser() {

        try {
            ArrayList<UserEntity> userEntities = userRepository.findAllByUsernameNotNull();

            if (userEntities.isEmpty()) return null;
            else {
                ArrayList<UserDTO> userDTOS = new ArrayList<>();

                for (UserEntity userEntity : userEntities) {
                    UserDTO userDTO = new UserDTO();
                    BeanUtils.copyProperties(userEntity, userDTO);

                    Integer loyalty = getUserLoyalty(userEntity.getUserId());
                    userDTO.setLoyalty(loyalty);

                    userDTOS.add(userDTO);
                }

                return userDTOS;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserDTO getUser(String usernameOrEmail) {

        try {
            UserEntity userEntity = userRepository.findByUsername(usernameOrEmail);
            if (null == userEntity) userEntity = userRepository.findByEmail(usernameOrEmail);

            if (null == userEntity) return null;
            else {
                UserDTO returnValue = new UserDTO();
                BeanUtils.copyProperties(userEntity, returnValue);

                Integer loyalty = getUserLoyalty(userEntity.getUserId());
                returnValue.setLoyalty(loyalty);

                return returnValue;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {

        try {
            if (isUsernameExist(userDTO.getUsername()) || isEmailExist(userDTO.getEmail())) {
                return null;
            } else {

                UserEntity userEntity = new UserEntity();
                BeanUtils.copyProperties(userDTO, userEntity);

                String UUID = Base64.getUrlEncoder().withoutPadding().encodeToString(userEntity.getUsername().getBytes());

                userEntity.setUserId(UUID);

                UserDTO returnValue = new UserDTO();
                BeanUtils.copyProperties(userRepository.save(userEntity), returnValue);

                return returnValue;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserDTO updateUser(String userId, UserDTO userDTO) {

        String updateEmail = userDTO.getEmail();
        String updateAddress = userDTO.getAddress();

        try {
            UserEntity userEntity = userRepository.findByUserId(userId);
            if (null == userEntity) return null;
            else {
                if (null != updateEmail &&
                        updateEmail.trim().length() > 0 &&
                        !isEmailExist(updateEmail)) {

                    userEntity.setEmail(updateEmail);
                }

                if (null != updateEmail) {
                    userEntity.setAddress(updateAddress);
                }

                UserDTO returnValue = new UserDTO();
                BeanUtils.copyProperties(userRepository.save(userEntity), returnValue);

                return returnValue;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deleteUser(String userId) {
        try {
            UserEntity userEntity = userRepository.findByUserId(userId);
            if (null == userEntity) return false;
            else {
                userRepository.delete(userEntity);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private Integer getUserLoyalty(String userId) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authentication", "simple_api_key_for_authentication");
            headers.set("Accept", "application/json");

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<LoyaltyDTO> response = restTemplate.exchange(
                    URL_LOYALTY + userId, HttpMethod.GET, entity, LoyaltyDTO.class);

            return response.getBody().getPoint();

        } catch (Exception e){
            return 0;
        }
    }

    private boolean isUsernameExist(String username) {
        try {
            return userRepository.existsByUsername(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isEmailExist(String email) {
        try {
            return userRepository.existsByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
