package vuz.person;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import vuz.person.entity.Person;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Person goodPerson() {
        Person p = new Person();
        p.setName("Анна");
        p.setSurname("Новикова");
        p.setBirthDate(LocalDate.of(2007, 4, 12));
        p.setGender("Ж");
        p.setSnils("999-888-777 66");
        return p;
    }

    @Test
    void correctPersonHasNoErrors() {
        assertTrue(validator.validate(goodPerson()).isEmpty());
    }

    @Test
    void snilsWithoutNumberIsOk() {
        Person p = goodPerson();
        p.setSnils(null);
        assertTrue(validator.validate(p).isEmpty());
    }

    @Test
    void wrongGenderAndSnils() {
        Person p = goodPerson();
        p.setGender("X");
        p.setSnils("12345");
        Set<ConstraintViolation<Person>> errors = validator.validate(p);
        assertEquals(2, errors.size());
    }

    @Test
    void emptyNameAndFutureBirthDate() {
        Person p = goodPerson();
        p.setName(" ");
        p.setBirthDate(LocalDate.now().plusDays(1));
        assertEquals(2, validator.validate(p).size());
    }
}
