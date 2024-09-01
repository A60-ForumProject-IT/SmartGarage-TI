package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.Avatar;
import com.telerikacademy.web.smartgarageti.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AvatarService {
    Avatar getUserAvatar(User user);

    Avatar uploadAvatar(User user, MultipartFile avatarFile) throws IOException;

    void deleteAvatarFromUser(User user) throws IOException;

    Avatar initializeDefaultAvatar(User user);

    Avatar initializeDefaultAvatar();
}
