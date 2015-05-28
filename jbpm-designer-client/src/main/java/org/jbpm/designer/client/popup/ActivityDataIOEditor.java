package org.jbpm.designer.client.popup;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import org.jboss.errai.marshalling.client.Marshalling;
import org.jbpm.designer.client.shared.AssignmentData;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.jbpm.designer.client.shared.Variable.VariableType;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

@Dependent
public class ActivityDataIOEditor extends BaseModal {

    public interface GetDataCallback {
        public void getData(String assignmentData);
    }

    GetDataCallback callback = null;

    @Inject
    private ActivityDataIOEditorWidget inputAssignmentsWidget;

    @Inject
    private ActivityDataIOEditorWidget outputAssignmentsWidget;

    private Button btnOK;

    private Button btnCancel;


    public ActivityDataIOEditor() {
        super();
        this.setWidth((double) Window.getClientWidth() * 0.6D + "px");
    }

    private List<String> dataTypes = new ArrayList<String>();
    private List<String> dataTypeDisplayNames = new ArrayList<String>();

    @PostConstruct
    public void init() {
        setTitle("Data I/O Editor");

        inputAssignmentsWidget.setVariableType(VariableType.INPUT);
        this.add(inputAssignmentsWidget);

        outputAssignmentsWidget.setVariableType(VariableType.OUTPUT);
        this.add(outputAssignmentsWidget);

        btnOK = new Button( "OK" );
        btnOK.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                //Window.alert(inputAssignmentsWidget.getAssignmentsAsString());
                //Window.alert(outputAssignmentsWidget.getAssignmentsAsString());
                if (callback != null) {
                    AssignmentData data = new AssignmentData(inputAssignmentsWidget.getData(),
                            outputAssignmentsWidget.getData(), dataTypes, dataTypeDisplayNames);
                    String sData = Marshalling.toJSON(data);
                    callback.getData(sData);
                }
                hide();
            }
        });
        this.add(btnOK);

        btnCancel = new Button( "Cancel" );
        btnCancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                hide();
            }
        });
        this.add(btnCancel);

    }

    @Override
    public void onShow(Event e) {
    }

    @Override
    public void onHide(Event e) {
    }

    public void setCallback(GetDataCallback callback) {
        this.callback = callback;
    }

    public void setInputAssignmentRows(List<AssignmentRow> inputAssignmentRows) {
        inputAssignmentsWidget.setData(inputAssignmentRows);
    }

    public void setOutputAssignmentRows(List<AssignmentRow> outputAssignmentRows) {
        outputAssignmentsWidget.setData(outputAssignmentRows);
    }

    public void setDataTypes(List<String> dataTypes, List<String> dataTypeDisplayNames) {
        this.dataTypes = dataTypes;
        this.dataTypeDisplayNames = dataTypeDisplayNames;

        inputAssignmentsWidget.setDataTypes(dataTypeDisplayNames);
        outputAssignmentsWidget.setDataTypes(dataTypeDisplayNames);
    }

    public void setProcessVariables(List<String> processVariables) {
        List<String> inProcessVariables = new ArrayList<String>();
        inProcessVariables.add("Constant ...");
        inProcessVariables.addAll(processVariables);
        inputAssignmentsWidget.setProcessVariables(inProcessVariables);

        outputAssignmentsWidget.setProcessVariables(processVariables);
    }

    //   public void hide() {
 //       Window.alert(activityDataIOEditorWidget.getAssignmentsAsString());
 //       super.hide();
 //   }
}
