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

    public ResponseParser(Response response, Type type) {
        this.response = response;
        this.type = type;
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
        final Gson gson = new GsonBuilder().setDateFormat(DateUtils.COMPACT_DATE_PATTERN).create();
        final GsonConverter converter = new GsonConverter(gson);
        return (T) converter.fromBody(response.getBody(), type);
    }
}
