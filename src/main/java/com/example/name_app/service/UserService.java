package com.example.name_app.service;

import com.example.name_app.dto.UserRequestDTO;
import com.example.name_app.entity.UserProfile;
import com.example.name_app.exception.BadRequestException;
import com.example.name_app.exception.ResourceNotFoundException;
import com.example.name_app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

/**
You want to throw an exception if the list is empty:
(Note: Changed to ResourceNotFoundException because an empty database is a missing resource issue,
not a bad user input request.)

If you want to throw an exception when no users exist, check the list directly.
Streams and Optionals are unnecessary overhead here.
*/
    public List<UserProfile> getAllUsersProfiles(){
//          List<UserProfile> allUsersList = userRepository.findAll();

//        if(allUsersList.isEmpty()){
//            throw new ResourceNotFoundException("Error: No user profiles found!"); // 404 Not Found
//        }

        /**
        * returning an empty list [] is better than a 404 Error:
        * It is not an Error: An empty database table is a valid state and data structure.
        *  It simply means you have zero users registered right now.
        * It is a successful query that returned zero items
        ****
        * In REST API design, a 404 Not Found status should be strictly reserved for when
        * a specific single resource does not exist (e.g., trying to fetch /api/users/999 when ID 999 is missing).
        ****
        */
        return userRepository.findAll();
    }
/**
 When to use Optional (Single Record Fetch):
 Only use Optional when fetching a single record by an identifier.
 Spring Data JPA's findById() natively returns an Optional.
*/
    public UserProfile getUserProfileById(Long id){
        return userRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private boolean validateIfEmptyOrBlank(String str){
        return str.isBlank();
    }

    private boolean validateContainsNonLetters(String str){
        /**
        * matches() method with a regular expression.

        * ^[a-zA-Z]+$: Matches a string containing only English letters from start to finish
        * ! (Negation): Inverts the result. If the string does not consist entirely of letters,
          it contains at least one non-letter character.
        */

        //boolean hasNonLetters = !str.matches("^[a-zA-Z\\s-]+$");


        /**
         * If you prefer avoiding regular expressions for better performance,
           use functional streams with the built-in Character.isLetter method

         * chars(): Converts the string into an IntStream of character codes.
         * anyMatch(...): Evaluates to true as soon as it finds a single character that satisfies the condition.
         * !Character.isLetter(ch): Checks if the character is anything other than a letter ...
         * To avoid the code from throwing a BadRequestException for common names containing spaces,
         * apostrophes, or hyphens (e.g., "Mary Jane" or "O'Connor") we want to permit compound names,
         * we check for whitespace or specific characters alongside letters...
         */

        boolean hasNonLetters = str.chars().anyMatch(ch -> !Character.isLetter(ch) &&
                ch != '-' && ch != ' ' && ch != '\'');

        return hasNonLetters;




    }

    // Business Logic: Validate incoming network payload and transform into database entity
    public UserProfile saveUserProfile(UserRequestDTO request){
        String fName = request.firstName().trim();
        String lName = request.lastName().trim();


        // Validation check before even talking to the database engine
        if(validateIfEmptyOrBlank(fName) || validateIfEmptyOrBlank(lName) ||
        validateContainsNonLetters(fName) || validateContainsNonLetters(lName)){
            throw new BadRequestException("Name cannot be blank and must contain only letters, spaces, or hyphens!");

        }

        // Mapping data safely from Record DTO fields to Database Entity columns
        UserProfile newUserProfile = new UserProfile(fName, lName);
        return userRepository.save(newUserProfile);
    }

    public void deleteUserProfile(Long id){
        if(!userRepository.existsById(id))
            throw new ResourceNotFoundException("Cannot delete: User ID " + id + " does not exist!");

        userRepository.deleteById(id);

    }


// ************************************************** Stream ********************************************************

/** When to use Stream (Large Datasets)
* Do not use Stream just to check if a list is empty.
* Only use Java Streams if you need to filter, transform, or map the user data before returning it.


* If you use streaming directly from the database for large datasets, handle it like this:
import java.util.stream.Stream;
import org.springframework.transaction.annotation.Transactional;

// In your Repository interface:
// @Query("select u from UserProfile u")
// Stream<UserProfile> streamAllUsers();

@Transactional(readOnly = true)
public List<UserDTO> getAllUserDtos() {
    try (Stream<UserProfile> userStream = userRepository.streamAllUsers()) {
        return userStream
            .filter(UserProfile::isActive) // Filter data
            .map(UserDTO::new)             // Transform data
            .toList();
    }
}

*/

// ************************************************** Stream ********************************************************
}

