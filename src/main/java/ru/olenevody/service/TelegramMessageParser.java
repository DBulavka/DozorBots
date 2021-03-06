package ru.olenevody.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bulavka.Bots.model.Location;

import java.util.Properties;

@Component
public class TelegramMessageParser implements MessageParser {

    @Autowired
    Properties properties;

    @Override
    public boolean isAdminCommand(String text) {
        String formattedText = text.trim().toUpperCase();
        return formattedText.equals(properties.getProperty("telegram.command.me"))
                || formattedText.equals(properties.getProperty("telegram.command.chat"))
                || formattedText.equals(properties.getProperty("telegram.command.chat.id"));
    }

    @Override
    public boolean isGameCommand(String text) {
        return text.trim().startsWith("/") && !isAdminCommand(text);
    }

    @Override
    public boolean isCode(String text) {
        return text.trim().matches("^\\d+$");
    }

    @Override
    public boolean isCoordinates(String text) {
        return text.trim().matches("^[0-9]{2}(\\s|,|\\.)[0-9]*(\\s|,|\\.)*[0-9]{2}(\\s|,|\\.)[0-9]*$");
    }

    @Override
    public TelegramMessageType getMessageType(String text) {
        if (isAdminCommand(text)) {
            return TelegramMessageType.ADMIN_COMMAND;
        } else if (isGameCommand(text)) {
            return TelegramMessageType.GAME_COMMAND;
        } else if (isCode(text)) {
            return TelegramMessageType.CODE;
        } else if (isCoordinates(text)) {
            return TelegramMessageType.LOCATION;
        } else {
            return null;
        }
    }

    @Override
    public Location parseCoordinates(String text) {
        String[] tmp = text.split("(\\s|\\.|,)");
        double longitude = Double.parseDouble(tmp[0].trim() + "." + tmp[1].trim());
        double latitude;
        if (tmp.length == 4) {
            latitude = Double.parseDouble(tmp[2].trim() + "." + tmp[3].trim());
        } else {
            latitude = Double.parseDouble(tmp[3].trim() + "." + tmp[4].trim());
        }
        return new Location(latitude, longitude);
    }
}
