package nextstep.member.ui.auth;

import nextstep.member.application.JwtTokenProvider;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String BEARER_PREFIX = "Bearer";
	private final JwtTokenProvider jwtTokenProvider;

	public AuthArgumentResolver(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(Auth.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		String authorization = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorization == null || !BEARER_PREFIX.equals(authorization.split(" ")[0])) {
			throw new IllegalArgumentException();
		}

		String accessToken = authorization.split(" ")[1];

		if (!jwtTokenProvider.validateToken(accessToken)) {
			throw new IllegalArgumentException(accessToken);
		}

		String email = jwtTokenProvider.getPrincipal(accessToken);

		return email;
	}
}
