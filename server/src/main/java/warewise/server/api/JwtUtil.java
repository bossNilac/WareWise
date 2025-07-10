package warewise.server.api;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for generating, validating, and managing
 * JSON Web Tokens (JWT) for authentication purposes.
 */
public class JwtUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Use a static key for consistent validation
    private static final long EXPIRATION_TIME_MS = 86400000; // 24 hours
    private static final long LOGIN_EXPIRATION_TIME_MS = 3600000; // 1 hour

    private static final Set<String> revokedTokens = new HashSet<>();


    /**
     * Generates a JWT token for the specified username with a
     * fixed expiration time.
     *
     * @param username the username to include in the token.
     * @return the generated JWT token as a string.
     */
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Validates the given JWT token's signature and expiration.
     *
     * @param token the JWT token to validate.
     * @return true if the token is valid; false otherwise.
     */
    public static boolean isValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Extracts the username (subject) from the given JWT token.
     *
     * @param token the JWT token.
     * @return the username contained in the token.
     */
    public static String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    /**
     * Marks the given JWT token as revoked so it can no longer be used.
     *
     * @param token the JWT token to revoke.
     */
    public static void removeToken(String token) {
        revokedTokens.add(token);
    }

    /**
     * Checks if the given JWT token has been revoked.
     *
     * @param token the JWT token to check.
     * @return true if the token is revoked; false otherwise.
     */
    public static boolean isRevoked(String token) {
        return revokedTokens.contains(token);
    }

    /**
     * Generates a JWT token for the specified username with a
     * fixed expiration time.
     *
     * @param username the username to include in the token.
     * @return the generated JWT token as a string.
     */
    public static String generateLoginToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + LOGIN_EXPIRATION_TIME_MS))
                .signWith(SECRET_KEY)
                .compact();
    }


}
