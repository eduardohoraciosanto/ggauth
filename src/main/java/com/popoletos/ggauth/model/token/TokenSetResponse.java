package com.popoletos.ggauth.model.token;

import lombok.Builder;

@Builder
public record TokenSetResponse(String accessToken, String refreshToken) {}
