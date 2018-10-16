package org.openhab.ui.habot.nlp.skill.thermostat.internal;

import java.io.InputStream;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.items.events.ItemEventFactory;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.semantics.SemanticsPredicates;
import org.eclipse.smarthome.core.semantics.SemanticsService;
import org.eclipse.smarthome.core.semantics.model.equipment.HVAC;
import org.eclipse.smarthome.core.semantics.model.point.Setpoint;
import org.eclipse.smarthome.core.semantics.model.property.Temperature;
import org.openhab.ui.habot.card.CardBuilder;
import org.openhab.ui.habot.nlp.Intent;
import org.openhab.ui.habot.nlp.IntentInterpretation;
import org.openhab.ui.habot.nlp.Skill;
import org.openhab.ui.habot.nlp.UnsupportedLanguageException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = Skill.class, immediate = true)
public class IncreaseTemperatureSkill implements Skill {

    private ItemRegistry itemRegistry;
    private SemanticsService semanticsService;
    private CardBuilder cardBuilder;
    private EventPublisher eventPublisher;

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

        String location = intent.getEntities().get("location");
        if (location != null) {
            Set<Item> items = semanticsService.getItemsInLocation(location, Locale.ROOT).stream()
                    .filter(SemanticsPredicates.isA(HVAC.class)).collect(Collectors.toSet());
            if (items.isEmpty()) {
                interpretation
                        .setAnswer("Sorry, I didn't find anything to increase the temperature in the " + location);
            } else if (items.size() > 1) {
                interpretation.setAnswer("Weird, I found more than 1 HVAC item in the " + location);
            } else {
                eventPublisher
                        .post(ItemEventFactory.createCommandEvent(items.iterator().next().getName(), OnOffType.ON));
                interpretation.setAnswer("Okay, I'm turning on the heating in the " + location
                        + ", this should increase the temperature!");
                interpretation.setCard(cardBuilder.buildCard(intent, items));
            }
        } else {
            Set<Item> setpoints = itemRegistry.getAll().stream().filter(
                    SemanticsPredicates.isA(Setpoint.class).and(SemanticsPredicates.relatesTo(Temperature.class)))
                    .collect(Collectors.toSet());

            if (setpoints.isEmpty()) {
                interpretation.setAnswer("Sorry, I didn't find any temperature setpoint in your items");
            } else if (setpoints.size() > 1) {
                interpretation.setAnswer("I found more than 1 temperature setpoint... I don't know what to do!");
            } else {
                Item setpoint = setpoints.iterator().next();
                Double currentSetpoint = new Double(Double.parseDouble(setpoint.getState().toString()) + 1);
                eventPublisher.post(ItemEventFactory.createCommandEvent(setpoint.getName(),
                        setpoint.getAcceptedCommandTypes().contains(DecimalType.class)
                                ? DecimalType.valueOf(currentSetpoint.toString())
                                : PercentType.valueOf(currentSetpoint.toString())));

                interpretation.setAnswer(
                        "Okay, I'm increasing the temperature setpoint a little, it should get warmer soon!");
                interpretation.setCard(cardBuilder.buildCard(intent, setpoints));
            }
        }

        return interpretation;
    }

    ///////////////

    @Reference
    protected void setItemRegistry(ItemRegistry itemRegistry) {
        if (this.itemRegistry == null) {
            this.itemRegistry = itemRegistry;
        }
    }

    protected void unsetItemRegistry(ItemRegistry itemRegistry) {
        if (itemRegistry == this.itemRegistry) {
            this.itemRegistry = null;
        }
    }

    @Reference
    public void setSemanticsService(SemanticsService semanticsService) {
        this.semanticsService = semanticsService;
    }

    public void unsetSemanticsService(SemanticsService semanticsService) {
        this.semanticsService = null;
    }

    @Reference
    protected void setCardBuilder(CardBuilder cardBuilder) {
        this.cardBuilder = cardBuilder;
    }

    protected void unsetCardBuilder(CardBuilder cardBuilder) {
        this.cardBuilder = null;
    }

    @Reference
    protected void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    protected void unsetEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = null;
    }
}
