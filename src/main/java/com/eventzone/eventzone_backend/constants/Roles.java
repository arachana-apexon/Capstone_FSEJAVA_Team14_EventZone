package com.eventzone.eventzone_backend.constants;

/**
 * Utility class containing role constants for the application.
 * This class cannot be instantiated.
 */
public final class Roles {

    public static final String ATTENDEE = "ATTENDEE";
    public static final String ORGANISER = "ORGANISER";
    public static final String ADMIN = "ADMIN";

    /**
     * Private constructor to prevent instantiation.
     */
    private Roles() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
