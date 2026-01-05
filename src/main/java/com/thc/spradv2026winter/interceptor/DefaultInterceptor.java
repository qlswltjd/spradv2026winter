package com.thc.spradv2026winter.interceptor;

import com.thc.spradv2026winter.util.TokenFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;

public class DefaultInterceptor implements HandlerInterceptor {

    final TokenFactory tokenFactory;
    public DefaultInterceptor(TokenFactory tokenFactory) {
        this.tokenFactory = tokenFactory;
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //컨트롤러 진입 전에 호출되는 메서드
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("preHandle / request [{}]", request);

        // Interceptor에서 req에서 정보 뽑아와서 null,prefix 검사 후 유효기간 확인하는 거 실행.
        String accessToken = request.getHeader("Authorization");
        System.out.println("accessToken: " + accessToken);
        Long userId = null;
        if(accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
            userId = tokenFactory.validateToken(accessToken);
        }

        request.setAttribute("userId", userId);

        return true;
        /*주석 처리한 아래 내용은 header랑 attribute 맞게 오는지 확인하는 용도*/
//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            String headerValue = request.getHeader(headerName);
//            //logger.info("[HEADER] " + headerName + " : " + headerValue);
//        }
//        Enumeration<String> attributeNames = request.getAttributeNames();
//        while (attributeNames.hasMoreElements()) {
//            String attributeName = attributeNames.nextElement();
//            Object attributeValue = request.getAttribute(attributeName);
//            //logger.info("[ATTRIBUTE] " + attributeName + " : " + attributeValue);
//        }

        /* 밑에는 respose가 request할때도 Null인 상태로 있다는 거를 확인하기 위해서 한거*/
//        Collection<String> resHeaderNames = response.getHeaderNames();
//        //logger.info("[1HEADER RES] " + resHeaderNames);
//        for (String each : resHeaderNames) {
//            String resHeaderValue = response.getHeader(each);
//            //logger.info("[HEADER RES] " + each + " : " + resHeaderValue);
//        }

    }

    //컨트롤러 실행 후에 호출되는 메서드
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.info("postHandle / request [{}]", request);

        Collection<String> resHeaderNames = response.getHeaderNames();
        //logger.info("[2HEADER RES] " + resHeaderNames);
        for (String each : resHeaderNames) {
            String resHeaderValue = response.getHeader(each);
            //logger.info("[HEADER RES] " + each + " : " + resHeaderValue);
        }
    }

    //모든것을 마친 후 실행되는 메서드
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.info("afterCompletion / request [{}]", request);

        Collection<String> resHeaderNames = response.getHeaderNames();
        //logger.info("[3HEADER RES] " + resHeaderNames);
        for (String each : resHeaderNames) {
            String resHeaderValue = response.getHeader(each);
            //logger.info("[HEADER RES] " + each + " : " + resHeaderValue);
        }
    }

}