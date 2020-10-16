package com.lordjoe.ssh;

/**
 * This class is used to save and load accounts values (mail-private key) from a file as properties
 * Values are stored as username,,,,,privateKey (change this description if this changes) and are splited accordingly in return fo methods
 * com.lordjoe.ssh.AccountsMailKey
 * User: Simone
 * Date: 09/07/2020
 * com.lordjoe.ssh.AccountsData
 */

import com.lordjoe.utilities.Encrypt;

import java.io.*;
import java.util.*;

public class SSHUserData {

    public static final String DELIMITER = ",";

    public static final String ENCRYPTED_NAME = "/opt/blastserver/EncryptedProperties.txt";

    private static Properties accountsTableX = new Properties();
    private static Map<String, SSHUserData> accountsByEmail = new HashMap<>();
    private static Map<String, SSHUserData> accounts = new HashMap<>();

    public static String PRIVATE_KEY1_FILE1 = "/home/Steve/.ssh/HPC.ppk";// "private.ppk"; //id_rsa_list.ppk";
    public static final String PUBLIC_KEY1_FILE1 = "/home/Steve/.ssh/HPC.pub";// "public.pub";




    //Get the private key using mail as properties index
    //if the email is unknown return empty string (function containsMail shall be used before getting the key)
    public static String getPrivateKey(String email){
        guaranteeUsers();
        if (accounts.containsKey(email)){
            return accounts.get(email).getClearPrivateKey();
        }else
            return ("");
    }

    //Get the username to login into the HPC using mail as properties index
    //if the email is unknown return empty string (function containsMail shall be used before getting the key)
    public static String getUserName(String email){
        guaranteeUsers();
        if (accounts.containsKey(email)){
            SSHUserData accountsData = accounts.get(email);
            return accountsData.userName;
        }else
            return ("");
    }

    //Return true if the email is known
    public static boolean containsMail(String email){
        guaranteeUsers();
        if (accounts.containsKey(email)){
            return true;
        }else
            return false;
    }

    protected static void guaranteeUsers() {
        if(accounts.isEmpty())   {

            // saving the properties in file
            //accountsTable.setProperty("lordjoe2000@gmail.com", PRIVATE_KEY);
            //System.out.println("Properties has been set in HashTable: " + accountsTable);
            //saveProperties(accountsTable);
            //System.out.println("Properties has been saved in: " + accountsTable);

            // loading the saved properties
            loadEncryptedFile(ENCRYPTED_NAME,accountsTableX,accounts)  ;
                /*
                loadProperties(ENCRYPTED_NAME,accountsTableX);

                for (String o : accountsTableX.stringPropertyNames()) {
                    String email = o;
                    String value = accountsTableX.getProperty(o);
                    String[] items = value.split(DELIMITER);
                    //        AccountsData ac = new AccountsData(email,items[0],Encrypt.encryptString(items[1]));
                    AccountsData ac = new AccountsData(email,items[0], items[1]);
                    accounts.put(email,ac);
                }

                 */

        }
    }

    private static void loadEncryptedFile(String file,Properties temp,Map<String, SSHUserData> decrypted) {
        try {
            decrypted.clear();
            loadProperties(file,temp);

            for (String o : temp.stringPropertyNames()) {
                String name = o;
                String value = temp.getProperty(o);
                String[] items = value.split(DELIMITER);
                //        AccountsData ac = new AccountsData(email,items[0],Encrypt.encryptString(items[1]));
                SSHUserData ac = new SSHUserData(name,items[0], items[1]);
                decrypted.put(name,ac);
                accounts.put(ac.email,ac);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }

    private static void loadUnEncryptedFile(String file,Properties temp,Map<String, SSHUserData> decrypted) {
        try {
            decrypted.clear();
            loadProperties(file,temp);

            for (String o : temp.stringPropertyNames()) {
                String name = o;
                String value = temp.getProperty(o);
                String[] items = value.split(DELIMITER);
                String encryptedPublicKey = encryptAsNeeded(items[1]) ;
                SSHUserData ac = new SSHUserData(name,items[0], encryptedPublicKey);
                decrypted.put(name,ac);
                accounts.put(ac.email,ac);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }



    //Return and array of strings with all emails for which a private key is present
    public static String[] getAllUsersMails(){
        guaranteeUsers();
        List<String>  emails = new ArrayList<>();
        for (String s : accounts.keySet()) {
            SSHUserData accountsData = accounts.get(s);
            emails.add(accountsData.email);
        }
        Collections.sort(emails);
        String[] strings = emails.toArray(new String[] {});
        return strings;
    }



    static void loadProperties(String FileName,Properties p) throws IOException {
        File file=new File(FileName);
        FileInputStream fi = new FileInputStream(file);
        p.load(fi);
        fi.close();
        //System.out.println("After Loading properties: " + p);
    }

    public final String userName;
    public final String email;
    public final String publicKeyFile;
    public final String privateKeyFile;
    public final String passphrase;

    public SSHUserData(String userName, String email, String encryptedPublicKey) {
        this.userName = userName;
        this.email = email;
        publicKey = decryptAsNeeded(encryptedPublicKey);
        this.encryptedPublicKey = encryptAsNeeded(encryptedPublicKey);;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SSHUserData that = (SSHUserData) o;
        return Objects.equals(userName, that.userName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(encryptedPublicKey, that.encryptedPublicKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, email, encryptedPublicKey);
    }

    public String getClearPrivateKey()
    {
        return Encrypt.decryptString(encryptedPublicKey);
    }

    public String asTextString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(email);
        sb.append("=");
        sb.append(userName);
        sb.append(DELIMITER);
        sb.append(privateKeyFile);
        sb.append(DELIMITER);
        sb.append(publicKeyFile);
        if(passphrase != null) {
            sb.append(DELIMITER);
            sb.append(passphrase);

        }


        return sb.toString();
    }

    protected static void loadUsers(String name)
    {
        loadUsers(name,accountsTableX,accounts);

    }

    protected static void loadUsers(String name,Properties p1,Map<String, SSHUserData> users) {
        try {
            // saving the properties in file
            //accountsTable.setProperty("lordjoe2000@gmail.com", PRIVATE_KEY);
            //System.out.println("Properties has been set in HashTable: " + accountsTable);
            //saveProperties(accountsTable);
            //System.out.println("Properties has been saved in: " + accountsTable);

            // loading the saved properties
            loadProperties(name,p1);

            for (String o : p1.stringPropertyNames()) {
                String email = o;
                String value = p1.getProperty(o);
                String[] items = value.split(DELIMITER);
                SSHUserData ac = new SSHUserData(email,items[0],items[1]);
                //      AccountsData ac = new AccountsData(email,items[0], items[1]);
                users.put(email,ac);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }



    public static void usage(String[] args)  {
        System.out.println("usage <filename> create open file from encrypted at /opt/blastserver ");
        System.out.println("usage <filename> <outfile> create ebecrypted file from open file");
    }



    public static void main(String[] args) throws IOException {

        System.out.println("Working Directory = " + System.getProperty("user.dir"));


        System.out.println("Steven username is "+getUserName("lordjoe2000@gmail.com"));
        System.out.println("Steven Private key is "+getPrivateKey("lordjoe2000@gmail.com"));
        System.out.println("Simone username is "+getUserName("simone.zorzan@list.lu"));
        System.out.println("Simone Private key is "+getPrivateKey("simone.zorzan@list.lu"));
    }

}
