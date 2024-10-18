package readings;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
class ReadingRepositoryTest {

    @Test
    void shouldSelectAllReadings() throws SQLException {
      List <Reading> readingList = ReadingRepository.getAllReadings();
    }
}