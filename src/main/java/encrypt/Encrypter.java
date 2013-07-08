package encrypt;

import org.jasypt.util.text.BasicTextEncryptor;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 1/7/13
 * Time: 09:40
 * To change this template use File | Settings | File Templates.
 *
 * Basic class use for encryption.
 */
public class Encrypter {

    private Preferences prefs;
    private BasicTextEncryptor textEncryptor;

    public Encrypter() {
        this.textEncryptor = new BasicTextEncryptor();
        prefs = Preferences.userNodeForPackage(Encrypter.class);
        String key = prefs.get("key", "");
        if(key.length()==0) {
            throw new RuntimeException("Please set key for encryption executing this class's main.");
        }
        textEncryptor.setPassword(key);
    }


    public String encrypt(String toEncrypt) {
        //try {
            return textEncryptor.encrypt(toEncrypt);
            //return URLEncoder.encode(toEncrypt,"UTF-8");
        //} catch (UnsupportedEncodingException e) {
        //    throw new RuntimeException(e);
        //}
    }

    public String decrypt(String encrypted) {
        //try {
            return textEncryptor.decrypt(encrypted);
            //return URLDecoder.decode(encrypted,"UTF-8");
        //} catch (UnsupportedEncodingException e) {
        //    throw new RuntimeException(e);
        //}
    }

    public static void main(String[] args) throws BackingStoreException {
        Preferences pref = Preferences.userNodeForPackage(Encrypter.class);
        pref.put("key",args[0]);
        pref.flush();
        System.out.println("Key set with argument "+args[0]);
    }


}
