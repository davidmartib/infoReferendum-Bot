package cat.referendum;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;

public class Bot extends TelegramLongPollingBot {

    private static final Logger log = LogManager.getLogger(Bot.class);

    public static Boolean botIniciat = false;

    public static String nomBot = "";
    public static String tokenBot = "";
    private static BotSession sessioDelBot = null;

    public static Boolean iniciar() {
        Boolean resultat = botIniciat;

        try {
            if (!botIniciat) {
                ApiContextInitializer.init();
                TelegramBotsApi botsApi = new TelegramBotsApi();
                try {
                    sessioDelBot = botsApi.registerBot(new Bot());
                    resultat = (sessioDelBot != null);
                } catch (TelegramApiException e) {
                    log.error(String.format("ReferendumBot.iniciar.Excepcio %s", e.getMessage()));
                }
                botIniciat = resultat;
            }
        } catch (Exception e) {
            log.error(String.format("ReferendumBot.iniciar.Outer.Excepcio %s", e.getMessage()));
        }

        return resultat;
    }

    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            Message m = update.getMessage();

            User u = m.getFrom();

            Chat c = update.getMessage().getChat();

            String textRebut = update.getMessage().getText();
            String resposta = String.format("No he entés: %s", textRebut);

            if (textRebut.toUpperCase().equals("/START"))
                resposta = "Benvingut!\nIntentaré ajudar-te a localitzar el teu centre de votació pel referendum de l'1 d'Octubre de 2017.\nHas de posar el teu *DNI*, *Data de naixement* en format dia/mes/any i el *codi postal* del lloc on estàs empadronat, separat amb un espai en blanc.\n\nExemple: 43445443X 12/1/1984 08080";
            else resposta = Utils.revisarDades(textRebut);

            SendMessage missatge = new SendMessage();
            missatge.setChatId(update.getMessage().getChatId());
            missatge.setText(resposta);
            missatge.setParseMode("Markdown");

            try {
                sendMessage(missatge);
                log.info(String.format("MISSATGE rebut=[%s]", textRebut));
            } catch (Exception e) {
                log.error(String.format("TELEGRAM UpdateReceived.SendMessage.Excepcio %s", e.getMessage()));
            }
        }

    }

    public String getBotUsername() {
        return nomBot;
    }

    public String getBotToken() {
        return tokenBot;
    }

}
