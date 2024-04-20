import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Controller {

    private final Class<?> modelClass;
    private Object modelInstance;
    private final LinkedHashMap<String, Object> variables;

    public Controller(String modelName) {
        this.variables = new LinkedHashMap<>();
        try {
            modelClass = Class.forName("models." + modelName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Controller readDataFrom(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String variableName = parts[0];
                String[] values = Arrays.copyOfRange(parts, 1, parts.length);
                variables.put(variableName, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Controller runModel() {
        try {
            modelInstance = modelClass.getDeclaredConstructor().newInstance();
            Field nnField = modelClass.getDeclaredField("LL");
            nnField.setAccessible(true);
            double[] yearsValues= Arrays.stream((String[]) variables.get("LATA"))
                    .mapToDouble(Double::parseDouble)
                    .toArray();
            nnField.setInt(modelInstance, yearsValues.length);
            try {
                for (Field field : modelInstance.getClass().getDeclaredFields()) {
                    if (field.isAnnotationPresent(models.Bind.class)) {
                        field.setAccessible(true);
                        String fieldName = field.getName();
                        if (variables.containsKey(fieldName)) {
                            double[] doubleValues = Arrays.stream((String[]) variables.get(fieldName))
                                    .mapToDouble(Double::parseDouble)
                                    .toArray();
                            double[] result = new double[nnField.getInt(modelInstance)];
                            for (int i = 0; i < result.length; i++) {
                                if (i < doubleValues.length) {
                                    result[i] = doubleValues[i];
                                } else {
                                    result[i] = doubleValues[doubleValues.length - 1];
                                }
                            }
                            field.set(modelInstance, result);
                        }
                    }
                }
                Method runMethod = modelInstance.getClass().getMethod("run");
                runMethod.invoke(modelInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (Field field : modelInstance.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(models.Bind.class)) {
                    field.setAccessible(true);
                    try {
                        variables.put(field.getName(), field.get(modelInstance));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
    public Controller runScriptFromFile(String scriptFileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(scriptFileName))) {
            StringBuilder scriptContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                scriptContent.append(line).append("\n");
            }

            return runScript(scriptContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Controller runScript(String script) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("groovy");
            try {
                engine.eval(script, new SimpleBindings(variables));
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            variables.entrySet().removeIf(entry -> entry.getKey().length() == 1 && Character.isLowerCase(entry.getKey().charAt(0)));
            for (Field field : modelInstance.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(models.Bind.class)) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    if (variables.containsKey(fieldName) && !variables.containsKey("LL")) {
                        double[] doubleValues = Arrays.stream((Object[]) variables.get(fieldName))
                                .mapToDouble(obj -> ((Integer) obj).doubleValue())
                                .toArray();
                        field.set(modelInstance, doubleValues);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
    public String getResultsAsTsv() {
        StringBuilder resultAsTsv = new StringBuilder();
        String[] yearsValues = (String[]) variables.get("LATA");
        for (String lataValue : yearsValues) {
            resultAsTsv.append("\t").append(lataValue);
        }
        resultAsTsv.append("\n");
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String columnName = entry.getKey();
            if (!columnName.equals("LATA") & !columnName.equals("LL")) {
                resultAsTsv.append(columnName);
                Object columnValues = entry.getValue();

                if (columnValues instanceof double[] values) {
                    for (double value : values) {
                        resultAsTsv.append("\t").append(value);
                    }
                    resultAsTsv.append("\n");
                }
            }
        }
        return resultAsTsv.toString();
    }
}