package org.jbpm.designer.client.popup;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.jbpm.designer.client.shared.Variable.VariableType;

@Dependent
@Templated("ActivityDataIOEditorWidget.html#widget" )
public class ActivityDataIOEditorWidget extends Composite {


    private List<String> dataTypes;
    private List<String> processVariables;

    private VariableType variableType = VariableType.INPUT;

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

    @PostConstruct
    public void init() {
    }

    public void setVariableType(VariableType variableType) {
        this.variableType = variableType;
        if (variableType.equals(VariableType.INPUT)) {
            tabletitle.appendChild(new Label("Input Variables and Assignments").getElement());
            processvarorconstantth.setInnerText("Source");
        }
        else {
            tabletitle.appendChild(new Label("Output Variables and Assignments").getElement());
            processvarorconstantth.setInnerText("Target");
        }
    }

    @EventHandler("addVarButton")
    public void handleAddvarButton(ClickEvent e) {
        AssignmentRow newAssignment = new AssignmentRow();
        newAssignment.setVariableType(variableType);
        List<AssignmentRow> as = assignments.getValue();
        as.add(newAssignment);

        AssignmentListItemWidget widget = assignments.getWidget(assignments.getValue().size() - 1);
        widget.setDataTypes(dataTypes);
        widget.setProcessVariables(processVariables);
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
            assignments.getWidget(i).setDataTypes(dataTypes);
        }
    }

    public void setProcessVariables(List<String> processVariables) {
        this.processVariables = processVariables;
        for (int i = 0; i < assignments.getValue().size(); i++) {
            assignments.getWidget(i).setProcessVariables(processVariables);
        }
    }
}
