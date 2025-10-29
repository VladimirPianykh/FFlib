package com.bpa4j.util.codegen.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Gson-based JSON provider for Jersey.
 * @author AI-generated
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GsonJsonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object>{
	private final Gson gson=new GsonBuilder().create();

	@Override
	public boolean isReadable(Class<?> type,Type genericType,Annotation[] annotations,MediaType mediaType){
		return MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType);
	}

	@Override
	public Object readFrom(Class<Object> type,Type genericType,Annotation[] annotations,MediaType mediaType,MultivaluedMap<String,String> httpHeaders,InputStream entityStream) throws IOException{
		try(Reader reader=new InputStreamReader(entityStream)){
			return gson.fromJson(reader,genericType!=null?genericType:type);
		}
	}

	@Override
	public boolean isWriteable(Class<?> type,Type genericType,Annotation[] annotations,MediaType mediaType){
		return MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType);
	}

	@Override
	public void writeTo(Object object,Class<?> type,Type genericType,Annotation[] annotations,MediaType mediaType,MultivaluedMap<String,Object> httpHeaders,OutputStream entityStream) throws IOException{
		try(Writer writer=new OutputStreamWriter(entityStream)){
			gson.toJson(object,genericType!=null?genericType:type,writer);
		}
	}
}


