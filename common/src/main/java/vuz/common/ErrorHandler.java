package vuz.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// ловит ошибки всех контроллеров и отдаёт {"error": "..."}
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> api(ApiException e) {
        return error(e.getStatus(), e.getMessage());
    }

    // ошибки валидации (@Valid), собираем все сообщения в одну строку
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException e) {
        List<String> messages = new ArrayList<>();
        for (FieldError field : e.getBindingResult().getFieldErrors()) {
            messages.add(field.getDefaultMessage());
        }
        Collections.sort(messages);
        return error(HttpStatus.BAD_REQUEST, "Ошибка в данных: " + String.join("; ", messages));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> database(DataAccessException e) {
        Throwable t = e;
        while (t != null && !(t instanceof SQLException)) {
            t = t.getCause();
        }
        if (t == null) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка БД: " + e.getMessage());
        }
        SQLException sql = (SQLException) t;
        return fromSqlState(sql.getSQLState(), sql.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> badJson() {
        return error(HttpStatus.BAD_REQUEST, "Некорректный JSON или неверный формат данных");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> badParam(MethodArgumentTypeMismatchException e) {
        if ("id".equals(e.getName())) {
            return error(HttpStatus.BAD_REQUEST, "id должен быть числом");
        }
        return error(HttpStatus.BAD_REQUEST, "Неверное значение параметра " + e.getName());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> badMethod(HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/events/end-of-year")) {
            return error(HttpStatus.METHOD_NOT_ALLOWED, "Используйте POST");
        }
        return error(HttpStatus.METHOD_NOT_ALLOWED, "Метод не поддерживается");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> unknownPath() {
        return error(HttpStatus.NOT_FOUND, "Неизвестный адрес");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> other(Exception e) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
    }

    // коды ошибок postgres -> http
    public static ResponseEntity<Map<String, String>> fromSqlState(String state, String msg) {
        if (state == null) state = "";
        switch (state) {
            case "23505":
                return error(HttpStatus.CONFLICT, "Нарушена уникальность: " + msg);
            case "23503":
                return error(HttpStatus.CONFLICT, "Нарушена связь между таблицами: " + msg);
            case "23514":
                return error(HttpStatus.BAD_REQUEST, "Недопустимое значение поля: " + msg);
            case "23502":
                return error(HttpStatus.BAD_REQUEST, "Не заполнено обязательное поле: " + msg);
            case "22007":
            case "22008":
            case "22P02":
                return error(HttpStatus.BAD_REQUEST, "Неверный формат данных: " + msg);
            default:
                return error(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка БД: " + msg);
        }
    }

    private static ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
