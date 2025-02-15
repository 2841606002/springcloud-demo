package com.example.uaa.controller;

import com.example.entity.User;
import com.example.uaa.repository.UserRepository;
import com.example.uaa.service.CustomUserDetailsService;
import com.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * 普通登录接口
     *
     * @param user 用户信息（username, password）
     * @return JWT
     */
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        // 验证token中的用户名和密码
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        // 加载用户详情
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        // 生成 JWT
        return jwtUtil.createJWT(
                userDetails.getUsername(), // 用户 ID
                userDetails.getUsername(), // 用户名
                userDetails.getAuthorities().iterator().next().getAuthority() // 用户角色
        );
    }

    /**
     * GitHub OAuth2 第三方登录回调接口
     *
     * @return JWT
     */
    @GetMapping("/oauth2/github")
    public String githubLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            String username = oauth2User.getAttribute("login"); // GitHub 用户名
            String email = oauth2User.getAttribute("email"); // GitHub 邮箱

            // 检查用户是否存在，如果不存在则创建用户
            User user = userDetailsService.findOrCreateUser(username, email, "USER");

            // 生成 JWT
            return jwtUtil.createJWT(
                    user.getUsername(), // 用户 ID
                    user.getUsername(), // 用户名
                    user.getRole() // 用户角色
            );
        }
        return "登陆失败";
    }

    /**
     * LDAP 登录接口
     *
     * @param user 用户信息（username, password）
     * @return JWT 或错误信息
     */
    @PostMapping("/ldap-login")
    public ResponseEntity<String> ldapLogin(@RequestBody User user) {
        try {
            // 验证用户名和密码
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            // 加载用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

            // 生成 JWT
            String token = jwtUtil.createJWT(
                    userDetails.getUsername(), // 用户 ID
                    userDetails.getUsername(), // 用户名
                    userDetails.getAuthorities().iterator().next().getAuthority() // 用户角色
            );
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名不存在或密码错误！");
        }
    }
}