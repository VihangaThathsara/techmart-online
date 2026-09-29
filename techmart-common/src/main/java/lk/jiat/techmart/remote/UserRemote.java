package lk.jiat.techmart.remote;

import jakarta.ejb.Remote;
import lk.jiat.techmart.dto.UserDTO;

import java.util.List;

@Remote
public interface UserRemote {
    UserDTO login(String username, String password);
    UserDTO getUserByUsername(String username);
    List<UserDTO> getAllUsers();
}
