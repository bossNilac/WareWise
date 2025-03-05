package com.warewise.common.encryption;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Encrypt {

    private static final int ITERATIONS = 4;
    private static final int MEMORY = 262144; // 256 MB
    private static final int PARALLELISM = 4;

    private static final String pepper = System.getenv("PEPPER_KEY");


    // Hash the password (during registration)
    public static String hashPassword(String password) {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2d);

        try {
            return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, pepper+password);
        } finally {
            argon2.wipeArray(password.toCharArray());
        }
    }

    // Verify the password (during login)
    public static boolean verifyPassword(String storedHash, String inputPassword) {
        Argon2 argon2 = Argon2Factory.create((Argon2Factory.Argon2Types.ARGON2d));
        return argon2.verify(storedHash, pepper+inputPassword);
    }

    public static void main(String[] args) {
        System.out.println(hashPassword("calin123"));
    }

}
