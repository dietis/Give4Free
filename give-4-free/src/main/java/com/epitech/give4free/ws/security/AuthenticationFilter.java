package com.epitech.give4free.ws.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.epitech.give4free.ws.SpringApplicationContext;
import com.epitech.give4free.ws.service.UserService;
import com.epitech.give4free.ws.shared.dto.UserDto;
import com.epitech.give4free.ws.ui.model.request.UserLoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	private final AuthenticationManager authenticationManager;
	
	public AuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req,
			HttpServletResponse res) throws AuthenticationException {
		try {
			UserLoginRequestModel creds = new ObjectMapper()
					.readValue(req.getInputStream(), UserLoginRequestModel.class);

			System.out.println("so login attempt for " + creds.getEmail() + creds.getPassword());
			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							creds.getEmail(),
							creds.getPassword(),
							new ArrayList<>())
					);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	
	@Override
	protected void successfulAuthentication(HttpServletRequest req,
			HttpServletResponse res, 
			FilterChain chain, 
			Authentication auth) throws IOException, ServletException {

		String userName = ((User)auth.getPrincipal()).getUsername();

		String token = Jwts.builder()
				.setSubject(userName)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPORATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.TOKEN_SECRET)
				.compact();

		UserService userService = (UserService)SpringApplicationContext.getBean("userServiceImpl");
		UserDto userDto = userService.getUser(userName);

		res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
		res.addHeader("UserID", userDto.getUserId());

		JsonObject myobject = new JsonObject();
		myobject.addProperty("UserID", userDto.getUserId());
		myobject.addProperty(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
		String json = new Gson().toJson(myobject);

		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(json);
	}
}
