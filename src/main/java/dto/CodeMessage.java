package dto;

import enums.MessageType;
import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Data
public class CodeMessage {
    private SendMessage sendMessage;
    private EditMessageText editMessageText;
    private MessageType type;
}
