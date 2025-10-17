package com.example.rockStadium.repository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.rockStadium.model.Profile;
import com.example.rockStadium.model.User;
import com.example.rockStadium.model.UserPreference;

/**
 * Pruebas de integración para UserPreferenceRepository
 * Valida las operaciones de persistencia con la base de datos
 */
@DataJpaTest
class UserPreferenceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    private User testUser;
    private Profile testProfile;
    private UserPreference testPreference;

    @BeforeEach
    void setUp() {
        // Crear usuario de prueba
        testUser = User.builder()
                .email("test@example.com")
                .password("hashedPassword123")
                .build();
        entityManager.persist(testUser);

        // Crear perfil de prueba
        testProfile = Profile.builder()
                .name("Test Profile")
                .user(testUser)
                .build();
        entityManager.persist(testProfile);

        // Crear preferencia de prueba
        testPreference = UserPreference.builder()
                .profile(testProfile)
                .searchRadius(BigDecimal.valueOf(10.0))
                .emailNotifications(true)
                .build();

        entityManager.flush();
    }

    @Test
    void testSaveUserPreference_Success() {
        // Guardar preferencia
        UserPreference saved = userPreferenceRepository.save(testPreference);
        entityManager.flush();

        // Verificar que se guardó correctamente
        assertThat(saved).isNotNull();
        assertThat(saved.getUserPreferenceId()).isNotNull();
        assertThat(saved.getSearchRadius()).isEqualByComparingTo(BigDecimal.valueOf(10.0));
        assertThat(saved.getEmailNotifications()).isTrue();
        assertThat(saved.getProfile()).isEqualTo(testProfile);
    }

    @Test
    void testFindById_Success() {
        // Guardar preferencia
        UserPreference saved = userPreferenceRepository.save(testPreference);
        entityManager.flush();

        // Buscar por ID
        Optional<UserPreference> found = userPreferenceRepository.findById(saved.getUserPreferenceId());

        // Verificar que se encontró
        assertThat(found).isPresent();
        assertThat(found.get().getUserPreferenceId()).isEqualTo(saved.getUserPreferenceId());
        assertThat(found.get().getSearchRadius()).isEqualByComparingTo(BigDecimal.valueOf(10.0));
    }

    @Test
    void testFindById_NotFound() {
        // Buscar ID inexistente
        Optional<UserPreference> found = userPreferenceRepository.findById(999);

        // Verificar que no se encontró
        assertThat(found).isEmpty();
    }

    @Test
    void testUpdateUserPreference_Success() {
        // Guardar preferencia inicial
        UserPreference saved = userPreferenceRepository.save(testPreference);
        entityManager.flush();

        // Actualizar valores
        saved.setSearchRadius(BigDecimal.valueOf(25.0));
        saved.setEmailNotifications(false);

        // Guardar actualización
        UserPreference updated = userPreferenceRepository.save(saved);
        entityManager.flush();

        // Verificar actualización
        Optional<UserPreference> found = userPreferenceRepository.findById(updated.getUserPreferenceId());
        assertThat(found).isPresent();
        assertThat(found.get().getSearchRadius()).isEqualByComparingTo(BigDecimal.valueOf(25.0));
        assertThat(found.get().getEmailNotifications()).isFalse();
    }

    @Test
    void testDeleteUserPreference_Success() {
        // Guardar preferencia
        UserPreference saved = userPreferenceRepository.save(testPreference);
        entityManager.flush();
        Integer preferenceId = saved.getUserPreferenceId();

        // Eliminar preferencia
        userPreferenceRepository.delete(saved);
        entityManager.flush();

        // Verificar que se eliminó
        Optional<UserPreference> found = userPreferenceRepository.findById(preferenceId);
        assertThat(found).isEmpty();
    }

    @Test
    void testSearchRadiusPrecision() {
        // Guardar con diferentes valores de precisión
        testPreference.setSearchRadius(BigDecimal.valueOf(15.75));
        UserPreference saved = userPreferenceRepository.save(testPreference);
        entityManager.flush();

        // Verificar precisión decimal
        Optional<UserPreference> found = userPreferenceRepository.findById(saved.getUserPreferenceId());
        assertThat(found).isPresent();
        assertThat(found.get().getSearchRadius()).isEqualByComparingTo(BigDecimal.valueOf(15.75));
    }

    @Test
    void testOneToOneRelationshipWithProfile() {
        // Guardar preferencia
        UserPreference saved = userPreferenceRepository.save(testPreference);
        entityManager.flush();
        entityManager.clear(); // Limpiar el contexto de persistencia

        // Buscar preferencia y verificar relación con profile
        Optional<UserPreference> found = userPreferenceRepository.findById(saved.getUserPreferenceId());
        assertThat(found).isPresent();
        assertThat(found.get().getProfile()).isNotNull();
        assertThat(found.get().getProfile().getProfileId()).isEqualTo(testProfile.getProfileId());
        assertThat(found.get().getProfile().getName()).isEqualTo("Test Profile");
    }

    @Test
    void testNullableFields() {
        // Crear preferencia con valores nulos permitidos
        UserPreference preferenceWithNulls = UserPreference.builder()
                .profile(testProfile)
                .searchRadius(null) // Permitido
                .emailNotifications(null) // Permitido
                .build();

        // Guardar
        UserPreference saved = userPreferenceRepository.save(preferenceWithNulls);
        entityManager.flush();

        // Verificar que se guardó con valores nulos
        Optional<UserPreference> found = userPreferenceRepository.findById(saved.getUserPreferenceId());
        assertThat(found).isPresent();
        assertThat(found.get().getSearchRadius()).isNull();
        assertThat(found.get().getEmailNotifications()).isNull();
    }

    @Test
    void testFindAll_Success() {
        // Guardar múltiples preferencias
        userPreferenceRepository.save(testPreference);

        // Crear segunda preferencia para otro perfil
        User anotherUser = User.builder()
                .email("another@example.com")
                .password("password456")
                .build();
        entityManager.persist(anotherUser);

        Profile anotherProfile = Profile.builder()
                .name("Another Profile")
                .user(anotherUser)
                .build();
        entityManager.persist(anotherProfile);

        UserPreference anotherPreference = UserPreference.builder()
                .profile(anotherProfile)
                .searchRadius(BigDecimal.valueOf(20.0))
                .emailNotifications(false)
                .build();
        userPreferenceRepository.save(anotherPreference);

        entityManager.flush();

        // Buscar todas las preferencias
        Iterable<UserPreference> allPreferences = userPreferenceRepository.findAll();

        // Verificar que existen al menos 2
        assertThat(allPreferences).isNotEmpty();
        assertThat(allPreferences).hasSize(2);
    }

    @Test
    void testExistsById_True() {
        // Guardar preferencia
        UserPreference saved = userPreferenceRepository.save(testPreference);
        entityManager.flush();

        // Verificar existencia
        boolean exists = userPreferenceRepository.existsById(saved.getUserPreferenceId());
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsById_False() {
        // Verificar que no existe
        boolean exists = userPreferenceRepository.existsById(999);
        assertThat(exists).isFalse();
    }

    @Test
    void testCount() {
        // Guardar preferencia
        userPreferenceRepository.save(testPreference);
        entityManager.flush();

        // Verificar conteo
        long count = userPreferenceRepository.count();
        assertThat(count).isGreaterThanOrEqualTo(1);
    }
}