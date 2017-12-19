package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.LibreriaApp;

import com.mycompany.myapp.domain.Libro;
import com.mycompany.myapp.repository.LibroRepository;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.domain.enumeration.Estado;
/**
 * Test class for the LibroResource REST controller.
 *
 * @see LibroResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LibreriaApp.class)
public class LibroResourceIntTest {

    private static final String DEFAULT_COD_LIBRO = "AAAAAAAAAA";
    private static final String UPDATED_COD_LIBRO = "BBBBBBBBBB";

    private static final Estado DEFAULT_ESTADO = Estado.PRESTADO;
    private static final Estado UPDATED_ESTADO = Estado.RESERVADO;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restLibroMockMvc;

    private Libro libro;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LibroResource libroResource = new LibroResource(libroRepository);
        this.restLibroMockMvc = MockMvcBuilders.standaloneSetup(libroResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Libro createEntity(EntityManager em) {
        Libro libro = new Libro()
            .codLibro(DEFAULT_COD_LIBRO)
            .estado(DEFAULT_ESTADO);
        return libro;
    }

    @Before
    public void initTest() {
        libro = createEntity(em);
    }

    @Test
    @Transactional
    public void createLibro() throws Exception {
        int databaseSizeBeforeCreate = libroRepository.findAll().size();

        // Create the Libro
        restLibroMockMvc.perform(post("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(libro)))
            .andExpect(status().isCreated());

        // Validate the Libro in the database
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeCreate + 1);
        Libro testLibro = libroList.get(libroList.size() - 1);
        assertThat(testLibro.getCodLibro()).isEqualTo(DEFAULT_COD_LIBRO);
        assertThat(testLibro.getEstado()).isEqualTo(DEFAULT_ESTADO);
    }

    @Test
    @Transactional
    public void createLibroWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = libroRepository.findAll().size();

        // Create the Libro with an existing ID
        libro.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLibroMockMvc.perform(post("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(libro)))
            .andExpect(status().isBadRequest());

        // Validate the Libro in the database
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCodLibroIsRequired() throws Exception {
        int databaseSizeBeforeTest = libroRepository.findAll().size();
        // set the field null
        libro.setCodLibro(null);

        // Create the Libro, which fails.

        restLibroMockMvc.perform(post("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(libro)))
            .andExpect(status().isBadRequest());

        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLibros() throws Exception {
        // Initialize the database
        libroRepository.saveAndFlush(libro);

        // Get all the libroList
        restLibroMockMvc.perform(get("/api/libros?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(libro.getId().intValue())))
            .andExpect(jsonPath("$.[*].codLibro").value(hasItem(DEFAULT_COD_LIBRO.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())));
    }

    @Test
    @Transactional
    public void getLibro() throws Exception {
        // Initialize the database
        libroRepository.saveAndFlush(libro);

        // Get the libro
        restLibroMockMvc.perform(get("/api/libros/{id}", libro.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(libro.getId().intValue()))
            .andExpect(jsonPath("$.codLibro").value(DEFAULT_COD_LIBRO.toString()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLibro() throws Exception {
        // Get the libro
        restLibroMockMvc.perform(get("/api/libros/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLibro() throws Exception {
        // Initialize the database
        libroRepository.saveAndFlush(libro);
        int databaseSizeBeforeUpdate = libroRepository.findAll().size();

        // Update the libro
        Libro updatedLibro = libroRepository.findOne(libro.getId());
        // Disconnect from session so that the updates on updatedLibro are not directly saved in db
        em.detach(updatedLibro);
        updatedLibro
            .codLibro(UPDATED_COD_LIBRO)
            .estado(UPDATED_ESTADO);

        restLibroMockMvc.perform(put("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLibro)))
            .andExpect(status().isOk());

        // Validate the Libro in the database
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeUpdate);
        Libro testLibro = libroList.get(libroList.size() - 1);
        assertThat(testLibro.getCodLibro()).isEqualTo(UPDATED_COD_LIBRO);
        assertThat(testLibro.getEstado()).isEqualTo(UPDATED_ESTADO);
    }

    @Test
    @Transactional
    public void updateNonExistingLibro() throws Exception {
        int databaseSizeBeforeUpdate = libroRepository.findAll().size();

        // Create the Libro

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restLibroMockMvc.perform(put("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(libro)))
            .andExpect(status().isCreated());

        // Validate the Libro in the database
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteLibro() throws Exception {
        // Initialize the database
        libroRepository.saveAndFlush(libro);
        int databaseSizeBeforeDelete = libroRepository.findAll().size();

        // Get the libro
        restLibroMockMvc.perform(delete("/api/libros/{id}", libro.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Libro.class);
        Libro libro1 = new Libro();
        libro1.setId(1L);
        Libro libro2 = new Libro();
        libro2.setId(libro1.getId());
        assertThat(libro1).isEqualTo(libro2);
        libro2.setId(2L);
        assertThat(libro1).isNotEqualTo(libro2);
        libro1.setId(null);
        assertThat(libro1).isNotEqualTo(libro2);
    }
}
