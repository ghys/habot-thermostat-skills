package org.openhab.ui.habot.nlp.skill.thermostat.internal;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.openhab.ui.habot.nlp.Intent;
import org.openhab.ui.habot.nlp.IntentInterpretation;
import org.openhab.ui.habot.nlp.Skill;
import org.openhab.ui.habot.nlp.UnsupportedLanguageException;
import org.osgi.service.component.annotations.Component;

@Component(service = Skill.class, immediate = true)
public class IncreaseTemperatureSkill implements Skill {

    @Override
    public String getIntentId() {
        return "increase-temperature";
    }

    @Override
    public InputStream getTrainingData(String language) throws UnsupportedLanguageException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("It's too cold!\n");
        stringBuilder.append("It's too cold in here!\n");
        stringBuilder.append("I'm a little cold\n");
        stringBuilder.append("It's a little cold here\n");
        stringBuilder.append("It's too cold in the <START:location> bedroom <END>\n");
        stringBuilder.append("It's too cold in the <START:location> living room <END>\n");
        stringBuilder.append("Increase the temperature in the <START:location> living room <END>\n");
        stringBuilder.append("Make the <START:location> first floor <END> warmer!\n");
        stringBuilder.append("Make the <START:location> bedroom <END> warmer!\n");
        stringBuilder.append("Please increase the temperature in the <START:location> bathroom <END>\n");
        stringBuilder.append("Increase the temperature in <START:location> amy's room <END> a little please\n");
        return IOUtils.toInputStream(stringBuilder.toString());
    }

    @Override
    public IntentInterpretation interpret(Intent intent, String language) {
        IntentInterpretation interpretation = new IntentInterpretation();
        interpretation.setAnswer("Okay, I will increase the temperature");

        return interpretation;
    }

}
