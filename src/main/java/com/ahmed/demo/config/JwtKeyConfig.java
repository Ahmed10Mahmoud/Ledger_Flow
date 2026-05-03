package com.ahmed.demo.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
public class JwtKeyConfig {
    @Bean
    public KeyPair keyPair() throws Exception {

        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);

        return gen.generateKeyPair();
    }

    @Bean
    public PrivateKey privateKey(KeyPair pair) {
        return pair.getPrivate();
    }

    @Bean
    public PublicKey publicKey(KeyPair pair) {
        return pair.getPublic();
    }
}
