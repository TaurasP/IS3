package lt.viko.eif.tpetrauskas;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.util.Base64;
import java.util.Scanner;

public class App {

    public static void main(String[] args) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {

        // Get message from user
        Scanner input = new Scanner(System.in);
        System.out.println("Iveskite zinutes teksta: ");
        String message = input.nextLine();

        // Generate public and private keys
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom();

        keyPairGenerator.initialize(2048, secureRandom);

        KeyPair pair = keyPairGenerator.generateKeyPair();

        PublicKey publicKey = pair.getPublic();

        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());

        System.out.println("Viesasis raktas: " + publicKeyString);

        PrivateKey privateKey = pair.getPrivate();

        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        System.out.println("Privatus raktas: " + privateKeyString);

        // Encrypt message
        Cipher encryptionCipher = Cipher.getInstance("RSA");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptedMessage =
                encryptionCipher.doFinal(message.getBytes());
        String encryption =
                Base64.getEncoder().encodeToString(encryptedMessage);
        System.out.println("Uzsifruota zinute: " + encryption);

        // Decrypt message
        Cipher decryptionCipher = Cipher.getInstance("RSA");
        decryptionCipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedMessage =
                decryptionCipher.doFinal(encryptedMessage);
        String decryption = new String(decryptedMessage);
        System.out.println("Atsifruota zinute: " + decryption);

        // Save encrypted message and public key to a file
        try {
            File file = new File("file.txt");
            file.createNewFile();
            FileWriter myWriter = new FileWriter(file);
            String dataToFile = encryption + " " + publicKeyString;
            myWriter.write(dataToFile);
            myWriter.flush();
            myWriter.close();
            System.out.println("Uzsifruota zinute ir viesasis raktas sekmingai issaugoti faile " + file.getName() + ": " + dataToFile);
        } catch (IOException e) {
            System.out.println("Klaida.");
            e.printStackTrace();
        }

        // Read encrypted message and public key from a file
        String dataFromFile = "";
        try {
            File myObj = new File("file.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                dataFromFile = myReader.nextLine();
                System.out.println("Uzsifruota zinute ir viesasis raktas sekmingai nuskaityti is " + myObj.getName() +  ": " + dataFromFile);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Klaida.");
            e.printStackTrace();
        }

        // Split message and public key from string from file
        String[] dataArrayFromFile = dataFromFile.split(" ", 2);
        String encryptedMessageFromFile = dataArrayFromFile[0];
        String encryptedPublicKeyFromFile = dataArrayFromFile[1];

        System.out.println("Uzsifruota zinute is failo: " + encryptedMessageFromFile);
        System.out.println("Uzsifruotas viesasis raktas is failo: " + encryptedPublicKeyFromFile);

        // Decrypt encrypted message
        decryptionCipher = Cipher.getInstance("RSA");
        decryptionCipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedMessageFromFile =
                decryptionCipher.doFinal(Base64.getDecoder().decode(encryptedMessageFromFile));
        String decryptionFromFile = new String(decryptedMessageFromFile);
        System.out.println("Atsifruota zinute is failo: " + decryptionFromFile);
    }
}
