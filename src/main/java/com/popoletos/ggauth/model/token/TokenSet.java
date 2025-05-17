package com.popoletos.ggauth.model.token;

import lombok.Builder;

@Builder
public record TokenSet(String accessToken, String refreshToken) {}
