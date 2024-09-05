package org.neocoder;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {

        //Generate method test
        TokenService tkn = new TokenService("clavesecreta123", "miAplicacion", 3600);

            String token = tkn.generate("userEjemplo", Arrays.asList("admin", "user"));
        System.out.println("Token: " + token);

        //AuthService test
        String pass = "hola1234";
        String hashedpass = AuthService.hashPassword(pass);

        System.out.println("Pass: " + pass + " Hashedpass: "+ hashedpass);

        System.out.println("Verify pass: "+ AuthService.comparePassword(pass, hashedpass));

    }



}