package org.zkoss.stateless.demo.richlet;

import org.zkoss.stateless.action.ActionTarget;
import org.zkoss.stateless.action.data.InputData;
import org.zkoss.stateless.annotation.*;
import org.zkoss.stateless.demo.util.Helper;
import org.zkoss.stateless.sul.*;
import org.zkoss.stateless.ui.*;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;

import java.util.*;

import static java.util.Arrays.asList;

@RichletMapping("/session")
public class SessionRichlet implements StatelessRichlet {

    private static final String NAME_BOX = "nameBox";
    private static final String USERNAME = "username";

    @RichletMapping("")
    public List<IComponent> index() {
        //Creating IComponents using the IComponent.of pattern
        //This allows for page declaration matching the actual page structure
        return asList(
                ITextbox.ofId(NAME_BOX).withAction(this::setUserName),
                renderUserName()
                );
    }

    private static ILabel renderUserName() {
        String username = Optional.ofNullable(Sessions.getCurrent().getAttribute(USERNAME)).map(Object::toString).orElse("unset");
        return ILabel.ofId(USERNAME).withValue(username);
    }

    @Action(type = Events.ON_CHANGE)
    public void setUserName(Self self, InputData inputData) {
        Sessions.getCurrent().setAttribute(USERNAME, inputData.getValue());
        UiAgent.getCurrent().replaceWith(Locator.ofId(USERNAME), renderUserName());
    }


}