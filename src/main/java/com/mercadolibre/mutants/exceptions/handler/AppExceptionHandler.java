package com.mercadolibre.mutants.exceptions.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.mercadolibre.mutants.exceptions.AdnException;
import com.mercadolibre.mutants.exceptions.handler.builder.ResponseErrorBuilder;
import com.mercadolibre.mutants.exceptions.handler.model.ResponseError;
import com.mercadolibre.mutants.util.BundleMessage;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {


	/**
	 * Messaging
	 */
	@Autowired
	private BundleMessage bungleMessage;

	/**
	 * Exception handling for business exception
	 *
	 * @param ex      exception
	 * @param request reqeust
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler({ AdnException.class })
	protected ResponseEntity<Object> handleAlgamoneyHandlerException(AdnException ex, WebRequest request) {
		ResponseError responseError = new ResponseErrorBuilder(bungleMessage).witchBusinesException(ex).build();
		return handleExceptionInternal(ex, Arrays.asList(responseError), new HttpHeaders(), HttpStatus.BAD_REQUEST,
				request);
	}


	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return handleExceptionInternal(ex, creteRespondeError(ex.getBindingResult()), headers, HttpStatus.BAD_REQUEST,
				request);
	}

	/**
	 * Prepare errors list of field validation
	 *
	 * @param bindingResult {@link BindingResult}
	 * @return {@link List} {@link ResponseError}
	 */
	private List<ResponseError> creteRespondeError(BindingResult bindingResult) {
		List<ResponseError> errors = new ArrayList<>();

		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			ResponseError error = new ResponseErrorBuilder(bungleMessage).withErrorCode(fieldError.getCode())
					.withErrorMessage(fieldError.toString()).withMessage(bungleMessage.getMessage(fieldError))
					.withStatusCode(HttpStatus.BAD_REQUEST).withArgsMessage(fieldError.getArguments()).build();
			errors.add(error);
		}
		return errors;
	}

}
