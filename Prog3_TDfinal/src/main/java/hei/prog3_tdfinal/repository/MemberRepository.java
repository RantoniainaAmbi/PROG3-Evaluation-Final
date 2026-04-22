package hei.prog3_tdfinal.repository;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final DBConnection dbConnection;

    public void save(UUID collectivityId, Map<String, Object> data) throws SQLException {
        String sql = "INSERT INTO member (first_name, last_name, birth_date, gender, address, job, phone, email, joining_date, collectivity_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, (String) data.get("firstName"));
            ps.setString(2, (String) data.get("lastName"));
            ps.setDate(3, Date.valueOf((String) data.get("birthDate")));
            ps.setString(4, (String) data.get("gender"));
            ps.setString(5, (String) data.get("address"));
            ps.setString(6, (String) data.get("job"));
            ps.setString(7, (String) data.get("phone"));
            ps.setString(8, (String) data.get("email"));
            ps.setDate(9, new java.sql.Date(System.currentTimeMillis()));
            ps.setObject(10, collectivityId);
            ps.executeUpdate();
        }
    }

    public void saveSponsorship(UUID candidateId, UUID sponsorId, String relation) throws SQLException {
        String sql = "INSERT INTO sponsorship (candidate_id, sponsor_id, relation) VALUES (?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, candidateId);
            ps.setObject(2, sponsorId);
            ps.setString(3, relation);
            ps.executeUpdate();
        }
    }
}
