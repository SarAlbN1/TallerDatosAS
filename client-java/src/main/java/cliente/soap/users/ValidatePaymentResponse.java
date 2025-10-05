package cliente.soap.users;

import jakarta.xml.bind.annotation.*;

/**
 * Clase generada para ValidatePaymentResponse del esquema XSD
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "isValid",
    "requestId",
    "status",
    "message"
})
@XmlRootElement(name = "ValidatePaymentResponse", namespace = "http://cliente.com/users")
public class ValidatePaymentResponse {

    protected boolean isValid;
    
    protected String requestId;
    
    @XmlElement(required = true)
    protected String status;
    
    protected String message;

    public boolean isIsValid() {
        return isValid;
    }

    public void setIsValid(boolean value) {
        this.isValid = value;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String value) {
        this.requestId = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }
}
