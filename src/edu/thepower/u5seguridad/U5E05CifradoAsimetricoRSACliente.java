package edu.thepower.u5seguridad;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.logging.Logger;

public class U5E05CifradoAsimetricoRSACliente {
    private static final Logger LOG = Logger.getLogger(U5E05CifradoAsimetricoRSACliente.class.getName());
    private static final String NOMBRE_CERTIFICADO = "resources/servidor.crt";
    private static final String ARCHIVO_CIFRADO = "resources/salida.bin";

    public static void main(String[] args) {
        try {
            //Acceder al certificado en resources
            CertificateFactory cf = CertificateFactory.getInstance("x.509");
            Certificate cert;
            try (FileInputStream fis = new FileInputStream(NOMBRE_CERTIFICADO)) {
                cert = cf.generateCertificate(fis);
            }
            LOG.info("Se ha accedido al certificado");

            //Acceder a la clave publica
            PublicKey pk = cert.getPublicKey();
            LOG.info("Clave publica obtenida");

            //Mensaje a cifrar
            String msg = "Never gonna give you up";
            byte[] textoPlano = msg.getBytes();

            //Cifrado del mensaje
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pk);
            byte[] textoCifrado = cipher.doFinal(textoPlano);
            LOG.info("Texto cifrado");

            //Guardamos en disco
            try(FileOutputStream fos = new FileOutputStream(ARCHIVO_CIFRADO)) {
                fos.write(textoCifrado);
            }
            LOG.info("Mensaje cifrado y guardado");
            System.out.println("El mensaje ha sido cifrado y almacenado");
        } catch (CertificateException | IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
