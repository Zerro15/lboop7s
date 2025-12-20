package com.lab7;

import com.lab7.util.DatabaseInitializer;

/**
 * Lightweight bootstrap helper for the LB7 backend components when running inside the
 * existing servlet container. The initialization is intentionally minimal because
 * servlet registrations are handled via annotations in this project.
 */
public final class Lab7Application {
    private Lab7Application() {
    }

    /**
     * Initializes database tables for the LB7 backend if they are missing. Can be called
     * from a ServletContextListener or manually during integration tests.
     */
    public static void initialize() {
        DatabaseInitializer.ensureReady();
    }
}
