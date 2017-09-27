package cat.referendum;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

public class Utils {

    private static final Logger log = LogManager.getLogger("referendumBot");

    public static String dirBD = "E:/TMP";

    public static String nodejsCmd = "C:/Program Files/nodejs/node.exe";
    public static String nodejsDecrypt = recursAFitxerTemporal("/Fitxers/decrypt.js");

    public static void carregarConfiguracio() {
        try {
            Properties p = new Properties();
            p.load(new FileInputStream("referendumBot.ini"));
            dirBD = p.getProperty("dirBD", "E:/TMP");
            nodejsCmd = p.getProperty("nodejsCmd", "C:/Program Files/nodejs/node.exe");
            Bot.nomBot = p.getProperty("nomBot");
            Bot.tokenBot = p.getProperty("tokenBot");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("Directori de la base de dades = " + dirBD);
        log.info("Executable node.exe de NodeJS = " + nodejsCmd);
        log.info("Nom del Bot = " + Bot.nomBot);
    }

    public static String executarComandament(String command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public static String recursAFitxerTemporal(String nomRecurs) {
        String resultat = "";
        String dirTemp = System.getProperty("java.io.tmpdir");
        Path dir = Paths.get(dirTemp + "/" + UUID.randomUUID().toString());
        try (InputStream is = Utils.class.getResourceAsStream(nomRecurs)) {
            Files.copy(is, dir);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        resultat = dir.toAbsolutePath().toString();
        return resultat;
    }

    public static String decrypt(String cadena, String password) {
        String resultat = "";
        resultat = executarComandament(nodejsCmd + " " + nodejsDecrypt + " " + cadena + " " + password);
        return resultat;
    }

    public static String revisarDades(String cadena) {
        String resultat = "";
        String[] dades = cadena.split("\\s+");
        if (dades.length == 3) {
            String DNI = dades[0].toUpperCase();
            String dataNaixement = dades[1];
            String codiPostal = dades[2];
            resultat = calcular(DNI, dataNaixement, codiPostal);
        } else
            resultat = "Recorda: *DNI*, *data de naixement* en format dia/mes/any i *codi postal*, SEPARAT per un espai en blanc!";
        return resultat;
    }

    public static String calcular(String dni, String dataNaixement, String codiPostal) {
        String resultat = "*Error al consultar les dades.*\nRevisa que les dades siguin correctes.\nTambé pot passar que el servidor estigui molt enfeinat i la teva consulta no s'hagi pogut processar.\nProva-ho més tard.";
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat df2 = new SimpleDateFormat("yyyyMMdd");
        try {
            Date data = df.parse(dataNaixement);
            String sha1 = sha256(sha256Multiple(dni.substring(dni.length() - 6) + df2.format(data) + codiPostal, 1714));
            String sha2 = sha256(sha1);
            String dir = sha2.substring(0, 2);
            String fitxer = sha2.substring(2, 4);
            String nomFitxer = Paths.get(dirBD, "db", dir, fitxer).toString() + ".db";
            try (BufferedReader br = new BufferedReader(new FileReader(nomFitxer))) {
                String linia;
                while ((linia = br.readLine()) != null) {
                    if (linia.substring(0, 60).equals(sha2.substring(4))) {
                        String r = decrypt(linia.substring(60), sha1);
                        String[] dades = r.split("#");
                        log.info(String.format("IDENTIFICAT amb DNI=%s CodiPostal=%s", dni, codiPostal));
                        resultat = String.format("*Lloc de votació:*\n%s\n%s\n%s\n*Districte:*%s\n*Seccio:*%s\n*Mesa:*%s", dades[0], dades[1], dades[2], dades[3], dades[4], dades[5]);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return resultat;
    }

    public static String sha256Multiple(String cadena, int iteracions) {
        String resultat = cadena;
        for (int i = 0; i < iteracions; i++) resultat = sha256(resultat);
        return resultat;
    }

    public static String sha256(String base) {
        String resultat = "";
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            resultat = hexString.toString();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return resultat;
    }

}
