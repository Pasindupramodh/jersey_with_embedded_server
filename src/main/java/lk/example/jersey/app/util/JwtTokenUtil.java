package lk.example.jersey.app.util;

import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import io.fusionauth.jwt.hmac.HMACVerifier;
import lk.example.jersey.app.model.UserDetails;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenUtil {

    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String ISSUER = "www.jial.lk";
    private static final String SECRET = "hG0:0&!Dv8t0$%eqDHHNU2&F_XSnuN/J%#W;vHPVaTM&:TUc*dn%a:nTvd)bE%4C";
    private static final Long TOKEN_LIFE = 1L;
    private static final Long REFRESH_TOKEN_LIFE = 43200L;
    private String generateToken(Map<String,String> claims ,Long expiration, String subject){
        Signer signer = HMACSigner.newSHA256Signer(SECRET);

// Build a new JWT with an issuer(iss), issued at(iat), subject(sub) and expiration(exp)
        JWT jwt = new JWT().setIssuer(ISSUER)
                .setIssuedAt(ZonedDateTime.now(ZoneOffset.UTC))
                .setSubject(subject)
                .setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(expiration));

// Sign and encode the JWT to a JSON string representation
//        jwt.addClaim("","");
        claims.keySet().forEach(k ->{
            if(claims.get(k)!= null){
                jwt.addClaim(k,claims.get(k));
            }
        });

        return JWT.getEncoder().encode(jwt, signer);
    }
    private Map<String,String> getClaimsFromToken(String token){
        Verifier verifier = HMACVerifier.newVerifier(SECRET);

// Verify and decode the encoded string JWT to a rich object
        JWT jwt = JWT.getDecoder().decode(token, verifier);
        Map<String,String> claims = new HashMap<>();
        if(jwt != null){
            jwt.getAllClaims().forEach((k,v)->{
                claims.put(k,v.toString());
            });
        }
        return claims;
    }
    public String getUsernameFromToken(String token){
            Map<String,String> claims = getClaimsFromToken(token);
            return claims.get(CLAIM_KEY_USERNAME);
    }
    public boolean validateToken(String token, UserDetails userDetails){
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getEmail()) && !isTokenExpired(token);
    }

    public Date getExpireDateFromToken(String token){
        Verifier verifier = HMACVerifier.newVerifier(SECRET);
        // Verify and decode the encoded string JWT to a rich object
        JWT jwt = JWT.getDecoder().decode(token, verifier);
        return new Date(jwt.expiration.toInstant().toEpochMilli());
    }

    private boolean isTokenExpired(String token){
        Date expireDate = getExpireDateFromToken(token);
        return expireDate.before(new Date(System.currentTimeMillis()));
    }

    public String generateAccessToken(UserDetails userDetails){
        Map<String,String> claims= new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME,userDetails.getEmail());
        claims.put(CLAIM_KEY_CREATED,new Date().toString());

        return generateToken(claims,TOKEN_LIFE,userDetails.getEmail());
    }

    public String generateRefreshToken(UserDetails userDetails){
        Map<String,String> claims= new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME,userDetails.getEmail());
        claims.put(CLAIM_KEY_CREATED,new Date().toString());

        return generateToken(claims,REFRESH_TOKEN_LIFE,userDetails.getEmail());
    }
}
