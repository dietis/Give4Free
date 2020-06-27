package com.epitech.give4free.ws.ui.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epitech.give4free.ws.service.UserService;
import com.epitech.give4free.ws.shared.dto.UserDto;
import com.epitech.give4free.ws.ui.model.request.UserDetailsRequestModel;
import com.epitech.give4free.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {

	@Autowired
	UserService userService;
	
	@GetMapping(path="/{userID}",
			consumes = { MediaType.APPLICATION_JSON_VALUE},
			produces = { MediaType.APPLICATION_JSON_VALUE }
	)
	public UserRest getUser(@PathVariable String userID)
	{
		UserRest returnValue = new UserRest();
		
		// user data object qui contient le vrai user, user service me donne des fonctions
		UserDto userDto = userService.getUserByUserID(userID);
		BeanUtils.copyProperties(userDto, returnValue);
		
		return returnValue;
	}

	@PostMapping(
			consumes = { MediaType.APPLICATION_JSON_VALUE},
			produces = { MediaType.APPLICATION_JSON_VALUE }
			)
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) 
	{
		UserRest returnValue = new UserRest();
		
		if (userDetails.getFirstName().isEmpty() || userDetails.getLastName().isEmpty() || userDetails.getEmail().isEmpty()) throw new RuntimeException("Error on user add check documentation");

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto createdUser = userService.createUser(userDto);
		BeanUtils.copyProperties(createdUser, returnValue);
		

		return returnValue;
	}

	@PutMapping(path="/{userID}",
				consumes = { MediaType.APPLICATION_JSON_VALUE},
				produces = { MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@PathVariable String userID, @RequestBody UserDetailsRequestModel userDetails)
	{
		UserRest returnValue = new UserRest();

		if (userDetails.getFirstName().isEmpty() || userDetails.getLastName().isEmpty() || userDetails.getEmail().isEmpty()) throw new RuntimeException("Error on user add check documentation");
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto updateUser = userService.updateUser(userID, userDto);
		BeanUtils.copyProperties(updateUser, returnValue);

		return returnValue;
	}

	@DeleteMapping(path="/{userID}",
			consumes = { MediaType.APPLICATION_JSON_VALUE},
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public String deleteUser(@PathVariable String userID)
	{
		userService.deleteUser(userID);
		return "deleted successfully";
	}
}
