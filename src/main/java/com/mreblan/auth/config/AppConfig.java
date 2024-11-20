package com.mreblan.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import com.mreblan.auth.repositories.UserRepository;
import com.mreblan.auth.services.IJwtService;
import com.mreblan.auth.services.IUserService;
import com.mreblan.auth.services.impl.JwtServiceImpl;
import com.mreblan.auth.services.impl.UserServiceImpl;

import io.jsonwebtoken.Jwts;

import java.util.List;

@Configuration
public class AppConfig {
    
    // @Bean
    // public BCryptPasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }
    //
    // @Bean
    // @Autowired
    // public IUserService userService(UserRepository repository) {
    //     return new UserServiceImpl(repository);
    // }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<String, String>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return new StringRedisTemplate(jedisConnectionFactory());
    } 
    

    @Bean
    public IJwtService jwtService() {
        return new JwtServiceImpl();
    }


    // @Bean
    // public Jwts jwts() {
    //     return new Jwts();
    // }
}
