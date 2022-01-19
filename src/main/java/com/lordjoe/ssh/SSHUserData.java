package com.lordjoe.ssh;

/**
 * This class is used to save and load accounts values (mail-private key) from a file as properties
 * Values are stored as username,,,,,privateKey (change this description if this changes) and are splited accordingly in return fo methods
 * com.lordjoe.ssh.AccountsMailKey
 * User: Simone
 * Date: 09/07/2020
 * com.lordjoe.ssh.AccountsData
 */

import com.lordjoe.utilities.FileUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class SSHUserData {
    public static final Random RND = new Random();
    public static final String DELIMITER = ",";

    public static final String KEYS_DIRECTORY = "/opt/blastserver/keys";
    public static final String USERS_FILE = "users.txt";

    private static Properties accountsTableX = new Properties();

    private static Map<String, SSHUserData> accounts = new HashMap<>();





    //Get the username to login into the HPC using mail as properties index
    //if the email is unknown return empty string (function containsMail shall be used before getting the key)
    public static SSHUserData getUser(String email){
        guaranteeUsers();
        return accounts.get(email);
    }

    //Get the username to login into the HPC using mail as properties index
    //if the email is unknown return empty string (function containsMail shall be used before getting the key)
    public static String getUserName(String email){
        SSHUserData user = getUser(  email);
        if(user != null)
            return user.userName;
        else
            return  "";
    }



    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
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
       String dirX = KEYS_DIRECTORY;
       if(isWindows())
           dirX = "C:" + dirX;
        if(accounts.isEmpty())   {
            File dir = new File(dirX);
            File users = new File(dir,USERS_FILE);
            loadUsers(users);

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



    static void loadProperties(File file,Properties p) throws IOException {
           FileInputStream fi = new FileInputStream(file);
        p.load(fi);
        fi.close();
        //System.out.println("After Loading properties: " + p);
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

    public SSHUserData(String userName, String email, String privateKeyFile,String publicKeyFile ) {
        this(userName,   email,   privateKeyFile,  publicKeyFile,null);
    }

    public SSHUserData(String userName, String email, String privateKeyFile,String publicKeyFile,String passphrase) {
        this.userName = userName;
        this.email = email;
        this.publicKeyFile = publicKeyFile;
        this.privateKeyFile = privateKeyFile;
        this.passphrase = passphrase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SSHUserData that = (SSHUserData) o;
        return Objects.equals(userName, that.userName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(publicKeyFile, that.publicKeyFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, email, publicKeyFile);
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

    public File getPrivateKeyFile()
    {
        String workingDir = System.getProperty("user.home");
        String privKeyAbsPath = workingDir + "/.ssh/";
        File dir = new File(privKeyAbsPath);
  //      File dir = new File(KEYS_DIRECTORY);
        File ret = new File(dir,privateKeyFile);
        if(!ret.exists())
            throw new IllegalStateException("Cannot find file private key file " + ret.getAbsolutePath());
        return ret;
    }

    public File getPubliceKeyFile()
    {
        String workingDir = System.getProperty("user.home");
        String privKeyAbsPath = workingDir + "/.ssh/";
        File dir = new File(privKeyAbsPath);
//        File dir = new File(KEYS_DIRECTORY);
        File ret = new File(dir,publicKeyFile);
        if(!ret.exists())
            throw new IllegalStateException("Cannot find file public key file " + ret.getAbsolutePath());
        return ret;
    }
    public String getPassPhrase()
    {
        if(passphrase == null)
            return null;
        if(true)
            return passphrase;
        File dir = new File(KEYS_DIRECTORY);
        File ret = new File(dir,passphrase);
        if(!ret.exists())
            throw new IllegalStateException("Cannot find passphrase file  " + ret.getAbsolutePath());
        return FileUtilities.readInFile(ret);
    }

    protected static void loadUsers(File name)
    {
        loadUsers(name, accounts);

    }

    protected static void loadUsers(File name,Map<String, SSHUserData> users) {
        try {
            Properties p1 = new Properties();
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
                SSHUserData ac;
                if(items.length == 4)
                    ac=  new SSHUserData(items[0],email,items[1],items[2],items[3]);
                else
                    ac=  new SSHUserData( items[0],email,items[1],items[2]);
                  users.put(email,ac);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }

    public String toString() {
        return userName;
    }

    public static SSHUserData getRandomUser()
    {
        String[] allUsersMails = SSHUserData.getAllUsersMails();
        String email = allUsersMails[RND.nextInt(allUsersMails.length)];
        SSHUserData user = SSHUserData.getUser(email);
        return user;
    }

    public static void usage(String[] args)  {
        System.out.println("usage <filename> create open file from encrypted at /opt/blastserver ");
        System.out.println("usage <filename> <outfile> create ebecrypted file from open file");
    }



    public static void main(String[] args) throws IOException {

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        guaranteeUsers();
        String[] allUsersMails = getAllUsersMails();
        for (int i = 0; i < allUsersMails.length; i++) {
            String allUsersMail = allUsersMails[i];
            SSHUserData user = getUser(allUsersMail);
            System.out.println(user.asTextString());
        }


    }

}
