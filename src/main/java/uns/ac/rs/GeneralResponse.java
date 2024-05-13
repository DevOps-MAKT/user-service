package uns.ac.rs;

import lombok.Data;

@Data
public class GeneralResponse<T> {
    public String message;
    public T data;

    public GeneralResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }
}
