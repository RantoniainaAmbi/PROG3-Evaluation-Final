package hei.prog3_tdfinal.repository;

@Repository
@RequiredArgsConstructor
public class CollectivityRepository {
    private final DBConnection dbConnection;

    public void save(Map<String, Object> data) throws SQLException {
        String sql = "INSERT INTO collectivity (name, number, city, specialty, creation_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, (String) data.get("name"));
            ps.setString(2, (String) data.get("number"));
            ps.setString(3, (String) data.get("city"));
            ps.setString(4, (String) data.get("specialty"));
            ps.setDate(5, new java.sql.Date(System.currentTimeMillis())); // Date de création historique [cite: 13]
            ps.executeUpdate();
        }
    }

    public boolean existsByName(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM collectivity WHERE name = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
