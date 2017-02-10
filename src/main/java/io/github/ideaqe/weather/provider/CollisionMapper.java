package io.github.ideaqe.weather.provider;

import io.github.ideaqe.weather.CollisionException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.CONFLICT;

@Provider
public class CollisionMapper implements ExceptionMapper<CollisionException> {

    @Override
    public Response toResponse(CollisionException exception) {
        return Response.status(CONFLICT).type(TEXT_PLAIN).entity(exception.getMessage()).build();
    }
}
