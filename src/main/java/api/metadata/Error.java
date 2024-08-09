package api.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Error {
    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("type")
    private String type;

    @JsonProperty("param")  // 添加新字段
    private String param;  // 假设字段类型为 String

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {  // Getter for type
        return type;
    }

    public void setType(String type) {  // Setter for type
        this.type = type;
    }

    public String getParam() {  // Getter for param
        return param;
    }

    public void setParam(String param) {  // Setter for param
        this.param = param;
    }
}
