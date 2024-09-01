package com.telerikacademy.web.smartgarageti.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.telerikacademy.web.smartgarageti.models.Avatar;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.repositories.contracts.AvatarRepository;
import com.telerikacademy.web.smartgarageti.repositories.contracts.UserRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class AvatarServiceImpl implements AvatarService {
    public static final int DEFAULT_AVATAR = 1;
    public static final String NOT_THE_DEFAULT_AVATAR = "Avatar with ID 1 is not the default avatar";
    private final Cloudinary cloudinary;
    private final AvatarRepository avatarRepository;
    private final UserRepository userRepository;

    @Autowired
    public AvatarServiceImpl(Cloudinary cloudinary, AvatarRepository avatarRepository, UserRepository userRepository) {
        this.cloudinary = cloudinary;
        this.avatarRepository = avatarRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Avatar uploadAvatar(User user, MultipartFile avatarFile) throws IOException {
        Avatar defaultAvatar = avatarRepository.findById(DEFAULT_AVATAR)
                .orElseThrow(() -> new IllegalStateException(NOT_THE_DEFAULT_AVATAR));
        Avatar avatar;
        if (user.getAvatar().equals(defaultAvatar)) {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(avatarFile.getBytes(), ObjectUtils.emptyMap());
            String avatarUrl = uploadResult.get("secure_url").toString();

            avatar = new Avatar();
            avatar.setAvatar(avatarUrl);
            avatarRepository.save(avatar);

            user.setAvatar(avatar);
            userRepository.save(user);
        } else {
            Avatar avatarToBeDeleted = user.getAvatar();
            Map<String, Object> uploadResult = cloudinary.uploader().upload(avatarFile.getBytes(), ObjectUtils.emptyMap());
            String avatarUrl = uploadResult.get("secure_url").toString();

            avatar = new Avatar();
            avatar.setAvatar(avatarUrl);
            avatarRepository.save(avatar);

            user.setAvatar(avatar);
            avatarRepository.delete(avatarToBeDeleted);
            userRepository.save(user);
        }
        return avatar;
    }

    @Override
    public Avatar getUserAvatar(User user) {
        if (user.getAvatar() != null) {
            return user.getAvatar();
        } else {
            return initializeDefaultAvatar(user);
        }
    }

    @Override
    public Avatar initializeDefaultAvatar(User user) {
        Avatar defaultAvatar = avatarRepository.findById(DEFAULT_AVATAR)
                .orElseThrow(() -> new IllegalStateException(NOT_THE_DEFAULT_AVATAR));
        user.setAvatar(defaultAvatar);
        userRepository.save(user);
        return defaultAvatar;
    }

    @Override
    public Avatar initializeDefaultAvatar() {
        Avatar defaultAvatar = avatarRepository.findById(DEFAULT_AVATAR)
                .orElseThrow(() -> new IllegalStateException(NOT_THE_DEFAULT_AVATAR));
        if (!"/images/DefaultUserAvatar.jpg".equals(defaultAvatar.getAvatar())) {
            throw new IllegalStateException(NOT_THE_DEFAULT_AVATAR);
        }
        return defaultAvatar;
    }

    @Override
    public void deleteAvatarFromUser(User user) throws IOException {
        Avatar avatar = user.getAvatar();
        if (avatar != null) {
            String publicId = extractPublicIdFromUrl(avatar.getAvatar());
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            user.setAvatar(null);
            userRepository.save(user);
            avatarRepository.delete(avatar);
        }
    }

    private String extractPublicIdFromUrl(String url) {
        String[] parts = url.split("/");
        String publicIdWithExtension = parts[parts.length - 1];
        return publicIdWithExtension.split("\\.")[0];
    }
}