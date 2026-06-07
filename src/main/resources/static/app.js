// Automatically adapts to 'http://localhost:8080/api/users' locally
// and adapts to 'https://production-backend-app-3mkn.onrender.com/api/users' on the cloud!
const apiUrl = '/api/users';


const nameForm = document.getElementById('name-form');
const idInput = document.getElementById('id-input');
const firstNameInput = document.getElementById('firstName-input');
const lastNameInput = document.getElementById('lastName-input');
const nameListEl = document.getElementById('nameList-el');
const submitButton = document.getElementById('submit-btn');
const deleteButton = document.getElementById('deleteProfile-btn');
const getProfileButton = document.getElementById('getProfile-btn');

// Listening to the 'submit' event catches both mouse clicks and keyboard 'Enter' presses!
nameForm.addEventListener('submit', (event) => {
  // By leaving event.preventDefault() in place, you force the browser to stay on the page, 
  // allowing your asynchronous promises to safely finish saving and fetching data,
  // instead of being cut off mid-flight by the page destroy cycle during the Network call,
  // which would result in the Database entry being missing or never created.
  
  // It reminds you (or anyone else reading your code) that this isn't just about blocking a reload, 
  // it is about saving your network requests from being destroyed.
  // Saves Debugging Time: If you look at this code months from now, 
  // you will instantly remember why that parameter is there.
  event.preventDefault(); // Layer 2 protection: explicitly blocks form reload
  submitFullName();
});



getProfileButton.addEventListener('click', (event) => {
    event.preventDefault(); // Layer 2 protection: explicitly blocks form reload
    const userId = idInput.value.trim();
    idInput.value = "";
    if(userId) {
        getProfile(userId);
    }
    else {
        alert('Please enter a valid ID to fetch a profile.');
    }
});

deleteButton.addEventListener('click', (event) => {
    event.preventDefault(); // Layer 2 protection: explicitly blocks form reload
    const userId = idInput.value.trim();
    idInput.value = "";
    if(userId) {
        deleteName(userId);
    }
    else {
        alert('Please enter a valid ID to delete a profile.');
    }
});

//ONE listener outside fetchNames() (run this once on page load):
nameListEl.addEventListener('click', (event) => {
    
    if (event.target.classList.contains('delete-btn')) {
        const userId = event.target.dataset.id;
        deleteName(userId);
    }
});

function validateName(name) {
    const isValidInput = (str) => /^[a-zA-Z]+(?:[ -][a-zA-Z]+)*$/.test(str);
    return isValidInput(name);
}

function submitFullName() {
    const fName = firstNameInput.value.trim();
    const lName = lastNameInput.value.trim();

    if(validateName(fName) && validateName(lName)) {
        postFullName(fName, lName);
    }
    else{
        alert('Please enter valid first and last names. Only letters, spaces, and hyphens are allowed.');
    }

}

async function deleteName(userId) {
    const requestOptions = {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' }
    }

    const deleteUrl = `${apiUrl}/${userId}`;
    try{
        const response = await fetch(deleteUrl, requestOptions);
        if(response.ok){
            alert('Name deleted successfully!');
            fetchNames(); // Refresh the list after deletion
        }
        else{
            alert('Error deleting name. Please try again.');
        }
    }
    catch (error) {
        console.error('Error deleting name:', error);
        alert('Error deleting name. Please try again.');
    }   
}

async function fetchNames() {

    const requestOptions = {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    };

    try {
        const  response = await fetch(apiUrl, requestOptions);
        if(response.ok) {
            nameListEl.innerHTML = '';
            const data = await response.json();
            /**
                for(const user of data) {
                const listItem = document.createElement('li');
                listItem.textContent = `${user.firstName} ${user.lastName}`;
                const deleteItem = document.createElement('button');
                 // This closure safely remembers 'user.id' forever
                deleteItem.addEventListener('click', () => deleteName(user.id));
                deleteItem.textContent = 'Delete';
                listItem.appendChild(deleteItem);
                nameListEl.appendChild(listItem);
            }
             */


            // Alternative: Event Delegation (Better Performance)
            /**
             * Instead of adding dozens of individual listeners to every single button, you can add one 
             * single listener to the parent container (nameListEl). This is highly efficient and 
             * automatically handles any dynamically added elements.
             */
            // To do this, embed the ID directly into the HTML using a data-* attribute
            for(const user of data) {
                const listItem = document.createElement('li');
                listItem.innerHTML = `
                ${user.firstName} ${user.lastName}
                <button class="delete-btn" data-id="${user.id}">Delete</button>
                `;
                nameListEl.appendChild(listItem);
            } 

    }
    }catch (error) {
        console.error('Error fetching names:', error);
        alert('Error fetching names. Please try again.');
    }
}

async function getProfile(userId) {
    const requestOptions = {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    };

     // Explicitly parse input into a number clean base 10 to match Java Long type
    const cleanId = parseInt(userId, 10);
    if (isNaN(cleanId)) {
        alert('Please enter a valid numeric ID.');
        return;
    }

    const getUrl = `${apiUrl}/${cleanId}`;
    try{
        const response = await fetch(getUrl, requestOptions);
        if(response.ok) {
            const data = await response.json();

            // Clear the list before displaying the fetched profile
            // Safely clear children without corrupting the parent HTML element node structure
            while(nameListEl.firstChild) {
                nameListEl.removeChild(nameListEl.firstChild);
            }

            // standard innerHTML rendering which forces browser redrawing reliably
            const listItem = document.createElement('li');
            listItem.innerHTML = `
                ${data.firstName} ${data.lastName}
                <button class="delete-btn" data-id="${data.id}">Delete</button>
            `;

            
            nameListEl.appendChild(listItem);
        }else{
            alert('Error fetching profile. Please check the ID and try again.');
        }
        

    }catch (error) {
        console.error('Error fetching profile:', error);
        alert('Error fetching profile. Please try again.');
    }
}


async function postFullName(firstName, lastName) {

    const registerBody = {
        firstName: firstName,
        lastName: lastName
    };

    const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(registerBody)
    };

    try {
        const response = await fetch(apiUrl, requestOptions);
        if (response.ok) {
            const data = await response.json();
            console.log('Response from server:', data);
            alert('Name submitted successfully!');

            // Clear the input fields
            firstNameInput.value = '';
            lastNameInput.value = '';
            fetchNames(); // Refresh the list after submission
        }
    }
    catch (error) {
        console.error('Error submitting name:', error);
        alert('Error submitting name. Please try again.');
    }

}

fetchNames(); // Initial fetch to populate the list on page load