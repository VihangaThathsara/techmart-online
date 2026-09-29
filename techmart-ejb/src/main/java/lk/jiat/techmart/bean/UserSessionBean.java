package lk.jiat.techmart.bean;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import lk.jiat.techmart.interceptor.PerformanceInterceptor;
import lk.jiat.techmart.dto.UserDTO;
import lk.jiat.techmart.remote.UserRemote;
import lk.jiat.techmart.store.TechMartStore;

import java.util.List;
import lk.jiat.techmart.util.PasswordHash;
import java.util.logging.Logger;

@Stateless
@Interceptors(PerformanceInterceptor.class)
public class UserSessionBean implements UserRemote {
    private static final Logger LOGGER = Logger.getLogger(UserSessionBean.class.getName());


    @PostConstruct
    public void init() {
        LOGGER.info("User session bean initialized");
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info("User session bean destroyed");
    }

    @Override
    public UserDTO login(String username, String password) {
        if (username == null || password == null) return null;
        UserDTO user = TechMartStore.findUser(username);
        if (user != null && PasswordHash.verify(password, user.getPassword())) {
            LOGGER.info("Login success for " + username);
            return new UserDTO(user.getId(), user.getUsername(), null, user.getFirstName(), user.getEmail(), user.getRole());
        }
        LOGGER.info("Login failed for " + username);
        return null;
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        return TechMartStore.findUser(username);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return TechMartStore.users();
    }
}
