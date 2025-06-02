import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import static org.junit.jupiter.api.Assertions.*;

public class EndToEndTest {

    private static final String BASE_URL = "http://localhost:3000/patients";
    private static final HttpClient client = HttpClient.newHttpClient();

    private int generateUniqueId() {
        return (int) (System.currentTimeMillis() % 10);
    }

    private String buildPatientJson(
            int id, String firstName, String oms, boolean genderMale, String date_of_birth) {
        return String.format("""
            {
                "id": %d,
                "first_name": "%s",
                "second_name": "Test",
                "oms_policy_number": "%s",
                "gender_male": %s,
                "date_of_birth": "%s"
            }
        """, id, firstName, oms, genderMale, date_of_birth);
    }

    @org.junit.jupiter.api.Test
    public void testCreatePatient() throws Exception {
        int id = generateUniqueId();
        String json = buildPatientJson(
                id,
                "Alice",
                "1112223334445555",
                true,
                "1111-11-11"
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @org.junit.jupiter.api.Test
    public void testUpdatePatient() throws Exception {
        int id = generateUniqueId();
        String originalJson = buildPatientJson(
                id,
                "Bob",
                "0001112223334444",
                true,
                "1111-11-11");
        String updatedJson = buildPatientJson(
                id,
                "Updated",
                "9998887776665551",
                false,
                "1111-11-11");

        // Create
        HttpRequest create = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(originalJson))
                .build();

        client.send(create, BodyHandlers.ofString());

        // Update
        HttpRequest update = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(BodyPublishers.ofString(updatedJson))
                .build();

        HttpResponse<String> updateResponse = client.send(update, BodyHandlers.ofString());
        assertEquals(200, updateResponse.statusCode());
    }

    @org.junit.jupiter.api.Test
    public void testGetAllPatientsIncludesNewPatient() throws Exception {
        var id = generateUniqueId();
        var name = "TestName";
        String json = buildPatientJson(
                id,
                name,
                "5554443332221111",
                true,
                "1111-11-11");

        // Create
        HttpRequest create = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(json))
                .build();

        var r = client.send(create, BodyHandlers.ofString());

        // Get All
        HttpRequest getAll = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(getAll, BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(name));
    }

    @org.junit.jupiter.api.Test
    public void testDeletePatient() throws Exception {
        int id = generateUniqueId();
        String json = buildPatientJson(
                id,
                "Delete",
                "444333222111000",
                true,
                "1111-11-11");

        // Create
        HttpRequest create = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(json))
                .build();

        client.send(create, BodyHandlers.ofString());

        // Delete
        HttpRequest delete = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(delete, BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode());
    }

    public static HttpRequest buildRequest(String json) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(json))
                .build();
    }

    @org.junit.jupiter.api.Test
    public void testInvalidOmsNumber() throws Exception {
        int id = generateUniqueId();
        String json = buildPatientJson(
                id,
                "Alice",
                "ABC123",
                true,
                "1111-11-11"
        );

        var request = buildRequest(json);

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        assertNotEquals(201, response.statusCode());
        assertTrue(response.body().contains("Invalid"));
    }

    @org.junit.jupiter.api.Test
    public void testInvalidDateOfBirth() throws Exception {
        int id = generateUniqueId();
        String json = buildPatientJson(
                id,
                "Bob",
                "1234567890123456",
                true,
                "1111-222-11"
        );

        var request = buildRequest(json);

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        assertNotEquals(201, response.statusCode());
        assertTrue(response.body().contains("Invalid"));
    }

    @org.junit.jupiter.api.Test
    public void testMissingRequiredField() throws Exception {
        int id = generateUniqueId();
        String json = """
        {
            "id": %d,
            "second_name": "Smith",
            "date_of_birth": "2000-01-01",
            "oms_policy_number": "1234567890123456",
            "gender_male": true
        }
    """.formatted(id);

        var request = buildRequest(json);

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        assertNotEquals(201, response.statusCode());
    }


}

