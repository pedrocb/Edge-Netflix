package portal;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {

    public Response toResponse(Exception exception) {
        exception.printStackTrace();
        return Response.status(500).build();
    }
}
