package rso.frontend.ui;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwtParser;

@Route("main")
@PageTitle("Main")
public class MainView extends VerticalLayout
{

    public MainView() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = ((String) authentication.getDetails());

        DefaultJwtParser defaultJwtParser = new DefaultJwtParser();
        defaultJwtParser.setSigningKey("veryhardsecret");
        DefaultClaims object = ((DefaultClaims) defaultJwtParser.parse(token).getBody());

        Button clickMeBtn = new Button("Click me", e -> Notification.show("Hello, Spring+Vaadin user!"));

        TextField textFieldToken= new TextField();

        textFieldToken.setValue(token);

        add(clickMeBtn, textFieldToken);
    }
}