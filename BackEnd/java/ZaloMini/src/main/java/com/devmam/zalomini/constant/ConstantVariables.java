package com.devmam.zalomini.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantVariables {

    @Value("${constant.key.signer-key}")
    public static String SIGNER_KEY;
    @Value("${constant.key.signer-key}")
    public void setSignerKey(String signerKey) {
        SIGNER_KEY = signerKey;
    }

    @Value("${security.jwt.token.expire-length}")
    public static Long KEY_TIME_OUT;
    @Value("${security.jwt.token.expire-length}")
    public void setKeyTimeOut(Long keyTimeOut) {
        KEY_TIME_OUT = keyTimeOut;
    }

    @Value("${spring.data.redis.host}")
    public static String REDIS_HOST;
    @Value("${spring.data.redis.host}")
    public void setRedisHost(String redisHost) {
        REDIS_HOST = redisHost;
    }

    @Value("${spring.data.redis.port}")
    public static int REDIS_PORT;
    @Value("${spring.data.redis.port}")
    public void setRedisPort(int redisPort) {
        REDIS_PORT = redisPort;
    }

    @Value("${spring.data.redis.database}")
    public static String REDIS_DATABASE;
    @Value("${spring.data.redis.database}")
    public void setRedisDatabase(String redisDatabase) {
        REDIS_DATABASE = redisDatabase;
    }

    @Value("${system.mail.account}")
    public static String SYSTEM_EMAIL;
    @Value("${system.mail.account}")
    public void setSystemEmail(String systemEmail) {
        SYSTEM_EMAIL = systemEmail;
    }

    @Value("${system.mail.key}")
    public static String MAIL_KEY;
    @Value("${system.mail.key}")
    public void setMailKey(String mailKey) {
        MAIL_KEY = mailKey;
    }
}
