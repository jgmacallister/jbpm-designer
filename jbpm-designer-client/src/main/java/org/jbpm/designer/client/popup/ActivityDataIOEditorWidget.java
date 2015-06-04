package org.jbpm.designer.client.popup;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ValueListBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.jbpm.designer.client.shared.Variable.VariableType;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated("ActivityDataIOEditorWidget.html#widget" )
public class ActivityDataIOEditorWidget extends Composite {


    private List<String> dataTypes;
    private List<String> processVariables;

    private VariableType variableType = VariableType.INPUT;

    boolean isSingleVar = false;

    @Inject
    @DataField
    private Button addVarButton;

    @DataField
    private final Element tabletitle = DOM.createLabel();

    @DataField
    private final Element processvarorconstantth = DOM.createTH();

    /**
     * The list of assignments that currently exist.
     */
    @Inject
    @DataField
    @Table(root="tbody")
    private ListWidget<AssignmentRow, AssignmentListItemWidget> assignments;

    @Inject
    private Event<NotificationEvent> notification;


    class ListBoxValues {
        List<String> acceptableValues = new ArrayList<String>();
        List<String> customValues = new ArrayList<String>();

        List<ValueListBox<String>> observers = new ArrayList<ValueListBox<String>>();

        void register(ValueListBox<String> listBox, List<String> acceptableValues, List<String> customValues) {
            if (acceptableValues != null) {
                for (int i = 0; i < acceptableValues.size(); i++) {
                    String value = acceptableValues.get(i);
                    if (! this.acceptableValues.contains(value)) {
                        this.acceptableValues.add(i, value);
                    }
                    else {
                        // all new entries to be added are at the start
                        break;
                    }
                }
            }
            if (customValues != null) {
                for (int i = 0; i < customValues.size(); i++) {
                    String value = customValues.get(i);
                    if (! this.customValues.contains(value)) {
                        this.customValues.add(i, value);
                    }
                    else {
                        // all new entries to be added are at the start
                        break;
                    }
                }
            }
            if (!observers.contains(listBox)) {
                observers.add(listBox);
            }
            update();
        }
        void unregister(ValueListBox<String> listBox) {
            observers.remove(listBox);
        }
        void addAcceptableValue(String newValue) {
            if (newValue != null && !acceptableValues.contains(newValue)) {
                acceptableValues.add(0, newValue);
                update();
            }
        }
        void addAcceptableValues(List<String> newValues) {
            for (String newValue : newValues) {
                if (newValue != null && !acceptableValues.contains(newValue)) {
                    acceptableValues.add(0, newValue);
                }
            }
            update();
        }
        void addCustomValue(String newValue) {
            if (newValue != null && !customValues.contains(newValue)) {
                customValues.add(newValue);
            }
        }
        boolean isCustomValue(String value) {
            if (value == null || value.isEmpty()) {
                return false;
            }
            else {
                return customValues.contains(value);
            }
        }
        void update() {
            for (ValueListBox<String>  observer : observers) {
                observer.setAcceptableValues(acceptableValues);
            }
        }
        boolean containsListBox(ValueListBox<String> listBox) {
            if (listBox == null) {
                return false;
            }
            else {
                return observers.contains(listBox);
            }
        }
    }

    ListBoxValues dataTypeListBoxValues = new ListBoxValues();
    ListBoxValues processVarListBoxValues = new ListBoxValues();


    @PostConstruct
    public void init() {
    }

    public void setIsSingleVar(boolean isSingleVar) {
        this.isSingleVar = isSingleVar;
        if (variableType.equals(VariableType.INPUT)) {
            processvarorconstantth.setInnerText("Source");
            if (isSingleVar) {
                tabletitle.setInnerText("Input Variable and Assignment");
            }
            else {
                tabletitle.setInnerText("Input Variables and Assignments");
            }
        }
        else {
            processvarorconstantth.setInnerText("Target");
            if (isSingleVar) {
                tabletitle.setInnerText("Output Variable and Assignment");
            }
            else {
                tabletitle.setInnerText("Output Variables and Assignments");
            }
        }
    }

    public void setVariableType(VariableType variableType) {
        this.variableType = variableType;
    }

    @EventHandler("addVarButton")
    public void handleAddvarButton(ClickEvent e) {
        if (isSingleVar && assignments.getValue().size() > 0) {
            notification.fire(new NotificationEvent("Only single entry allowed", NotificationEvent.NotificationType.ERROR));
        }
        else {
            addAssignment();
        }
    }

    public void addAssignment() {
        AssignmentRow newAssignment = new AssignmentRow();
        newAssignment.setVariableType(variableType);
        List<AssignmentRow> as = assignments.getValue();
        as.add(newAssignment);

        AssignmentListItemWidget widget = assignments.getWidget(assignments.getValue().size() - 1);
        widget.setDataTypes(dataTypes, dataTypeListBoxValues);
        widget.setProcessVariables(processVariables, processVarListBoxValues);
        widget.setAssignments(assignments.getValue());
    }

    public void setData(List<AssignmentRow> assignmentRows) {
        assignments.setValue(assignmentRows);

        for (int i = 0; i < assignmentRows.size(); i++) {
            assignments.getWidget(i).setAssignments(assignments.getValue());
        }

    }

    public List<AssignmentRow> getData() {
        return assignments.getValue();
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public void setDataTypes(List<String> dataTypes) {
        this.dataTypes = dataTypes;
        for (int i = 0; i < assignments.getValue().size(); i++) {
            assignments.getWidget(i).setDataTypes(dataTypes, dataTypeListBoxValues);
        }
    }

    public void setProcessVariables(List<String> processVariables) {
        this.processVariables = processVariables;
        for (int i = 0; i < assignments.getValue().size(); i++) {
            assignments.getWidget(i).setProcessVariables(processVariables, processVarListBoxValues);
        }
    }

}
