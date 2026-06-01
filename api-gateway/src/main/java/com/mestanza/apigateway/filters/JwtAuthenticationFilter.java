package com.mestanza.apigateway.filters;

import com.mestanza.apigateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		super(Config.class);
		this.jwtUtil = jwtUtil;
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			String path = exchange.getRequest().getURI().getPath();

			if (isPublicEndpoint(path)) {
				return chain.filter(exchange);
			}

			String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				log.warn("Token no proporcionado para: {}", path);
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}

			String token = authHeader.substring(7);

			try {
				if (!jwtUtil.validateToken(token)) {
					log.warn("Token inválido para: {}", path);
					exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
					return exchange.getResponse().setComplete();
				}

				String username = jwtUtil.extractUsername(token);
				String role = jwtUtil.extractRole(token);

				log.debug("Usuario autenticado: {} con rol: {}", username, role);
				return chain.filter(exchange);

			} catch (Exception e) {
				log.error("Error validando token: {}", e.getMessage());
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}
		};
	}

	private boolean isPublicEndpoint(String path) {
		return path.startsWith("/auth/") ||
			   path.startsWith("/actuator/") ||
			   path.equals("/favicon.ico") ||
			   path.startsWith("/error") ||
			   path.startsWith("/fallback/");
	}

	public static class Config {
	}
}
