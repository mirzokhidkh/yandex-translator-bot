package controller;

import dto.CodeMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import static enums.MessageType.EDIT;
import static enums.MessageType.MESSAGE;
import static util.InlineButton.*;
import static util.KeyboardButtons.*;

public class GeneralController {
    public CodeMessage handle(String text, Long chatId, Integer messageId) {
        CodeMessage codeMessage = new CodeMessage();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        switch (text) {
            case "/start":
                sendMessage.setText("Welcome to *yandex-translator* bot");
                sendMessage.setParseMode("Markdown");
                sendMessage.setReplyMarkup(
                        keyboardMarkup(
                                rowCollection(
                                        row(
                                                inlineKeyboardButton("go to Menu",MENU)
                                        )
                                )
                        )
                );
                codeMessage.setSendMessage(sendMessage);
                codeMessage.setType(MESSAGE);
                break;
            case "/help":
                sendMessage.setText("HELP");
                break;
            case "/settings":
                sendMessage.setText("SETTINGS");
                break;
            case MENU:
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(String.valueOf(chatId));
                editMessageText.setMessageId(messageId);
                editMessageText.setText("Choose one of the following languages depending on which language you enter your word.");
                editMessageText.setParseMode("Markdown");
                editMessageText.setReplyMarkup(
                        keyboardMarkup(
                                rowCollection(
                                        row(
                                                inlineKeyboardButton("Russian", RU, ":ru:"),
                                                inlineKeyboardButton("English", EN, ":gb:"),
                                                inlineKeyboardButton("Turkish", TR, ":tr:")
                                        )
                                )
                        )
                );
                codeMessage.setEditMessageText(editMessageText);
                codeMessage.setType(EDIT);
                break;
            default:
                sendMessage.setText("This command does not exist");
        }


        return codeMessage;
    }
}
