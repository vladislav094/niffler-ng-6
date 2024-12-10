package guru.qa.niffler.controller;

import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository usersRepository;

  @Test
  void currentUserEndpoint() throws Exception {
    UserEntity userDataEntity = new UserEntity();
    userDataEntity.setUsername("dima");
    userDataEntity.setCurrency(CurrencyValues.RUB);
    usersRepository.save(userDataEntity);

    mockMvc.perform(get("/internal/users/current")
            .contentType(MediaType.APPLICATION_JSON)
            .param("username", "dima")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("dima"));
  }

  @Test
  void allUsersEndpoint() throws Exception {
    UserEntity firstUser = new UserEntity();
    firstUser.setUsername("a.user");
    firstUser.setCurrency(CurrencyValues.RUB);
    UserEntity secondUser = new UserEntity();
    secondUser.setUsername("b.user");
    secondUser.setCurrency(CurrencyValues.RUB);

    usersRepository.save(firstUser);
    usersRepository.save(secondUser);
    List<UserEntity> allUsers = usersRepository.findAll();
    System.out.println(allUsers.size());

    mockMvc.perform(get("/internal/users/all")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("username", "a")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2))) // Проверяем, что массив содержит 2 элемента
            .andExpect(jsonPath("$[0].username").value("a.user")) // Проверяем первого пользователя
            .andExpect(jsonPath("$[1].username").value("b.user")); // Проверяем второго пользователя
  }
}