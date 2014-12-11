package com.fanpics.opensource.android.modelrecord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;

public class ResponseParser<T> {
    private final Response response;
    private final Type type;
    private String dateFormat;

    public ResponseParser(Response response, Type type) {
        this.response = response;
        this.type = type;
    }

    public ResponseParser(Response response, Type type, String dateFormat) {
        this.response = response;
        this.type = type;
        this.dateFormat = dateFormat;
    }

    public Result<T> parse() {
        try {
            final T object = parseFromResponse();
            return new Result<T>(response, object);
        } catch (ConversionException e) {
            throw new RuntimeException("Response body must match type passed in at constructor", e);
        }
    }

    private T parseFromResponse() throws ConversionException {
        final Gson gson = getGson();
        final GsonConverter converter = new GsonConverter(gson);
        return (T) converter.fromBody(response.getBody(), type);
    }

    public Gson getGson() {
        Gson gson;

        if (dateFormat != null) {
            gson = new GsonBuilder().setDateFormat(dateFormat).create();
        } else {
            gson = new GsonBuilder().create();
        }

        return gson;
    }
}
