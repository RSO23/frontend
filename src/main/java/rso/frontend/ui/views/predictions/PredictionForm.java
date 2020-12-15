package rso.frontend.ui.views.predictions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import rso.frontend.backend.dto.SummonerNamesDto;
import rso.frontend.backend.util.SecurityUtils;

public class PredictionForm extends FormLayout
{
    private static final Logger log = LoggerFactory.getLogger(PredictionForm.class);

    TextArea summonerNamesTextField = new TextArea("Summoner names");

    Button save = new Button("Predict");
    Button close = new Button("Cancel");

    Binder<SummonerNamesDto> binder = new BeanValidationBinder<>(SummonerNamesDto.class);

    private SummonerNamesDto summonerNamesDto;

    public PredictionForm()
    {
        addClassName("predictions-form");
        summonerNamesTextField.setAutofocus(true);
        summonerNamesTextField.setPlaceholder("ROX Smeb joined the room.\n"
                                            + "ROX Peanut joined the room.\n"
                                            + "ROX Kuro joined the room.\n"
                                            + "ROX Pray joined the room.\n"
                                            + "ROX GorillA joined the room.");

        binder.bind(summonerNamesTextField,
                dto -> dto.getSummonerNames() != null ? dto.getSummonerNames().toString() : "",
                (dto, s) -> {
                    List<String> joined = Arrays.stream(s.split("\n"))
                            .map(s1 -> s1.split("joined")[0].replaceFirst("\\s++$", ""))
                            .collect(Collectors.toList());
                    dto.setSummonerNames(joined);
                });
        add(summonerNamesTextField, createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndPredict());
        close.addClickListener(event -> fireEvent(new PredictionForm.CloseEvent(this, binder.getBean())));

        binder.addStatusChangeListener(statusChangeEvent -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, close);
    }

    public void setSummonerNamesDto(SummonerNamesDto summonerNamesDto) {
        this.summonerNamesDto = summonerNamesDto;
        binder.readBean(summonerNamesDto);
    }

    private void validateAndPredict() {
        try {
            binder.writeBean(summonerNamesDto);
            fireEvent(new PredictionForm.SaveEvent(this, summonerNamesDto));
        } catch (ValidationException e) {
            log.warn(e.getLocalizedMessage());
        }
    }

    public abstract static class PredictionFormEvent extends ComponentEvent<PredictionForm>
    {
        private final SummonerNamesDto summonerNamesDto;

        protected PredictionFormEvent(PredictionForm source, SummonerNamesDto summonerNamesDto) {
            super(source, false);
            this.summonerNamesDto = summonerNamesDto;
        }

        public SummonerNamesDto getSummonerNamesDto() {
            summonerNamesDto.setTeam(300);
            summonerNamesDto.setUserId(SecurityUtils.getUserId());
            return summonerNamesDto;
        }
    }

    public static class SaveEvent extends PredictionForm.PredictionFormEvent
    {
        SaveEvent(PredictionForm predictionForm, SummonerNamesDto gameAccountDto) {
            super(predictionForm, gameAccountDto);
        }
    }

    public static class CloseEvent extends PredictionForm.PredictionFormEvent
    {
        CloseEvent(PredictionForm predictionForm, SummonerNamesDto gameAccountDto) {
            super(predictionForm, gameAccountDto);
        }
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
