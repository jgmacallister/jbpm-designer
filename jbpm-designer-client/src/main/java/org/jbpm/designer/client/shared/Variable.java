package org.jbpm.designer.client.shared;

import java.util.List;

public class Variable {

    public enum VariableType {
        INPUT,
        OUTPUT,
        PROCESS
    }

    ;

    private VariableType variableType;

    private String name;

    private String dataType;

    private String customDataType;

    public Variable(VariableType variableType) {
        this.variableType = variableType;
    }

    public Variable(String name, VariableType variableType) {
        this.name = name;
        this.variableType = variableType;
    }

    public Variable(String name, VariableType variableType, String dataType, String customDataType) {
        this.name = name;
        this.variableType = variableType;
        this.dataType = dataType;
        this.customDataType = customDataType;
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public void setVariableType(VariableType variableType) {
        this.variableType = variableType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCustomDataType() {
        return customDataType;
    }

    public void setCustomDataType(String customDataType) {
        this.customDataType = customDataType;
    }

    public String toString() {
        if (name != null && !name.isEmpty()) {
            StringBuilder sb = new StringBuilder().append(name);
            if (customDataType != null && !customDataType.isEmpty()) {
                sb.append(':').append(customDataType);
            }
            else if (dataType != null && !dataType.isEmpty()) {
                sb.append(':').append(dataType);
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * Deserializes a variable, checking whether the datatype is custom or not
     *
     * @param s
     * @param variableType
     * @param dataTypes
     * @return
     */
    public static Variable deserialize(String s, VariableType variableType, List<String> dataTypes) {
        Variable var = new Variable(variableType);
        String[] varParts = s.split(":");
        if (varParts.length > 0) {
            String name = varParts[0];
            if (!name.isEmpty()) {
                var.setName(name);
                if (varParts.length == 2) {
                    String dataType = varParts[1];
                    if (!dataType.isEmpty()) {
                        if (dataTypes != null && dataTypes.contains(dataType)) {
                            var.setDataType(dataType);
                        } else {
                            var.setCustomDataType(dataType);
                        }
                    }
                }
            }
        }
        return var;
    }

    /**
     * Deserializes a variable, NOT checking whether the datatype is custom
     *
     * @param s
     * @param variableType
     * @return
     */
    public static Variable deserialize(String s, VariableType variableType) {
        return deserialize(s, variableType, null);
    }
}