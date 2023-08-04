package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@WebMvcTest(UserController.class)
/*
https://github.com/eugenp/tutorials/blob/master/spring-boot-modules/spring-boot-validation/src/test/java/com/baeldung/beanvalidation/application/UserControllerIntegrationTest.java
https://www.baeldung.com/spring-boot-bean-validation
https://www.baeldung.com/spring-boot-testing
https://www.bezkoder.com/spring-boot-webmvctest/
https://www.baeldung.com/junit-5-runwith
https://www.javacodegeeks.com/2019/09/spring-boot-testing-junit-5.html
https://javabydeveloper.com/spring-boot-junit-5-test-example/
https://habr.com/ru/articles/561520/

 */
@AutoConfigureMockMvc
class UserControllerTest {
    //@Autowired
    //private UserController userController;
    @Autowired(required=true)
    private MockMvc mockMvc;

    /*@Test
    public void whenUserControllerInjected_thenNotNull() throws Exception {
        assertThat(userController).isNotNull();
    }*/
    /*


    @Test
    @DisplayName("Subscription message service test ")
    void testSubscriptionMessage() {
        userController.getUsers();
        String user = "Peter";

        String message = messageService.getSubscriptionMessage(user);
        assertEquals("Hello "+user+", Thanks for the subscription!", message);
    }

     */
    @Test
    public void whenGetRequestToUsers_thenCorrectResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

}