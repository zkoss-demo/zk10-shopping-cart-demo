package org.zkoss.stateless.demo.richlet;

import static java.util.Arrays.asList;

import org.zkoss.stateless.action.ActionTarget;
import org.zkoss.stateless.annotation.*;
import org.zkoss.stateless.demo.util.Helper;
import org.zkoss.stateless.sul.*;
import org.zkoss.stateless.ui.*;
import org.zkoss.zk.ui.event.Events;

import java.util.List;

@RichletMapping("/simple")
public class SimpleRichlet implements StatelessRichlet {

    @RichletMapping("")
    public List<IComponent> index() {
        //Creating IComponents using the IComponent.of pattern
        //This allow for page declaration matching the actual page structure
        return asList(
                IVlayout.of(asList(
                        ILabel.of("Simple form demo").withSclass("main-title"),
                        IHlayout.of(asList(
                                ITextbox.ofId("tbUserId")
                                        .withPlaceholder("User Id").withConstraint("no empty").withWidth("200px")
                                        //Actions are the equivalent to event listeners. They target a method, in this case the inputUpdate method on the same Class
                                        .withInstant(true).withAction(this::inputUpdate),
                                ITextbox.ofId("tbUserDisplayName").withPlaceholder("User display name").withWidth("200px")
                                        .withInstant(true).withAction(this::inputUpdate),
                                ITextbox.ofId("tbUserPassword").withPlaceholder("Password").withType("password")
                                        .withWidth("200px").withInstant(true).withAction(this::inputUpdate)
                        )).withHflex("min"),
                        //Templating with parameters can be done easily, by creating a method which returns the UI elements
                        (IHlayout) displayLabelTemplate("lbUserId", "User Id").withStyle("padding-top: 20px;"),
                        displayLabelTemplate("lbUserDisplayName", "User Name"),
                        displayLabelTemplate("lbUserPassword", "User Password"),
                        //This button also receive an action, but targetting a different method
                        IButton.of("Send data").withAction(this::sendFormData)
                )).withHflex("min").withSclass("main-view")
        );
    }

    private IHlayout displayLabelTemplate(String id, String label) {
        return IHlayout.of(asList(
                ILabel.of(label + ": ").withId(id + "Label").withSclass("data-label"),
                ILabel.ofId(id).withSclass("data-content")
        )).withWidth("600px");
    }

    //When declaring an action, we can request values from the UI.
    //When the action is triggered, the UI will provide the requested values from the current state of the page at client-side
    @Action(type = Events.ON_CHANGE)
    public void inputUpdate(@ActionVariable(targetId = ActionTarget.SELF, field = "value") String newValue, @ActionVariable(targetId = ActionTarget.SELF, field = "id") String id) {
        Helper.log("Recieved onChange");
        String targetLabelId = "l" + id.substring(1);
        //Since the components don't have a state at server-side, we use a locator to locate a target IComponent existing in the page, which we can then update
        //This locator finds elements by ID, but you can also use relashionships, such self, parent, nextSibling, etc.
        Locator locator = Locator.ofId(targetLabelId);
        //IComponent updater is how a new value is being "writen back" to the client UI.
        //Once a component is located in the UI, we can modify its value and other properties
        UiAgent.getCurrent().smartUpdate(locator, new ILabel.Updater().value(newValue));
    }

    @Action(type = Events.ON_CLICK)
    public void sendFormData(@ActionVariable(targetId = "tbUserId", field = "value") String userId,
                             @ActionVariable(targetId = "tbUserDisplayName", field = "value") String userDisplayName,
                             @ActionVariable(targetId = "tbUserPassword", field = "value") String userPassword) {
        Helper.log("do something with data " + asList(userId, userDisplayName, userPassword));
    }


}