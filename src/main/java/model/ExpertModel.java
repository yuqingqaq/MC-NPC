package model;

public class ExpertModel {
    private String name;
    private String field;  // 专家领域

    public ExpertModel(String name, String field) {
        this.name = name;
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public String getField() {
        return field;
    }
}