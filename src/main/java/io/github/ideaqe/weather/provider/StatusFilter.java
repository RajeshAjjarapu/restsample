package io.github.ideaqe.weather.provider;


import io.github.ideaqe.weather.Created;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.annotation.Annotation;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

@Provider
public class StatusFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        if (response.getStatus() == NO_CONTENT.getStatusCode()) {
            Annotation[] annotations = response.getEntityAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Created){
                    response.setStatus(CREATED.getStatusCode());
                    break;
                }
            }
        }
    }
}
