package ru.netology;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.domain.UserGenerator;

import java.time.Duration;
import java.util.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppCardDeliveryTest {
    Date date = new Date();
    Calendar cal = Calendar.getInstance();
    UserGenerator.User user = UserGenerator.Registration.generateUser("ru");
    private SelenideElement dateField = $("[data-test-id='date'] .input__control");
    private SelenideElement cityField = $("[data-test-id='city'] .input__control");
    private SelenideElement phoneField = $("[data-test-id='phone'] .input__control");
    private SelenideElement nameField = $("[data-test-id='name'] .input__control");
    private SelenideElement agreementTick = $("[data-test-id='agreement']");
    private SelenideElement successNotification = $("[data-test-id='success-notification']");

    @BeforeAll
    static void headless() {
        Configuration.headless = true;
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void setUpDate() {
        cal.setTime(date);
        open("http://localhost:9999");
    }

    public void setDate(int shift) {
        //this hack is here because .clear() doesn't work :(
        dateField.sendKeys(Keys.CONTROL + "A");
        dateField.sendKeys(Keys.BACK_SPACE);
        dateField.setValue(UserGenerator.generateDate(shift));
    }

    public void inputName(boolean set) {
        if (set) {
            nameField.setValue(user.getName());
        }
    }

    public void inputCity(boolean set) {
        if (set) {
            cityField.sendKeys(user.getCity());
        }
    }

    public void inputPhone(boolean set) {
        if (set) {
            phoneField.setValue(user.getPhone());
        }
    }

    public void tickAgreement(boolean set) {
        if (set) {
            agreementTick.click();
        }
    }

    public void clickButton(boolean set) {
        if (set) {
            $(".button").click();
        }
    }

    public void fillIn(boolean name, boolean city, boolean phone, boolean agreement, boolean button) {
        inputName(name);
        inputCity(city);
        inputPhone(phone);
        tickAgreement(agreement);
        clickButton(button);
    }

    @Test
    public void positiveTest() {
        setDate(5);
        fillIn(true, true, true, true, true);
        successNotification.shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    public void negativeCityOutOfBoundsTest() {
        cityField.sendKeys("Корсаков");
        setDate(5);
        fillIn(true, false, true, true, true);
        $("[data-test-id='city'].input_invalid").shouldBe(visible).shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    public void negativeNonRussianCityTest() {
        cityField.sendKeys(UserGenerator.generateCity("en"));
        setDate(5);
        fillIn(true, false, true, true, true);
        $("[data-test-id='city'].input_invalid").shouldBe(visible).shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    public void negativeNoCityTest() {
        setDate(5);
        fillIn(true, false, true, true, true);
        $("[data-test-id='city'].input_invalid .input__sub").shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    public void negativeNonRussianNameTest() {
        nameField.setValue(UserGenerator.generateName("en"));
        fillIn(false, true, true, true, true);
        $("[data-test-id='name'].input_invalid .input__sub").shouldBe(visible).shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    public void yoNameTest() {
        nameField.setValue("Алёна");
        setDate(5);
        fillIn(false, true, true, true, true);
        successNotification.shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    public void negativeNonAlphaNameTest() {
        nameField.setValue("А! овар,.");
        setDate(5);
        fillIn(false, true, true, true, true);
        $("[data-test-id='name'].input_invalid .input__sub").shouldBe(visible).shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    public void negativeNoNameTest() {
        setDate(5);
        fillIn(false, true, true, true, true);
        $("[data-test-id='name'].input_invalid .input__sub").shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    public void negativeAnyPhoneTest() {
        phoneField.setValue("000");
        setDate(5);
        fillIn(true, true, false, true, true);
        $("[data-test-id='phone'].input_invalid .input__sub").shouldBe(visible);
    }

    @Test
    public void negativeNonNumPhoneTest() {
        phoneField.setValue("lksajdahfdf");
        setDate(5);
        fillIn(true, true, false, true, true);
        phoneField.shouldHave(attribute("value", "+"));
    }

    @Test
    public void negativeNoPhoneTest() {
        setDate(5);
        fillIn(true, true, false, true, true);
        $("[data-test-id='phone'].input_invalid .input__sub").shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    public void autofillTest() {
        cityField.sendKeys("Мо");
        $$(".menu-item .menu-item__control").find(exactText("Москва")).click();
        cityField.shouldHave(attribute("value", "Москва"));
        setDate(5);
        fillIn(true, false, true, true, true);
        successNotification.shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    public void calendarWidgetTest() {
        dateField.click();

        Calendar newCal = Calendar.getInstance();//get a new calendar to track the new date
        newCal.add(newCal.DATE, 7);
        String newDate = newCal.get(newCal.DAY_OF_MONTH) + "";
        if (cal.get(cal.MONTH) != newCal.get(newCal.MONTH)) {//click the right arrow if the new and old months don't match
            $$(".calendar__arrow_direction_right").last().click();
        }

        //set up the check of the new month's number
        $$(".calendar__row .calendar__day[data-day]").find(exactText(newDate)).click();//set the new date
        String actual = dateField.getValue().substring(3, 5);//get the numeric value of the new month
        int temp = newCal.get(newCal.MONTH) + 1; //get the numeric value of the expected month (zero-indexed)
        String expected = temp + ""; //convert it to string
        if (temp < 10) {//pad with a zero if September or earlier
            expected = "0" + expected;
        }

        $(".button").click();
        fillIn(true, true, true, true, true);
        successNotification.shouldBe(visible, Duration.ofSeconds(15));
        assertEquals(actual, expected);
    }

    @Test
    public void rescheduleTest() {
        fillIn(true, true, true, true, true);
        setDate(7);
        clickButton(true);
        $(".button.button_size_s").shouldHave(exactText("Перепланировать")).shouldBe(visible, Duration.ofSeconds(7)).click();
        successNotification.shouldBe(visible, Duration.ofSeconds(15));
    }
}