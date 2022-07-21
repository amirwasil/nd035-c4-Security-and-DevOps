package com.example.demo.controllers;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		boolean isAnyNullOrBlank = (ObjectUtils.anyNull(createUserRequest.getUsername(), createUserRequest.getPassword(), createUserRequest.getConfirmPassword())
				|| StringUtils.isAnyBlank(createUserRequest.getUsername(), createUserRequest.getPassword(), createUserRequest.getConfirmPassword()));

		if (isAnyNullOrBlank) {
			log.info("Create User: FAILED; Reason: Request contains null/blank fields");
			return ResponseEntity.badRequest().build();
		}

		boolean isPasswordLengthFulfilled = createUserRequest.getPassword().length() >= 7;
		boolean isPasswordEqualsConfirmPassword = createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword());

		if (!isPasswordLengthFulfilled || !isPasswordEqualsConfirmPassword) {
			log.info("Create User: FAILED; Reason: Password does not match or is less then 7 characters");
			return ResponseEntity.badRequest().build();
		}

		if (userRepository.findByUsername(createUserRequest.getUsername()) != null) {
			log.info("Create User: FAILED; Reason: Username already exists");
			return ResponseEntity.badRequest().build();
		}

		User user = new User();
		Cart cart = new Cart();
		user.setUsername(createUserRequest.getUsername());
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		user.setCart(cart);
		cartRepository.save(cart);
		userRepository.save(user);
		log.info("Create User: SUCCESS; Username: " + user.getUsername());
		return ResponseEntity.ok(user);
	}
}
