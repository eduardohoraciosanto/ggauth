package com.popoletos.ggauth.model.token;

import lombok.Builder;

@Builder
public record UserTokenSetRequest(String playerId) {}
