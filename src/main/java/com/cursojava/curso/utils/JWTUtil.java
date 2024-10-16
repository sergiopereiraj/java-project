package com.cursojava.curso.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64; // Importar Base64
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${security.jwt.secret}")
    private String key;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.ttlMillis}")
    private long ttlMillis;

    private final Logger log = LoggerFactory.getLogger(JWTUtil.class);

    /**
     * Convierte el string de la clave secreta en un SecretKey usando Base64.
     */
    private SecretKey getSigningKey() {
        byte[] apiKeySecretBytes = Base64.getDecoder().decode(key); // Decodificar con Base64
        return Keys.hmacShaKeyFor(apiKeySecretBytes); // Crear clave para firma HMAC
    }

    /**
     * Crea un nuevo token JWT.
     *
     * @param id      Identificador del JWT
     * @param subject El "subject" o tema del token
     * @return Token JWT generado
     */
    public String create(String id, String subject) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // Construcción del JWT con sus claims
        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(getSigningKey(), signatureAlgorithm); // Firma con la clave secreta

        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    /**
     * Valida y lee el valor "subject" del JWT.
     *
     * @param jwt Token JWT a validar
     * @return Subject del token JWT
     */
    public String getValue(String jwt) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // Clave secreta para firmar
                .build()
                .parseClaimsJws(jwt)  // Validar y parsear JWT
                .getBody();

        return claims.getSubject();
    }

    /**
     * Valida y lee el valor "id" del JWT.
     *
     * @param jwt Token JWT a validar
     * @return ID del token JWT
     */
    public String getKey(String jwt) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // Clave secreta para firmar
                .build()
                .parseClaimsJws(jwt)  // Validar y parsear JWT
                .getBody();

        return claims.getId();
    }
}
