package com.thc.spradv2026winter.controller;

import com.thc.spradv2026winter.dto.UserDto;
import com.thc.spradv2026winter.util.TokenFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;

@RequiredArgsConstructor
@RequestMapping("/api/auth") // 모든 메서드에 앞에 붙는 메핑!!!
@RestController // 페이지 리턴이 아니라, 객체 리턴할꺼에요!
public class AuthRestController {

    final TokenFactory tokenFactory;

    @PostMapping("")
    public ResponseEntity<Void> access(HttpServletRequest request) {
        // header로 refreshToken 오고 처리하는거
        String refreshToken = request.getHeader("RefreshToken");
        if(!refreshToken.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // RefreshToken 앞에 있는 Sring 스페이스까지 해서 0-6이라서 7부터 시작
        refreshToken = refreshToken.substring(7);

        String accessToken = tokenFactory.createAccessToken(refreshToken);
        if(accessToken == null) { // accessToken 만들때 검증하는거 3개 있었는데 거기 걸리면 null이 옴. 유효기간, db 있는지, userId 일치하는지
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().header("Authorization", "Bearer " + accessToken).build();
    }

}