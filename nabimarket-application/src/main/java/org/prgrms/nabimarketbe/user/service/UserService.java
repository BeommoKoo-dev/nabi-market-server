package org.prgrms.nabimarketbe.user.service;

import javax.validation.Valid;

import org.prgrms.nabimarketbe.aws.service.S3FileUploadService;
import org.prgrms.nabimarketbe.error.BaseException;
import org.prgrms.nabimarketbe.error.ErrorCode;

import org.prgrms.nabimarketbe.user.dto.request.UserNicknameUpdateRequestDTO;
import org.prgrms.nabimarketbe.user.dto.request.UserProfileUpdateRequestDTO;
import org.prgrms.nabimarketbe.user.dto.response.UserGetResponseDTO;
import org.prgrms.nabimarketbe.user.dto.response.UserResponseDTO;
import org.prgrms.nabimarketbe.user.dto.response.UserUpdateResponseDTO;
import org.prgrms.nabimarketbe.user.entity.User;
import org.prgrms.nabimarketbe.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final S3FileUploadService s3FileUploadService;

    private final CheckService checkService;

    @Transactional(readOnly = true)
    public UserResponseDTO<UserGetResponseDTO> getUserByToken(String token) {
        Long userId = checkService.parseToken(token);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        UserGetResponseDTO userGetResponseDTO = UserGetResponseDTO.from(user);
        UserResponseDTO<UserGetResponseDTO> userResponseDTO = new UserResponseDTO<>(userGetResponseDTO);

        return userResponseDTO;
    }

    @Transactional
    public UserResponseDTO<UserUpdateResponseDTO> updateUserImageUrl(
        String token,
        UserProfileUpdateRequestDTO userProfileUpdateRequestDTO
    ) {
        Long userId = checkService.parseToken(token);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        String imageUrl = userProfileUpdateRequestDTO.imageUrl();

        if (user.getImageUrl() != null) {
            String url = user.getImageUrl();
            s3FileUploadService.deleteImage(url);
        }

        user.updateImageUrl(imageUrl);
        UserUpdateResponseDTO userUpdateResponseDTO = UserUpdateResponseDTO.from(user);

        return new UserResponseDTO<>(userUpdateResponseDTO);
    }

    @Transactional
    public UserResponseDTO<UserUpdateResponseDTO> updateUserNickname(
        String token,
        @Valid UserNicknameUpdateRequestDTO userUpdateRequestDTO
    ) {
        Long userId = checkService.parseToken(token);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        String updateNickname = userUpdateRequestDTO.nickname();
        if (userRepository.existsUserByNickname(updateNickname)) {
            throw new BaseException(ErrorCode.USER_NICKNAME_NOT_UNIQUE);
        }
        user.updateNickname(updateNickname);

        UserUpdateResponseDTO userUpdateResponseDTO = UserUpdateResponseDTO.from(user);

        return new UserResponseDTO<>(userUpdateResponseDTO);
    }
}
