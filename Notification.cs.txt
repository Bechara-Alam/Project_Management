public class Notification
{
    public int Id { get; set; }

    public string? Message { get; set; }

    

   
    public int PatientId { get; internal set; }
    public DateTime CreatedAt { get; internal set; }
}