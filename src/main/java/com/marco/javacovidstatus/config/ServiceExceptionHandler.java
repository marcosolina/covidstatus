package com.marco.javacovidstatus.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.marco.javacovidstatus.model.rest.SimpleResponse;
import com.marco.utils.MarcoException;
import com.marco.utils.http.MarcoResponse;

@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
    private MessageSource msgSource;
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExceptionHandler.class);

	@ExceptionHandler(value = { MarcoException.class })
	public ResponseEntity<MarcoResponse> handleMarcoExceptions(MarcoException ex, WebRequest request) {
		SimpleResponse httpResponse = new SimpleResponse();
		httpResponse.addError(ex);

		if (LOGGER.isDebugEnabled()) {
			ex.printStackTrace();
		}

		return new ResponseEntity<>(httpResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<MarcoResponse> handleGenericExceptions(Exception ex, WebRequest request) {
		SimpleResponse httpResponse = new SimpleResponse();
		httpResponse.addError(new MarcoException(msgSource.getMessage("COVID00001", null, LocaleContextHolder.getLocale())));

		if (LOGGER.isDebugEnabled()) {
			ex.printStackTrace();
		}

		return new ResponseEntity<>(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
