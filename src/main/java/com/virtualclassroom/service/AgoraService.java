package com.virtualclassroom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AgoraService {

    private static final Logger log = LoggerFactory.getLogger(AgoraService.class);

    @Value("${agora.app-id:}")
    private String appId;

    @Value("${agora.app-certificate:}")
    private String appCertificate;

    @Value("${agora.token.expire-seconds:3600}")
    private long tokenTtlSeconds;

    public String getAppId() {
        return appId;
    }

    public boolean isConfigured() {
        return appId != null && !appId.isBlank();
    }

    /**
     * Generate a temporary RTC token for a given channel and uid.
     * If appCertificate is not configured, returns null (use appId with app certificate disabled projects).
     *
     * Note: Implementing full AccessToken2 here would add significant code. For production,
     * add Agora's official token builder (AccessToken2 / RtcTokenBuilder2) as a dependency or
     * a utility class and replace this stub.
     */
    public String generateRtcTokenOrNull(String channelName, String uid, String role) {
        if (appId == null || appId.isBlank()) {
            log.warn("Agora appId is not configured; frontend will not be able to join RTC");
            return null;
        }
        if (appCertificate == null || appCertificate.isBlank()) {
            // App certificate disabled; token not required depending on project settings
            log.info("Agora appCertificate not configured; returning null token (certificate-disabled mode)");
            return null;
        }
        // Placeholder: integrate AccessToken2/RtcTokenBuilder2 here.
        // Returning null to force client to use temp token acquisition later or fail fast in dev.
        log.warn("Agora token generation not yet implemented. Please integrate AccessToken2 and remove this stub.");
        return null;
    }

    public long getExpiryTimestamp() {
        return Instant.now().getEpochSecond() + Math.max(60, tokenTtlSeconds);
    }
}
