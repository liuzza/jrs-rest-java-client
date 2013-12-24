package com.jaspersoft.jasperserver.jaxrs.client.builder;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class RequestBuilder<T> {

    //static members
    private static final String PROTOCOL = "http://";
    private static final String URI = "/rest_v2";

    private static final Client client;
    private static final WebTarget usersWebTarget;

    //instance members
    protected Class responseClass;
    protected WebTarget concreteTarget;


    static  {
        client = ClientBuilder.newClient();
        client.register(HttpAuthenticationFeature.basic("jasperadmin", "jasperadmin"))
                .register(JacksonFeature.class);

        String host = getUrlProperty("host");
        String port = getUrlProperty("port");
        String context = getUrlProperty("context");

        usersWebTarget = client.target(PROTOCOL + host + ":" + port + "/" + context + URI);
    }

    protected RequestBuilder() {
        concreteTarget = usersWebTarget;
    }

    protected RequestBuilder(Class responseClass){
        this();
        this.responseClass = responseClass;
    }

    private static String getUrlProperty(String name){
        InputStream is = RequestBuilder.class.getClassLoader().getResourceAsStream("url.properties");
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(name);
    }

    public RequestBuilder<T> setPath(String path){
        concreteTarget = concreteTarget.path(path);
        return this;
    }

}