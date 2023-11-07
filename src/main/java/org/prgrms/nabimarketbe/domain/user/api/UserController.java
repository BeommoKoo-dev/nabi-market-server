package org.prgrms.nabimarketbe.domain.user.api;

import org.prgrms.nabimarketbe.domain.user.dto.response.UserGetResponseDTO;
import org.prgrms.nabimarketbe.domain.user.dto.response.UserResponseDTO;
import org.prgrms.nabimarketbe.domain.user.service.UserService;
import org.prgrms.nabimarketbe.global.util.ResponseFactory;
import org.prgrms.nabimarketbe.global.util.model.SingleResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public SingleResult<UserGetResponseDTO> getUserByToken(@RequestHeader(name = "authorization") String token) {
        UserResponseDTO userResponseDTO = userService.getUserByToken(token);
        UserGetResponseDTO userGetResponseDTO = new UserGetResponseDTO(userResponseDTO);

        return ResponseFactory.getSingleResult(userGetResponseDTO);
    }

    @PutMapping("/profile-image")
    public SingleResult<UserResponseDTO>  updateUserImageUrl(
            @RequestParam Long userId,
            @RequestPart("file") MultipartFile file
    ) {
        userService.updateUserImageUrl(userId, file);

        return ResponseFactory.getSingleResult(userService.findById(userId));
    }
}
