package com.example.tatwa10;

import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiService {

    public static final String BASE_URL = "http://192.168.0.102:5116/api/";

    // LOGIN
    public static String loginUser(String staffId, String password) {
        return post(BASE_URL + "Auth/login",
                "{\"staffId\":\"" + staffId + "\",\"password\":\"" + password + "\"}");
    }

    // REGISTER DOCTOR
    // ✅ CORRECT
    public static String registerDoctor(String staffId, String fullName, String password, String phone, String specialization) {

        try {
            JSONObject json = new JSONObject();
            json.put("staffId", staffId);
            json.put("fullName", fullName);
            json.put("passwordHash", password);
            json.put("phone", phone);
            json.put("specialization", specialization);

            return post(BASE_URL + "Doctors", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
    public static String getReviews(int doctorId) {
        return get(BASE_URL + "Reviews/" + doctorId);
    }
    // CREATE APPOINTMENT
    public static void createAppointment(String jsonData) {
        postRaw(BASE_URL + "appointments", jsonData);
    }

    // GET DOCTORS
    public static String getDoctors() {
        return get(BASE_URL + "doctors");
    }

    // REQUESTED
    public static String getRequestedAppointments() {
        return get(BASE_URL + "appointments/requested");
    }
    public static String reserveRoom(String json) {
        return post(BASE_URL + "rooms/reserve", json);
    }
    // ACCEPT
    public static void acceptAppointment(int id) {
        simpleCall(BASE_URL + "appointments/accept/" + id, "PUT");
    }

    // REJECT
    public static void rejectAppointment(int id) {
        simpleCall(BASE_URL + "appointments/reject/" + id, "DELETE");
    }

    // ACCEPTED
    public static String getAcceptedAppointments() {
        return get(BASE_URL + "appointments/accepted");
    }

    // COMPLETED
    public static String getCompletedAppointments() {
        return get(BASE_URL + "appointments/completed");
    }
    public static void cancelRoom(int id) {
        simpleCall(BASE_URL + "rooms/cancel/" + id, "PUT");
    }
    // COMPLETE
    public static void completeAppointment(int id) {

        try {

            URL url = new URL(BASE_URL + "appointments/complete/" + id);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();

            Log.d("COMPLETE_API", "Response Code: " + responseCode);

            if (responseCode == 200) {
                Log.d("COMPLETE_API", "SUCCESS");
            } else {
                Log.e("COMPLETE_API", "FAILED");
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("COMPLETE_API", "ERROR: " + e.getMessage());
        }
    }

    // NOTIFICATIONS
    public static String getNotifications(int patientId) {
        return get(BASE_URL + "notifications/" + patientId);
    }

    public static String getPrescriptions(int patientId) {
        return get(BASE_URL + "prescriptions/" + patientId);
    }
    public static String getAllPrescriptions() {
        return get(BASE_URL + "prescriptions");
    }
    // SAVE FCM TOKEN
    public static void saveFcmToken(int patientId, String token) {
        postRaw(BASE_URL + "patients/save-token/" + patientId, "\"" + token + "\"");
    }

    // 🔥 ROOMS (FINAL ADD)
    public static String getRoomsByStage(String stage) {
        return get(BASE_URL + "rooms/" + stage);
    }

    public static void createRoomReservation(String jsonData) {
        postRaw(BASE_URL + "rooms/reserve", jsonData);
    }

    // =========================
    // HELPERS
    // =========================
    public static String getRooms() {
        return get(BASE_URL + "rooms");
    }

    static String get(String urlStr) {

        StringBuilder result = new StringBuilder();

        try {

            Log.d("API_CALL", "Calling: " + urlStr);

            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            Log.d("API_CALL", "Response Code: " + responseCode);

            BufferedReader reader;

            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage(); // ✅ SHOW REAL ERROR
        }

        return result.toString();
    }
    public static String updatePatientDetails(int patientId, JSONObject data) {
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(BASE_URL + "patients/update-details/" + patientId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            os.write(data.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            Log.d("UPDATE_API", "Response Code: " + responseCode);
            Log.d("UPDATE_API", "Sent JSON: " + data.toString());

            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }

        return result.toString();
    }
    public static String post(String urlStr, String json) {

        StringBuilder result = new StringBuilder();

        try {

            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();

            BufferedReader reader;

            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }

        return result.toString();
    }
    public static String bookAppointment(String json) {
        return post(BASE_URL + "appointments", json);
    }
    public static boolean isValidJsonArray(String response) {
        if (response == null) return false;
        response = response.trim();
        return response.startsWith("[") && response.endsWith("]");
    }
    private static void postRaw(String urlStr, String json) {

        try {

            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();

            connection.getResponseCode();
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String simpleCall(String urlStr, String method) {

        StringBuilder result = new StringBuilder();

        try {

            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();

            BufferedReader reader;

            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return result.toString();
    }

    // =========================
// REGISTER LAB
// =========================
    public static String registerLab(String staffId, String fullName, String password) {

        return post(BASE_URL + "Auth/register-lab",
                "{\"staffId\":\"" + staffId + "\","
                        + "\"fullName\":\"" + fullName + "\","
                        + "\"password\":\"" + password + "\"}");
    }

    public static String registerAdmin(String staffId, String fullName, String password) {
        return post(BASE_URL + "Auth/register-admin",
                "{\"staffId\":\"" + staffId + "\","
                        + "\"fullName\":\"" + fullName + "\","
                        + "\"password\":\"" + password + "\"}");
    }

    // =========================
// REGISTER PATIENT
// =========================
    public static String registerPatient(String name, String email, String password) {
        return post(BASE_URL + "Patients/register",
                "{\"fullName\":\"" + name + "\","
                        + "\"email\":\"" + email + "\","
                        + "\"password\":\"" + password + "\"}");
    }

    // =========================
// LOGIN PATIENT
// =========================
    public static String loginPatient(String email, String password) {
        return post(BASE_URL + "Patients/login",
                "{\"email\":\"" + email + "\","
                        + "\"password\":\"" + password + "\"}");
    }


    public static String getRoomHistory(int id) {
        return get(BASE_URL + "Rooms/history/" + id);
    }

    public static String getPatientById(int patientId) {
        return get(BASE_URL + "patients/" + patientId);    }
    public static String getPatients() {
        return get(BASE_URL + "patients");
    }


    public static String payAppointment(int id) {
        return simpleCall(BASE_URL + "appointments/pay/" + id, "PUT");
    }
    public static String getPrescriptionsByPatient(int patientId) {
        return get(BASE_URL + "prescriptions/patient/" + patientId);
    }


    public static String getDoctorPrescriptions(int doctorId) throws Exception {
        String url = BASE_URL + "prescriptions/doctor/" + doctorId;
        return get(url);
    }
    public static String addPrescription(String json) throws Exception {
        String url = BASE_URL + "prescriptions/add";
        return post(url, json);
    }

   }


