package com.serotonin.mango.db;

public class DatabaseAccessUtils {
    public String decrypt(String input) {
        return input;
    }

    //  private static final String ALGORITHM = "Blowfish";

    //    public String decrypt(String input) {
    //        StringEncrypter se = new StringEncrypter("TR58yrqPswXJubYGiRdARw==", ALGORITHM);
    //        return se.decodeToString(input);
    //    }
    //
    //    public static void main(String[] args) {
    //        //        // Generate a key
    //        //        String key = StringEncrypter.generateKey(ALGORITHM);
    //        //        System.out.println(key);
    //
    //        //
    //        //        // Encrypt a password with it.
    //        //        StringEncrypter se = new StringEncrypter(key, ALGORITHM);
    //        //        System.out.println(se.encodeString("mangoPassword"));
    //
    //        // Decrypt
    //        System.out.println(new DatabaseAccessUtils().decrypt("bWFuZ29QYXNzd29yZA=="));
    //    }
}
