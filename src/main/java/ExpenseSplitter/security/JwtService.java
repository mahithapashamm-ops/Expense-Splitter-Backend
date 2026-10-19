package ExpenseSplitter.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key = Keys.hmacShaKeyFor(
        System.getenv().getOrDefault(
            "JWT_SECRET",
            "ExpenseSplitterDemoSecretKey2026Secure"
            + "ForJWTSigning1234567890"
        ).getBytes(StandardCharsets.UTF_8)
    );

    private final long expiration = 86400000;

    public String generateToken(String email) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
            .subject(email)
            .issuedAt(new Date(now))
            .expiration(new Date(now + expiration))
            .signWith(key)
            .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
}
