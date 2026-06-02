package com.example.name_app;

import com.example.name_app.dto.UserRequestDTO;
import com.example.name_app.entity.UserProfile;
import com.example.name_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // This annotation explicitly tells Spring to create the MockMvc bean
class NameAppApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;


	/**
	 * Jackson's core object mapper, automatically instantiated and managed as a Bean
	 * by Spring Boot's autoConfiguration engine.

	 * - @Autowired: Tells the Spring framework to inject its pre-configured mapper instance here.
	 * - ObjectMapper: A FasterXML Jackson utility (not a Spring native tool) used to
	 *   automatically serialize Java Records/POJOs to JSON strings, and deserialize JSON back into objects.
	 */
	@Autowired
	// Jackson mapper automatically pre-configured and injected by Spring Boot for JSON conversion
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		// Clears out your Docker test table before every single test run
		// This keeps each test run completely clean and isolated!
		userRepository.deleteAll();
	}

	@Test
	void shouldCreateUserSuccessfully() throws Exception{
		UserRequestDTO payload = new UserRequestDTO("Mary Jane", "O'Connor");

		mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.firstName").value("Mary Jane"))
				.andExpect(jsonPath("$.lastName").value("O'Connor"));
	}

	@Test
	void shouldRejectInvalidNames() throws Exception{
		UserRequestDTO badPayload = new UserRequestDTO("John123", "Doe");

		mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(badPayload)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Bad Request"))
				.andExpect(jsonPath("$.message").exists());

	}

	@Test
	void shouldFetchAllUsersProfiles() throws Exception{
		// Pre-insert two test records into your database table via the repository
		userRepository.save(new UserProfile("Alex", "Smith"));
		userRepository.save(new UserProfile("Emma", "Watson"));

		mockMvc.perform(get("/api/users"))

				.andExpect(status().isOk())
				// CORRECT: Verifies the server responded with JSON
				//.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(2)) // Confirms exactly 2 items return in the array
				.andExpect(jsonPath("$[0].firstName").value("Alex"))
				.andExpect(jsonPath("$[1].firstName").value("Emma"));
	}

	@Test
	void shouldFetchUserProfileById() throws Exception{
		UserProfile savedUser = new UserProfile("Ahmad", "Altarifi");
		userRepository.save(savedUser);
		//Long id = payload.getId();

		//mockMvc.perform(get("/api/users/{id}", id)
		mockMvc.perform(get("/api/users/" + savedUser.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedUser.getId()))
				.andExpect(jsonPath("$.firstName").value("Ahmad"))
				.andExpect(jsonPath("$.lastName").value("Altarifi"));
	}

	@Test
	void shouldReturn404WhenUserNotFound() throws Exception{
		mockMvc.perform(get("/api/users/999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()) // Asserts that your custom resource validation triggered a 404
				.andExpect(jsonPath("$.error").value("Not Found"))
				.andExpect(jsonPath("$.message").exists());
	}

	@Test
	void shouldDeleteUserProfileSuccessfully() throws Exception{
		UserProfile savedUser = userRepository.save(new UserProfile("To Be", "Deleted"));
		mockMvc.perform(delete("/api/users/" + savedUser.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent()); // Verifies the HTTP 204 status response

		// Final verification check: Make sure it actually vanished from the database container!
		assert(!userRepository.existsById(savedUser.getId()));


	}

	//Here is how content() look side-by-side in a complete POST test so you can see the two different roles:
	/**
	mockMvc.perform(post("/api/users")
        .contentType(MediaType.APPLICATION_JSON) // 1. INPUT: Telling the server "I am sending JSON"
        .content(objectMapper.writeValueAsString(payload))) // 1. INPUT: The actual request body
		.andExpect(status().isCreated())
		// ---------------------------------------------------------------------------------
		.andExpect(content().contentType(MediaType.APPLICATION_JSON)) // 2. ASSERTION: Checking response type
		.andExpect(jsonPath("$.id").exists()); // 2. ASSERTION: Checking response data

	 */


}
