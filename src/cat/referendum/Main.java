package cat.referendum;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Utils.carregarConfiguracio();
        if (Bot.iniciar()) {
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}
