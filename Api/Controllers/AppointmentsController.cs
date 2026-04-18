using HospitalAPI.Data;
using HospitalAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using FirebaseAdmin.Messaging;
using System.Collections.Generic;
namespace HospitalAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AppointmentsController : ControllerBase
    {
        private readonly HospitalDbContext _context;

        public AppointmentsController(HospitalDbContext context)
        {
            _context = context;
        }

        // ==========================
        // GET ALL APPOINTMENTS
        // ==========================
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Appointment>>> GetAppointments()
        {
            return await _context.Appointments.ToListAsync();
        }

        // ==========================
        // CREATE APPOINTMENT
        // ==========================
        [HttpPost]
        public async Task<IActionResult> BookAppointment([FromBody] Appointment appointment)
        {
            try
            {
                if (appointment == null)
                    return BadRequest("Appointment is null");

                appointment.Status = "requested";

                _context.Appointments.Add(appointment);
                await _context.SaveChangesAsync();

                return Ok(new { success = true, message = "Saved!" });
            }
            catch (Exception ex)
            {
                return BadRequest(new
                {
                    error = ex.Message,
                    inner = ex.InnerException?.Message
                });
            }
        }
        // ==========================
        // GET REQUESTED APPOINTMENTS
        // ==========================
        [HttpGet("requested")]
        public async Task<IActionResult> GetRequestedAppointments()
        {
            var appointments = await _context.Appointments
                .Where(a => a.Status == "requested")
                .Join(_context.Patients,
                    a => a.PatientId,
                    p => p.PatientId,
                    (a, p) => new
                    {
                        a.Id,
                        a.PatientId,
                        a.DoctorId,
                        patientName = p.FullName, // ⭐ IMPORTANT
                        a.Date,
                        a.Time,
                        a.Status
                    })
                .ToListAsync();

            return Ok(appointments);
        }
        // ==========================
        // ACCEPT APPOINTMENT
        // ==========================
        [HttpPut("accept/{id}")]
        public async Task<IActionResult> AcceptAppointment(int id)
        {
            var appointment = await _context.Appointments
                .FirstOrDefaultAsync(a => a.Id == id);

            if (appointment == null)
                return NotFound("Appointment not found");

            // Update appointment status
            appointment.Status = "accepted";

            // Create notification in database
            var notification = new Notification
            {
                PatientId = appointment.PatientId,
                Message = $"Your appointment on {appointment.Date} at {appointment.Time} has been accepted",
                CreatedAt = DateTime.Now
            };

            _context.Notifications.Add(notification);

            // Save changes first
            await _context.SaveChangesAsync();

            // Find patient
            var patient = await _context.Patients
     .FirstOrDefaultAsync(p => p.PatientId == appointment.PatientId);

            // Send Firebase Push Notification
            if (patient != null && !string.IsNullOrEmpty(patient.FcmToken))
            {
                try
                {
                    var message = new Message()
                    {
                        Token = patient.FcmToken,

                        Notification = new FirebaseAdmin.Messaging.Notification
                        {
                            Title = "Hospital App",
                            Body = notification.Message
                        },

                        Android = new AndroidConfig
                        {
                            Priority = Priority.High
                        },

                        Data = new Dictionary<string, string>
                {
                    { "type", "appointment" },
                    { "message", notification.Message }
                }
                    };

                    await FirebaseMessaging.DefaultInstance.SendAsync(message);
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Firebase error: " + ex.Message);
                }
            }

            return Ok("Appointment accepted");
        }
        // ==========================
        // REJECT APPOINTMENT
        // ==========================
        [HttpDelete("reject/{id}")]
        public async Task<IActionResult> RejectAppointment(int id)
        {
            var appointment = await _context.Appointments.FindAsync(id);

            if (appointment == null)
                return NotFound("Appointment not found");

            _context.Appointments.Remove(appointment);

            await _context.SaveChangesAsync();

            return Ok(new
            {
                message = "Appointment rejected"
            });
        }

        // ==========================
        // GET ACCEPTED APPOINTMENTS
        // ==========================
        [HttpGet("accepted")]
        public async Task<IActionResult> GetAcceptedAppointments()
        {
            var list = await _context.Appointments
                .Where(a => a.Status != null && a.Status.Trim().ToLower() == "accepted")

                // join patient
                .Join(_context.Patients,
                    a => a.PatientId,
                    p => p.PatientId,
                    (a, p) => new { a, p })

                // 🔥 join doctor (THIS FIX)
                .Join(_context.Doctors,
                    ap => ap.a.DoctorId,
                    d => d.Id,
                    (ap, d) => new
                    {
                        ap.a.Id,
                        ap.a.PatientId,
                        ap.a.DoctorId,

                        patientName = ap.p.FullName,
                        doctorName = d.FullName, // ✅ THIS LINE
                        paymentStatus = ap.a.PaymentStatus,
                        ap.a.Date,
                        ap.a.Time,
                        ap.a.Status
                    })

                .ToListAsync();

            return Ok(list);
        }
        // ==========================
        // COMPLETE APPOINTMENT
        // ==========================
        [HttpPut("complete/{id}")]
        public async Task<IActionResult> CompleteAppointment(int id)
        {
            var appointment = await _context.Appointments.FindAsync(id);

            if (appointment == null)
                return NotFound("Appointment not found");

            appointment.Status = "completed";

            await _context.SaveChangesAsync();

            return Ok(new
            {
                message = "Appointment marked as completed"
            });
        }

        // ==========================
        // GET COMPLETED APPOINTMENTS
        // ==========================
        [HttpGet("completed")]
        public async Task<IActionResult> GetCompletedAppointments()
        {
            var list = await _context.Appointments
                .Where(a => a.Status != null && a.Status.Trim().ToLower() == "completed")
                .Join(_context.Patients,
                    a => a.PatientId,
                    p => p.PatientId,
                    (a, p) => new
                    {
                        a.Id,
                        a.PatientId,
                        a.DoctorId,
                        patientName = p.FullName, // ⭐ IMPORTANT
                        a.Date,
                        a.Time,
                        a.Status
                    })
                .ToListAsync();

            return Ok(list);
        }
        [HttpGet("pending")]
        public async Task<ActionResult<IEnumerable<Appointment>>> GetPendingAppointments()
        {
            return await _context.Appointments
                .Where(a => a.Status == "accepted") // 🔥 NOT pending
                .ToListAsync();
        }
        [HttpPut("pay/{id}")]
        public async Task<IActionResult> PayAppointment(int id)
        {
            var appointment = await _context.Appointments.FindAsync(id);

            if (appointment == null)
                return NotFound();

            appointment.PaymentStatus = "paid"; // 🔥 THIS IS KEY

            await _context.SaveChangesAsync();

            return Ok();
        }
        [HttpPut("mark-paid/{id}")]
        public async Task<IActionResult> MarkAsPaid(int id)
        {
            var appointment = await _context.Appointments.FindAsync(id);

            if (appointment == null)
                return NotFound("Appointment not found");

            appointment.Status = "accepted"; // or "paid"

            await _context.SaveChangesAsync(); // 🔥 MUST EXIST

            return Ok(new { message = "Appointment marked as paid" });
        }
        [HttpGet("accepted/{patientId}")]
        public async Task<IActionResult> GetAcceptedAppointmentsByPatient(int patientId)
        {
            var list = await _context.Appointments
                .Where(a =>
    a.PatientId == patientId &&
    a.Status != null &&
    new[] { "accepted", "completed" }
        .Contains(a.Status.Trim().ToLower())
)

                .Join(_context.Patients,
                    a => a.PatientId,
                    p => p.PatientId,
                    (a, p) => new { a, p })

                .Join(_context.Doctors,
                    ap => ap.a.DoctorId,
                    d => d.Id,
                    (ap, d) => new
                    {
                        ap.a.Id,
                        ap.a.PatientId,
                        ap.a.DoctorId,
                        patientName = ap.p.FullName,
                        doctorName = d.FullName,
                        paymentStatus = ap.a.PaymentStatus,
                        ap.a.Date,
                        ap.a.Time,
                        ap.a.Status
                    })

                .ToListAsync();

            return Ok(list);
        }
    }
}
