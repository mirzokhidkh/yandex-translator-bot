package controller;

import dto.CodeMessage;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static util.KeyboardButtons.*;

public class MainController extends TelegramLongPollingBot {
    private final GeneralController generalController;
    private final TranslatorController translatorController;

    public MainController() {
        this.generalController = new GeneralController();
        this.translatorController = new TranslatorController();
    }


    @Override
    public String getBotUsername() {
        return "yandex_translator";
    }

    @Override
    public String getBotToken() {
        return "2002941124:AAF_IAO77dbJzI1BcSFasEBclTaso058Dmg";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Message message = update.getMessage();
            SendMessage sendMessage = new SendMessage();
            if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String data = callbackQuery.getData();
                message = callbackQuery.getMessage();
                Long chatId = message.getChatId();
                Integer messageId = message.getMessageId();

                EditMessageText editMessageText = new EditMessageText();

                switch (data) {
                    case EN:
                    case RU:
                    case TR:
                    case EN_RU:
                    case EN_TR:
                        sendMsg(this.translatorController.handle(data, chatId, messageId));
                        break;
                    case MENU:
                        sendMsg(this.generalController.handle(data, chatId, messageId));
                        break;
                    default:
                        editMessageText.setText("This command does not exist");
                }

            } else {
                Long chatId = update.getMessage().getChatId();
                String text = message.getText();
                Integer messageId = message.getMessageId();

                if (text.equals("/start") || text.equals("/help") || text.equals("/settings")) {
                    sendMsg(this.generalController.handle(text, chatId, messageId));
                } else if (this.translatorController.getLangMap().containsKey(chatId)) {
                    sendMsg(this.translatorController.handle(text, chatId, messageId));
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(CodeMessage codeMessage) {
        try {
            switch (codeMessage.getType()) {
                case MESSAGE:
                    execute(codeMessage.getSendMessage());
                    break;
                case EDIT:
                    execute(codeMessage.getEditMessageText());
                    break;
                default:
                    break;
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
