package org.jbpm.designer.client.popup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.ValueListBox;
import com.github.gwtbootstrap.client.ui.base.Style;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
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

    Map<ValueListBox<String>, List<String>> mapAcceptableValues = new HashMap<ValueListBox<String>, List<String>>();

    public static final String EDIT_PROMPT = "Edit ";
    public static final String CUSTOM_PROMPT = "Custom ...";
    public static final String ENTER_TYPE_PROMPT = "Enter type...";
    public static final String CONSTANT_PROMPT = "Constant ...";
    public static final String ENTER_CONSTANT_PROMPT = "Enter constant...";

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

    private void initEditableListBox(final ValueListBox<String> listBox, final TextBox textBox, final boolean bQuoteStringValues,
            final String customPrompt, final String placeholder, final String editPrompt) {
        textBox.setVisible(true);
        textBox.setPlaceholder(placeholder);
        listBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                String newValue = valueChangeEvent.getValue();
                // If "Custom..." selected, show textBox
                if (customPrompt.equals(newValue)) {
                    textBox.setValue("");
//                    listBox.setVisible(false);
//                    textBox.setVisible(true);
                    textBox.setFocus(true);
                } else if (newValue.startsWith(editPrompt)) {
                    textBox.setValue(newValue.substring(editPrompt.length(), newValue.length() - 3));
//                    listBox.setVisible(false);
//                    textBox.setVisible(true);
                    textBox.setFocus(true);
                }
                // else set textBox to empty
                else if (newValue != null) {
                    setModelTextValue(textBox, "");
                }
            }
        });

        textBox.addBlurHandler(new BlurHandler() {
            @Override public void onBlur(BlurEvent blurEvent) {
                String cdt = textBox.getValue();
                if (cdt != null) {
                    if (bQuoteStringValues) {
                        cdt = createQuotedConstant(cdt);
                    }
                    // Set the value even if it's ""
                    listBox.setValue(cdt);

                    if (!cdt.isEmpty()) {
                        // Add Edit <custom> ..." to acceptableValues
                        // N.B. Don't add custom value itself, because selecting it
                        // causes an error when the dialog is dismissed
                        List<String> acceptableValues = getAcceptableValues(listBox);
                        String promptWithValue = editPrompt + cdt + "...";
                        if (!acceptableValues.contains(promptWithValue)) {
                            acceptableValues.add(cdt);
                            acceptableValues.add(promptWithValue);
                            listBox.setAcceptableValues(acceptableValues);
                        }
                    }
                }
                //textBox.setVisible(false);
                //listBox.setVisible(true);
            }
        });


//        textBox.addKeyPressHandler(new KeyPressHandler() {
//            @Override public void onKeyPress(KeyPressEvent keyPressEvent) {
//                listBox.setValue("");
//            }
//        });

    }

    protected void setModelTextValue(final TextBox textBox, String value) {
        if (textBox == customDataType) {
            assignment.getModel().setCustomDataType(value);
        }
        else if (textBox == constant) {
            assignment.getModel().setConstant(value);
        }
    }

    @PostConstruct
    private void init() {
        // Configure dataType and customDataType controls
        initEditableListBox(dataType, customDataType, false, CUSTOM_PROMPT, ENTER_TYPE_PROMPT, EDIT_PROMPT);

        // Configure dataType and customDataType controls
        initEditableListBox(processVar, constant, true, CONSTANT_PROMPT, ENTER_CONSTANT_PROMPT, EDIT_PROMPT);
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
        mapAcceptableValues.put(dataType, dataTypes);
        dataType.setAcceptableValues(dataTypes);
    }

    public void setProcessVariables(List<String> processVariables) {
        mapAcceptableValues.put(processVar, processVariables);
        processVar.setAcceptableValues(processVariables);
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(ClickEvent e) {
        assignments.remove(assignment.getModel());
    }

    private List<String> getAcceptableValues(final ValueListBox<String> listBox) {
        return mapAcceptableValues.get(listBox);
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
            List<String> acceptableDataTypes = getAcceptableValues(dataType);
            if (acceptableDataTypes != null && !acceptableDataTypes.contains(cdt)) {
                acceptableDataTypes.add(cdt);
            }
            dataType.setValue(cdt);
        }

        String con = assignment.getModel().getConstant();
        if (con != null && !con.isEmpty()) {
            con = createQuotedConstant(con);
            List<String> acceptableProcessVars = getAcceptableValues(processVar);
            if (acceptableProcessVars != null && !acceptableProcessVars.contains(con)) {
                acceptableProcessVars.add(con);
            }
            processVar.setValue(con);
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

    protected String createQuotedConstant(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        try
        {
            Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return "\"" + str + "\"";
        }
        return str;
    }

}
