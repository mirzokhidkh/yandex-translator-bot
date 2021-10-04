package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vdurmont.emoji.EmojiParser;
import dto.CodeMessage;
import lombok.Data;
import model.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import static enums.MessageType.EDIT;
import static enums.MessageType.MESSAGE;
import static util.InlineButton.*;
import static util.InlineButton.inlineKeyboardButton;
import static util.KeyboardButtons.*;

@Data
public class TranslatorController {
    private Map<Long, String> langsMap = new HashMap<>();
    static final String KEY = "dict.1.1.20211002T091939Z.2e4a88e184b9a2a3.e25fb1fc148fb50552c219d440e75afa7e913758";

    public CodeMessage handle(String text, Long chatId, Integer messageId) {
        CodeMessage codeMessage = new CodeMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(chatId));

        if (text.startsWith(EN) || text.startsWith(RU) || text.startsWith(TR)) {
            if (langsMap.containsKey(chatId)) {
                langsMap.remove(chatId);
            }
            editMessageText.setMessageId(messageId);
            switch (text) {
                case EN:
                    editMessageText.setText("Choose one of them: ");
                    editMessageText.setParseMode("Markdown");
                    editMessageText.setReplyMarkup(
                            keyboardMarkup(
                                    rowCollection(
                                            row(
                                                    inlineKeyboardButton(EmojiParser.parseToUnicode(":gb:EN :arrow_right: RU:ru:"), EN_RU),
                                                    inlineKeyboardButton(EmojiParser.parseToUnicode(":gb:EN :arrow_right: TR:tr:"), EN_TR)
                                            ),
                                            row(
                                                    inlineKeyboardButton("Back", MENU, ":back:")
                                            )
                                    )
                            )
                    );
                    codeMessage.setEditMessageText(editMessageText);
                    codeMessage.setType(EDIT);
                    break;
                case RU:
                    editMessageText.setText("Choose one of them: ");
                    editMessageText.setParseMode("Markdown");
                    editMessageText.setReplyMarkup(
                            keyboardMarkup(
                                    rowCollection(
                                            row(
                                                    inlineKeyboardButton(EmojiParser.parseToUnicode(":ru:RU :arrow_right: EN:gb:"), RU_EN),
                                                    inlineKeyboardButton(EmojiParser.parseToUnicode(":ru:RU :arrow_right: TR:tr:"), RU_TR)
                                            ),
                                            row(
                                                    inlineKeyboardButton("Back", MENU, ":back:")
                                            )
                                    )
                            )
                    );
                    codeMessage.setEditMessageText(editMessageText);
                    codeMessage.setType(EDIT);
                    break;
                case TR:
                    editMessageText.setText("Choose one of them: ");
                    editMessageText.setParseMode("Markdown");
                    editMessageText.setReplyMarkup(
                            keyboardMarkup(
                                    rowCollection(
                                            row(
                                                    inlineKeyboardButton(EmojiParser.parseToUnicode(":tr:TR :arrow_right: EN:gb:"), TR_EN),
                                                    inlineKeyboardButton(EmojiParser.parseToUnicode(":tr:TR :arrow_right: RU:ru:"), TR_RU)
                                            ),
                                            row(
                                                    inlineKeyboardButton("Back", MENU, ":back:")
                                            )
                                    )
                            )
                    );
                    codeMessage.setEditMessageText(editMessageText);
                    codeMessage.setType(EDIT);
                    break;
                case EN_RU:
                case EN_TR:
                case RU_EN:
                case RU_TR:
                case TR_EN:
                case TR_RU:
                    editMessageText.setText("Enter word : ");
                    langsMap.put(chatId, text.toLowerCase());
                    codeMessage.setEditMessageText(editMessageText);
                    codeMessage.setType(EDIT);
                    break;
                default:
                    sendMessage.setText("This command does not exist");
                    codeMessage.setSendMessage(sendMessage);
            }

            return codeMessage;
        }


        if (langsMap.containsKey(chatId)) {
            String lang = langsMap.get(chatId);
            List<TrItem> translations = translate(lang, text);
            if (translations.size() == 0) {
                sendMessage.setText("Sorry , this word translation does not found");
            } else {
                StringBuilder str = new StringBuilder();
                int i = 0;
                for (TrItem trItem : translations) {
                    str.append("*").append(++i).append(" - translation : ").append("*").append("_").append(trItem.getText()).append("_");
                    str.append("\n");
                    if (trItem.getSyn() != null) {
                        str.append("*").append("Synonym : ").append("*");
                        for (SynItem synItem : trItem.getSyn()) {
                            str.append("_").append(synItem.getText()).append("_")   ;
                        }
                        str.append("\n");
                    }
                    if (trItem.getMean() != null) {
                        str.append("*").append("Meaning : ").append("*");
                        for (MeanItem meanItem : trItem.getMean()) {
                            str.append("_").append(meanItem.getText()).append("_");
                        }
                        str.append("\n");
                    }
                    if (trItem.getEx() != null) {
                        str.append("*").append("Examples : ").append("*");
                        str.append("\n");
                        for (ExItem ex : trItem.getEx()) {
                            str.append("\t\t").append("_").append(ex.getText()).append(" - ");
                            for (TrItem item : ex.getTr()) {
                                str.append(item.getText()).append("_");
                            }
                            str.append("\n");
                        }
                    }
                    str.append("------------------------------------------------------------------------\n");
                }
                sendMessage.setText(String.valueOf(str));
                sendMessage.setParseMode("Markdown");
            }
            sendMessage.setReplyMarkup(
                    getBackBtn(lang.substring(0, 2).toUpperCase())
            );
            codeMessage.setSendMessage(sendMessage);
            codeMessage.setType(MESSAGE);
        }


        return codeMessage;
    }

    private InlineKeyboardMarkup getBackBtn(String lang) {
        return keyboardMarkup(
                rowCollection(
                        row(
                                inlineKeyboardButton("Back", lang, ":back:")
                        )
                )
        );
    }

    public static List<TrItem> translate(String lang, String text) {
        List<TrItem> translations = new ArrayList<>();
        try {
            URL url = new URL("https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=" + KEY + "&lang=" + lang + "&text=" + text);
            URLConnection connection = url.openConnection();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            DicResult dicResult = gson.fromJson(reader, DicResult.class);
            for (DefItem defItem : dicResult.getDef()) {
                for (TrItem trItem : defItem.getTr()) {
                    translations.add(trItem);
//                    System.out.println(trItem.getText());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return translations;
    }

//    public static List<String> translate(String lang, String text) {
//        List<String> translations = new ArrayList<>();
//        try {
//            URL url = new URL("https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=" + KEY + "&lang=" + lang + "&text=" + text);
//            URLConnection connection = url.openConnection();
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
//            DicResult dicResult = gson.fromJson(reader, DicResult.class);
//            for (DefItem defItem : dicResult.getDef()) {
//                for (TrItem trItem : defItem.getTr()) {
//                    translations.add(trItem.getText());
////                    System.out.println(trItem.getText());
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return translations;
//    }
}
