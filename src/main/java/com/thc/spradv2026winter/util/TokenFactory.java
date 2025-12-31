package com.thc.spradv2026winter.util;

import com.thc.spradv2026winter.domain.RefreshToken;
import com.thc.spradv2026winter.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;

@RequiredArgsConstructor
@Component // static 못씀
public class TokenFactory {

    final RefreshTokenRepository refreshTokenRepository;

    static int refreshTokenValidityHour = 12;
    static int accessTokenValidityHour = 1;

    // 토큰 생성
    public String createToken(Long userId, int termHour){
        // userId + 유효기간(plusHours) -> 암호화
        LocalDateTime now = LocalDateTime.now();
        System.out.println("1. now : " + now);
        now = now.plusHours(termHour);
        System.out.println("2. now : " + now);
        String token = null;
        String info = userId + "_" + now;
        try {
            token = AES256Cipher.AES_Encode(null, info);
        } catch (Exception e) {}
        System.out.println("3. token : " + token);
        return token;
    }

    // refresh token 생성
    public String createRefreshToken(Long userId){
        return createToken(userId, refreshTokenValidityHour);
    }
    //access token 생성
    public String createAccessToken(String refreshToken){
        // 1. 먼저 refresh token을 받으면 token의 유효기간을 봐야함. -> db 쓰기 전 검사해서 자원 줄이기
        Long userId = validateToken(refreshToken);
        // userId 즉 유효기간이 만료가 됐을수 있음.
        if(userId == null){
            return null;
        }
        // 2. 유효기간이 맞다면 db에서 refreshtoken 있는지 확인
        RefreshToken entity = refreshTokenRepository.findByContent(refreshToken);
        if(entity == null){ // db에 그런 정보 없음.
            return null;
        }
        Long userIdFromToken = entity.getUserId();
        System.out.println("userIdFromToken : " + userIdFromToken);
        //3. db에 token에 해당하는 userId랑 입력 들어온 token에 userId랑 비교 -> token이 고유하지는 않을 수 있음.
        if(!userIdFromToken.equals(userId)){
            return null;
        }
        System.out.println("userId : " + userId);

        return createToken(userId, accessTokenValidityHour);
    }

    // token 검증
    public Long validateToken(String token){
        // token 검증에서 중요한 거는 유효기간임
        String info = null;
        try{
            // 지금 우리가 _로 userId랑 유효기간을 나눴기 때문에 split("_")으로 두개 분리해서 사용
            info = AES256Cipher.AES_Decode(null, token);;
            String[] array_info = info.split("_");
            Long userId = Long.parseLong(array_info[0]);
            LocalDateTime now = LocalDateTime.now();
            String due = array_info[1];
            String nowTime = now.toString();

            // String 비교
            String[] tempArray = {due,nowTime};
            Arrays.sort(tempArray); // asc정렬 작은게 앞(현재시간이 due보다 전에 있어야 함.)

            if(nowTime.equals(tempArray[0])){// 따라서 array[0]이 현재시간이 되면 올바르게 유효기간이 남아있다는거
                return userId;
            }
        } catch (Exception e) {}
        return null; // 유효기간이 넘었으면 userId 반환하지 않음.
    }
}
