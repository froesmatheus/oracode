package matheusfroes.oracode.utils;

/**
 * Created by mathe on 03/04/2016.
 */
public class Utils {

    public static String formatOracode(String oracode) {
        int n = Integer.parseInt(oracode);
        int ind = 5 - String.valueOf(n).length();

        String def = "ORA-";
        String fin = "";
        for (int i = 0; i < ind; i++) {
            fin += "0";
        }

        fin += n;

        def += fin;

        return def;
    }
}
