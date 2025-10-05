package cliente.soap.users;

import jakarta.xml.bind.annotation.*;

/**
 * Clase generada para GetRandomUserRequest del esquema XSD
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "requestId"
})
@XmlRootElement(name = "GetRandomUserRequest", namespace = "http://cliente.com/users")
public class GetRandomUserRequest {

    protected String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String value) {
        this.requestId = value;
    }
}
