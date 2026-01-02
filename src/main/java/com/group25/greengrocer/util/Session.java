package com.group25.greengrocer.util;

import com.group25.greengrocer.model.User;

/**
 * Session management utility class for storing and managing the current logged-in user.
 * 
 * This class provides a simple singleton-like pattern to store the current user session
 * throughout the application lifecycle. It allows controllers and other components to
 * access the currently authenticated user without passing it as a parameter.
 * 
 * Usage:
 * The session should be set after successful login and cleared upon logout.
 * The current user can be retrieved from anywhere in the application using getCurrentUser().
 * 
 * Thread Safety:
 * This class uses static fields and is not thread-safe. It is intended for single-threaded
 * JavaFX applications.
 */
public class Session {
    /**
     * The current logged-in user. Null if no user is logged in.
     */
    private static User currentUser;

    /**
     * Sets the current user session.
     * 
     * This method should be called after a successful login to establish
     * the user session for the application.
     * 
     * @param user The User object representing the logged-in user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Gets the current user session.
     * 
     * Returns the User object that was set via setCurrentUser().
     * Returns null if no user is currently logged in or if the session has been cleared.
     * 
     * @return The current User object, or null if no user is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Clears the current user session.
     * 
     * This method should be called when the user logs out to ensure
     * no user data remains in memory.
     */
    public static void clear() {
        currentUser = null;
    }
}
