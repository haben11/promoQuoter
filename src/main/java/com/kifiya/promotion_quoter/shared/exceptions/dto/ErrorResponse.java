package com.kifiya.promotion_quoter.shared.exceptions.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
   LocalDateTime timeStamp,
   int status,
   String error,
   String errorMessage,
   String path,
   List<String> errors
) {}
