package cliente.soap.users;

import jakarta.xml.bind.annotation.*;

/**
 * Clase generada para GetRandomUserResponse del esquema XSD
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "user",
    "requestId",
    "status"
})
@XmlRootElement(name = "GetRandomUserResponse", namespace = "http://cliente.com/users")
public class GetRandomUserResponse {

    @XmlElement(required = true)
    protected User user;
    
    protected String requestId;
    
    @XmlElement(required = true)
    protected String status;

    public User getUser() {
        return user;
    }

    public void setUser(User value) {
        this.user = value;
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
}
