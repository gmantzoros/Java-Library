import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Serializable;
public class UserData implements Serializable {
	
    public Map<String, User> usersByUsername = new HashMap<>();

    public boolean addUser(User user) {
        if (usersByUsername.containsKey(user.getUsername())) {
            return false; // User already exists with this username
        }
        usersByUsername.put(user.getUsername(), user);
        return true;
    }

    public User getUserByUsername(String username) {
        return usersByUsername.get(username);
    }
    
    //Function for deleting a user
    public void deleteUser(String username, String filename) {
        // Remove user from the map
        usersByUsername.remove(username);
        // Save the updated map to the file
        saveUsersToFile(filename);
    }
    
    // Method to load users from a file and return them as an ObservableList
    @SuppressWarnings("unchecked")
	public ObservableList<User> loadUsers(String filename) {
        ObservableList<User> userList = FXCollections.observableArrayList();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            Object object = in.readObject();
            // Assuming the file contains a Map<String, User>
            if (object instanceof Map) {
                Map<String, User> users = (Map<String, User>) object;
                userList.addAll(users.values());
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing user data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return userList;
    }

    public boolean authenticate(String username, String password) {
        User user = getUserByUsername(username);
        return user != null && user.getPassword().equals(password);
    }
    
    public void saveUsersToFile(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(usersByUsername);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadUsersFromFile(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            Object object = in.readObject();
            if (object instanceof Map) {
                usersByUsername = (Map<String, User>) object;
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing user data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }
}
