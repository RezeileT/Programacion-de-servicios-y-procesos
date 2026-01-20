package edu.thepower.u5seguridad;

import org.apache.commons.io.IOUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.logging.Logger;

public class U5E05CifradoAsimetricoRSAServidor {
    private static final Logger LOG = Logger.getLogger(U5E05CifradoAsimetricoRSACliente.class.getName());
    private static final String NOMBRE_CERTIFICADO = "resources/servidor.jks";
    private static final String ARCHIVO_CIFRADO = "resources/salida.bin";
    private static char[] STORE_PASS = "changeit".toCharArray();
    private static String KEY_ALIAS = "servidor";
    private static char[] KEY_PASS = "servidor".toCharArray();

    public static void main(String[] args) {
        //Acceder al almacén de claves
        try{
            KeyStore ks = KeyStore.getInstance("JKS");
            try(FileInputStream fis = new FileInputStream(NOMBRE_CERTIFICADO)){
                ks.load(fis, STORE_PASS);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
                throw new RuntimeException(e);
            }
            LOG.info("Accedido al almacén de claves");

            //Obtener clave privada
            Key key = ks.getKey(KEY_ALIAS, STORE_PASS);
            if (!(key instanceof PrivateKey)){
                throw new IllegalStateException("La clave recuperada no es una clave privada");
            }
            key = (PrivateKey)key;
            LOG.info("Clave recuperada");

            //Recuperamos mensaje cifrado en disco
            byte[] textoCifrado;
            try (FileInputStream fis = new FileInputStream(ARCHIVO_CIFRADO)){
                textoCifrado = fis.readAllBytes();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            LOG.info("Mensaje leído");

            //Recibimos el mensaje
            Cipher cipher = Cipher.getInstance("RSA/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] textoPlano = cipher.doFinal(textoCifrado);
            LOG.info("Mensaje descifrado");

            //Mostramos el mensaje
            String texto = new String(textoPlano);
            System.out.println(texto);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


}
