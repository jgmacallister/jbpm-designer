package org.jbpm.designer.client.popup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.ValueListBox;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.jbpm.designer.client.shared.Variable.VariableType;

/**
 * A templated widget that will be used to display a row in a table of
 * {@link AssignmentRow}s.
 */
@Templated("ActivityDataIOEditorWidget.html#assignment")
public class AssignmentListItemWidget extends Composite implements HasModel<AssignmentRow> {

    /**
     * Errai's data binding module will automatically bind the provided instance
     * of the model (see {@link #setModel(AssignmentRow)}) to all fields annotated
     * with {@link Bound}. If not specified otherwise, the bindings occur based on
     * matching field names (e.g. assignment.name will automatically be kept in
     * sync with the data-field "name")
     */
    @Inject
    @AutoBound
    private DataBinder<AssignmentRow> assignment;

    // You can also choose to instantiate your own widgets. Injection is not
    // required. In case of Element, direct injection is not supported.
    @Inject
    @Bound
    @DataField
    private TextBox name;

    @Bound
    @DataField
    //private final Element dataType = DOM.createTD();
    private ValueListBox<String> dataType = new ValueListBox<String>(new Renderer<String>() {
        public String render(String object) {
            String s = "";
            if (object != null) {
                s = object.toString();
            }
            return s;
        }
        public void render(String object, Appendable appendable) throws IOException {
            String s = render(object);
            appendable.append(s);
        }
    });
    List<String> acceptableDataTypes = new ArrayList<String>();

    @Inject
    @Bound
    @DataField
    private TextBox customDataType;

    @DataField
    private final Element direction = DOM.createTD();

    @Bound
    @DataField
    //private final Element processVar = DOM.createTD();
    private ValueListBox<String> processVar = new ValueListBox<String>(new Renderer<String>() {
        public String render(String object) {
            String s = "";
            if (object != null) {
                s = object.toString();
            }
            return s;
        }
        public void render(String object, Appendable appendable) throws IOException {
            String s = render(object);
            appendable.append(s);
        }
    });

    @Inject
    @Bound
    @DataField
    private TextBox constant;

    @Inject
    @DataField
    private Button deleteButton;

    /**
     * List of Assignments the current assignment is in
     */
    private List<AssignmentRow> assignments;

    public void setAssignments(List<AssignmentRow> assignments) {
        this.assignments = assignments;
    }

    @PostConstruct
    private void init() {
        customDataType.setVisible(false);
        dataType.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                String newValue = valueChangeEvent.getValue();
                // If "Custom..." or the customDataType selected, show customDataType
                if ("Custom ...".equals(newValue)
                        || (newValue != null && !newValue.isEmpty() && newValue.equals(customDataType.getValue()))) {
//                    dataType.setVisible(false);
                    customDataType.setVisible(true);
                    customDataType.setFocus(true);
                }
                // else if selected value is not the value in customDataType,
                // set customDataType to null
                else if (newValue != null && !newValue.isEmpty()) {
                    if (!newValue.equals(customDataType.getValue())) {
                        assignment.getModel().setCustomDataType(null);
                    }
                }
            }
        });

        customDataType.addBlurHandler(new BlurHandler() {
            @Override public void onBlur(BlurEvent blurEvent) {
                String cdt = customDataType.getValue();
                if (cdt != null && !cdt.isEmpty()) {
                    if (!acceptableDataTypes.contains(cdt)) {
                        acceptableDataTypes.add(cdt);
                    }
                    dataType.setValue(cdt);
                }
                customDataType.setVisible(false);
//                dataType.setVisible(true);
            }
        });

        customDataType.addKeyPressHandler(new KeyPressHandler() {
            @Override public void onKeyPress(KeyPressEvent keyPressEvent) {
                assignment.getModel().setDataType(null);
            }
        });

        processVar.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                assignment.getModel().setConstant(null);
            }
        });

        constant.addKeyPressHandler(new KeyPressHandler() {
            @Override public void onKeyPress(KeyPressEvent keyPressEvent) {
                assignment.getModel().setProcessVar(null);
            }
        });

    }

    @Override
    public AssignmentRow getModel() {
        return assignment.getModel();
    }

    @Override
    public void setModel(AssignmentRow model) {
        assignment.setModel(model);

        initAssignmentControls();
    }

    public void setDataTypes(List<String> dataTypes) {
        this.acceptableDataTypes = dataTypes;
        dataType.setAcceptableValues(dataTypes);
    }

    public void setProcessVariables(List<String> processVariables) {
        processVar.setAcceptableValues(processVariables);
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(ClickEvent e) {
        assignments.remove(assignment.getModel());
    }

    /**
     * Updates the display of this row according to the state of the
     * corresponding {@link AssignmentRow}.
     */
    private void initAssignmentControls() {
        deleteButton.setIcon(IconType.REMOVE);

        if (assignment.getModel().getVariableType() == VariableType.INPUT) {
            direction.appendChild(new Icon(IconType.ARROW_LEFT).getElement());
        }
        else {
            direction.appendChild(new Icon(IconType.ARROW_RIGHT).getElement());
            constant.setVisible(false);
        }

        String cdt = assignment.getModel().getCustomDataType();
        if (cdt != null && !cdt.isEmpty()) {
            if (!acceptableDataTypes.contains(cdt)) {
                acceptableDataTypes.add(cdt);
            }
            dataType.setValue(cdt);
        }
    /*
        if (assignment.getModel().isDone()) {
            removeStyleName("issue-open");
            addStyleName("issue-closed");
        }
        else {
            removeStyleName("issue-closed");
            addStyleName("issue-open");
        }
    */
    }


}
