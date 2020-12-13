package rso.frontend.ui.views.gameAccounts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import rso.frontend.backend.dto.GameAccountDto;
import rso.frontend.backend.util.SecurityUtils;

public class GameAccountsForm extends FormLayout
{
    private static final Logger log = LoggerFactory.getLogger(GameAccountsForm.class);

    TextField username = new TextField("Username");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<GameAccountDto> binder = new BeanValidationBinder<>(GameAccountDto.class);

    private GameAccountDto gameAccountDto;

    public GameAccountsForm() {
        addClassName("game-account-form");
        binder.bindInstanceFields(this);
        add(username, createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> validateAndDelete());
        close.addClickListener(event -> fireEvent(new CloseEvent(this, binder.getBean())));

        binder.addStatusChangeListener(statusChangeEvent -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    public void setGameAccount(GameAccountDto gameAccountDto) {
        this.gameAccountDto = gameAccountDto;
        binder.readBean(gameAccountDto);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(gameAccountDto);
            fireEvent(new SaveEvent(this, gameAccountDto));
        } catch (ValidationException e) {
            log.warn(e.getLocalizedMessage());
        }
    }

    private void validateAndDelete() {
        try {
            binder.writeBean(gameAccountDto);
            fireEvent(new DeleteEvent(this, gameAccountDto));
        } catch (ValidationException e) {
            log.warn(e.getLocalizedMessage());
        }
    }

    public abstract static class GameAccountFormEvent extends ComponentEvent<GameAccountsForm> {
        private final GameAccountDto gameAccountDto;

        protected GameAccountFormEvent(GameAccountsForm source, GameAccountDto gameAccountDto) {
            super(source, false);
            this.gameAccountDto = gameAccountDto;
        }

        public GameAccountDto getGameAccountDto() {
            gameAccountDto.setUserId(SecurityUtils.getUserId());
            return gameAccountDto;
        }
    }

    public static class SaveEvent extends GameAccountFormEvent {
        SaveEvent(GameAccountsForm gameAccountsForm, GameAccountDto gameAccountDto) {
            super(gameAccountsForm, gameAccountDto);
        }
    }

    public static class DeleteEvent extends GameAccountFormEvent {
        DeleteEvent(GameAccountsForm gameAccountsForm, GameAccountDto gameAccountDto) {
            super(gameAccountsForm, gameAccountDto);
        }
    }

    public static class CloseEvent extends GameAccountFormEvent {
        CloseEvent(GameAccountsForm gameAccountsForm, GameAccountDto gameAccountDto) {
            super(gameAccountsForm, gameAccountDto);
        }
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
