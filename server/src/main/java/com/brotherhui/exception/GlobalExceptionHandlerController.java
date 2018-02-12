package com.brotherhui.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlerController {

	@ExceptionHandler(Throwable.class)
	public String handleException(Exception e) {
		e.printStackTrace();
		return "error server";
	}
	
}