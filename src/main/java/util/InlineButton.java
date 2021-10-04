package util;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.List;

public class InlineButton {
    public static InlineKeyboardButton inlineKeyboardButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(callbackData);
        return button;
    }

    public static InlineKeyboardButton inlineKeyboardButton(String text, String callbackData, String emoji) {
        String emojiText = EmojiParser.parseToUnicode(emoji + " " + text);
        InlineKeyboardButton button = new InlineKeyboardButton(emojiText);
        button.setCallbackData(callbackData);
        return button;
    }

    public static List<InlineKeyboardButton> row(InlineKeyboardButton... buttons) {
        return Arrays.asList(buttons);
    }

    public static List<List<InlineKeyboardButton>> rowCollection(List<InlineKeyboardButton>... rows) {
        return Arrays.asList(rows);
    }

    public static InlineKeyboardMarkup keyboardMarkup(List<List<InlineKeyboardButton>> rowCollection) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(rowCollection);
        return keyboardMarkup;
    }

}
