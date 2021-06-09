package ru.netology.domain;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class UserGenerator {

    private UserGenerator() {
    }

    public static String generateDate(int shift) {
        return LocalDate.now().plusDays(shift).format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
    }

    public static String generateCity(String locale) {
        // TODO: добавить логику для объявления переменной city и задания её значения, генерацию можно выполнить
        // с помощью Faker, либо используя массив валидных городов и класс Random
        Faker faker = new Faker(Locale.forLanguageTag(locale));
        return faker.address().cityName();
    }

    public static String generateName(String locale) {
        Faker faker = new Faker(Locale.forLanguageTag(locale));
        return faker.name().fullName();
    }

    public static String generatePhone(String locale) {
        Faker faker = new Faker(Locale.forLanguageTag(locale));
        return faker.numerify("+############");
    }

    public static class Registration {
        private Registration() {
        }

        public static User generateUser(String locale) {
            return new User(generateCity(locale), generateName(locale), generatePhone(locale));
        }
    }

    @Value
    public static class User {
        String city;
        String name;
        String phone;

        User(String city, String name, String phone) {
            this.city = city;
            this.name = name;
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public String getCity() {
            return city;
        }

        public String getPhone() {
            return phone;
        }
    }

}
