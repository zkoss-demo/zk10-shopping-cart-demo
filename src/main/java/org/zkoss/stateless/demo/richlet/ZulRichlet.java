package org.zkoss.stateless.demo.richlet;

import org.zkoss.stateless.annotation.*;
import org.zkoss.stateless.sul.*;
import org.zkoss.stateless.ui.*;
import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zk.ui.event.Events;

import java.util.List;

/**
 * demonstrate how to build UI in zul with stateless components.
 */
@RichletMapping("/zul")
public class ZulRichlet implements StatelessRichlet {

    @RichletMapping("")
    public List<IComponent> index() {
        // build UI via a zul but render it in stateless components
        return Immutables.createComponents("stateless-page.zul", null);
    }

    /**
     * register action handler based on zul.
     * @param firstMemberValue field = "value" is default: you don't need to specify it explicitly
     * @param operation
     */
    @Action(from = "#calculate", type = Events.ON_CLICK)
    public void calculate(@ActionVariable(targetId = "firstMember", field = "value") int firstMemberValue,
                          @ActionVariable(targetId = "secondMember", field = "value") int secondMemberValue,
                          @ActionVariable(targetId = "operation", field = "selectedIndex") int operation) {

        int result = 0;
        switch (operation) {
            case 0: { //add
                result = firstMemberValue + secondMemberValue;
                break;
            }
            case 1: {  //multiply
                result = firstMemberValue * secondMemberValue;
                break;
            }
            default:
                result = 0;
                break;
        }
        UiAgent.getCurrent().smartUpdate(Locator.ofId("result"), new ILabel.Updater().value("result: "+result));
    }

}
