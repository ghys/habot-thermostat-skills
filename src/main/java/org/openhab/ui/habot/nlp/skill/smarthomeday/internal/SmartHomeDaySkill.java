package org.openhab.ui.habot.nlp.skill.smarthomeday.internal;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.openhab.ui.habot.card.Card;
import org.openhab.ui.habot.nlp.Intent;
import org.openhab.ui.habot.nlp.IntentInterpretation;
import org.openhab.ui.habot.nlp.Skill;
import org.openhab.ui.habot.nlp.UnsupportedLanguageException;
import org.osgi.service.component.annotations.Component;

@Component(service = Skill.class, immediate = true)
public class SmartHomeDaySkill implements Skill {

    @Override
    public String getIntentId() {
        return "smart-home-day";
    }

    @Override
    public InputStream getTrainingData(String language) throws UnsupportedLanguageException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Where will we meet?\n");
        stringBuilder.append("Hey HABot, where will you be next week-end?\n");
        stringBuilder.append("What are you doing next Sunday?\n");
        return IOUtils.toInputStream(stringBuilder.toString());
    }

    @Override
    public IntentInterpretation interpret(Intent intent, String language) {
        IntentInterpretation interpretation = new IntentInterpretation();
        interpretation.setAnswer("I'll be at the Smart Home Day in Ludwigsburg, Germany - see you there!! 🤗");

        Card card = new Card("HbCard");
        card.setTitle("Smart Home Day");
        card.setSubtitle("Forum am Schlosspark, Ludwigsburg, DE Sunday, October 21, 2018");

        org.openhab.ui.habot.card.Component carousel = new org.openhab.ui.habot.card.Component("HbCarousel");
        carousel.addConfig("arrows", true);

        org.openhab.ui.habot.card.Component image = new org.openhab.ui.habot.card.Component("HbImage");
        image.addConfig("item", "SmartHomeDay_Logo");
        org.openhab.ui.habot.card.Component image2 = new org.openhab.ui.habot.card.Component("HbImage");
        image2.addConfig("item", "EclipseCon_Logo");
        org.openhab.ui.habot.card.Component image3 = new org.openhab.ui.habot.card.Component("HbImage");
        image3.addConfig("item", "SmartHomeDay_Map");
        carousel.addComponent("slides", image);
        // carousel.addComponent("slides", image2);
        carousel.addComponent("slides", image3);

        card.addComponent("media", carousel);
        card.setEphemeral(true);

        interpretation.setCard(card);

        return interpretation;
    }

}
