public class Patient
{
    public int PatientId { get; set; }

    public string FullName { get; set; }

    public string Email { get; set; }   // ✅ THIS FIXES THE RED ERROR

    public string Password { get; set; }

    public string? Phone { get; set; }
    public string? FcmToken { get; set; }

    public DateTime? DateOfBirth { get; set; }
    public string? NationalId { get; set; }
    public string? BloodType { get; set; }
    public string? Allergies { get; set; }
    public string? Diseases { get; set; }
    public string? Medications { get; set; }
    public string? Address { get; set; }
    public string? Country { get; set; }
    public string? City { get; set; }
}