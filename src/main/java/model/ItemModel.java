package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemModel {
    private String name;
    private String imagePath;

    @JsonCreator
    public ItemModel(@JsonProperty("name") String name, @JsonProperty("image_path") String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return name;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }
}