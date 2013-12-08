package com.hustaty.homeautomation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

//import javax.crypto.*;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectOutputStream;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;

/**
 * User: llisivko
 * Date: 2/24/13
 * Time: 7:20 PM
 */
public class HeatingFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.heating_fragment, container, false);

        //HACK for devices without MENU button
        Button settingsButton = (Button)view.findViewById(R.id.settingsLaunch);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

//        SecretKey key = null;
//        try {
//            key = KeyGenerator.getInstance("AES").generateKey();
//            writeToFile("secretkey.dat", key);
//            Cipher cipher = Cipher.getInstance("AES");
//            cipher.init(Cipher.ENCRYPT_MODE, key);
//            javax.crypto.SealedObject sealedObject = new SealedObject("message", cipher);
//            writeToFile("sealed.dat", sealedObject);
//
//
//            String algorithmName = sealedObject.getAlgorithm();
//            Cipher cipher2 = Cipher.getInstance(algorithmName);
//            cipher.init(Cipher.DECRYPT_MODE, key);
//            String text = (String) sealedObject.getObject(cipher);
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

        return view;
    }

//    private static void writeToFile(String filename, Object object) throws Exception {
//        FileOutputStream fos = null;
//        ObjectOutputStream oos = null;
//
//        try {
//            fos = new FileOutputStream(new File(filename));
//            oos = new ObjectOutputStream(fos);
//            oos.writeObject(object);
//            oos.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (oos != null) {
//                oos.close();
//            }
//            if (fos != null) {
//                fos.close();
//            }
//        }
//    }
}
