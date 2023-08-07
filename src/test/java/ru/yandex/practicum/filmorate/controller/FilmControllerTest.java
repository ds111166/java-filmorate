package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldFirstGetFilms() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Collection<Film> films = objectMapper.readValue(response, new TypeReference<Collection<Film>>() {
        });
        assertEquals(films.size(), 0, "Неверное количество созданных фильмов!");
        List<Film> newFilms = new ArrayList<>();
        newFilms.add(Film.builder()
                .name("Name1")
                .description("this film1")
                .duration(120)
                .releaseDate(LocalDate.of(1990, 1, 1))
                .build());
        newFilms.add(Film.builder()
                .name("Name2")
                .description("this film2")
                .duration(120)
                .releaseDate(LocalDate.of(1990, 1, 1))
                .build());
        newFilms.add(Film.builder()
                .name("Name3")
                .description("this film3")
                .duration(120)
                .releaseDate(LocalDate.of(1990, 1, 1))
                .build());
        for (Film film : newFilms) {
            final MvcResult mvcResult1 = mockMvc.perform(post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(film)))
                    .andExpect(status().isCreated()).andReturn();
            final String contentAsString = mvcResult1.getResponse().getContentAsString();
            final Film currentFilm = objectMapper.readValue(contentAsString, Film.class);
            film.setId(currentFilm.getId());
        }
        mvcResult = mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        response = mvcResult.getResponse().getContentAsString();
        films = objectMapper.readValue(response, new TypeReference<Collection<Film>>() {
        });
        final List<Integer> filmsIds = films.stream().map(Film::getId).collect(Collectors.toList());
        final List<Integer> newFilmIds = newFilms.stream().map(Film::getId).collect(Collectors.toList());
        assertEquals(filmsIds.size(), newFilmIds.size(), "количество созданных фильмов не корректно");
        assertTrue(filmsIds.containsAll(newFilmIds), "не верный список идентификаторо фильмов");

    }

    @Test
    public void shouldCreateFilm() throws Exception {
        Film newFilm = Film.builder()
                .name("Name")
                .description("this film")
                .duration(120)
                .releaseDate(LocalDate.of(1990, 1, 1))
                .build();
        final ResultActions resultActions = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.description").value("this film"))
                .andExpect(jsonPath("$.duration").value(120))
                .andExpect(jsonPath("$.releaseDate").value("1990-01-01"));
        final MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        final Film film = objectMapper.readValue(response, Film.class);
        resultActions.andExpect(jsonPath("$.id").value(film.getId()));
    }

    @Test
    public void shouldNotCreateFilmWithEmptyTitle() throws Exception {
        Film newFilm = Film.builder()
                .description("null name film")
                .duration(120)
                .releaseDate(LocalDate.of(1990, 1, 1))
                .build();
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateFilmWithDescriptionOfMoreThan200Characters() throws Exception {
        Film newFilm = Film.builder()
                .name("Name")
                .description("len description > 200 characters !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                .duration(120)
                .releaseDate(LocalDate.of(1991, 12, 1))
                .build();
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateFilmWithReleaseDateEarlierThan_1895_12_28() throws Exception {
        Film newFilm = Film.builder()
                .name("Name")
                .description("date reliase film  < 1895.12.28")
                .duration(120)
                .releaseDate(LocalDate.of(1895, 12, 27))
                .build();
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateFilmWithNonPositiveDuration() throws Exception {

        Film newFilm = Film.builder()
                .name("Name")
                .description("film duration <= 0")
                .duration(0)
                .releaseDate(LocalDate.of(1895, 12, 27))
                .build();
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCreateAndUpdateFilm() throws Exception {
        Film newFilm = Film.builder()
                .name("Name")
                .description("film desription")
                .duration(10)
                .releaseDate(LocalDate.of(1995, 12, 27))
                .build();

        final ResultActions resultActions = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.description").value("film desription"))
                .andExpect(jsonPath("$.duration").value(10))
                .andExpect(jsonPath("$.releaseDate").value("1995-12-27"));

        final MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        final Film film = objectMapper.readValue(response, Film.class);
        final Integer filmId = film.getId();
        resultActions.andExpect(jsonPath("$.id").value(filmId));

        newFilm.setId(filmId);
        newFilm.setDescription("film upadate");
        newFilm.setDuration(100);
        newFilm.setReleaseDate(LocalDate.of(1995, 12, 30));
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(filmId))
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.description").value("film upadate"))
                .andExpect(jsonPath("$.duration").value(100))
                .andExpect(jsonPath("$.releaseDate").value("1995-12-30"));
    }

    @Test
    public void shouldCreateAndupdateFilm_recordNotFound() throws Exception {
        Film newFilm = Film.builder()
                .name("Name")
                .description("film desription")
                .duration(10)
                .releaseDate(LocalDate.of(1995, 12, 27))
                .build();

        final ResultActions resultActions = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.description").value("film desription"))
                .andExpect(jsonPath("$.duration").value(10))
                .andExpect(jsonPath("$.releaseDate").value("1995-12-27"));

        final MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        final Film film = objectMapper.readValue(response, Film.class);
        final Integer filmId = film.getId();
        resultActions.andExpect(jsonPath("$.id").value(filmId));

        newFilm.setId(999888);
        newFilm.setDescription("film upadate");
        newFilm.setDuration(100);
        newFilm.setReleaseDate(LocalDate.of(1995, 12, 30));
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isNotFound());
    }
}
